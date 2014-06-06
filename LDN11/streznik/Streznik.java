package streznik;

import transakcija.Artikel;
import transakcija.TipTransakcije;
import transakcija.Transakcija;

import javax.net.ssl.*;
import java.io.*;
import java.net.Socket;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.SecureRandom;

public class Streznik {

    static private SecureRandom secureRandom;
    private final String host;
    private final int port;
    private final String imeCertifikata;
    private final String geslo;
    private KeyStore clientKeyStore;
    private KeyStore serverKeyStore;
    private SSLContext sslContext;

    public Streznik(String host, int port, String imeCertifikata, String geslo) {
        this.host = host;
        this.port = port;
        this.imeCertifikata = imeCertifikata;
        this.geslo = geslo;
    }

    public static void main(String[] args) {
        String host = "localhost", ime = null, geslo = null;
        int port = 12345;
        if (args.length > 0)
            host = args[0];
        if (args.length > 1)
            try {
                port = Integer.parseInt(args[1]);
            } catch (Exception e) {
                port = 12345;
            }
        if (args.length > 2)
            ime = args[2];
        if (args.length > 3)
            geslo = args[3];

        if ((ime == null || ime.isEmpty()) || (geslo == null || geslo.isEmpty()))
            System.out.println("Namig: privzeti certifikat je server.private, geslo je serverpwd");
        Console console;
        while ((ime == null || ime.isEmpty()) || (geslo == null || geslo.isEmpty())) {
            try {
                console = System.console();
                if (console != null) {
                    if (ime == null || ime.isEmpty()) {
                        ime = console.readLine("Vnesite ime certifikata: ").trim();
                    }
                    if (geslo == null || geslo.isEmpty()) {
                        char[] pwd = console.readPassword("Vnesite geslo: ");
                        geslo = new String(pwd).trim();
                    }
                } else {
                    throw new NullPointerException("console");
                }
            } catch (Exception ex) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
                try {
                    if (ime == null || ime.isEmpty()) {
                        System.out.print("Vnesite ime certifikata: ");
                        ime = reader.readLine().trim();
                    }
                    if (geslo == null || geslo.isEmpty()) {
                        System.out.print("Vnesite geslo: ");
                        geslo = reader.readLine().trim();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        System.out.printf("Streznik: %s:%d | certifikat: %s ; geslo: %s\n", host, port, ime, geslo);
        secureRandom = new SecureRandom();
        secureRandom.nextInt();
        new Streznik(host, port, ime, geslo).listen();
    }

    private void setupClientKeyStore() throws GeneralSecurityException, IOException {
        clientKeyStore = KeyStore.getInstance("JKS");
        clientKeyStore.load(new FileInputStream("client.public"), "public".toCharArray());
    }

    private void setupServerKeystore() throws GeneralSecurityException, IOException {
        serverKeyStore = KeyStore.getInstance("JKS");
        serverKeyStore.load(new FileInputStream(imeCertifikata), geslo.toCharArray());
    }

    private void setupSSLContext() throws GeneralSecurityException, IOException {
        TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
        tmf.init(clientKeyStore);

        KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
        kmf.init(serverKeyStore, geslo.toCharArray());

        sslContext = SSLContext.getInstance("TLS");
        sslContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), secureRandom);
    }

    public void listen() {
        SSLServerSocket ss = null;
        try {
            setupClientKeyStore();
            setupServerKeystore();
            setupSSLContext();

            SSLServerSocketFactory sf = sslContext.getServerSocketFactory();
            ss = (SSLServerSocket) sf.createServerSocket(port);

            ss.setNeedClientAuth(true);
            ss.setEnabledCipherSuites(new String[]{"TLS_RSA_WITH_AES_128_CBC_SHA"});

            System.out.println("Zaganjam streznik....");
            System.out.printf("Poslusam na %s:%d\n\n", host, port);


            while (true) {
                Socket socket = ss.accept();
                ((SSLSocket) socket).startHandshake();
                String user = ((SSLSocket) socket).getSession().getPeerPrincipal().getName();
                int cnIndex = user.indexOf("CN=");
                if (cnIndex > 0)
                    user = user.substring(cnIndex + 3);
                System.out.println("Zahteva uporabnika: " + user);

                handleConnection(socket);
            }

        } catch (IOException ioe) {
            try {
                if (ss != null)
                    ss.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("IOException: " + ioe);
            ioe.printStackTrace();
            System.exit(1);
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
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
            System.out.println("poslan xml\n" + xmlOdg);
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
