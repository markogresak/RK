package streznik;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import transakcija.Transakcija;

public class Streznik {

	private String host;
	private int port;

	public Streznik(String host, int port) {
		this.host = host;
		this.port = port;
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
			out.writeUTF(Skladisce.obdelajTransakcijo(t).getXMLDocumentString());
		} catch (Exception e) {
			System.err.println("Napaka pri obdelavi transakcije");
			if (t != null)
				out.writeUTF(t.setOdgovor("Napaka pri obdelavi ukaza!")
						.getXMLDocumentString());
			else
				out.writeUTF("Napaka pri obdelavi transakcije, napacni podatki za transakcijo");
		}

		// beri vrstico (samo da se ustavi program - da ahko pokažemo povezavo z
		// netstat)
		// BufferedReader bufferRead = new BufferedReader(new
		// InputStreamReader(System.in));
		// String s = bufferRead.readLine();

		server.close();
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

}
