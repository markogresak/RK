package odjemalec;

import transakcija.Artikel;
import transakcija.TipTransakcije;
import transakcija.Transakcija;

import javax.net.ssl.*;
import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.SecureRandom;

public class Odjemalec {
    private static final BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
    private static SecureRandom secureRandom;
    private final String host;
    private final int port;
    private final String ime;
    private final String geslo;
    private KeyStore clientKeyStore;
    private KeyStore serverKeyStore;
    private SSLContext sslContext;

    public Odjemalec(String host, int port, String ime, String geslo) {
        this.host = host;
        this.port = port;
        this.ime = ime;
        this.geslo = geslo;
    }

    public static void main(String[] args) {
        String host = "localhost", ime = null, geslo = null;
        int port = 12345;
        if (args.length > 0)
            host = args[0];
        if (args.length > 1) {
            try {
                port = Integer.parseInt(args[1]);
            } catch (Exception e) {
                port = 12345;
            }
        }
        if (args.length > 2)
            ime = args[2];
        if (args.length > 3)
            geslo = args[3];

        if((ime == null || ime.isEmpty()) || (geslo == null || geslo.isEmpty()))
            System.out.println("Namig: privzeti certifikat je rk.private, geslo je rkpwd123");
        Console console = null;
        while ((ime == null || ime.isEmpty()) || (geslo == null || geslo.isEmpty())) {
            try {
                console = System.console();
                if (console != null) {
                    if (ime == null || ime.isEmpty()) {
                        ime = console.readLine("Vnesite ime certifikata (ime.private): ").trim();
                    }
                    if (geslo == null || geslo.isEmpty()) {
                        char[] pwd = console.readPassword("Vnesite geslo: ");
                        geslo = new String(pwd).trim();
                    }
                } else {
                    throw new NullPointerException("console");
                }
            } catch (Exception ex) {
                try {
                    if (ime == null || ime.isEmpty()) {
                        System.out.print("Vnesite ime certifikata (ime.private): ");
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
        secureRandom = new SecureRandom();
        secureRandom.nextInt();
        while (true)
            new Odjemalec(host, port, ime, geslo).connect();
    }

    private void setupServerKeystore() throws GeneralSecurityException, IOException {
        serverKeyStore = KeyStore.getInstance("JKS");
        serverKeyStore.load(new FileInputStream("server.public"), "public".toCharArray());
    }

    private void setupClientKeyStore() throws GeneralSecurityException, IOException {
        clientKeyStore = KeyStore.getInstance("JKS");
        clientKeyStore.load(new FileInputStream(ime), geslo.toCharArray());
    }

    private void setupSSLContext() throws GeneralSecurityException, IOException {
        TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
        tmf.init(serverKeyStore);

        KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
        kmf.init(clientKeyStore, geslo.toCharArray());

        sslContext = SSLContext.getInstance("TLS");
        sslContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), secureRandom);
    }

    public void connect() {
        try {
            setupServerKeystore();
            setupClientKeyStore();
            setupSSLContext();

            SSLSocketFactory sf = sslContext.getSocketFactory();
            SSLSocket socket = (SSLSocket) sf.createSocket(host, port);

            socket.setEnabledCipherSuites(new String[]{"TLS_RSA_WITH_AES_128_CBC_SHA"});

            socket.startHandshake();
            handleConnection(socket);
        } catch (UnknownHostException uhe) {
            System.out.println("Unknown host: " + host);
            uhe.printStackTrace();
        } catch (IOException ioe) {
            System.out.println("IOException: " + ioe);
            ioe.printStackTrace();
        } catch (GeneralSecurityException e) {
            System.out.println("GeneralSecurityException: " + e);
            e.printStackTrace();
        }
    }

    protected void handleConnection(Socket client) throws IOException {
        DataOutputStream out = new DataOutputStream(client.getOutputStream());

        out.writeUTF(preberiPodatke());
        try {
            Transakcija tOdgovor = new Transakcija(new DataInputStream(client.getInputStream()));
            System.out.printf("Odgovor strežnika:%n%s%n", tOdgovor.getOdgovor().trim().equals("") ?
                    "Ukaz ni vrnil rezultata" : tOdgovor.getOdgovor());
        } catch (Exception e) {
            System.err.println("Prislo je do napake pri odgovoru iz streznika!");
        }
        client.close();
    }

    private String preberiPodatke() throws IOException {
        String tipString = null;
        TipTransakcije tip;
        do {
            if (tipString != null)
                System.out.println("Tip transakcije ne obstaja! Poizkusite znova.");
            System.out.print("Vnesite ukaz (dobava, izdaja, inventura) ali q za izhod: ");
            tipString = reader.readLine().trim();
            if (tipString.equals("q"))
                System.exit(0);
        } while ((tip = TipTransakcije.getByName(tipString)) == null);

//        String stranka = null;
//        do {
//            if (stranka != null)
//                System.out.println("Vneseno ime ne sme biti prazen niz! Poizkusite znova.");
//            System.out.print("Vnesite vaše ime: ");
//            stranka = reader.readLine().trim();
//        } while (stranka.length() == 0);

        if (tip != TipTransakcije.Inventura) {
            String artikel = null;
            do {
                if (artikel != null)
                    System.out.println("Vnesen artikel ne sme biti prazen niz! Poizkusite znova.");
                System.out.print("Vnesite naziv artikla: ");
                artikel = reader.readLine().trim();
            } while (artikel.length() == 0);

            int kolicina;
            boolean kprvic = true, kNiStevilo = false;
            do {
                if (kNiStevilo)
                    System.out.println("Kolicina mora biti celo stevilo! Poizkusite znova.");
                if (!kprvic)
                    System.out.println("Stevilo artiklov mora biti pozitivno! Poizkusite znova.");
                System.out.print("Vnesite stevilo artiklov: ");
                try {
                    kolicina = Integer.parseInt(reader.readLine().trim());
                    kNiStevilo = false;

                } catch (Exception e) {
                    kolicina = Integer.MIN_VALUE;
                    kNiStevilo = true;
                }
                kprvic = false;
            } while (kolicina <= 0);

            int idSkladisca;
            boolean iprvic = true, iNiStevilo = false;
            do {
                if (iNiStevilo)
                    System.out.println("ID skladisca mora biti celo stevilo! Poizkusite znova.");
                if (!iprvic)
                    System.out.println("ID skladisca mora biti pozitiven! Poizkusite znova.");
                System.out.print("Vnesite ID skladisca: ");
                try {
                    idSkladisca = Integer.parseInt(reader.readLine().trim());
                    iNiStevilo = false;
                } catch (Exception e) {
                    idSkladisca = Integer.MIN_VALUE;
                    iNiStevilo = true;
                }
                iprvic = false;
            } while (idSkladisca <= 0);
            return new Transakcija(tip, this.ime, new Artikel[]{new Artikel(
                    artikel, kolicina, idSkladisca)}).getXMLDocumentString();
        } else
            return new Transakcija(tip, this.ime,
                    new Artikel[]{Artikel.empty()}).getXMLDocumentString();
    }
}