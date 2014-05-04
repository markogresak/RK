package streznik;

import transakcija.Artikel;
import transakcija.TipTransakcije;
import transakcija.Transakcija;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Streznik {

    private final String host;
    private final int port;

    public Streznik(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public static void main(String[] args) {
        String host = "localhost";
        int port = 12345;
        if (args.length > 0)
            host = args[0];
        if (args.length > 1)
            try {
                port = Integer.parseInt(args[1]);
            } catch (Exception e) {
                port = 12345;
            }
        new Streznik(host, port).listen();
    }

    public void listen() {
        ServerSocket listener = null;
        try {
            listener = new ServerSocket(port);
            Socket server;

            System.out.println("Zaganjam streznik....");
            System.out.printf("Poslusam na %s:%d%n", host, port);
            while (true) {
                server = listener.accept();
                handleConnection(server);
            }

        } catch (IOException ioe) {
            try {
                if (listener != null)
                    listener.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("IOException: " + ioe);
            ioe.printStackTrace();
            System.exit(1);
        }
    }

    protected void handleConnection(Socket server) throws IOException {
        DataOutputStream out = new DataOutputStream(server.getOutputStream());

        Transakcija t = null;
        try {
            t = new Transakcija(new DataInputStream(server.getInputStream()));
            System.out.println("Nova zahteva:");
            System.out.println(t.getXMLDocumentString());
            // poslji sporocilo in zapri povezavo
            Transakcija odg = Skladisce.obdelajTransakcijo(t);
            String xmlOdg = odg.getXMLDocumentString();
//            System.out.println("poslan xml\n"+xmlOdg);
            out.writeUTF(xmlOdg);
        } catch (Exception e) {
            System.err.println("Napaka pri obdelavi transakcije");
            Transakcija napaka;
            if (t != null)
                napaka = new Transakcija(TipTransakcije.Napaka, "__NULL__", new Artikel[]{Artikel.empty()},
                        "Napaka pri obdelavi ukaza!");
            else
                napaka = new Transakcija(TipTransakcije.Napaka, "__NULL__", new Artikel[]{Artikel.empty()},
                        "Napaka pri obdelavi transakcije, napacni podatki za transakcijo!");

            out.writeUTF(napaka.getXMLDocumentString());
        }

        // beri vrstico (samo da se ustavi program - da ahko pokažemo povezavo z
        // netstat)
        // BufferedReader bufferRead = new BufferedReader(new
        // InputStreamReader(System.in));
        // String s = bufferRead.readLine();

        server.close();
    }

}
