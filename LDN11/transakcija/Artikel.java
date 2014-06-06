package transakcija;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.security.InvalidParameterException;
import java.util.ArrayList;

public class Artikel {

    private int _id;
    private String _naziv;
    private int _kolicina;
    private int _skladisce;

    public Artikel(String naziv, int kolicina, int skladisce) {
        init(naziv, kolicina, skladisce);
    }

    public Artikel(Node artikel) throws IllegalArgumentException {
        NodeList atributi = artikel.getChildNodes();
        init(atributi.item(1).getTextContent(),
                Integer.parseInt(atributi.item(3).getTextContent()),
                Integer.parseInt(atributi.item(5).getTextContent()));
    }

    private Artikel() {
        _id = -1;
        _naziv = "__NULL__";
        _kolicina = 0;
        _skladisce = -1;
    }

    public static Artikel empty() {
        return new Artikel();
    }

    /**
     * Inicializiraj trenutno instanco Artikla
     *
     * @param naziv     - naziv artikla
     * @param kolicina  - stevilcna kolicina artiklov
     * @param skladisce - skladisce, kamor so artikli shranjeni
     * @throws InvalidParameterException - v primeru da kateri od parametrov ne ustreza
     */
    private void init(String naziv, int kolicina, int skladisce) throws InvalidParameterException {
        Artikel prazen = Artikel.empty();
        if (naziv.equals(prazen._naziv) && kolicina == prazen._kolicina && skladisce == prazen._skladisce) {
            this._naziv = prazen._naziv;
            this._kolicina = prazen._kolicina;
            this._skladisce = prazen._skladisce;
            this._id = prazen._id;
            return;
        }

        // Če je naziv null ali prazen String, sporoči napako
        if (naziv == null || naziv.trim().equals(""))
            throw new InvalidParameterException("Naziv Artikla == null || \"\"");
        this._naziv = naziv;

        // Če je količina manjša od 0 (negativna količina ni podprta), sporoči napako
        if (kolicina < 0)
            throw new InvalidParameterException("kolicina < 0");
        this._kolicina = kolicina;

        // Če je števikla skladišča manjša ali enaka 0, sporoči napako
        if (skladisce <= 0)
            throw new InvalidParameterException("skladisce <= 0");
        this._skladisce = skladisce;

        // Nastavi id Artikla
        this._id = ArtikelID.getID(naziv);
    }

    /**
     * Naziv trenutne instance Artikla
     */
    public String getNaziv() {
        return _naziv;
    }

    /**
     * Količina trenutne instance Artikla
     */
    public int getKolicina() {
        return _kolicina;
    }

    /**
     * Stevilka skladišča, v katerm je trenutna instanca Artikla
     */
    public int getSkladisce() {
        return _skladisce;
    }

    /**
     * Avtomatsko dodan ID artikla
     */
    public int getId() {
        return _id;
    }

    /**
     * Spremeni vrednost kolicine
     *
     * @param spremenba - sprememba kolicine, lahko je pozitivna ali negativna
     * @return - v primeru da je kolicina negativna false, v ostalih primerih true
     */
    public boolean spremeniKolicino(int spremenba) {
        if (getKolicina() + spremenba < 0)
            return false;
        this._kolicina = getKolicina() + spremenba;
        return true;
    }

    /**
     * V podanem DOM dokumentu ustvari XML element s podatki trenutne instance Artikla
     *
     * @param doc - DOM dokument v katerm bo element ustvarjen (!!! NE BO DODAN !!!)
     * @return - DOM Element, v katerm so XML nodi trenutne instance Artikla
     */
    public Element getXMLElement(Document doc) {
        // Naredi element artikel
        Element artikel = doc.createElement("artikel");
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
     *
     * @return - String z vsebino elementa trenutne instance Artikla
     */
//    public String getXMLElementString() {
//        // Za vsako serializacijo trenutne instance je potrebno
//        // inicializirati novo instanco objekta Document
//        Document doc = XMLHelper.newEmptyDocument();
//        if (doc == null)
//            return null;
//        doc.appendChild(getXMLElement(doc));
//        String xmlString = XMLHelper.serializeDocumentToString(doc, true);
//        return xmlString.substring(xmlString.indexOf("?>", 1) + 2).trim();
//    }

    /**
     * Serializiraj XML element iz getXMLElement v String,
     * izhod je formatirna XML element
     *
     * @return - String z vsebino elementa trenutne instance Artikla
     */
//    public String getXMLElementString() {
//        return getXMLElementString(true, true);
//    }
    @Override
    public String toString() {
        String koncnica, kol = String.valueOf(getKolicina());
        int l = kol.length();
        if (l == 1 || (l > 2 && kol.charAt(l - 2) == '0')) {
            if (kol.endsWith("1"))
                koncnica = "";
            else if (kol.endsWith("2"))
                koncnica = "a";
            else if ((kol.endsWith("3") || kol.endsWith("4")))
                koncnica = "i";
            else koncnica = "ov";
        } else koncnica = "ov";

        return String.format("Artikel: %s, %d kos%s v skladiscu %d", getNaziv(), getKolicina(), koncnica, getSkladisce());
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof Artikel) && ((Artikel) obj).getNaziv().equals(this.getNaziv());
    }
}

class ArtikelID {
    private static final ArrayList<String> nazivi = new ArrayList<String>();
    private static final ArrayList<Integer> idji = new ArrayList<Integer>();
    private static int trenutniID = 1;

    public static int getID(String naziv) {
        if (naziv == null || naziv.trim().equals(""))
            return -1;
        if (nazivi.contains(naziv))
            return idji.get(nazivi.indexOf(naziv));
        else {
            nazivi.add(naziv);
            idji.add(trenutniID);
            return trenutniID++;
        }
    }

}