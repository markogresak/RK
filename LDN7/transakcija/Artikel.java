package transakcija;
import java.security.InvalidParameterException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class Artikel {

	private static int _stevecId = 1;
	
	private int _id;
	private String _naziv;
	private int _kolicina;
	private int _skladisce;
	
	public Artikel(String naziv, int kolicina, int skladisce) {
		init(naziv, kolicina, skladisce);
	}

	public Artikel(Node artikel) throws NumberFormatException, IllegalArgumentException {
		NodeList atributi = artikel.getChildNodes();
		init(atributi.item(1).getTextContent(),
				Integer.parseInt(atributi.item(3).getTextContent()),
				Integer.parseInt(atributi.item(5).getTextContent()));
	}

	/**
	 * Inicializiraj trenutno instanco Artikla
	 * @param naziv
	 * @param kolicina
	 * @param skladisce
	 * @throws InvalidParameterException - v primeru da kateri od parametrov ne ustreza
	 */
	private void init(String naziv, int kolicina, int skladisce) throws InvalidParameterException {
		// Če je naziv null ali prazen String, sporoči napako
		if(naziv == null || naziv.trim().equals(""))
			throw new InvalidParameterException("Naziv Artikla == null || \"\"");
		this._naziv = naziv;
		
		// Če je količina manjša od 0 (negativna količina ni podprta), sporoči napako 
		if(kolicina < 0)
			throw new InvalidParameterException("kolicina < 0");
		this._kolicina = kolicina;
		
		// Če je števikla skladišča manjša ali enaka 0, sporoči napako
		if(skladisce <= 0)
			throw new InvalidParameterException("skladisce <= 0");
		this._skladisce = skladisce;
		
		// Nastavi id Artikla
		this._id = _stevecId++;
		// TODO: id artikla, ki še ne obstaja
	}
	
	/**
	 * Naziv trenutne instance Artikla
	 * @return
	 */
	public String getNaziv() {
		return _naziv;
	}
	
	/**
	 * Količina trenutne instance Artikla
	 * @return
	 */
	public int getKolicina() {
		return _kolicina;
	}
	
	/**
	 * Stevilka skladišča, v katerm je trenutna instanca Artikla
	 * @return
	 */
	public int getSkladisce() {
		return _skladisce;
	}
	
	/**
	 * Avtomatsko dodan ID artikla
	 * @return
	 */
	public int getId() {
		return _id;
	}
	
	/**
	 * Spremeni vrednost kolicine
	 * @param spremenba - sprememba kolicine, lahko je pozitivna ali negativna
	 * @return - v primeru da je kolicina negativna false, v ostalih primerih true
	 */
	public boolean spremeniKolicino(int spremenba) {
		if(getKolicina() + spremenba < 0)
			return false;
		_kolicina = getKolicina() + spremenba;
		return true;
	}

	/**
	 * V podanem DOM dokumentu ustvari XML element s podatki trenutne instance Artikla
	 * @param doc - DOM dokument v katerm bo element ustvarjen (!!! NE BO DODAN !!!)
	 * @param addId - ali naj doda id k elementu artikel
	 * @return - DOM Element, v katerm so XML nodi trenutne instance Artikla
	 */
	 public Element getXMLElement(Document doc, boolean addId) {
		 // Naredi element artikel
		 Element artikel = doc.createElement("artikel");
		 if(addId)
			 artikel.setAttribute("id", String.valueOf(getId()));
		 // Naredi element naziv, dodaj text in ga vstavi v artikel
		 Element naziv = doc.createElement("naziv");
		 naziv.setTextContent(getNaziv());
		 artikel.appendChild(naziv);
		
		 // Naredi element količina, dodaj text in ga vstavi v artikel
		 Element kolicina = doc.createElement("kolicina");
		 kolicina.setTextContent(String.valueOf(getKolicina()));
		 artikel.appendChild(kolicina);
		
		 // Naredi element skladišče, dodaj text in ga vstavi v artikel
		 Element skladisce = doc.createElement("skladisce");
		 skladisce.setTextContent(String.valueOf(getSkladisce()));
		 artikel.appendChild(skladisce);
		 
		 return artikel;
	 }
	 
	 /**
	  * Serializiraj XML element iz getXMLElement v String
	  * @param formatted - ali naj bo XML String formatiran
	  * @return - String z vsebino elementa trenutne instance Artikla
	  */
	 public String getXMLElementString(boolean formatted, boolean addId) {
		// Za vsako serializacijo trenutne instance je potrebno
		// inicializirati novo instanco objekta Document
		Document doc = XMLHelper.newEmptyDocument();
		if (doc == null)
			return null;
		doc.appendChild(getXMLElement(doc, addId));
		String xmlString = XMLHelper.serializeDocumentToString(doc, formatted);
		return xmlString.substring(xmlString.indexOf("?>", 1) + 2).trim();
	 }
	 
	 /**
	  * Serializiraj XML element iz getXMLElement v String,
	  * izhod je formatirna XML element
	  * @return - String z vsebino elementa trenutne instance Artikla
	  */
	 public String getXMLElementString() {
		 return getXMLElementString(true, true);
	 }
	 
	 @Override
	public String toString() {
		String koncnica = "", kol = String.valueOf(getKolicina());
		if(kol.endsWith("2")) koncnica = "a";
		else if(kol.endsWith("4")) koncnica = "i";
		else koncnica = "ov";
		
		return String.format("Artikel [ID=%d]: %s, %d kos%s v skladi????u %d", getId(), getNaziv(), getKolicina(), koncnica, getSkladisce());
	}
	 
	@Override
	public boolean equals(Object obj) {
		return ((Artikel)obj).getNaziv().equals(this.getNaziv());
	}
}