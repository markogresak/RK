package odjemalec;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;

import transakcija.*;

public class Odjemalec {
	private String host;
	private int port;
	
    private static final BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
    
    public Odjemalec(String host, int port) {
    	this.host = host;
    	this.port = port;
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
        DataInputStream in = new DataInputStream(client.getInputStream());
        
        out.writeUTF(preberiPodatke());
        System.out.println("Odgovor strežnika:\n"+ in.readUTF());
        client.close();
    }
    
    private String preberiPodatke() throws IOException {
    	String tipString = null;
    	TipTransakcije tip = null;
    	do {
    		if(tipString != null)
    			System.out.println("Tip transakcije ne obstaja! Poizkusite znova.");
	    	System.out.print("Vnesite ukaz (dobava, izdaja, inventura): ");
	        tipString = reader.readLine().trim();
        } while ((tip = TipTransakcije.getByName(tipString)) == null);
        
        String stranka = null;
    	do {
    		if(stranka != null)
    			System.out.println("Vneseno ime ne sme biti prazen niz! Poizkusite znova.");
	    	System.out.print("Vnesite vaše ime: ");
	        stranka = reader.readLine().trim();
        } while (stranka.length() == 0);
        
    	String artikel = null;
    	do {
    		if(artikel != null)
    			System.out.println("Vnesen artikel ne sme biti prazen niz! Poizkusite znova.");
	    	System.out.print("Vnesite naziv artikla: ");
	        artikel = reader.readLine().trim();
        } while (artikel.length() == 0);
    	
    	int kolicina = 0;
    	boolean kprvic = true;
    	do {
    		if(!kprvic)
    			System.out.println("Stevilo artiklov mora biti pozitivno! Poizkusite znova.");
	    	System.out.print("Vnesite stevilo artiklov: ");
	        kolicina = Integer.parseInt(reader.readLine().trim());
	        kprvic = false;
        } while (kolicina <= 0);
    	
    	int idSkladisca = 0;
    	boolean iprvic = true;
    	do {
    		if(!iprvic)
    			System.out.println("ID skladisca mora biti pozitiven! Poizkusite znova.");
	    	System.out.print("Vnesite ID skladisca: ");
	        idSkladisca = Integer.parseInt(reader.readLine().trim());
	        iprvic = false;
        } while (idSkladisca <= 0);
    	
    	return new Transakcija(tip, stranka, new Artikel[] { 
    			new Artikel(artikel, kolicina, idSkladisca)}).getXMLDocumentString();
    }
    
    public static void main(String[] args) {
    	String host = "localhost";
        int port = 12345;    
        if (args.length > 0)
          host = args[0];
        if (args.length > 1)
        	try{
        		port = Integer.parseInt(args[1]);
        	} catch (Exception e) {
        		port = 12345;
        	}
        while (true)
        	new Odjemalec(host, port).connect();
    }
}