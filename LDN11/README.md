LDN11: SSL/TLS
--------

Skladiščni program, ki zna brati in shranjevati XML datoteke ter generirati odgovore na 3 različne tipe datotek:
*   dobavnica *(Primer: datoteka xml/dobavnica.xml)*
*   izdajnica *(Primer: datoteka xml/izdajnica.xml)*
*   inventura *(Primer: datoteka xml/inventura.xml)*

Dodana komunikacija med odjemalcem in strežnikom, komunikacija poteka preko XML sporočil, ki se populirajo s podatki, ki jih je vnesel uporabnik oziroma podatki, ki jih je generiral strežnik po obdelavi ukaza.

### Datoteke: ###

 * transakcija/Artikel.java: razred v katerem shranim tage artikel
 * transakcija/TipTransakcije.java: enum za tipe (namesto plain string)
 * transakcija/Transakcija.java: hrani transakcijo, iz parametrov ali datoteke
 * transakcija/XMLHelper.java: razred s pomožnimi statičnimi metodami

### Odjemalec: ###
 * odjemalec/Odjemalec.java: interakcija z uporabnikom, pošilja na strežnik

### Strežnik: ###
* streznik/Streznik.java: procesira zahteve odjemalca
* streznik/StanjeSkladisca.java: shranjuje stanje zalog v skladiščih

### Zaščitena povezava (SSL/TLS): ###
Uporaba public/private parov v datoteki *certifikati*
* server.public: javni ključ strežnika
* server.private: zasebni ključ strežnika
* client.public: javni ključ, ki je skupen vsem uporabnikom
* rk.private: zasebni ključ rdeče kapice
* babica.private: zasebni ključ babice

### Skripte: ###
* ./certifikati/generatekeys.sh: generira public/private pare za zaščiteno povezavo med odjemalcem in strežnikom
* ./certifikati/generatekeys.bat: enako kot *generatekeys.sh*, le da deluje na Windows operacijskem sistemu
