public enum TipTransakcije {

	Dobava("dobava", 0), Izdaja("izdaja", 1), Inventura("inventura", 2);

	private String _tip;
	private int _id;

	private TipTransakcije(String tip, int id) {
		this._tip = tip;
		this._id = id;
	}

	public String getName() {
		return _tip;
	}
	
	public int getId() {
		return _id;
	}

	/**
	 * Poišče TipTransakcije po imenu, ne glede na vhodni format (case in presledki)
	 * @param name - ime tipa iz TipTransakcije 
	 * @return
	 */
	public static TipTransakcije getByName(String name) {
		if (name == null || (name = name.trim().toLowerCase()).equals(""))
			return null;
		return TipTransakcije.valueOf(name.substring(0, 1).toUpperCase() + name.substring(1));
	}
}
