--KESKUSDIVARI
--java -classpath postgresql-42.2.1.jar:. Keskusdivari
CREATE TABLE Divari (
  divari_id VARCHAR(10),
  nimi VARCHAR (30) NOT NULL,
  osoite VARCHAR (50) NOT NULL,
  nettisivu VARCHAR (30) NOT NULL,
  PRIMARY KEY (divari_id));

CREATE TABLE Teos (
  teos_tunnus INT,
  ISBN VARCHAR(30),
  nimi VARCHAR (50) NOT NULL,
  tekija VARCHAR (50) NOT NULL,
  tyyppi VARCHAR (20) NOT NULL,
  luokka VARCHAR (20) NOT NULL,
  julkaisuvuosi INT NOT NULL,
  sisaanostohinta DECIMAL NOT NULL,
  paino DECIMAL NOT NULL,
  PRIMARY KEY (teos_tunnus));
        
CREATE TABLE Nide (
  nide_tunnus INT,
  teos_tunnus INT NOT NULL,
  divari_id VARCHAR(10) NOT NULL,
  hinta DECIMAL NOT NULL,
  kunto VARCHAR(20) NOT NULL,
  saldo INT NOT NULL,
  PRIMARY KEY (nide_tunnus),
  FOREIGN KEY (teos_tunnus) REFERENCES Teos(teos_tunnus),
  FOREIGN KEY (divari_id) REFERENCES Divari(divari_id));
  
  
CREATE TABLE Tilausnide (
  tilausnide_tunnus INT,
  nide_tunnus INT NOT NULL,
  tilaus_id INT NOT NULL,
  PRIMARY KEY (tilausnide_tunnus),
  FOREIGN KEY (nide_tunnus) REFERENCES Nide(nide_tunnus),
  FOREIGN KEY (tilaus_id) REFERENCES Tilaus(tilaus_id)); 	
   
CREATE TABLE Asiakas (
  tunnus VARCHAR(20),
  salasana VARCHAR (20) NOT NULL,
  nimi VARCHAR (30) NOT NULL,
  osoite VARCHAR (40) NOT NULL,
  puhelin INT NOT NULL,
  sposti VARCHAR (30) NOT NULL,
  PRIMARY KEY (tunnus));
   
CREATE TABLE Tilaus (
  tilaus_id INT,
  asiakas_tunnus VARCHAR(30) NOT NULL,
  yhteishinta DECIMAL,
  pvm DATE,
  tila VARCHAR (20),
  postimaksut DECIMAL,
  kokonaispaino DECIMAL,
  PRIMARY KEY (tilaus_id),
  FOREIGN KEY (asiakas_tunnus) REFERENCES Asiakas(tunnus));

  
--CREATE SCHEMA keskusdivari;
--SET search_path to keskusdivari;
--ALTER SCHEMA keskusdivari OWNER TO tiko2018r15;

--luontilauseet

--ALTER TABLE Teos OWNER TO tiko2018r15;
--ALTER TABLE Divari OWNER TO tiko2018r15;
--ALTER TABLE Nide OWNER TO tiko2018r15;
--ALTER TABLE Asiakas OWNER TO tiko2018r15;
--ALTER TABLE Tilaus OWNER TO tiko2018r15;
--ALTER TABLE Tilausnide OWNER TO tiko2018r15;

INSERT INTO Nide VALUES (1, 1, 'D2', 15.50,'Normaali');

INSERT INTO Teos VALUES (1, '9155430674', 'Elektran tytär', 'Madeleine Brent','romaani','romantiikka',1986,10.50,900.50);

INSERT INTO Teos VALUES (2, '9156381451', 'Tuulentavoittelijan morsian', 'Madeleine Brent','romaani','romantiikka',1978,8,725);

INSERT INTO Teos VALUES (3, NULL,'Turms kuolematon', 'Mika Waltari','romaani','historia',1995,6.30,580);

INSERT INTO Teos VALUES (4, NULL,'Komisario Palmun erehdys', 'Mika Waltari','romaani','dekkari',1940,7,500);

INSERT INTO Teos VALUES (5, NULL,'Friikkilän pojat Mexicossa', 'Shelton Gilbert','sarjakuva','juumori',1989,5.50,200);

INSERT INTO Teos VALUES (6, '9789510396230','Miten saan ystäviä, menestystä, vaikutusvaltaa', 'Dale Carnegien','tietokirja','opas',1939,6.50,450);

INSERT INTO Teos VALUES (7, '5690210','Karibian sankarit', 'Kulker Kulkersson','sarjakuva','huumori',2007,2.99,80);


INSERT INTO Asiakas VALUES ('abc','moro','Peetter pera','Kulkeronkatu 3', 04040404,'moromoro@gmail.com');

INSERT INTO Asiakas VALUES ('bba','xdee','Mikael Maunula','Murinakuja 5 A 12', 056913,'mikael.maunula@gmail.com');

INSERT INTO Asiakas VALUES ('abba','jujup','Erika Ekman','Pörinätie 11', 040123123,'eerikaa1@gmail.com');

INSERT INTO Asiakas VALUES ('yllapitaja','yllapitajad2','d2 yllapitaja','Tutankhamonin katu 3 E 42 ', 3581234,'helpinfo@galleingalle.com');

INSERT INTO Divari VALUES ('D2','Gallein galle','Tutankhamoninkatu 5 D','www.galleingalle.fi');

INSERT INTO tilaus VALUES (8, 'abc', 21, );



UPDATE yksdivari.Teos
SET tyyppi = 'romaani', luokka = 'romantiikka'
WHERE teos_tunnus = 1;

UPDATE yksdivari.Teos
SET tyyppi = 'romaani', luokka = 'romantiikka'
WHERE teos_tunnus = 2;

UPDATE yksdivari.Teos
SET tyyppi = 'romaani', luokka = 'historia'
WHERE teos_tunnus = 3;

UPDATE yksdivari.Teos
SET tyyppi = 'romaani', luokka = 'dekkari'
WHERE teos_tunnus = 4;

UPDATE yksdivari.Teos
SET tyyppi = 'sarjakuva', luokka = 'huumori'
WHERE teos_tunnus = 5;

UPDATE yksdivari.Teos
SET tyyppi = 'tietokirja', luokka = 'opas'
WHERE teos_tunnus = 6;

UPDATE yksdivari.Teos
SET tyyppi = 'sarjakuva', luokka = 'huumori'
WHERE teos_tunnus = 7;

INSERT INTO Tilaus VALUES (1,'abc',15,'2008-11-11','odottaa',2.8);

INSERT INTO Nide VALUES (2,2,'D2',17.59,'Hyva', 1);

INSERT INTO Nide VALUES (3,3,'D2',13,'Normaali', 1);

INSERT INTO Nide VALUES (4,4,'D2',9,'Huono', 1);

INSERT INTO Nide VALUES (5,2,'D2',11.25,'Normaali', 1); 

INSERT INTO Nide VALUES (12,7,'D2',4.50,'Normaali', 3); 

UPDATE yksdivari.nide
set saldo = 7
WHERE nide_tunnus = 9;

UPDATE yksdivari.nide
set saldo = 2
WHERE nide_tunnus = 8;

SELECT asiakas.nimi AS asiakas, SUM(tilaus.yhteishinta), tilaus.pvm
FROM keskusdivari.asiakas
INNER JOIN tilaus
ON asiakas.tunnus = tilaus.asiakas_tunnus
WHERE pvm < now() - INTERVAL '1 year' 
OR pvm > now() - INTERVAL '1 year'
GROUP BY asiakas.nimi, tilaus.pvm;



SELECT asiakas.nimi AS asiakas, COUNT(tilausnide.tilausnide_tunnus)
FROM keskusdivari.asiakas
INNER JOIN keskusdivari.tilaus
ON asiakas.tunnus = tilaus.asiakas_tunnus
INNER JOIN keskusdivari.tilausnide
ON tilaus.tilaus_id = tilausnide.tilaus_id
WHERE pvm >= date_trunc('year', now() - interval '1 year') AND pvm < date_trunc('year', now())
GROUP BY asiakas.nimi
ORDER BY asiakas.nimi ASC;




INSERT INTO Nide VALUES (8,4,'D1',8.25,'Huono', 1);

INSERT INTO Nide VALUES (9,2,'D1',11.55,'Normaali', 1); 

INSERT INTO Nide VALUES (9,5,'D1',4.79,'Normaali', 7); 
INSERT INTO Nide VALUES (11,6,'D1',14.55,'Hyva', 1); 

INSERT INTO Tilausnide VALUES (1,1,1);
INSERT INTO Tilausnide VALUES (2,2,1);

SELECT COUNT(nide.teos_tunnus)
FROM nide
WHERE nide.kunto = 'Hyva';

SELECT COUNT(nide.teos_tunnus)
FROM nide
WHERE nide.kunto = 'Huono';


SELECT COUNT(nide.teos_tunnus)
FROM nide
WHERE nide.kunto = 'Normaali';



--YKSITTÄINEN


--CREATE SCHEMA yksdivari
--SET search_path to yksdivari;
--ALTER SCHEMA yksdivari OWNER TO tiko2018r15;

CREATE TABLE Divari (
  divari_id VARCHAR(10),
  nimi VARCHAR (30) NOT NULL,
  osoite VARCHAR (50) NOT NULL,
  nettisivu VARCHAR (30) NOT NULL,
  PRIMARY KEY (divari_id));
  
CREATE TABLE Teos (
  teos_tunnus INT,
  ISBN VARCHAR(30),
  nimi VARCHAR (50) NOT NULL,
  tekija VARCHAR (50) NOT NULL,
  tyyppi VARCHAR (20) NOT NULL,
  luokka VARCHAR (20) NOT NULL,
  julkaisuvuosi INT NOT NULL,
  sisaanostohinta DECIMAL NOT NULL,
  paino DECIMAL NOT NULL,
  PRIMARY KEY (teos_tunnus));
        
CREATE TABLE Nide (
  nide_tunnus INT,
  teos_tunnus INT NOT NULL,
  divari_id VARCHAR(10) NOT NULL,
  hinta DECIMAL NOT NULL,
  kunto VARCHAR(20) NOT NULL,
  saldo INT NOT NULL,
  PRIMARY KEY (nide_tunnus),
  FOREIGN KEY (teos_tunnus) REFERENCES Teos(teos_tunnus),
  FOREIGN KEY (divari_id) REFERENCES Divari(divari_id));
  
  
CREATE TABLE Tyontekija (
  tyontekija_tunnus VARCHAR(20),
  divari_id VARCHAR(10),
  salasana VARCHAR (20) NOT NULL,
  PRIMARY KEY (tyontekija_tunnus),
  FOREIGN KEY (divari_id) REFERENCES Divari(divari_id));  
  
 --ALTER TABLE Tyontekija OWNER TO tiko2018r15;
 --ALTER TABLE Teos OWNER TO tiko2018r15;
 --ALTER TABLE Nide OWNER TO tiko2018r15;
  
INSERT INTO Teos VALUES (1, 'D1','9155430674', 'Elektran tytär', 'Madeleine Brent','romantiikka','romaani',1986,10.50,900.50);

INSERT INTO Teos VALUES (2, 'D1','9156381451', 'Tuulentavoittelijan morsian', 'Madeleine Brent','romantiikka','romaani',1978,8,725);

INSERT INTO Teos VALUES (3, 'D1',NULL,'Turms kuolematon', 'Mika Waltari','Historia','romaani',1995,6.30,580);

INSERT INTO Teos VALUES (4, 'D1',NULL,'Komisario Palmun erehdys', 'Mika Waltari','dekkari','romaani',1940,7,500);

INSERT INTO Teos VALUES (5, 'D1',NULL,'Friikkilän pojat Mexicossa', 'Shelton Gilbert','huumori','sarjakuva',1989,5.50,200);

INSERT INTO Teos VALUES (6, 'D1','9789510396230','Miten saan ystäviä, menestystä, vaikutusvaltaa', 'Dale Carnegien','opas','tietokirja',1939,6.50,450);
   
INSERT INTO Divari VALUES ('D1','Lassen Lehti','Ryokaleentanner 42','www.LassenLethi.fi');

INSERT INTO Tyontekija VALUES ('yllapitaja','D1','yllapitajad1');

DELETE FROM nide where saldo = 0;

SELECT teos.luokka, cast(round(AVG(CAST(nide.hinta AS numeric(12,2))),2) as decimal(12,2)) keskihinta_luokalle, SUM(nide.hinta) AS kokonaismyyntihinta_luokalle
FROM keskusdivari.Teos
INNER JOIN keskusdivari.nide
ON teos.teos_tunnus = nide.teos_tunnus
WHERE saldo > 0
GROUP BY teos.luokka
ORDER BY teos.luokka ASC;





