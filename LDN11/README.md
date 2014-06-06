LDN7: Programiranje vtičev (Java/Python, odjemalec-strežnik)
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
