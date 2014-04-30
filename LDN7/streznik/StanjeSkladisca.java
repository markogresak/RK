package streznik;

import java.util.ArrayList;

import transakcija.Artikel;

public class StanjeSkladisca {

	private int _idSkladisca;
	private ArrayList<String> _uporabniki;
	private ArrayList<ArrayList<Artikel>> _artikli;

	public StanjeSkladisca(int idSkladisca) {
		this._idSkladisca = idSkladisca;
	}

	public int getIdSkladisca() {
		return _idSkladisca;
	}

	public ArrayList<String> getUporabniki() {
		return _uporabniki;
	}

	public ArrayList<ArrayList<Artikel>> getArtikli() {
		return _artikli;
	}

	private ArrayList<Artikel> getArtikliUporabnik(int iUporabnik) {
		if (iUporabnik < 0)
			return null;
		return _artikli.get(iUporabnik);
	}

	private int najdiUporabnika(String uporabnik) {
		int index = -1;
		if (_uporabniki.contains(uporabnik))
			for (int i = 0; i < _uporabniki.size(); i++)
				if (uporabnik.equals(_uporabniki.get(i))) {
					index = i;
					break;
				}
		if (index == -1) {
			index = _uporabniki.size();
			_uporabniki.add(uporabnik);
		}
		return index;
	}

	private int najdiArtikel(Artikel artikel, int iUporabnik) {
		int index = -1;
		ArrayList<Artikel> artUporabnik = getArtikliUporabnik(iUporabnik);
		if (artUporabnik.contains(artikel))
			for (int i = 0; i < artUporabnik.size(); i++)
				if (artikel.equals(artUporabnik.get(i))) {
					index = i;
					break;
				}
		if (index == -1) {
			index = artUporabnik.size();
			_artikli.get(iUporabnik).add(artikel);
		}
		return index;
	}

	private void spremeniArtikel(String uporabnik, Artikel artikel, boolean dobava) {
		int iUporabnik = najdiUporabnika(uporabnik);
		int iArtikel = najdiArtikel(artikel, iUporabnik);
		if (iArtikel < getArtikliUporabnik(iUporabnik).size() - 1)
			_artikli.get(iUporabnik).get(iArtikel)
					.spremeniKolicino(artikel.getKolicina() * (dobava ? 1 : -1));
	}

	public void dobavaArtikla(String uporabnik, Artikel artikel) {
		spremeniArtikel(uporabnik, artikel, true);
	}

	public void izdajaArtikla(String uporabnik, Artikel artikel) {
		spremeniArtikel(uporabnik, artikel, false);
	}
}
