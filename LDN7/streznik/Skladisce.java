package streznik;

import java.util.ArrayList;

import transakcija.Artikel;
import transakcija.TipTransakcije;
import transakcija.Transakcija;

public class Skladisce {

	private static ArrayList<Skladisce> skladisca = new ArrayList<Skladisce>();

	private int _idSkladisca;
	private ArrayList<String> _stranke;
	private ArrayList<ArrayList<Artikel>> _artikli;

	public Skladisce(int idSkladisca) {
		this._idSkladisca = idSkladisca;
	}

	public int getIdSkladisca() {
		return _idSkladisca;
	}

	public ArrayList<String> getStranke() {
		return _stranke;
	}

	public ArrayList<ArrayList<Artikel>> getArtikli() {
		return _artikli;
	}

	private ArrayList<Artikel> getArtikliStranka(int iStranka) {
		if (iStranka < 0)
			return null;
		return _artikli.get(iStranka);
	}

	private int najdiStranka(String stranka) {
		int index = -1;
		if (_stranke.contains(stranka))
			for (int i = 0; i < _stranke.size(); i++)
				if (stranka.equals(_stranke.get(i))) {
					index = i;
					break;
				}
		if (index == -1) {
			index = _stranke.size();
			_stranke.add(stranka);
		}
		return index;
	}

	private int najdiArtikel(Artikel artikel, int iStranka) {
		int index = -1;
		ArrayList<Artikel> artstranka = getArtikliStranka(iStranka);
		if (artstranka.contains(artikel))
			for (int i = 0; i < artstranka.size(); i++)
				if (artikel.equals(artstranka.get(i))) {
					index = i;
					break;
				}
		if (index == -1) {
			index = artstranka.size();
			_artikli.get(iStranka).add(artikel);
		}
		return index;
	}

	private boolean spremeniArtikel(String stranka, Artikel artikel,
			boolean dobava) {
		int iStranka = najdiStranka(stranka);
		int iArtikel = najdiArtikel(artikel, iStranka);
		if (iArtikel < getArtikliStranka(iStranka).size() - 1)
			return _artikli
					.get(iStranka)
					.get(iArtikel)
					.spremeniKolicino(artikel.getKolicina() * (dobava ? 1 : -1));
		return true;
	}

	private static Transakcija dobava(Transakcija t) {
		Artikel[] art = t.getArtikli();
		for (int i = 0; i < art.length; i++)
			najdiSkladisce(art[i])
					.spremeniArtikel(t.getStranka(), art[i], true);
		return t.generateOdgovor();
	}

	private static Transakcija izdaja(Transakcija t) {
		Artikel[] art = t.getArtikli();
		for (int i = 0; i < art.length; i++) {
			Skladisce s = najdiSkladisce(art[i]);
			if (!s.spremeniArtikel(t.getStranka(), art[i], true))
				return t.setOdgovor(String
						.format("Napaka pri izdaji artikla %s, zahtevana kolicina = %d, stanje v skladiscu = %d",
								art[i].getNaziv(),
								art[i].getKolicina(),
								s.getArtikliStranka(
										s.najdiStranka(t.getStranka()))
										.get(s.najdiArtikel(art[i],
												s.najdiStranka(t.getStranka())))
										.getKolicina()));
		}
		return t.generateOdgovor();
	}

	private static Transakcija stanjeSkladisca(String stranka) {
		ArrayList<Artikel> art = new ArrayList<Artikel>();
		ArrayList<Skladisce> skl = najdiSkladiscaStranka(stranka);
		for (Skladisce s : skl)
			art.addAll(s.getArtikliStranka(s.najdiStranka(stranka)));
		return new Transakcija(TipTransakcije.Inventura, stranka,
				art.toArray(new Artikel[art.size()])).generateOdgovor();
	}

	public static Transakcija obdelajTransakcijo(Transakcija t) {
		if (t.getTip().equals(TipTransakcije.Dobava))
			return dobava(t);
		else if (t.getTip().equals(TipTransakcije.Izdaja))
			return izdaja(t);
		else if (t.getTip().equals(TipTransakcije.Inventura))
			return stanjeSkladisca(t.getStranka());
		else
			return null;
	}

	@Override
	public boolean equals(Object obj) {
		return ((Skladisce) obj)._idSkladisca == this._idSkladisca;
	}

	public static Skladisce najdiSkladisce(Artikel a) {
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

	public static ArrayList<Skladisce> najdiSkladiscaStranka(String stranka) {
		ArrayList<Skladisce> skl = new ArrayList<>();
		for (Skladisce s : skladisca)
			for (String str : s.getStranke())
				if (str.equals(stranka)) {
					skl.add(s);
					break;
				}
		return skl;
	}
}
