import java.io.IOException;

public class SkladiscniProgram {

	public static void main(String[] args) throws IOException {
		Transakcija dobavnica = new Transakcija("xml/dobavnica.xml");
		dobavnica.writeToXMLFile("dobavnica.xml");
		System.out.println(dobavnica.generateOdgovor().getXMLDocumentString());
		dobavnica.writeToXMLFile("xml/odgovori/dobavnica-odgovor.xml");
		
		Transakcija izdajnica = new Transakcija("xml/izdajnica.xml");
		izdajnica.writeToXMLFile("izdajnica.xml");
		System.out.println(izdajnica.generateOdgovor().getXMLDocumentString());
		izdajnica.writeToXMLFile("xml/odgovori/izdajnica-odgovor.xml");
		
		Transakcija inventura = new Transakcija("xml/inventura.xml");
		inventura.writeToXMLFile("inventura.xml");
		System.out.println(inventura.generateOdgovor().getXMLDocumentString());
		inventura.writeToXMLFile("xml/odgovori/inventura-odgovor.xml");
	}

}
