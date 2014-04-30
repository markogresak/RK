LDN2: Skladišče - kreiranje in razčlenjevanje XML sporočil
--------

Skladiščni program, ki zna brati in shranjevati XML datoteke ter generirati odgovore na 3 različne tipe datotek:
*   dobavnica *(Primer: datoteka xml/dobavnica.xml)*
*   izdajnica *(Primer: datoteka xml/izdajnica.xml)*
*   inventura *(Primer: datoteka xml/inventura.xml)*

**Glavni program *(metoda main)* se nahaja v datoteki SkladiscniProgram.java**

### Datoteke: ###

 * Artikel.java: razred v katerem shranim tage artikel
 * TipTransakcije.java: enum za tipe (namesto plain string)
 * Transakcija.java: shrani celotno transakcijo, iz parametrov ali datoteke
 * XMLHelper.java: razred s pomožnimi statičnimi metodami
 * SkladiscniProgram.java: glavni program (testi)

