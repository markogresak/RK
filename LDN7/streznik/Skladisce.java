package streznik;

import transakcija.Artikel;
import transakcija.TipTransakcije;
import transakcija.Transakcija;

import java.util.ArrayList;

class Skladisce {

    private static final ArrayList<Skladisce> skladisca = new ArrayList<Skladisce>();

    private final int _idSkladisca;
    private final ArrayList<String> _stranke;
    private final ArrayList<ArrayList<Artikel>> _artikli;

    private Skladisce(int idSkladisca) {
        this._idSkladisca = idSkladisca;
        this._stranke = new ArrayList<String>();
        this._artikli = new ArrayList<ArrayList<Artikel>>();
    }

    private static Transakcija dobava(Transakcija t) {
        Artikel[] art = t.getArtikli();
        for (Artikel anArt : art) najdiSkladisce(anArt).spremeniArtikel(t.getStranka(), anArt, true);
        return t.generateOdgovor();
    }

    private static Transakcija izdaja(Transakcija t) {
        Artikel[] art = t.getArtikli();
        for (Artikel anArt : art) {
            Skladisce s = najdiSkladisce(anArt);
            if (!s.spremeniArtikel(t.getStranka(), anArt, false)) {
                int iStranka = s.najdiStranka(t.getStranka(), false);
                if (iStranka < 0)
                    return t.setOdgovor(String.format("Napaka pri izdaji artikla %s, stranka nima zalog v tem skladiscu!", anArt.getNaziv()));
                else {
                    int iArtikel = s.najdiArtikel(anArt, iStranka);
                    if (iArtikel < 0)
                        return t.setOdgovor(String.format("Napaka pri izdaji artikla %s, artikel ne obstaja v skladiscu stranke!", anArt.getNaziv()));
                    else
                        return t.setOdgovor(String.format("Napaka pri izdaji artikla %s, zahtevana kolicina = %d, stanje v skladiscu = %d",
                                anArt.getNaziv(),
                                anArt.getKolicina(),
                                s.getArtikliStranka(iStranka).get(iArtikel).getKolicina()));
                }
            }
        }
        return t.generateOdgovor();
    }

    private static Transakcija inventura(Transakcija t) {
        String stranka = t.getStranka();
        ArrayList<Artikel> art = new ArrayList<Artikel>();
        ArrayList<Skladisce> skl = najdiSkladiscaStranka(stranka);
        for (Skladisce s : skl) {
            int iStranka = s.najdiStranka(stranka, false);
            if (iStranka == -1)
                continue;
            art.addAll(s.getArtikliStranka(iStranka));
        }

        if (art.size() == 0)
            return new Transakcija(TipTransakcije.Inventura, stranka, new Artikel[]{Artikel.empty()},
                    String.format("Stranka %s nima v skladiscih nobenega artikla.", stranka));
        else
            return new Transakcija(TipTransakcije.Inventura, stranka, art.toArray(new Artikel[art.size()])).generateOdgovor();
    }

    public static Transakcija obdelajTransakcijo(Transakcija t) {
        if (t.getTip().equals(TipTransakcije.Dobava))
            return dobava(t);
        else if (t.getTip().equals(TipTransakcije.Izdaja))
            return izdaja(t);
        else if (t.getTip().equals(TipTransakcije.Inventura))
            return inventura(t);
        else
            return null;
    }

    private static Skladisce najdiSkladisce(Artikel a) {
        int index = -1;
        for (int i = 0; i < skladisca.size(); i++)
            if (skladisca.get(i).getIdSkladisca() == a.getSkladisce()) {
                index = i;
                break;
            }
        if (index == -1) {
            skladisca.add(new Skladisce(a.getSkladisce()));
            index = skladisca.size() - 1;
        }
        return skladisca.get(index);
    }

    private static ArrayList<Skladisce> najdiSkladiscaStranka(String stranka) {
        ArrayList<Skladisce> skl = new ArrayList<Skladisce>();
        for (Skladisce s : skladisca)
            for (String str : s.getStranke())
                if (str.equals(stranka)) {
                    skl.add(s);
                    break;
                }
        return skl;
    }

    int getIdSkladisca() {
        return _idSkladisca;
    }

    ArrayList<String> getStranke() {
        return _stranke;
    }

//    public ArrayList<ArrayList<Artikel>> getArtikli() {
//        return _artikli;
//    }

    private ArrayList<Artikel> getArtikliStranka(int iStranka) {
        if (iStranka < 0)
            return null;
        return _artikli.get(iStranka);
    }

    private int najdiStranka(String stranka, boolean dodajNovo) {
        int index = -1;
        if (_stranke.size() > 0 && _stranke.contains(stranka))
            for (int i = 0; i < _stranke.size(); i++)
                if (stranka.equals(_stranke.get(i))) {
                    index = i;
                    break;
                }
        if (index == -1 && dodajNovo) {
            index = _stranke.size();
            _stranke.add(stranka);
            _artikli.add(new ArrayList<Artikel>());
        }
        return index;
    }

    private int najdiArtikel(Artikel artikel, int iStranka) {
        int index = -1;
        ArrayList<Artikel> artStranka = getArtikliStranka(iStranka);
        if (artStranka.contains(artikel))
            for (int i = 0; i < artStranka.size(); i++)
                if (artikel.equals(artStranka.get(i))) {
                    index = i;
                    break;
                }

        return index;
    }

    private boolean spremeniArtikel(String stranka, Artikel artikel, boolean dobava) {
        int iStranka = najdiStranka(stranka, dobava);
        if (iStranka == -1)
            return false;
        int iArtikel = najdiArtikel(artikel, iStranka);
        if (iArtikel == -1) {
            if (dobava) {
//                iArtikel = _artikli.get(iStranka).size();
                _artikli.get(iStranka).add(artikel);
                return true;
            } else
                return false;
        }
        boolean rezultat = _artikli.get(iStranka).get(iArtikel).spremeniKolicino(artikel.getKolicina() * (dobava ? 1 : -1));
        if (_artikli.get(iStranka).get(iArtikel).getKolicina() == 0)
            _artikli.get(iStranka).remove(iArtikel);
        return rezultat;
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof Skladisce) && ((Skladisce) obj)._idSkladisca == this._idSkladisca;
    }
}
