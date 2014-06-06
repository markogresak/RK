package odjemalec;

import transakcija.Artikel;
import transakcija.TipTransakcije;
import transakcija.Transakcija;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;

public class Odjemalec {
    private static final BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
    private final String host;
    private final int port;

    public Odjemalec(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public static void main(String[] args) {
        String host = "localhost";
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
        while (true)
            new Odjemalec(host, port).connect();
    }

    public void connect() {
        try {
            Socket client = new Socket(host, port);
            handleConnection(client);
        } catch (UnknownHostException uhe) {
            System.out.println("Unknown host: " + host);
            uhe.printStackTrace();
        } catch (IOException ioe) {
            System.out.println("IOException: " + ioe);
            ioe.printStackTrace();
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

        String stranka = null;
        do {
            if (stranka != null)
                System.out.println("Vneseno ime ne sme biti prazen niz! Poizkusite znova.");
            System.out.print("Vnesite vaše ime: ");
            stranka = reader.readLine().trim();
        } while (stranka.length() == 0);

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
            return new Transakcija(tip, stranka, new Artikel[]{new Artikel(
                    artikel, kolicina, idSkladisca)}).getXMLDocumentString();
        } else
            return new Transakcija(tip, stranka,
                    new Artikel[]{Artikel.empty()}).getXMLDocumentString();
    }
}