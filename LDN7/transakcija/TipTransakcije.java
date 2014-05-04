package transakcija;

public enum TipTransakcije {

    Dobava("dobava", 0), Izdaja("izdaja", 1), Inventura("inventura", 2), Napaka("napaka", 3);

    private final String _tip;
    private final int _id;

    private TipTransakcije(String tip, int id) {
        this._tip = tip;
        this._id = id;
    }

    /**
     * Poišče TipTransakcije po imenu, ne glede na vhodni format (case in
     * presledki)
     *
     * @param name - ime tipa iz TipTransakcije
     */
    public static TipTransakcije getByName(String name) {
        if (name == null || (name = name.trim().toLowerCase()).equals(""))
            return null;
        try {
            return TipTransakcije.valueOf(name.substring(0, 1).toUpperCase() + name.substring(1));
        } catch (Exception e) {
            return null;
        }
    }

    public String getName() {
        return _tip;
    }

    public int getId() {
        return _id;
    }
}
