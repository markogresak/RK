import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.security.InvalidParameterException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class Transakcija {
	
	private TipTransakcije _tip;
	private String _stranka;
	private Artikel[] _artikli;
	private String _odgovor;
	private Document _doc;
	
	/**
	 * Konstruktor, ki objekt inicializira s podanimi podatki
	 * @param tip - tip transakcije
	 * @param stranka - ime stranke, na katero se nanaša transakcija
	 * @param artikli - artikli, ki so obravnavani v transakciji
	 */
	public Transakcija(TipTransakcije tip, String stranka, Artikel[] artikli) {
		init(tip, stranka, artikli, "");
	}
	
	public Transakcija(TipTransakcije tip, String stranka, Artikel[] artikli, String odgovor) {
		init(tip, stranka, artikli, odgovor);
	}

	/**
	 * Konstruktor, ki objekt inicializira s podatki iz podane datoteke
	 * @param datoteka - xml datoteka s podatki o transakciji
	 */
	public Transakcija(File datoteka){
		
		// Če datoteka ne obstaja, obvesti o napaki
		if (!datoteka.exists())
			throw new InvalidParameterException("Datoteka " + datoteka.getAbsolutePath() + " ne obstaja!");
			
		// Odpri podan dokument in ga parsaj
		Document doc = XMLHelper.newParsedDocument(datoteka);
		
		// Preberi vsebino atributa tip v tagu transakcija
		TipTransakcije tip = TipTransakcije.getByName(doc.getElementsByTagName("transakcija").item(0).getAttributes().getNamedItem("tip").getTextContent());
		
		// Preberi vsebino elementa stranka
		String stranka = doc.getElementsByTagName("stranka").item(0).getTextContent();
		
		// Preberi vsebino elementa odgovor
		Node nOdgovor = doc.getElementsByTagName("odgovor").item(0); 
		String odgovor =  nOdgovor != null ? nOdgovor.getTextContent() : "";
		
		// Poišči vse tage artikel in iz vsakega naredi objekt Artikel
		NodeList artikliList = doc.getElementsByTagName("artikel");
		Artikel[] artikli = new Artikel[artikliList.getLength()];
		for(int i=0;i<artikliList.getLength();i++)
			artikli[i] = new Artikel(artikliList.item(i));
		
		// Z zbranimi podatki iniclializiraj trenutno instanco Transakcija
		init(tip, stranka, artikli, odgovor);
	}
	
	/**
	 * Konstruktor, ki objekt inicializira s podatki iz datoteke na podani poti
	 * @param pot - pot do xml datoteke s podatki o transakciji
	 */
	public Transakcija(String pot){
		this(new File(pot));
	}
	
	private void init(TipTransakcije tip, String stranka, Artikel[] artikli, String odgovor) {
		// Če je tip transkacije null ali prazen String, sporoči napako
		if(tip == null)
			throw new InvalidParameterException("tip transakcije == null");
		this._tip = tip;
		
		// Če je stranka null ali prazen String, sporoči napako
		if(stranka == null || stranka.trim().equals(""))
			throw new InvalidParameterException("stranka == null || \"\"");
		this._stranka = stranka;
		
		// Če je tabela artikli null ali brez elementov, ali pa je kateri od
		// elementov null, obvesti o napaki
		if(artikli == null || artikli.length == 0)
			throw new InvalidParameterException("artikli == null || artikli.length == 0");
		for(int i=0;i<artikli.length;i++)
			if(artikli[i] == null)
				throw new InvalidParameterException("artikel["+ i +"] == null");
		this._artikli = artikli;
		
		this._odgovor = odgovor != null ? odgovor : "";
		
		generateXMLDocument();
	}
	
	private void generateXMLDocument() {
		// Ustvari element transakcija in dodaj atribut tip
		_doc = XMLHelper.newEmptyDocument();
		
		Element elTransakcija = _doc.createElement("transakcija");
		elTransakcija.setAttribute("tip", getTip().getName());
		elTransakcija.setAttribute("odgovor", String.valueOf(!getOdgovor().trim().equals("")));
		
		Element elZadnji = _artikli[_artikli.length-1].getXMLElement(_doc, true);
		elTransakcija.appendChild(elZadnji);
		
		for(int i=_artikli.length-2;i>=0;i--){
			Element el = _artikli[i].getXMLElement(_doc, true);
			elTransakcija.insertBefore(el, elZadnji);
			elZadnji = el;
		}
		
		Element elOdgovor = _doc.createElement("odgovor");
		elOdgovor.setTextContent(getOdgovor());
		elTransakcija.insertBefore(elOdgovor, elZadnji);
		
		Element elStranka = _doc.createElement("stranka");
		elStranka.setTextContent(getStranka());
		
		elTransakcija.insertBefore(elStranka, elOdgovor);
		
		_doc.appendChild(elTransakcija);

	}
	
	public TipTransakcije getTip() {
		return _tip;
	}
	
	public String getStranka() {
		return _stranka;
	}
	
	public String getOdgovor() {
		return _odgovor;
	}
	
	public Artikel[] getArtikli() {
		return _artikli;
	}
	
	public Document getXMLDocument(){
		return _doc;
	}
	
	private void setOdgovor(String odgovor) {
		this._odgovor = odgovor;
	}
	
	public Transakcija generateOdgovor() {
		StringBuilder odgovor = new StringBuilder();
		odgovor.append(String.format("%s stranke %s: ", getTip().getName(), getStranka()));
		for(int i=0;i<getArtikli().length;i++){
			if(i > 0)
				odgovor.append(" ; ");
			odgovor.append(getArtikli()[i].toString());
		}
		setOdgovor(odgovor.toString());
		generateXMLDocument();
		return this;
	}
	
	public String getXMLDocumentString() {
		return XMLHelper.serializeDocumentToString(getXMLDocument(), true);
	}
	
	public void writeToXMLFile(File f) throws IOException {
		BufferedWriter out = new BufferedWriter(new PrintWriter(f, "UTF-8"));
		out.write(getXMLDocumentString());
		out.close();
	}
	
	public void writeToXMLFile(String pot) throws IOException {
		writeToXMLFile(new File(pot));
	}
	
}
