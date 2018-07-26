//Santeri Tuomisto & Aleksi Lahtinen
//422465 & 424411
//Keskusdivari
      
import java.sql.*;
import java.util.Scanner;
      
public class Keskusdivari {
      
  private static final String PROTOKOLLA = "jdbc:postgresql:";
  private static final String PALVELIN = "dbstud2.sis.uta.fi";
  private static final int PORTTI = 5432;
  private static final String TIETOKANTA = "tiko2018r15";
  private static final String KAYTTAJA = "al424411";
  private static final String SALASANA = "6663";
  private static Scanner sc = new Scanner(System.in);
      
  //Onko käyttäjä kirjautunut sisään vai ei. Ja asiakkaan tunnus
  private static boolean kirjautunut = false;
  private static String kirjautunutAsiakasTunnus = "";
    
  //Koko divarin pyörintä.
  private static boolean divarinPyorinta = true;
      
  //onko ylläpitäjä
  private static boolean yllapitaja = false;
  private static boolean yllapitaja1 = false;
    
  //asiakkaan tilaus_id tietokantaan, joka asettuu kun asiakas lisää tuotteen ostoskoriin ja nollautuu kun tilaus lähtee.
  private static int tilaus_id;
  private static boolean ensimmainenTuote = true;
  private static boolean kesken = false;
   
  //Vakioidaan postimaksut, jotta helpompi päivittää myöhemmin
  private static final double POSTIMAKSU50G = 1.40;
  private static final double POSTIMAKSU100G = 2.10;
  private static final double POSTIMAKSU250G = 2.80;
  private static final double POSTIMAKSU500G = 5.60;
  private static final double POSTIMAKSU1000G = 8.40;
  private static final double POSTIMAKSU2000G = 14.00;
      
  public static void main(String args[]) {
    Connection con = null;
    try {
      con = DriverManager.getConnection(PROTOKOLLA + "//" + PALVELIN + ":" + PORTTI + "/" + TIETOKANTA, KAYTTAJA, SALASANA);
            
      //koko divarin pyörintä.
      //jos käyttäjä ei ole kirjautunut sisään niin mennään kirjautumiskomentoihin, josta voi valita joko rekisteröitymisen tai kirjautumisen.
      //jos käyttäjä on kirjautunut niin mennään divarikomentoihin, jossa on haku jne.
      while(divarinPyorinta == true) {
        if(kirjautunut == false)
          kirjautumisKomennot(con);
        else if(kirjautunut == true && yllapitaja == true && yllapitaja1 == false) 
          yllapitajaKomennot(con);
        else if(kirjautunut == true && yllapitaja == false && yllapitaja1 == false)
          divariKomennot(con);
        else if (kirjautunut == true && yllapitaja1 == true && yllapitaja == false)
          yllapitajaYksittainenKomennot(con); 
            
      }
    }
    catch (SQLException poikkeus) {
      System.out.println("Tapahtui seuraava virhe: " + poikkeus.getMessage());
    }
    if (con != null) try {
      con.close();
    } 
    catch(SQLException poikkeus) {
      System.out.println("Yhteyden sulkeminen tietokantaan ei onnistunut. Lopetetaan ohjelman suoritus.");
      return;
    }
  }
      
  //Kun käyttäjä ei ole vielä kirjautunut sisään. Komennot kirjautumiseen ja rekisteröitymiseen.
  public static void kirjautumisKomennot(Connection con) {
    boolean pyoriiko = true;
    while(pyoriiko == true) {
      System.out.println("");
      System.out.println("<---------------------------------------->");
      System.out.println("<-------Tervetuloa Keskusdivariin!------->");
      System.out.println("--> Vanha käyttäjä kirjaudu sisään komennolla: kirjaudu");
      System.out.println("--> D1 divariin kirjaudu komennolla: kirjaudud1");
      System.out.println("--> Uusi käyttäjä Tee tunnus komennolla: uusi");
      System.out.println("--> Järjestelmän voi sulkea komennolla: sulje");
      System.out.println("<---------------------------------------->");
      System.out.print("kirjaudu/kirjaudud1/uusi/sulje: ");
      String komento = sc.nextLine();
      
      if(komento.equals("kirjaudu"))
        pyoriiko = kirjautuminen(con);
      else if (komento.equals("kirjaudud1"))
        pyoriiko = kirjautuminenYksittainenDivari(con);
      else if(komento.equals("uusi"))
        rekisteroityminen(con);
      else if(komento.equals("sulje")) {
        pyoriiko = false;
        divarinPyorinta = false;
             
      }
      else
        System.out.println("Virheellinen komento");
    }
  }
    
  //kun käyttäjä on kirjautunut sisään. Komennot hakuun jne.
  public static void divariKomennot(Connection con) {
    boolean pyoriiko = true;
    while(pyoriiko == true) {
      System.out.println("");
      System.out.println("<---------------------------------------->");
      System.out.println("<--Tervetuloa käyttämään keskusdivaria!-->");
      System.out.println("--> Hae tuotetta komennolla: haku");
      System.out.println("--> Selaa tuotteita komennolla: selaa");
      System.out.println("--> Katso ostoskoria komennolla: ostoskori");
      System.out.println("--> Katso vanhoja tilauksiasi komennolla: tilaukset");
      System.out.println("--> Järjestelmän voi sulkea komennolla: sulje");
      System.out.println("<---------------------------------------->");
      System.out.print("haku/selaa/ostoskori/tilaukset/sulje: ");
      String komento = sc.nextLine();
      
      if(komento.equals("haku"))
        hakuKomennot(con);
      else if(komento.equals("selaa"))
        teosSelaus(con);
      else if(komento.equals("ostoskori"))
        ostoskori(con);
      else if(komento.equals("tilaukset"))
        tilaukset(con);
      else if(komento.equals("sulje")) {
        pyoriiko = false;
        divarinPyorinta = false;
        kirjautunut = false;
      }
      else
        System.out.println("Virheellinen komento");
    }
  }
    
  //Ylläpitäjän kommenot
  public static void yllapitajaKomennot(Connection con) {
    boolean pyoriiko = true;
    while(pyoriiko == true) {
      System.out.println("<---------------------------------------->");
      System.out.println("<---------------Ylläpitäjä--------------->");
      System.out.println("--> Lisää uusia teoksia: lisääteos");
      System.out.println("--> Selaa teoksia ja lisää niteitä: lisäänide");
      System.out.println("--> Kysely R1 komennolla: r1");
      System.out.println("--> Kysely R2 komennolla: r2");
      System.out.println("--> Kysely R3 komennolla: r3");
      System.out.println("--> Järjestelmän voi sulkea komennolla: sulje");
      System.out.println("<---------------------------------------->");
      System.out.print("lisääteos/lisäänide/r1/r2/r3/sulje: ");
      String komento = sc.nextLine();
      
      if(komento.equals("lisäänide"))
        lisaaNideDivarilleKeskusdivari(con);
      else if(komento.equals("lisääteos"))
        lisaaTeosDivarilleKeskusdivari(con);
      else if(komento.equals("r1"))
        R1(con);
      else if(komento.equals("r2"))
        ryhmitteleNiteetR2(con);
      else if (komento.equals("r3"))
        vuodenVanhatOstoksetR3(con);
      else if(komento.equals("sulje")){ 
        pyoriiko = false;
        divarinPyorinta = false;
        kirjautunut = false;
      }
      else{
        System.out.println("Virheellinen komento");
      }
         
    }
  }
        
  //Rekisteröityminen
  public static void rekisteroityminen(Connection con) {
    System.out.println("<---------------------------------------->");
    System.out.println("<-----------Rekisteröityminen!----------->");
    System.out.println("<---------------------------------------->");
    boolean pyoriiko = true;
          
    while(pyoriiko == true) {
      try {
        boolean okei = false;
        String tunnus = "";
        String salasana = "";
        String nimi = "";
        String osoite = "";
        int puhelin = 0;
        String sposti = "";
            
        while(okei == false) {
          System.out.println("--> SYÖTÄ KÄYTTÄJÄTUNNUS. Huom. tällä kirjaudut sisään!");
          System.out.println("OHJE: tunnus EI voi olla tyhjä TAI 'taakse'-sana");
          System.out.print("Syötä käyttäjätunnus: ");
          tunnus = sc.nextLine();
          if(tunnus.equals("")) {
            System.out.println("VIRHE tunnus ei voi olla tyhjä");
          }
          else if(tunnus.equals("taakse")) {
            System.out.println("VIRHE tunnus ei voi olla 'taakse'-sana");
          }
          else
            okei = true;
        }
        okei = false;
        while(okei == false) {
          System.out.println("--> SYÖTÄ SALASANA.");
          System.out.println("OHJE: salasana EI voi olla tyhjä");
          System.out.print("Syötä salasana: ");
          salasana = sc.nextLine();
          if(salasana.equals("")) {
            System.out.println("VIRHE salasana ei voi olla tyhjä");
          }
          else
            okei = true;
        }
        okei = false;
        while(okei == false) {
          System.out.println("--> SYÖTÄ NIMESI.");
          System.out.println("OHJE: nimi EI voi olla tyhjä");
          System.out.print("Syötä nimi: ");
          nimi = sc.nextLine();
          if(nimi.equals("")) {
            System.out.println("VIRHE nimi ei voi olla tyhjä");
          }
          else
            okei = true;
        }
        okei = false;
        while(okei == false) {
          System.out.println("--> SYÖTÄ OSOITE.");
          System.out.println("OHJE: osoite EI voi olla tyhjä");
          System.out.print("Syötä osoite: ");
          osoite = sc.nextLine();
          if(osoite.equals("")) {
            System.out.println("VIRHE osoite ei voi olla tyhjä");
          }
          else
            okei = true;
        }
        okei = false;
        while(okei == false) {
          System.out.println("--> SYÖTÄ PUHELIN.");
          System.out.println("OHJE: puhelin EI voi olla tyhjä");
          System.out.print("Syötä puhelin: ");
          puhelin = sc.nextInt();
          if(puhelin > 1)
            okei = true;
        }
        sc.nextLine();
        okei = false;
        while(okei == false) {
          System.out.println("--> SYÖTÄ SÄHKÖPOSTI.");
          System.out.println("OHJE: sähköposti EI voi olla tyhjä. Voit halutessasi antaa @-merkin muodossa (at).");
          System.out.print("Syötä sähköposti: ");
          sposti = sc.nextLine();
          if(sposti.equals("")) {
            System.out.println("VIRHE sähköposti ei voi olla tyhjä");
          }
          else if(sposti.contains("@") || sposti.contains("(at)") && sposti.contains("."))
            okei = true;
          else
            System.out.println("Varmista että sähköposti on oikeassa muodossa!");
        }
            
        PreparedStatement lisaaAsiakas = con.prepareStatement("INSERT INTO keskusdivari.Asiakas VALUES (?, ?, ?, ?, ?, ?)");
      
        lisaaAsiakas.clearParameters();
        lisaaAsiakas.setString(1, tunnus);
        lisaaAsiakas.setString(2, salasana);
        lisaaAsiakas.setString(3, nimi);
        lisaaAsiakas.setString(4, osoite);
        lisaaAsiakas.setInt(5, puhelin);
        lisaaAsiakas.setString(6, sposti);
      
        lisaaAsiakas.executeUpdate();
        lisaaAsiakas.close();
        pyoriiko = false;
      }
      catch (SQLException e) {
        System.out.println("Tapahtui seuraava virhe: " + e.getMessage());
      }
    }
  }
      
  //kysytään käyttäjän tunnus ja salasana, verrataan niitä tietokantaan. 
  public static boolean kirjautuminen(Connection con) {
          
    //Palautetaan kirjautumiskomennoille, jotta sen luuppi loppuu.
    boolean kirjautuminenKaynnissa = true;
      
    boolean pyoriiko = true;
    System.out.println("");
    System.out.println("<----------Sisäänkirjautuminen!---------->");
    System.out.println("--> Pääset halutessasi taaksepäin komennolla: taakse");
    System.out.println("Syötä tunnuksesi tai komento: taakse");
    System.out.println("<---------------------------------------->");
    while(pyoriiko == true) {
            
      System.out.print("Syötä tunnuksesi/taakse-komento: ");
      String tunnus = sc.nextLine();
      
      if(tunnus.equals("taakse")) {
        pyoriiko = false;
      }
      else {
        System.out.print("Salasana: ");
        String salasana = sc.nextLine();
      
        try {
          PreparedStatement kirjautuminen = con.prepareStatement("SELECT tunnus, salasana FROM keskusdivari.Asiakas WHERE tunnus = ? AND salasana = ?");
      
          kirjautuminen.clearParameters();
          kirjautuminen.setString(1, tunnus);
          kirjautuminen.setString(2, salasana);
      
          ResultSet rs = kirjautuminen.executeQuery();
      
          String tunnusCheck = "";
          String salasanaCheck = "";
      
          while (rs.next()) {
            tunnusCheck = rs.getString("tunnus");
            salasanaCheck = rs.getString("salasana");
          }
      
          if(tunnus.equals(tunnusCheck) && salasana.equals(salasanaCheck)) {
            System.out.println("Tunnus ja salasana oikein!");
            kirjautunutAsiakasTunnus = tunnus;
            kirjautunut = true;
            pyoriiko = false;
            kirjautuminenKaynnissa = false;
            //JOS YLLÄPITÄJÄN TUNNUS
            if(tunnus.equals("yllapitaja")) {
              yllapitaja = true;
            }
          }
          else {
            System.out.println("<-----VIRHE tunnuksessa tai salasanassa!----->");
            System.out.println("Kokeile uudestaan tai palaa takaisin komennolla: taakse");
          }
      
        kirjautuminen.close();
        }
        catch (SQLException e) {
            System.out.println("Tapahtui seuraava virhe: " + e.getMessage());
        }
      }
    }
    //True jos käyttäjä ei onnistunut kirjautumaan sisään. False jos tunnukset oli oikein.
    return kirjautuminenKaynnissa;
  }
   
  public static boolean kirjautuminenYksittainenDivari(Connection con) {
          
    //Palautetaan kirjautumiskomennoille, jotta sen luuppi loppuu.
    boolean kirjautuminenKaynnissa = true;
      
    boolean pyoriiko = true;
    System.out.println("<---------------------------------------->");
    System.out.println("<-------Ylläpitäjän kirjautuminen-------->");
    System.out.println("--> Pääset halutessasi taaksepäin komennolla: taakse");
    System.out.println("Syötä tunnuksesi tai komento: taakse");
    System.out.println("<---------------------------------------->");
    while(pyoriiko == true) {
            
      System.out.print("Syötä tunnuksesi/taakse-komento: ");
      String tunnus = sc.nextLine();
      
      if(tunnus.equals("taakse")) {
        pyoriiko = false;
      }
      else {
        System.out.print("Salasana: ");
        String salasana = sc.nextLine();
      
        try {
          PreparedStatement kirjautuminen = con.prepareStatement("SELECT tyontekija_tunnus, salasana FROM yksdivari.Tyontekija "
                  + "WHERE tyontekija_tunnus = ? AND salasana = ?");
      
          kirjautuminen.clearParameters();
          kirjautuminen.setString(1, tunnus);
          kirjautuminen.setString(2, salasana);
      
          ResultSet rs = kirjautuminen.executeQuery();
      
          String tunnusCheck = "";
          String salasanaCheck = "";
      
          while (rs.next()) {
            tunnusCheck = rs.getString("tyontekija_tunnus");
            salasanaCheck = rs.getString("salasana");
          }
      
          if(tunnus.equals(tunnusCheck) && salasana.equals(salasanaCheck)) {
            System.out.println("Tunnus ja salasana oikein!");
            kirjautunut = true;
            pyoriiko = false;
            kirjautuminenKaynnissa = false;
            //JOS YLLÄPITÄJÄN TUNNUS
            if(tunnus.equals("yllapitaja")) {
              yllapitaja1 = true;
            }
          }
          else {
            System.out.println("<---VIRHE tunnuksessa tai salasanassa!--->");
            System.out.println("Kokeile uudestaan tai palaa takaisin komennolla: taakse");
          }
       
        kirjautuminen.close();
        }
        catch (SQLException e) {
            System.out.println("Tapahtui seuraava virhe: " + e.getMessage());
        }
      }
    }
    //True jos käyttäjä ei onnistunut kirjautumaan sisään. False jos tunnukset oli oikein.
    return kirjautuminenKaynnissa;
  }
    
 //Ylläpitäjän kommenot yksittäiseen divariin
  public static void yllapitajaYksittainenKomennot(Connection con) {
    boolean pyoriiko = true;
    while(pyoriiko == true) {
      System.out.println("<---------------------------------------->");
      System.out.println("<-----Ylläpitäjä yksittäinen divari------>");
      System.out.println("--> Lisää uusia teoksia komennolla: lisää");
      System.out.println("--> Järjestelmän voi sulkea komennolla: sulje");
      System.out.println("<---------------------------------------->");
      System.out.print("lisää/sulje: ");
      String komento = sc.nextLine();
      
      if(komento.equals("lisää")){
        lisaaUusiTeosYksittaiseen(con);
      }else if(komento.equals("sulje")){ 
        pyoriiko = false;
        divarinPyorinta = false;
        kirjautunut = false;
      }
      else{
        System.out.println("Virheellinen komento");
        }
       
    }
  }
    
  //lisätään nide divarille teoksesta, joka löytyy tietokannasta
  public static void lisaaNideDivarilleKeskusdivari(Connection con) {
      boolean pyoriiko = true;
      boolean okei = false;
      int nideTunnus = 0;  
      int teosTunnus = 0;
      String divariTunnus = "";
      double hinta = 0;
      String kunto = "";
      int saldo = 1;
      int uusiSaldo = 0;
      
    System.out.println("<---------------------------------------->");
    System.out.println("--> Valitse teos komennolla valitse x, jossa x on teoksen numero: valitse x");      
    System.out.println("--> Palaa taakse komennolla: taakse");
    try{ 
    while(pyoriiko == true) {
        teosSelausTeoksenLisäys(con);
      System.out.println(" ");    
      System.out.print("valitse x/taakse: ");
      String komento = sc.nextLine();
   
      if(komento.equals("taakse")) {
        pyoriiko = false;
      }
      else {
      
        String[] osat = komento.split(" ");
        if(osat[0].equals("valitse")) {
          try {
              
            teosTunnus = Integer.parseInt(osat[1]);
             
            Statement stmt = con.createStatement();
            ResultSet tuotteet = stmt.executeQuery("SELECT * FROM keskusdivari.divari");
  
            ResultSetMetaData rss = tuotteet.getMetaData();
  
            int numberOfColumns = rss.getColumnCount();
            System.out.println("<-----Keskusdivarissa olevat divarit----->");
            System.out.println("");
            while (tuotteet.next()) {
                for (int i = 1; i <= numberOfColumns; i++) {
                    String s = tuotteet.getString(i);
                    if(tuotteet.wasNull())
                        System.out.println("NULL");
                    else
                        System.out.print(s + " ");
                    }
                    System.out.println();
                }
            System.out.println(" ");
            while(okei == false) {
              System.out.println("--> SYÖTÄ DIVARIN TUNNUS, JOLLE NIDE LISÄTÄÄN");
              System.out.println("OHJE: tunnus ei voi olla tyhjä (Gallein Galle = D2)");
              System.out.print("Syötä divarin tunnus: ");
              divariTunnus = sc.nextLine();
              if(divariTunnus.equals("")) {
                System.out.println("VIRHE tunnus oli tyhjä!");
              }
              else
                okei = true;
            }
            okei = false;
            while(okei == false) {
              System.out.println("--> SYÖTÄ NITEEN HINTA");
              System.out.println("OHJE: Hinta >= 0.1");
              System.out.print("Syötä hinta: ");
              hinta = sc.nextDouble();
              if(hinta < 0.1) {
                System.out.println("VIRHE hinta ei voi olla < 0.1");
              }
              else
                okei = true;
            }
            sc.nextLine();
            okei = false;
            while(okei == false) {
              System.out.println("--> SYÖTÄ NITEEN KUNTO");
              System.out.println("OHJE: Hyva, Huono, Normaali");
              System.out.print("Syötä kunto: ");
              kunto = sc.nextLine();
              if(kunto.equals("Hyva") || kunto.equals("Huono") || kunto.equals("Normaali")) {
                okei = true;
              }
              else
                System.out.println("VIRHE kunto oli tyhjä tai väärän arvoinen");
            }
            String sql = "SELECT * FROM keskusdivari.nide WHERE teos_tunnus = '" + teosTunnus + "'"
                    + " AND divari_id = '" + divariTunnus + "'" +  " AND kunto = '" + kunto + "'";
                         
            Statement check = con.createStatement();
            ResultSet checkSet = check.executeQuery(sql);
            //jos löytyy vastaava, kasvatetaan vain saldoa
        if (checkSet.next()){
            int tmp = 0;
            String setSaldo = "UPDATE keskusdivari.nide SET saldo = ? "
                 + "WHERE teos_tunnus = '" + teosTunnus + "'"
                + " AND divari_id = '" + divariTunnus + "'" +  " AND kunto = '" + kunto + "'";
            PreparedStatement saldoKasvatus = con.prepareStatement(setSaldo);
                  
            tmp = haeNiteenTunnus(con, teosTunnus, divariTunnus, kunto);
            uusiSaldo = haeSaldo(con, tmp);
            uusiSaldo++;
                  
            saldoKasvatus.setInt(1, uusiSaldo);
                  
            saldoKasvatus.executeUpdate();
            saldoKasvatus.close();
            pyoriiko = false;   
            System.out.println("Niteen saldoa kasvatettiin!");  
            //jos ei löytynyt tietokannasta, luodaan täysin uusi nide   
            }else{
                nideTunnus = seuraavaVapaaTunnus(con, "SELECT nide_tunnus FROM keskusdivari.nide ORDER BY nide.nide_tunnus ASC", 
                "nide_tunnus");
                PreparedStatement nideLisays = con.prepareStatement("INSERT INTO keskusdivari.Nide VALUES (?, ?, ?, ?, ?, ?)");
 
                nideLisays.clearParameters();
                nideLisays.setInt(1, nideTunnus);
                nideLisays.setInt(2, teosTunnus);
                nideLisays.setString(3, divariTunnus);
                nideLisays.setDouble(4, hinta);
                nideLisays.setString(5, kunto);
                nideLisays.setInt(6, saldo);
                  
                nideLisays.executeUpdate(); 
                nideLisays.close();
                  
                pyoriiko = false;
                System.out.println("Uusi nide lisätty!");
                 
               }
                         
              
             } 
              catch (SQLException e) {
                System.out.println("Tapahtui seuraava virhe: " + e.getMessage());
             }
              catch (Exception e) {
                System.out.println("Tapahtui seuraava virhe: " + e.getMessage());
              }
            }
            else
              System.out.println("Virheellinen komento!");
            }
          }
  
        }catch (Exception e){
            System.out.println("Tapahtui seuraava virhe: " + e.getMessage());  
        }
    }
  
  //hakee niteen tunnuksen, jonka saldoa ollaan kasvattamssa
  public static int haeNiteenTunnus(Connection con, int teos, String divari, String kunto){    
    int nide = 0;       
    try {
      PreparedStatement nideHaku = con.prepareStatement("SELECT nide_tunnus FROM keskusdivari.nide WHERE teos_tunnus = ? "
      + " AND divari_id = ?" + " AND kunto = ?");
      nideHaku.clearParameters();
      nideHaku.setInt(1, teos);
      nideHaku.setString(2, divari);
      nideHaku.setString(3, kunto);
      ResultSet rs = nideHaku.executeQuery();
                
      while (rs.next()) {
        nide = rs.getInt("nide_tunnus");
      }
         
      nideHaku.close();
    }
    catch (SQLException e) {
      System.out.println("Tapahtui seuraava virhe: " + e.getMessage());
    }
    return nide;   
  }
       
  public static void teosSelausTeoksenLisäys(Connection con) {
    System.out.println("<---------------------------------------->");
    System.out.println("<--------Divarissa oleva teokset--------->");
    System.out.println("<---------------------------------------->");
    
    try {
      Statement stmt = con.createStatement();
          
      ResultSet rs = stmt.executeQuery("SELECT Teos_tunnus, nimi, tyyppi, luokka, tekija, julkaisuvuosi FROM keskusdivari.Teos");
          
      ResultSetMetaData rsmd = rs.getMetaData();
          
      int col = rsmd.getColumnCount();
    
      for(int i = 1; i <= 6; i++) {
        if(i == 1)
          System.out.print("NRO ");
        else if(i == 2) {
          System.out.print("NIMI");
          for(int j = 4; j <= 50; j++)
                System.out.print(" ");
        }
        else if(i == 3) {
          System.out.print("TYYPPI");
          for(int j = 5; j < 20; j++)
                System.out.print(" ");
        }
        else if(i == 4) {
          System.out.print("LUOKKA");
          for(int j = 5; j < 20; j++)
                System.out.print(" ");
        }
        else if(i == 5) {
          System.out.print("TEKIJÄ");
          for(int j = 6; j <= 30; j++)
                System.out.print(" ");
        }
        else if(i == 6) 
          System.out.print("VUOSI ");
    
        System.out.print("| ");
    
      }
          
      System.out.println("");
    
      while (rs.next()) {
        for (int i = 1; i <= col; i++) {
          String s = rs.getString(i);
          int pituus = s.length();
          if(rs.wasNull())
            System.out.println("NULL");
          else {
            System.out.print(s);
            //NRO
            if(i == 1) {
              for(int j = pituus; j <= 3; j++)
                System.out.print(" ");
            }
            //NIMI
            else if(i == 2) {
              for(int j = pituus; j <= 50; j++)
                System.out.print(" ");
            }
            //TYYPPI
            else if(i == 3) {
              for(int j = pituus; j <= 20; j++)
                System.out.print(" ");
            }
            //LUOKKA
            else if(i == 4) {
              for(int j = pituus; j <= 20; j++)
                System.out.print(" ");
            }
            //TEKIJÄ
            else if(i == 5) {
              for(int j = pituus; j <= 30; j++)
                System.out.print(" ");
            }
            //JULKAISUVUOSI
            else if(i == 6) {
              for(int j = pituus; j <= 5; j++)
                System.out.print(" ");
            }
            System.out.print("| ");
          } 
        }
        System.out.println("");
      }
          
      stmt.close();
    }
    catch (SQLException e) {
        System.out.println("Tapahtui seuraava virhe: " + e.getMessage());
    }
  }
    
  //yksittäiseen divariin uuden teoksen lisääminen
  public static void lisaaUusiTeosYksittaiseen(Connection con){
    System.out.println("<---------------------------------------->");
    System.out.println("<------Teoksen lisääminen divariin------->");
    boolean pyoriiko = true;
             
    while(pyoriiko == true) {
      try {
           
        boolean okei = false;
            
        int tunnus = 0;
        String ISBN = "";
        String nimi = "";
        String tekija = "";
        String tyyppi = "";
        String luokka = "";
        int julkvuosi = 0;
        double ostohinta = 0;
        double paino = 0;
 
        while(okei == false) {
          System.out.println("--> SYÖTÄ UUDEN TEOKSEN ISBN.");
          System.out.println("OHJE: ISBN voi olla tyhjä.");
          System.out.print("Syötä ISBN: ");
          ISBN = sc.nextLine();
          okei = true;
        }
        okei = false;
        while(okei == false) {
          System.out.println("--> SYÖTÄ UUDEN TEOKSEN NIMI.");
          System.out.println("OHJE: Nimi ei voi olla tyhjä.");
          System.out.print("Syötä Nimi: ");
          nimi = sc.nextLine();
          if(nimi.equals("")) {
            System.out.println("VIRHE nimi ei voi olla tyhjä");
          }
          else
            okei = true;
        }
        okei = false;
        while(okei == false) {
          System.out.println("--> SYÖTÄ UUDEN TEOKSEN TEKIJÄ.");
          System.out.println("OHJE: Tekijä ei voi olla tyhjä");
          System.out.print("Syötä tekijän nimi: ");
          tekija = sc.nextLine();
          if(tekija.equals("")) {
            System.out.println("VIRHE tekija ei voi olla tyhjä");
          }
          else
            okei = true;
        }
        okei = false;
        while(okei == false) {
          System.out.println("--> SYÖTÄ UUDEN TEOKSEN TYYPPI.");
          System.out.println("OHJE: Tyyppi ei voi olla tyhjä (esim. romaani, sarjakuva, tietokirja).");
          System.out.print("Syötä tyyppi: ");
          tyyppi = sc.nextLine();
          if(tyyppi.equals("")) {
            System.out.println("VIRHE tyyppi ei voi olla tyhjä");
          }
          else
            okei = true;
        }
        okei = false;
        while(okei == false) {
          System.out.println("--> SYÖTÄ UUDEN TEOKSEN LUOKKA.");
          System.out.println("OHJE: Luokka ei voi olla tyhjä (esim. romantiikka, historia, dekkari, huumori, opas).");
          System.out.print("Syötä luokka: ");
          luokka = sc.nextLine();
          if(luokka.equals("")) {
            System.out.println("VIRHE luokka ei voi olla tyhjä");
          }
          else
            okei = true;
        }
        okei = false;
        while(okei == false) {
          System.out.println("--> SYÖTÄ UUDEN TEOKSEN JULKAISUVUOSI.");
          System.out.println("OHJE: julkaisuvuosi vähintään > 1900.");
          System.out.print("Syötä julkaisuvuosi: ");
          julkvuosi = sc.nextInt();
          if(julkvuosi < 1900) {
            System.out.println("VIRHE julkaisuvuosi oltava vähintään yli 1900");
          }
          else
            okei = true;
        }
        okei = false;
        while(okei == false) {
          System.out.println("--> SYÖTÄ UUDEN TEOKSEN SISÄÄNOSTOHINTA.");
          System.out.println("OHJE: Sisäänostohinta vähintään > 0.");
          System.out.print("Syötä sisäänostohinta: ");
          ostohinta = sc.nextDouble();
          if(ostohinta < 0) {
            System.out.println("VIRHE sisäänostohinnan oltava vähintään yli 0");
          }
          else
            okei = true;
        }
            
        okei = false;
        while(okei == false) {
          System.out.println("--> SYÖTÄ UUDEN TEOKSEN PAINO.");
          System.out.println("OHJE: paino vähintään > 0.");
          System.out.print("Syötä paino: ");
          paino = sc.nextDouble();
          if(paino < 0) {
            System.out.println("VIRHE painon oltava vähintään yli 0");
          }
          else
            okei = true;
        }
        tunnus = seuraavaVapaaTunnus(con, "SELECT teos_tunnus FROM yksdivari.teos ORDER BY teos.teos_tunnus", "teos_tunnus");
        PreparedStatement lisaaUusiTeos = con.prepareStatement("INSERT INTO yksdivari.Teos VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)");
     
        lisaaUusiTeos.clearParameters();
        lisaaUusiTeos.setInt(1, tunnus);
        lisaaUusiTeos.setString(2, ISBN);
        lisaaUusiTeos.setString(3, nimi);
        lisaaUusiTeos.setString(4, tekija);
        lisaaUusiTeos.setString(5, tyyppi);
        lisaaUusiTeos.setString(6, luokka);
        lisaaUusiTeos.setInt(7, julkvuosi);
        lisaaUusiTeos.setDouble(8, ostohinta);
        lisaaUusiTeos.setDouble(9, paino);
            
        lisaaUusiTeos.executeUpdate();
        lisaaUusiTeos.close();
        pyoriiko = false;
            
        System.out.println("Uusi teos lisätty divarille!");
        sc.nextLine();
      }
      catch (SQLException e) {
        System.out.println("Tapahtui seuraava virhe: " + e.getMessage());
      }
    }
  }
  
  public static void R1(Connection con) {
    System.out.println("<---------------------------------------->");
    System.out.println("<---------------R1 kysely---------------->");
    System.out.println("<---------------------------------------->");
 
    System.out.println("Hakutoimintojen demonstrointi. Virallisesti hakutoiminnot löytyvät asiakaspuolelta komennolla: haku");
 
    System.out.println("Haku teoksen nimen mukaan. Hakusana: Tuulen");
 
    teosTulostus(con, "SELECT DISTINCT teos.teos_tunnus, nimi, tyyppi, luokka, tekija, julkaisuvuosi FROM keskusdivari.teos, "
    + "keskusdivari.nide WHERE teos.teos_tunnus = nide.teos_tunnus AND nimi like ? AND saldo > 0 ORDER BY teos.teos_tunnus ASC", "Tuulen");
 
    System.out.println("");
    System.out.println("Haku tekijän mukaan. Hakusana: a");
 
    teosTulostus(con, "SELECT DISTINCT teos.teos_tunnus, nimi, tyyppi, luokka, tekija, julkaisuvuosi FROM keskusdivari.teos, "
    + "keskusdivari.nide WHERE teos.teos_tunnus = nide.teos_tunnus AND tekija like ? AND saldo > 0 ORDER BY teos.teos_tunnus ASC", "a");
 
    System.out.println("");
    System.out.println("Haku tyypin mukaan. Hakusana: romaani");
 
    teosTulostus(con, "SELECT DISTINCT teos.teos_tunnus, nimi, tyyppi, luokka, tekija, julkaisuvuosi FROM keskusdivari.teos, "
    + "keskusdivari.nide WHERE teos.teos_tunnus = nide.teos_tunnus AND tyyppi like ? AND saldo > 0 ORDER BY teos.teos_tunnus ASC", "romaani");
 
    System.out.println("");
    System.out.println("Haku luokan mukaan. Hakusana: romantiikka");
 
    teosTulostus(con, "SELECT DISTINCT teos.teos_tunnus, nimi, tyyppi, luokka, tekija, julkaisuvuosi FROM keskusdivari.teos, "
    + "keskusdivari.nide WHERE teos.teos_tunnus = nide.teos_tunnus AND luokka like ? AND saldo > 0 ORDER BY teos.teos_tunnus ASC", "romantiikka");
  }
 
  //lisää uuden teoksen keskusdivarissa (roolina D2 ylläpitäjä)
    public static void lisaaTeosDivarilleKeskusdivari(Connection con) {
        System.out.println("<---------------------------------------->");
        System.out.println("<---Teoksen lisääminen keskusdivariin---->");
        boolean pyoriiko = true;
                 
        while(pyoriiko == true) {
       try {
               
        boolean okei = false;
            
        int tunnus = 0;
        String ISBN = "";
        String nimi = "";
        String tekija = "";
        String tyyppi = "";
        String luokka = "";
        int julkvuosi = 0;
        double ostohinta = 0;
        double paino = 0;
      
        while(okei == false) {
          System.out.println("--> SYÖTÄ UUDEN TEOKSEN ISBN.");
          System.out.println("OHJE: ISBN voi olla tyhjä.");
          System.out.print("Syötä ISBN: ");
          ISBN = sc.nextLine();
          okei = true;
        }
        okei = false;
        while(okei == false) {
          System.out.println("--> SYÖTÄ UUDEN TEOKSEN NIMI.");
          System.out.println("OHJE: Nimi ei voi olla tyhjä.");
          System.out.print("Syötä Nimi: ");
          nimi = sc.nextLine();
          if(nimi.equals("")) {
            System.out.println("VIRHE nimi ei voi olla tyhjä");
          }
          else
            okei = true;
        }
        okei = false;
        while(okei == false) {
          System.out.println("--> SYÖTÄ UUDEN TEOKSEN TEKIJÄ.");
          System.out.println("OHJE: Tekijä ei voi olla tyhjä");
          System.out.print("Syötä tekijän nimi: ");
          tekija = sc.nextLine();
          if(tekija.equals("")) {
            System.out.println("VIRHE tekija ei voi olla tyhjä");
          }
          else
            okei = true;
        }
        okei = false;
        while(okei == false) {
          System.out.println("--> SYÖTÄ UUDEN TEOKSEN TYYPPI.");
          System.out.println("OHJE: Tyyppi ei voi olla tyhjä (esim. romaani, sarjakuva, tietokirja).");
          System.out.print("Syötä tyyppi: ");
          tyyppi = sc.nextLine();
          if(tyyppi.equals("")) {
            System.out.println("VIRHE tyyppi ei voi olla tyhjä");
          }
          else
            okei = true;
        }
        okei = false;
        while(okei == false) {
          System.out.println("--> SYÖTÄ UUDEN TEOKSEN LUOKKA.");
          System.out.println("OHJE: Luokka ei voi olla tyhjä (esim. romantiikka, dekkari, huumori, historia).");
          System.out.print("Syötä luokka: ");
          luokka = sc.nextLine();
          if(luokka.equals("")) {
            System.out.println("VIRHE luokka ei voi olla tyhjä");
          }
          else
            okei = true;
        }
        okei = false;
        while(okei == false) {
          System.out.println("--> SYÖTÄ UUDEN TEOKSEN JULKAISUVUOSI.");
          System.out.println("OHJE: julkaisuvuosi vähintään > 1900.");
          System.out.print("Syötä julkaisuvuosi: ");
          julkvuosi = sc.nextInt();
          if(julkvuosi < 1900) {
            System.out.println("VIRHE julkaisuvuosi oltava vähintään yli 1900");
          }
          else
            okei = true;
        }
        okei = false;
        while(okei == false) {
          System.out.println("--> SYÖTÄ UUDEN TEOKSEN SISÄÄNOSTOHINTA.");
          System.out.println("OHJE: Sisäänostohinta vähintään > 0.");
          System.out.print("Syötä sisäänostohinta: ");
          ostohinta = sc.nextDouble();
          if(ostohinta < 0) {
            System.out.println("VIRHE sisäänostohinnan oltava vähintään yli 0");
          }
          else
            okei = true;
        }
            
        okei = false;
        while(okei == false) {
          System.out.println("--> SYÖTÄ UUDEN TEOKSEN PAINO.");
          System.out.println("OHJE: paino vähintään > 0.");
          System.out.print("Syötä paino: ");
          paino = sc.nextDouble();
          if(paino < 0) {
            System.out.println("VIRHE painon oltava vähintään yli 0");
          }
          else
            okei = true;
        }
        tunnus =  seuraavaVapaaTunnus(con, "SELECT teos_tunnus FROM keskusdivari.teos ORDER BY teos.teos_tunnus ASC", "teos_tunnus");
        PreparedStatement lisaaUusiTeos = con.prepareStatement("INSERT INTO keskusdivari.Teos VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)");
        
        lisaaUusiTeos.clearParameters();
        lisaaUusiTeos.setInt(1, tunnus);
        lisaaUusiTeos.setString(2, ISBN);
        lisaaUusiTeos.setString(3, nimi);
        lisaaUusiTeos.setString(4, tekija);
        lisaaUusiTeos.setString(5, tyyppi);
        lisaaUusiTeos.setString(6, luokka);
        lisaaUusiTeos.setInt(7, julkvuosi);
        lisaaUusiTeos.setDouble(8, ostohinta);
        lisaaUusiTeos.setDouble(9, paino);
            
        lisaaUusiTeos.executeUpdate();
        lisaaUusiTeos.close();
        pyoriiko = false;
            
        System.out.println("Uusi teos lisätty divarille!");
        sc.nextLine();
          }
          catch (SQLException e) {
              System.out.println("Tapahtui seuraava virhe: " + e.getMessage());
          }
      }
    }
  
  //R2 kysely
  public static void ryhmitteleNiteetR2(Connection con){
    System.out.println("<---------------------------------------->");
    System.out.println("<---------------R2 kysely---------------->");
    System.out.println("<---------------------------------------->");
   
    try {
      Statement stmt = con.createStatement();
         
      ResultSet rs = stmt.executeQuery("SELECT teos.luokka, cast(round(AVG(CAST(nide.hinta AS numeric(12,2))),2) as decimal(12,2)) keskihinta_luokalle, SUM(nide.hinta) AS kokonaismyyntihinta_luokalle"
      + " FROM keskusdivari.Teos INNER JOIN keskusdivari.nide ON teos.teos_tunnus = nide.teos_tunnus WHERE saldo > 0"
      + " GROUP BY teos.luokka"
      + " ORDER BY teos.luokka ASC");
         
      ResultSetMetaData rsmd = rs.getMetaData();
         
      int col = rsmd.getColumnCount();
   
      for(int i = 1; i <= 3; i++) {
        if(i == 1){
          System.out.print("Luokka");
          for(int j = 10; j <= 15; j++)
            System.out.print(" ");
        }   
        else if(i == 2) {
          System.out.print("Keskihinta");
          for(int j = 10; j <= 15; j++)
            System.out.print(" ");
        }
        else if(i == 3) {
          System.out.print("Kokonaishinta");
          for(int j = 13; j <= 15; j++)
            System.out.print(" ");
        }
        System.out.print("| ");
      }
         
      System.out.println("");
         
      while (rs.next()) {
        for (int i = 1; i <= col; i++) {
          String s = rs.getString(i);
          if(s == null) {
            System.out.print("NULL");
            for(int j = 3; j <= 9; j++)
                System.out.print(" ");
          }
          else {
            System.out.print(s);
            int pituus = s.length();
            //LUOKKA
            if(i == 1) {
              for(int j = pituus; j <= 11; j++)
                System.out.print(" ");
            }
            //KESKIHINTA
            else if(i == 2) {
              for(int j = pituus; j <= 15; j++)
                System.out.print(" ");
            }
            //KOKONAISHINTA
            else if(i == 3) {
              for(int j = pituus; j <= 15; j++)
                System.out.print(" ");
            }
           
          } 
          System.out.print("| ");
        }
        System.out.println("");
      }
         
      stmt.close();
    }
    catch(SQLException e) {
      System.out.println("Virhe " + e);
    }
  }
  //R3 reporttia varten. Tulostaa asiakkaan yhden vuoden takaiset tilaukset
  public static void vuodenVanhatOstoksetR3(Connection con){
      System.out.println("<---------------------------------------->");
      System.out.println("<---------------R3 kysely---------------->");
      System.out.println("<---------------------------------------->");
   
    try {
      Statement stmt = con.createStatement();
         
      ResultSet rs = stmt.executeQuery("SELECT asiakas.nimi AS asiakas, COUNT(tilausnide.tilausnide_tunnus)"
      + " FROM keskusdivari.asiakas INNER JOIN keskusdivari.tilaus"
      + " ON asiakas.tunnus = tilaus.asiakas_tunnus"
      +  " INNER JOIN keskusdivari.tilausnide"
      +  " ON tilaus.tilaus_id = tilausnide.tilaus_id"
      + " WHERE pvm >= date_trunc('year', now() - interval '1 year') AND pvm < date_trunc('year', now())"
      + " GROUP BY asiakas.nimi"
      + " ORDER BY asiakas.nimi;");
         
      ResultSetMetaData rsmd = rs.getMetaData();
         
      int col = rsmd.getColumnCount();
   
      for(int i = 1; i <= 2; i++) {
        if(i == 1){
          System.out.print("Asiakas");
          for(int j = 10; j <= 23; j++)
            System.out.print(" ");
        }   
        else if(i == 2) {
          System.out.print("Teos_lkm");
          for(int j = 10; j <= 10; j++)
            System.out.print(" ");
             
        }
        System.out.print("| ");
      }
        
      System.out.println("");
         
      while (rs.next()) {
        for (int i = 1; i <= col; i++) {
          String s = rs.getString(i);
          if(s == null) {
            System.out.print("NULL");
            for(int j = 3; j <= 20; j++)
              System.out.print(" ");
          }
          else {
            System.out.print(s);
            int pituus = s.length();
            //ASIAKAS
            if(i == 1) {
              for(int j = pituus; j <= 20; j++)
                System.out.print(" ");
            }
            //NIDELKM
            else if(i == 2) {
              for(int j = pituus; j <= 8; j++)
                System.out.print(" ");
            }
              
          } 
          System.out.print("| ");
        }
        System.out.println("");
      }
         
      stmt.close();
    }
    catch(SQLException e) {
       System.out.println("Virhe " + e);
    }
  
  }
   
  //Hakutoiminnot
  public static void hakuKomennot(Connection con) {
    System.out.println("");
    System.out.println("<-------------Hakutoiminnot------------->");
    System.out.println("--> Hae teoksen nimen perusteella komennolla: nimi");
    System.out.println("--> Hae teoksen tekijän perusteella komennolla: tekija");
    System.out.println("--> Hae teoksia tyypin perusteella komennolla: tyyppi");
    System.out.println("--> Hae teoksia luokan perusteella komennolla: luokka");
    System.out.println("--> Pääset halutessasi taaksepäin komennolla: taakse");
    System.out.println("<---------------------------------------->");
   
    boolean jatkuuko = true;
    while(jatkuuko == true) {
      System.out.print("nimi/tekija/tyyppi/luokka/taakse: ");
      String komento = sc.nextLine();
   
      if(komento.equals("taakse"))
        jatkuuko = false;
      else if(komento.equals("nimi")) {
        nimiHaku(con);
        jatkuuko = false;
      }
      else if(komento.equals("tekija")) {
        tekijaHaku(con);
        jatkuuko = false;
      }
      else if(komento.equals("tyyppi")) {
        tyyppiHaku(con);
        jatkuuko = false;
      }
      else if(komento.equals("luokka")) {
        luokkaHaku(con);
        jatkuuko = false;
      }
      else
        System.out.println("Virheellinen komento!");
    }
  }
   
  //Kun haetaan teoksen nimen perusteella.
  public static void nimiHaku(Connection con) {
    System.out.println("");
    System.out.println("<----------Haku teoksen nimen perusteella------------>");
    System.out.println("--> Syötä hakulause. Esim Maltese.");
    System.out.println("--> Pääset halutessasi taaksepäin komennolla: taakse");
    System.out.println("<---------------------------------------->");
   
    boolean pyoriiko = true;
    boolean tyhjako = true;
   
    while(pyoriiko == true) {
      System.out.print("hakulause/taakse: ");
      String komento = sc.nextLine();
   
      if(komento.equals("taakse"))
        pyoriiko = false;
      else {
        tyhjako = teosTulostus(con, "SELECT DISTINCT teos.teos_tunnus, nimi, tyyppi, luokka, tekija, julkaisuvuosi FROM keskusdivari.teos, "
        + "keskusdivari.nide WHERE teos.teos_tunnus = nide.teos_tunnus AND nimi like ? AND saldo > 0 ORDER BY teos.teos_tunnus ASC", komento);
   
        if(tyhjako == false) {
          nideSelaus(con);
          pyoriiko = false;
        }
        else
          System.out.println("Ei tuloksia hakusanalla! Koita uudestaan toisella hakusanalla tai valitse selaus etusivulta!");
      }
    }
  }
   
  //kun haetaan tekijän perusteella.
  public static void tekijaHaku(Connection con) {
    System.out.println("");
    System.out.println("<----------Haku tekijän nimen perusteella------------>");
    System.out.println("--> Syötä hakulause. Esim Waltari.");
    System.out.println("--> Pääset halutessasi taaksepäin komennolla: taakse");
    System.out.println("<---------------------------------------->");
   
    boolean pyoriiko = true;
    boolean tyhjako = true;
   
    while(pyoriiko == true) {
      System.out.print("hakulause/taakse: ");
      String komento = sc.nextLine();
   
      if(komento.equals("taakse"))
        pyoriiko = false;
      else {
        tyhjako = teosTulostus(con, "SELECT DISTINCT teos.teos_tunnus, nimi, tyyppi, luokka, tekija, julkaisuvuosi FROM keskusdivari.teos, "
        + "keskusdivari.nide WHERE teos.teos_tunnus = nide.teos_tunnus AND tekija like ? AND saldo > 0 ORDER BY teos.teos_tunnus ASC", komento);
   
        if(tyhjako == false) {
          nideSelaus(con);
          pyoriiko = false;
        }
        else
          System.out.println("Ei tuloksia hakusanalla! Koita uudestaan toisella hakusanalla tai valitse selaus etusivulta!");
      }
    }
  }
   
  //kun haetaan tekijän perusteella.
  public static void tyyppiHaku(Connection con) {
    System.out.println("");
    System.out.println("<----------Haku teoksen tyypin perusteella------------>");
    System.out.println("--> Syötä hakulause. Esim. romaani/kuvakirja/sarjakuva");
    System.out.println("--> Pääset halutessasi taaksepäin komennolla: taakse");
    System.out.println("<---------------------------------------->");
       
    boolean pyoriiko = true;
    boolean tyhjako = true;
       
    while(pyoriiko == true) {
      System.out.print("hakulause/taakse: ");
      String komento = sc.nextLine();
   
      if(komento.equals("taakse"))
        pyoriiko = false;
      else {
        tyhjako = teosTulostus(con, "SELECT DISTINCT teos.teos_tunnus, nimi, tyyppi, luokka, tekija, julkaisuvuosi FROM keskusdivari.teos, "
        + "keskusdivari.nide WHERE teos.teos_tunnus = nide.teos_tunnus AND tyyppi like ? AND saldo > 0 ORDER BY teos.teos_tunnus ASC", komento);
   
        if(tyhjako == false) {
          nideSelaus(con);
          pyoriiko = false;
        }
        else
          System.out.println("Ei tuloksia hakusanalla! Koita uudestaan toisella hakusanalla tai valitse selaus etusivulta!");
      }
    }
  }
   
  //haku luokan perusteella
  public static void luokkaHaku(Connection con) {
    System.out.println("");
    System.out.println("<----------Haku teoksen luokan perusteella------------>");
    System.out.println("--> Syötä hakulause. Esim. romantiikka, seikkailu, sikailu");
    System.out.println("--> Pääset halutessasi taaksepäin komennolla: taakse");
    System.out.println("<---------------------------------------->");
   
    boolean pyoriiko = true;
    boolean tyhjako = true;
   
    while(pyoriiko == true) {
      System.out.print("hakulause/taakse: ");
      String komento = sc.nextLine();
   
      if(komento.equals("taakse"))
        pyoriiko = false;
      else {
        tyhjako = teosTulostus(con, "SELECT DISTINCT teos.teos_tunnus, nimi, tyyppi, luokka, tekija, julkaisuvuosi FROM keskusdivari.teos, "
        + "keskusdivari.nide WHERE teos.teos_tunnus = nide.teos_tunnus AND luokka like ? AND saldo > 0 ORDER BY teos.teos_tunnus ASC", komento);
   
        if(tyhjako == false) {
          nideSelaus(con);
          pyoriiko = false;
        }
        else
          System.out.println("Ei tuloksia hakusanalla! Koita uudestaan toisella hakusanalla tai valitse selaus etusivulta!");
      }
    }
  }
   
  //hakujen teoslistojen tulostus.
  public static boolean teosTulostus(Connection con, String lause, String hakulause) {
    boolean tyhjako = false;
    try {
      PreparedStatement teosTulostus = con.prepareStatement(lause);
     
      teosTulostus.clearParameters();
      teosTulostus.setString(1, "%" + hakulause + "%");
      ResultSet rs = teosTulostus.executeQuery();
         
      if(rs.isBeforeFirst() == false) {
        tyhjako = true;
      }
      else {
        ResultSetMetaData rsmd = rs.getMetaData();
             
        int col = rsmd.getColumnCount();
           
        for(int i = 1; i <= 6; i++) {
          if(i == 1)
            System.out.print("NRO ");
          else if(i == 2) {
            System.out.print("NIMI");
            for(int j = 4; j <= 50; j++)
                  System.out.print(" ");
          }
          else if(i == 3) {
            System.out.print("TYYPPI");
            for(int j = 5; j < 20; j++)
                  System.out.print(" ");
          }
          else if(i == 4) {
            System.out.print("LUOKKA");
            for(int j = 5; j < 20; j++)
                  System.out.print(" ");
          }
          else if(i == 5) {
            System.out.print("TEKIJÄ");
            for(int j = 6; j <= 30; j++)
                  System.out.print(" ");
          }
          else if(i == 6) 
            System.out.print("VUOSI ");
   
          System.out.print("| ");
   
        }      
        System.out.println("");
        while (rs.next()) {
          for (int i = 1; i <= col; i++) {
            String s = rs.getString(i);
            int pituus = s.length();
            if(rs.wasNull())
              System.out.println("NULL");
            else {
              System.out.print(s);
              //NRO
              if(i == 1) {
                for(int j = pituus; j <= 3; j++)
                  System.out.print(" ");
              }
              //NIMI
              else if(i == 2) {
                for(int j = pituus; j <= 50; j++)
                  System.out.print(" ");
              }
              //TYYPPI
              else if(i == 3) {
                for(int j = pituus; j <= 20; j++)
                  System.out.print(" ");
              }
              //LUOKKA
              else if(i == 4) {
                for(int j = pituus; j <= 20; j++)
                  System.out.print(" ");
              }
              //TEKIJÄ
              else if(i == 5) {
                for(int j = pituus; j <= 30; j++)
                  System.out.print(" ");
              }
              //JULKAISUVUOSI
              else if(i == 6) {
                for(int j = pituus; j <= 5; j++)
                  System.out.print(" ");
              }
              System.out.print("| ");
            } 
          }
          System.out.println("");
        }
        teosTulostus.close();
      }
    }
    catch(SQLException e) {
      System.out.println("Tapahtui seuraava virhe: " + e.getMessage());
    }
    return tyhjako;
  }
    
  //haetaan kaikki teokset kun halutaan selata kaikkia.
  public static void teosSelaus(Connection con) {
    System.out.println("");
    System.out.println("<---------------------------------------->");
    System.out.println("<-----Kaikki Keskusdivarin tuotteet------>");
    System.out.println("<---------------------------------------->");
    
    try {
      Statement stmt = con.createStatement();
          
      ResultSet rs = stmt.executeQuery("SELECT DISTINCT teos.Teos_tunnus, nimi, tyyppi, luokka, tekija, julkaisuvuosi FROM keskusdivari.Teos, keskusdivari.Nide WHERE teos.teos_tunnus = nide.teos_tunnus AND saldo > 0 ORDER BY teos.teos_tunnus ASC");
          
      ResultSetMetaData rsmd = rs.getMetaData();
          
      int col = rsmd.getColumnCount();
    
      for(int i = 1; i <= 6; i++) {
        if(i == 1)
          System.out.print("NRO ");
        else if(i == 2) {
          System.out.print("NIMI");
          for(int j = 4; j <= 50; j++)
                System.out.print(" ");
        }
        else if(i == 3) {
          System.out.print("TYYPPI");
          for(int j = 5; j < 20; j++)
                System.out.print(" ");
        }
        else if(i == 4) {
          System.out.print("LUOKKA");
          for(int j = 5; j < 20; j++)
                System.out.print(" ");
        }
        else if(i == 5) {
          System.out.print("TEKIJÄ");
          for(int j = 6; j <= 30; j++)
                System.out.print(" ");
        }
        else if(i == 6) 
          System.out.print("VUOSI ");
    
        System.out.print("| ");
    
      }
          
      System.out.println("");
    
      while (rs.next()) {
        for (int i = 1; i <= col; i++) {
          String s = rs.getString(i);
          int pituus = s.length();
          if(rs.wasNull())
            System.out.println("NULL");
          else {
            System.out.print(s);
            //NRO
            if(i == 1) {
              for(int j = pituus; j <= 3; j++)
                System.out.print(" ");
            }
            //NIMI
            else if(i == 2) {
              for(int j = pituus; j <= 50; j++)
                System.out.print(" ");
            }
            //TYYPPI
            else if(i == 3) {
              for(int j = pituus; j <= 20; j++)
                System.out.print(" ");
            }
            //LUOKKA
            else if(i == 4) {
              for(int j = pituus; j <= 20; j++)
                System.out.print(" ");
            }
            //TEKIJÄ
            else if(i == 5) {
              for(int j = pituus; j <= 30; j++)
                System.out.print(" ");
            }
            //JULKAISUVUOSI
            else if(i == 6) {
              for(int j = pituus; j <= 5; j++)
                System.out.print(" ");
            }
            System.out.print("| ");
          } 
        }
        System.out.println("");
      }
          
      stmt.close();
    
      //Siirrytään niteen valitsemiseen.
      nideSelaus(con);
    }
    catch (SQLException e) {
        System.out.println("Tapahtui seuraava virhe: " + e.getMessage());
    }
  }
    
  //niteen valitseminen teoksen jälkeen.
  public static void nideSelaus(Connection con) {
    
    boolean pyoriiko = true;
   
    System.out.println("<---------------------------------------->");
    System.out.println("--> Valitse tuote komennolla valitse x, jossa x on tuotteen numero: valitse x");      
    System.out.println("OHJE: valitsemalla tuotteen näet tuotteesta tarjolla olevat niteet ja hinnat.");
    System.out.println("--> Palaa taakse komennolla: taakse");
    System.out.println("<---------------------------------------->");
        
    while(pyoriiko == true) {
      System.out.print("valitse x/taakse: ");
      String komento = sc.nextLine();
    
      if(komento.equals("taakse")) {
        pyoriiko = false;
      }
      else {
        String[] osat = komento.split(" ");
        if(osat[0].equals("valitse")) {
          try {
            int x = Integer.parseInt(osat[1]);            
                
            PreparedStatement nideHaku = con.prepareStatement("SELECT nide_tunnus, nimi, hinta, kunto FROM keskusdivari.Teos, keskusdivari.Nide WHERE teos.teos_tunnus = nide.teos_tunnus AND nide.teos_tunnus = ? AND saldo > 0");
    
            nideHaku.clearParameters();
            nideHaku.setInt(1, x);
    
            ResultSet rs = nideHaku.executeQuery();
            System.out.println("");
            System.out.println("<---------------------------------------->");
            System.out.println("<---------Teoksen kaikki niteet---------->");
            System.out.println("<---------------------------------------->");
    
            for(int i = 1; i <= 4; i++) {
              if(i == 1)
                System.out.print("NRO ");
              else if(i == 2) {
                System.out.print("NIMI");
                for(int j = 5; j <= 50; j++)
                      System.out.print(" ");
              } 
              else if(i == 3) {
                System.out.print("HINTA");
                for(int j = 5; j < 8; j++)
                      System.out.print(" ");
              }
              else {
                System.out.print("KUNTO");
                for(int j = 5; j < 15; j++)
                      System.out.print(" ");
              }
              System.out.print("| ");
            }
                
            System.out.println("");
    
            while (rs.next()) {
              for (int i = 1; i <= 4; i++) {
                String s = rs.getString(i);
                int pituus = s.length();
                if(rs.wasNull())
                  System.out.println("NULL");
                else {
                  System.out.print(s);
                  //NRO
                  if(i == 1) {
                    for(int j = pituus; j <= 3; j++)
                      System.out.print(" ");
                  }
                  //NIMI
                  else if(i == 2) {
                    for(int j = pituus; j < 50; j++)
                    System.out.print(" ");
                  }
                  //hinta
                  else if(i == 3) {
                    for(int j = pituus; j < 8; j++)
                      System.out.print(" ");
                  }
                  //kunto
                  else {
                    for(int j = pituus; j < 15; j++)
                      System.out.print(" ");
                  }
                  System.out.print("| ");
                } 
              }
              System.out.println("");
            }
                
            nideHaku.close();
            nideSelausOstoskoriin(con);
            pyoriiko = false;
          } 
          catch (SQLException e) {
            System.out.println("Tapahtui seuraava virhe: " + e.getMessage());
          }
          catch (Exception e) {
            System.out.println("Tapahtui seuraava virhe: " + e.getMessage());
          }  
        }
        else
          System.out.println("Virheellinen komento!"); 
      }
    }
  }
    
  //nideSelauksen jälkeen mennään tänne. Kysytään halutaanko lisätä tuote ostoskoriin.
  public static void nideSelausOstoskoriin(Connection con) {
        
    boolean pyoriiko = true;
   
    System.out.println("<---------------------------------------->");
    System.out.println("--> Lisää tuote ostoskoriin komennolla (jossa x on niteen numero): osta x");      
    System.out.println("OHJE: lisäämisen jälkeen palaat etusivulle, josta pääset ostoskoriin komennolla: ostoskori");
    System.out.println("OHJE: ostoskorissa voit tilata tuotteesi tai vielä poistaa tuotteita");
    System.out.println("--> Palaa taakse komennolla: taakse");
    System.out.println("<---------------------------------------->");
        
    while(pyoriiko == true) {
      System.out.print("osta x/taakse: ");
      String komento = sc.nextLine();
    
      if(komento.equals("taakse")) {
        pyoriiko = false;
      }
      else {
        String[] osat = komento.split(" ");
        if(osat[0].equals("osta")) {
          try {
            int nide_tunnus = Integer.parseInt(osat[1]);            
                
            int tilausNide_Tunnus = seuraavaVapaaTunnus(con, "SELECT tilausnide_tunnus FROM keskusdivari.tilausnide", "tilausnide_tunnus");
                
            //Katsotaan onko ensimmäinen ostettu tuote, jos on niin asetetaan tilaus_id.
            //tarkistetaan onko asiakkaalla keskeneräistä tilausta, jos on niin jatketaan sitä.
            if(ensimmainenTuote == true) {
    
              onkoKesken(con);
    
              if(kesken == false) 
                tilaus_id = seuraavaVapaaTunnus(con, "SELECT tilaus_id FROM keskusdivari.tilaus", "tilaus_id");
    
              ensimmainenTuote = false;
            }
               
            //Lisätään tilaus jos ei ole keskeneräistä
            if(kesken == false) { 
              PreparedStatement lisaaTilaus = con.prepareStatement("INSERT INTO keskusdivari.Tilaus VALUES(?, ?, ?, ?, ?)");
                  
              lisaaTilaus.clearParameters();
              lisaaTilaus.setInt(1, tilaus_id);
              lisaaTilaus.setString(2, kirjautunutAsiakasTunnus);
              lisaaTilaus.setDouble(3, 0);
              lisaaTilaus.setDate(4, java.sql.Date.valueOf(java.time.LocalDate.now()));
              lisaaTilaus.setString(5, "kesken");
              lisaaTilaus.executeUpdate();
              lisaaTilaus.close();
   
              kesken = true;
            }
               
            //lisätään tilausnide
            PreparedStatement ostaNide = con.prepareStatement("INSERT INTO keskusdivari.Tilausnide VALUES(?, ?, ?)");
            ostaNide.clearParameters();
            ostaNide.setInt(1, tilausNide_Tunnus);
            ostaNide.setInt(2, nide_tunnus);
            ostaNide.setInt(3, tilaus_id);
            ostaNide.executeUpdate();
            ostaNide.close();
               
            //päivitetään tilauksen yhteishinta.
            double tuotteenHinta = haeArvo(con, nide_tunnus, "hinta", "SELECT hinta FROM Keskusdivari.Nide WHERE nide_tunnus = ?");
            double yhteishinta = haeArvo(con, tilaus_id, "yhteishinta", "SELECT yhteishinta FROM Keskusdivari.Tilaus WHERE tilaus_id = ?");
            yhteishinta = yhteishinta + tuotteenHinta;
    
            PreparedStatement hinnanPaivitys = con.prepareStatement("UPDATE Keskusdivari.Tilaus SET yhteishinta = ? WHERE tilaus_id = ?");
    
            hinnanPaivitys.clearParameters();
            hinnanPaivitys.setDouble(1, yhteishinta);
            hinnanPaivitys.setInt(2, tilaus_id);
            hinnanPaivitys.executeUpdate();
            hinnanPaivitys.close();
               
            //haetaan teos_tunnus
            int teos_tunnus = (int) haeArvo(con, nide_tunnus, "teos_tunnus", "SELECT teos.teos_tunnus FROM Keskusdivari.Teos, Keskusdivari.Nide WHERE Teos.teos_tunnus = Nide.teos_tunnus AND nide_tunnus = ?");
   
            //päivitetään tilauksen kokonaispainoa teoksen painon verran.
            double tuotteenPaino = haeArvo(con, teos_tunnus, "paino", "SELECT paino FROM Keskusdivari.Teos WHERE teos_tunnus = ?");
            double yhteispaino = haeArvo(con, tilaus_id, "kokonaispaino", "SELECT kokonaispaino FROM Keskusdivari.Tilaus WHERE tilaus_id = ?");
            yhteispaino = yhteispaino + tuotteenPaino;
    
            PreparedStatement painonPaivitys = con.prepareStatement("UPDATE Keskusdivari.Tilaus SET kokonaispaino = ? WHERE tilaus_id = ?");
    
            painonPaivitys.clearParameters();
            painonPaivitys.setDouble(1, yhteispaino);
            painonPaivitys.setInt(2, tilaus_id);
            painonPaivitys.executeUpdate();
            painonPaivitys.close();
              
            //haetaan niteen saldo keskusdivarin tietokannan päivitystä varten.
            int saldo = haeSaldo(con, nide_tunnus);
            saldo--;
            //poistetaan ostoskoriin lisätty nide saldoilta
            PreparedStatement saldonVaihto = con.prepareStatement("UPDATE Keskusdivari.Nide SET saldo = ? WHERE nide_tunnus = ?");
    
            saldonVaihto.clearParameters();
            saldonVaihto.setInt(1, saldo);
            saldonVaihto.setInt(2, nide_tunnus);
            saldonVaihto.executeUpdate();
            saldonVaihto.close();
               
            System.out.println("TUOTE LISÄTTIIN OSTOSKORIIN!");
            pyoriiko = false;
          }
          catch (SQLException e) {
            System.out.println("Tapahtui seuraava virhe: " + e.getMessage());
          }
          catch (Exception e) {
            System.out.println("Tapahtui seuraava virhe: " + e.getMessage());
          }
        }
        else
          System.out.println("Virheellinen komento!");
      }
    }
  }
    
  public static void postimaksujenPaivitys(Connection con, int sarake) {
    try{
      boolean jatkuuko = true;
      double kokonaispaino = haeArvo(con, sarake, "kokonaispaino", "SELECT kokonaispaino FROM Keskusdivari.Tilaus WHERE tilaus_id = ?");
      double postimaksut = 0;
      while(jatkuuko == true) {
        if(kokonaispaino <= 50) {
          postimaksut = postimaksut + POSTIMAKSU50G;
          jatkuuko = false;
        }
        else if(kokonaispaino <= 100) {
          postimaksut = postimaksut + POSTIMAKSU100G;
          jatkuuko = false;
        }
        else if(kokonaispaino <= 250) {
          postimaksut = postimaksut + POSTIMAKSU250G;
          jatkuuko = false;
        }
        else if(kokonaispaino <= 500) {
          postimaksut = postimaksut + POSTIMAKSU500G;
          jatkuuko = false;
        }
        else if(kokonaispaino <= 1000) {
          postimaksut = postimaksut + POSTIMAKSU1000G;
          jatkuuko = false;
        }
        else if(kokonaispaino <= 2000) {
          postimaksut = postimaksut + POSTIMAKSU2000G;
          jatkuuko = false;
        }
        else if(kokonaispaino > 2000) {
          postimaksut = postimaksut + POSTIMAKSU2000G;
          kokonaispaino = kokonaispaino - 2000;
        }
      }
      PreparedStatement postimaksujenPaivitys = con.prepareStatement("UPDATE Keskusdivari.Tilaus SET postimaksut = ? WHERE tilaus_id = ?");
    
      postimaksujenPaivitys.clearParameters();
      postimaksujenPaivitys.setDouble(1, postimaksut);
      postimaksujenPaivitys.setInt(2, sarake);
      postimaksujenPaivitys.executeUpdate();
      postimaksujenPaivitys.close();
    }
    catch(SQLException e) {
      System.out.println("Tapahtui seuraava virhe: " + e.getMessage());
    }
  }
   
  //hakee tuotteen nykyisen saldon.
  public static int haeSaldo(Connection con, int nide_tunnus) {
    int saldo = 0;
    
      try {
        PreparedStatement saldonHaku = con.prepareStatement("SELECT saldo FROM Keskusdivari.Nide WHERE nide_tunnus = ?");
        saldonHaku.clearParameters();
        saldonHaku.setInt(1, nide_tunnus);
        ResultSet rs = saldonHaku.executeQuery();
            
        while (rs.next()) {
          saldo = rs.getInt("saldo");
        }
     
        saldonHaku.close();
      }
      catch (SQLException e) {
          System.out.println("Tapahtui seuraava virhe: " + e.getMessage());
      }
    return saldo;
  }
    
  //hakee arvon.
  public static double haeArvo(Connection con, int tunnus, String sarake, String lause) {
    double arvo = 0;
      
      try {
        PreparedStatement arvonHaku = con.prepareStatement(lause);
        arvonHaku.clearParameters();
        arvonHaku.setInt(1, tunnus);
        ResultSet rs = arvonHaku.executeQuery();
              
        while (rs.next()) {
          arvo = rs.getDouble(sarake);
        }
       
        arvonHaku.close();
      }
      catch (SQLException e) {
        System.out.println("Tapahtui seuraava virhe: " + e.getMessage());
      }
    return arvo;
  }
    
  //Palauttaa seuraavan vapaan tunnuksen, parametreinä con, SQL-lause ja sarakkeen nimi. 
  public static int seuraavaVapaaTunnus(Connection con, String lause, String sarake) {
        
    int tunnus = 0;
    
    try {
      Statement stmt = con.createStatement();
          
      ResultSet rs = stmt.executeQuery(lause);
          
      while (rs.next()) {    
        int apu = rs.getInt(sarake);
        if(apu > tunnus)
          tunnus = apu;
      }
          
      tunnus++;
      stmt.close();
    }
    catch (SQLException e) {
      System.out.println("Tapahtui seuraava virhe: " + e.getMessage());
    }
    return tunnus;
  }
    
  //Katsotaan onko asiakkaalla keskeneräinen tilaus, hakee keskeneräisen tilauksen id:n.
  public static void onkoKesken(Connection con) {
    try {
      PreparedStatement tarkistusOnkoKesken = con.prepareStatement("SELECT tila, tilaus_id FROM keskusdivari.Tilaus WHERE asiakas_tunnus = ?");
      tarkistusOnkoKesken.clearParameters();
      tarkistusOnkoKesken.setString(1, kirjautunutAsiakasTunnus);
      ResultSet rs = tarkistusOnkoKesken.executeQuery();
    
      while(rs.next()) {
        String status = rs.getString("tila");
        if(rs.wasNull() == false && status.equals("kesken")) {
          tilaus_id = rs.getInt("tilaus_id");
          kesken = true;
        }
      }
      tarkistusOnkoKesken.close();
    }
    catch(SQLException e) {
      System.out.println("Tapahtui virhe " + e);
    }
  }
    
  //Ostoskori
  public static void ostoskori(Connection con) {
    
    boolean pyoriiko = true;
    System.out.println("");
    System.out.println("<---------------Ostoskori---------------->");
    System.out.println("--> Tilaa tuotteet komennolla: tilaa");      
    System.out.println("--> Poista tuote ostoskorista komennolla (jossa x on tuotteen nro): poista x");      
    System.out.println("--> Palaa taakse komennolla: taakse");
    System.out.println("<---------------------------------------->");
    
    while(pyoriiko == true) {
          
      onkoKesken(con);
    
      if(kesken == true) {
        postimaksujenPaivitys(con, tilaus_id);
        tulostaOstoskori(con);
   
        System.out.print("tilaa/poista x/taakse: ");
        String komento = sc.nextLine();
    
        if(komento.equals("taakse"))
          pyoriiko = false;
        else {
          String[] osat = komento.split(" ");
          if(osat[0].equals("poista")) {
            try {
              int numero = Integer.parseInt(osat[1]);
              System.out.println("Poistetaan ostoskorista tuote numero " + numero);
   
              //haetaan tilausnide_tunnus tuotteen poistoa varten.
              PreparedStatement haetilausnide = con.prepareStatement("SELECT tilausnide_tunnus FROM keskusdivari.tilausnide WHERE nide_tunnus = ? AND tilaus_id = ? ORDER BY tilausnide_tunnus ASC LIMIT 1;");
              haetilausnide.clearParameters();
              haetilausnide.setInt(1, numero);
              haetilausnide.setInt(2, tilaus_id);             
              ResultSet rss = haetilausnide.executeQuery();
                 
              int tilausnide_tunnus = 0;
              while(rss.next()) {
                tilausnide_tunnus = rss.getInt("tilausnide_tunnus");
              }
              haetilausnide.close();
   
              PreparedStatement tuotteenPoisto = con.prepareStatement("DELETE FROM Keskusdivari.Tilausnide WHERE tilausnide_tunnus = ?");
    
              tuotteenPoisto.clearParameters();
              tuotteenPoisto.setInt(1, tilausnide_tunnus);
              tuotteenPoisto.executeUpdate();
              tuotteenPoisto.close();
    
              //haetaan niteen saldopäivitystä varten.
              int saldo = haeSaldo(con, numero);
              saldo++;
    
              //lisätään ostoskorista poistettu nide saldoille
              PreparedStatement saldonVaihto = con.prepareStatement("UPDATE Keskusdivari.Nide SET saldo = ? WHERE nide_tunnus = ?");
    
              saldonVaihto.clearParameters();
              saldonVaihto.setInt(1, saldo);
              saldonVaihto.setInt(2, numero);
              saldonVaihto.executeUpdate();
              saldonVaihto.close();
    
              //tarkistus poistetaanko koko tilaus
              PreparedStatement tyhjaTilaus = con.prepareStatement("SELECT tilaus_id FROM keskusdivari.tilausnide WHERE tilaus_id = ?");
              tyhjaTilaus.clearParameters();
              tyhjaTilaus.setInt(1, tilaus_id);
              ResultSet rs = tyhjaTilaus.executeQuery();
                  
              //jos tyhjä niin poistetaan koko tilaus.
              if(rs.next() == false) {
                System.out.println("Poistetaan koko tilaus.");
                    
                //Koko tilauksen poisto
                PreparedStatement tilauksenPoisto = con.prepareStatement("DELETE FROM Keskusdivari.Tilaus WHERE tilaus_id = ?");
    
                tilauksenPoisto.clearParameters();
                tilauksenPoisto.setInt(1, tilaus_id);
                tilauksenPoisto.executeUpdate();
                tilauksenPoisto.close();
    
                kesken = false;
                ensimmainenTuote = true;
                pyoriiko = false;
              }
              //Muuten poistetaan tilauksen yhteishinnasta tuotteen hinta ja paino
              else {
                //haetaan teos_tunnus
                int teos_tunnus = (int) haeArvo(con, numero, "teos_tunnus", "SELECT teos.teos_tunnus FROM Keskusdivari.Teos, Keskusdivari.Nide WHERE Teos.teos_tunnus = Nide.teos_tunnus AND nide_tunnus = ?");
   
                //päivitetään tilauksen kokonaispainoa teoksen painon verran.
                double tuotteenPaino = haeArvo(con, teos_tunnus, "paino", "SELECT paino FROM Keskusdivari.Teos WHERE teos_tunnus = ?");
                double yhteispaino = haeArvo(con, tilaus_id, "kokonaispaino", "SELECT kokonaispaino FROM Keskusdivari.Tilaus WHERE tilaus_id = ?");
                yhteispaino = yhteispaino - tuotteenPaino;
       
                PreparedStatement painonPaivitys = con.prepareStatement("UPDATE Keskusdivari.Tilaus SET kokonaispaino = ? WHERE tilaus_id = ?");
       
                painonPaivitys.clearParameters();
                painonPaivitys.setDouble(1, yhteispaino);
                painonPaivitys.setInt(2, tilaus_id);
                painonPaivitys.executeUpdate();
                painonPaivitys.close();
   
                double tuotteenHinta = haeArvo(con, numero, "hinta", "SELECT hinta FROM Keskusdivari.Nide WHERE nide_tunnus = ?");
                double yhteishinta = haeArvo(con, tilaus_id, "yhteishinta", "SELECT yhteishinta FROM Keskusdivari.Tilaus WHERE tilaus_id = ?");
                yhteishinta = yhteishinta - tuotteenHinta;
    
                PreparedStatement hinnanPaivitys = con.prepareStatement("UPDATE Keskusdivari.Tilaus SET yhteishinta = ? WHERE tilaus_id = ?");
    
                hinnanPaivitys.clearParameters();
                hinnanPaivitys.setDouble(1, yhteishinta);
                hinnanPaivitys.setInt(2, tilaus_id);
                hinnanPaivitys.executeUpdate();
                hinnanPaivitys.close();
              }
              tyhjaTilaus.close();
            }
            catch(SQLException e) {
              System.out.println("Tapahtui virhe " + e);
            }
            catch(Exception e) {
              System.out.println("Tapahtui virhe " + e);
            }
          }
          else if(komento.equals("tilaa")) {
            try {
              double yhteispaino = haeArvo(con, tilaus_id, "kokonaispaino", "SELECT kokonaispaino FROM Keskusdivari.Tilaus WHERE tilaus_id = ?");
              //jos alle 2000g niin vaihdetaan tilauksen tila kesken -> valmis
              if(yhteispaino <= 2000)
                tilaus(con, tilaus_id);
              //muuten jaetaan tilaus osiin.
              else {
                System.out.println("Tilaus jaetaan osiin, koska se on yli 2000g.");
   
                int apu = tilaus_id;
                int kierrosluku =  (int) Math.ceil(yhteispaino / 2000.0);
                int[] niteet = haeNiteet(con);
                int nideNumero = 0;
   
                for(int i = 0; i < kierrosluku; i++) {
                  boolean jatkuuko = true;
                  int seuraavaVapaa = seuraavaVapaaTunnus(con, "SELECT tilaus_id FROM keskusdivari.tilaus", "tilaus_id");
                  boolean uusiTilausko = true;
                     
                  while(jatkuuko == true) {
                    yhteispaino = haeArvo(con, apu, "kokonaispaino", "SELECT kokonaispaino FROM Keskusdivari.Tilaus WHERE tilaus_id = ?");
                       
                    if(yhteispaino <= 2000) {
                      tilaus(con, apu);
                      jatkuuko = false;
                      postimaksujenPaivitys(con, apu);
                    }
                    //jos yli 2000g niin siirrettään yksi nide toiseen tilaukseen.
                    else {
                      if(uusiTilausko == true) {
                        PreparedStatement lisaaTilaus = con.prepareStatement("INSERT INTO keskusdivari.Tilaus VALUES(?, ?, ?, ?, ?)");
                   
                        lisaaTilaus.clearParameters();
                        lisaaTilaus.setInt(1, seuraavaVapaa);
                        lisaaTilaus.setString(2, kirjautunutAsiakasTunnus);
                        lisaaTilaus.setDouble(3, 0);
                        lisaaTilaus.setDate(4, java.sql.Date.valueOf(java.time.LocalDate.now()));
                        lisaaTilaus.setString(5, "kesken");
                        lisaaTilaus.executeUpdate();
                        lisaaTilaus.close();
   
                        uusiTilausko = false;
                      }
                      PreparedStatement tuotteenSiirto = con.prepareStatement("UPDATE Keskusdivari.Tilausnide SET tilaus_id = ? WHERE tilaus_id = ? AND nide_tunnus = ?");
    
                      tuotteenSiirto.clearParameters();
                      tuotteenSiirto.setInt(1, seuraavaVapaa);
                      tuotteenSiirto.setInt(2, apu);
                      tuotteenSiirto.setInt(3, niteet[nideNumero]);
                      tuotteenSiirto.executeUpdate();
                      tuotteenSiirto.close();
                         
                      int teos_tunnus = (int) haeArvo(con, niteet[nideNumero], "teos_tunnus", "SELECT teos.teos_tunnus FROM Keskusdivari.Teos, Keskusdivari.Nide WHERE Teos.teos_tunnus = Nide.teos_tunnus AND nide_tunnus = ?");
   
                      //vähennetään toisesta tilauksesta painoa ja lisätään uuteen painoa tuotteen verran.
                      double tuotteenPaino = haeArvo(con, teos_tunnus, "paino", "SELECT paino FROM Keskusdivari.Teos WHERE teos_tunnus = ?");
                      double kokonaispaino = haeArvo(con, apu, "kokonaispaino", "SELECT kokonaispaino FROM Keskusdivari.Tilaus WHERE tilaus_id = ?");
                      kokonaispaino = kokonaispaino - tuotteenPaino;
   
                      PreparedStatement painonPaivitysVanhaan = con.prepareStatement("UPDATE Keskusdivari.Tilaus SET kokonaispaino = ? WHERE tilaus_id = ?");
                      painonPaivitysVanhaan.clearParameters();
                      painonPaivitysVanhaan.setDouble(1, kokonaispaino);
                      painonPaivitysVanhaan.setInt(2, apu);
                      painonPaivitysVanhaan.executeUpdate();
                      painonPaivitysVanhaan.close();
   
                      kokonaispaino = haeArvo(con, seuraavaVapaa, "kokonaispaino", "SELECT kokonaispaino FROM Keskusdivari.Tilaus WHERE tilaus_id = ?");
                      kokonaispaino = kokonaispaino + tuotteenPaino;
   
                      PreparedStatement painonPaivitysUuteen = con.prepareStatement("UPDATE Keskusdivari.Tilaus SET kokonaispaino = ? WHERE tilaus_id = ?");
                      painonPaivitysUuteen.clearParameters();
                      painonPaivitysUuteen.setDouble(1, kokonaispaino);
                      painonPaivitysUuteen.setInt(2, seuraavaVapaa);
                      painonPaivitysUuteen.executeUpdate();
                      painonPaivitysUuteen.close();
                         
                      //päivitetään kokonaishinnat.
                      double tuotteenHinta = haeArvo(con, niteet[nideNumero], "hinta", "SELECT hinta FROM Keskusdivari.Nide WHERE nide_tunnus = ?");
                      double yhteishinta = haeArvo(con, apu, "yhteishinta", "SELECT yhteishinta FROM Keskusdivari.Tilaus WHERE tilaus_id = ?");
                      yhteishinta = yhteishinta - tuotteenHinta;
   
                      PreparedStatement hinnanPaivitysVanhaan = con.prepareStatement("UPDATE Keskusdivari.Tilaus SET yhteishinta = ? WHERE tilaus_id = ?");
                      hinnanPaivitysVanhaan.clearParameters();
                      hinnanPaivitysVanhaan.setDouble(1, yhteishinta);
                      hinnanPaivitysVanhaan.setInt(2, apu);
                      hinnanPaivitysVanhaan.executeUpdate();
                      hinnanPaivitysVanhaan.close();
   
                      yhteishinta = haeArvo(con, seuraavaVapaa, "yhteishinta", "SELECT yhteishinta FROM Keskusdivari.Tilaus WHERE tilaus_id = ?");
                      yhteishinta = yhteishinta + tuotteenHinta;
   
                      PreparedStatement hinnanPaivitysUuteen = con.prepareStatement("UPDATE Keskusdivari.Tilaus SET yhteishinta = ? WHERE tilaus_id = ?");
                      hinnanPaivitysUuteen.clearParameters();
                      hinnanPaivitysUuteen.setDouble(1, yhteishinta);
                      hinnanPaivitysUuteen.setInt(2, seuraavaVapaa);
                      hinnanPaivitysUuteen.executeUpdate();
                      hinnanPaivitysUuteen.close();
   
                      nideNumero++;
                    }
                  }
                  apu = seuraavaVapaa;
                }
              }
              pyoriiko = false;
              kesken = false;
              ensimmainenTuote = true;
            }
            catch(SQLException e) {
              System.out.println("Tapahtui virhe " + e);
            }
          }
          else
            System.out.println("Virheellinen komento!");
        }
      }
      else {
        System.out.println("Ostoskori tyhjä!");
        pyoriiko = false;
      }
    }
  }
   
  public static int[] haeNiteet(Connection con) {
    int[] niteet = new int[0];
    try {
      int niteidenMaara = 0;
      PreparedStatement arvonHaku = con.prepareStatement("SELECT nide_tunnus FROM keskusdivari.tilausnide WHERE tilaus_id = ?");
      arvonHaku.clearParameters();
      arvonHaku.setInt(1, tilaus_id);
      ResultSet rs = arvonHaku.executeQuery();    
      while (rs.next()) {
        niteidenMaara++;
      }
      arvonHaku.close();
      niteet = new int[niteidenMaara];
   
      int i = 0;
   
      PreparedStatement niteidenHaku = con.prepareStatement("SELECT nide_tunnus FROM keskusdivari.tilausnide WHERE tilaus_id = ?");
      niteidenHaku.clearParameters();
      niteidenHaku.setInt(1, tilaus_id);
      ResultSet rss = niteidenHaku.executeQuery();    
      while (rss.next()) {
        niteet[i] = rss.getInt("nide_tunnus");
        i++;
      }
      niteidenHaku.close();
    }
    catch(SQLException e) {
      System.out.println("Tapahtui virhe " + e);
    }
    return niteet;
  }
   
  //kun tilaus on OK lähteväksi.
  public static void tilaus(Connection con, int tunnus) {
    try {
      PreparedStatement tilanVaihto = con.prepareStatement("UPDATE Keskusdivari.Tilaus SET tila = 'valmis' WHERE tilaus_id = ?");
     
      tilanVaihto.clearParameters();
      tilanVaihto.setInt(1, tunnus);
      tilanVaihto.executeUpdate();
      tilanVaihto.close();     
     
      System.out.println("<---------------------------------------->");
      System.out.println("Tuotteesi on nyt tilattu ja lähetetään sinulle pikimmiten!");
      System.out.println("Kiitos, että käytit Keskusdivaria!");
      System.out.println("Näet tilauksesi etusivulta 'tilaukset'-komennolla.");
    }
    catch(SQLException e) {
      System.out.println("Tapahtui virhe " + e);
    }
  }
    
  //tulostetaan ostoskorin sisältö
  public static void tulostaOstoskori(Connection con) {
    try {
          
      PreparedStatement ostoskoriTulostus = con.prepareStatement("SELECT tilausnide.nide_tunnus, teos.nimi, teos.tekija, nide.hinta, teos.paino " +
      "FROM keskusdivari.tilausnide, keskusdivari.tilaus, keskusdivari.nide, keskusdivari.teos " +
      "WHERE tilaus.tilaus_id = tilausnide.tilaus_id AND tilausnide.nide_tunnus = nide.nide_tunnus AND nide.teos_tunnus = teos.teos_tunnus " +
      "AND asiakas_tunnus = ? AND tila = 'kesken' ORDER BY tilausnide.nide_tunnus ASC");
    
      ostoskoriTulostus.clearParameters();
      ostoskoriTulostus.setString(1, kirjautunutAsiakasTunnus);
      ResultSet rs = ostoskoriTulostus.executeQuery();
      ResultSetMetaData rsmd = rs.getMetaData();
      int col = rsmd.getColumnCount();
    
      for(int i = 1; i <= 5; i++) {
        if(i == 1)
          System.out.print("NRO ");
        else if(i == 2) {
          System.out.print("NIMI");
          for(int j = 4; j <= 50; j++)
            System.out.print(" ");
        }
        else if(i == 3) {
          System.out.print("TEKIJÄ");
          for(int j = 6; j <= 30; j++)
            System.out.print(" ");
        }
        else if(i == 4) {
          System.out.print("HINTA");
          for(int j = 5; j < 8; j++)
            System.out.print(" ");
        }
        else if(i == 5) {
          System.out.print("PAINO");
          for(int j = 5; j < 8; j++)
            System.out.print(" ");
        }
    
        System.out.print("| ");
    
      }
          
      System.out.println("");
          
      while (rs.next()) {
        for (int i = 1; i <= col; i++) {
          String s = rs.getString(i);
          int pituus = s.length();
          if(rs.wasNull())
            System.out.println("NULL");
          else {
            System.out.print(s);
            //NRO
            if(i == 1) {
              for(int j = pituus; j <= 3; j++)
                System.out.print(" ");
            }
            //NIMI
            else if(i == 2) {
              for(int j = pituus; j <= 50; j++)
                System.out.print(" ");
            }
            //TEKIJÄ
            else if(i == 3) {
              for(int j = pituus; j <= 30; j++)
                System.out.print(" ");
            }
            //HINTA JA PAINO
            else {
              for(int j = pituus; j < 8; j++)
                System.out.print(" ");
            }
            System.out.print("| ");
          } 
        }
        System.out.println("");
      }
          
      ostoskoriTulostus.close();
      System.out.println("<---------------------------------------->");
      PreparedStatement tilausTulostus = con.prepareStatement("SELECT yhteishinta, kokonaispaino, postimaksut " +
      "FROM keskusdivari.tilaus " +
      "WHERE asiakas_tunnus = ? AND tila = 'kesken'");
    
      tilausTulostus.clearParameters();
      tilausTulostus.setString(1, kirjautunutAsiakasTunnus);
      rs = tilausTulostus.executeQuery();      
    
      while(rs.next()) {
        System.out.println("Ostoskorin tuotteiden kokonaishinta: " + rs.getDouble("yhteishinta") + " euroa.");
        System.out.println("Ostoskorin tuotteiden kokonaispaino: " + rs.getDouble("kokonaispaino") + " grammaa.");
        System.out.println("Postimaksujen hinnasto: 50g->" + POSTIMAKSU50G + "e, 100g->" + POSTIMAKSU100G + "e, 250g->" + POSTIMAKSU250G + "e, 500g->" + POSTIMAKSU500G + "e, 1000g->" + POSTIMAKSU1000G + "e, 2000g->" + POSTIMAKSU2000G + "e.");
        System.out.println("Postimaksut: " + rs.getDouble("postimaksut") + " euroa.");    
      }
    
      tilausTulostus.close();
    }
    catch (SQLException e) {
        System.out.println("Tapahtui seuraava virhe: " + e.getMessage());
    }
    System.out.println("<---------------------------------------->");
  }
    
  //asiakkaan vanhat tilaukset
  public static void tilaukset(Connection con) {
    boolean pyoriiko = true;
    
    while(pyoriiko == true) {
      System.out.println("");
      System.out.println("<-------------Tilaushistoria------------->");
      System.out.println("Vanhat tilauksesi.");
      System.out.println("Keskeneräinen tilauksesi löytyy ostoskorista!");      
      System.out.println("--> Palaa taakse komennolla: taakse");
      System.out.println("<---------------------------------------->");
    
      tilauksetTulostus(con);
    
      System.out.print("taakse: ");
      String komento = sc.nextLine();
      if(komento.equals("taakse")) 
        pyoriiko = false;
      else
        System.out.println("Virheellinen komento!");
    
    }
  }
    
  //tulostetaan asiakkaan vanhat tilaukset
  public static void tilauksetTulostus(Connection con) {
    try {
      PreparedStatement tilaushistoria = con.prepareStatement("SELECT tilaus_id, yhteishinta, pvm, tila FROM keskusdivari.tilaus WHERE asiakas_tunnus = ? ORDER BY tilaus_id");
    
      tilaushistoria.clearParameters();
      tilaushistoria.setString(1, kirjautunutAsiakasTunnus);
      ResultSet rs = tilaushistoria.executeQuery();
      ResultSetMetaData rsmd = rs.getMetaData();
      int col = rsmd.getColumnCount();
    
      for(int i = 1; i <= 4; i++) {
        if(i == 1)
          System.out.print("NRO ");
        else if(i == 2) {
          System.out.print("HINTA");
          for(int j = 5; j <= 9; j++)
            System.out.print(" ");
        }
        else if(i == 3) {
          System.out.print("PVM");
          for(int j = 3; j <= 9; j++)
            System.out.print(" ");
        }
        else if(i == 4) {
          System.out.print("TILA");
          for(int j = 4; j <= 9; j++)
            System.out.print(" ");
        }
        System.out.print("| ");
      }
          
      System.out.println("");
          
      while (rs.next()) {
        for (int i = 1; i <= col; i++) {
          String s = rs.getString(i);
          if(s == null) {
            System.out.print("NULL");
            for(int j = 4; j <= 9; j++)
                System.out.print(" ");
          }
          else {
            System.out.print(s);
            int pituus = s.length();
            //NRO
            if(i == 1) {
              for(int j = pituus; j <= 3; j++)
                System.out.print(" ");
            }
            //HINTA
            else if(i == 2) {
              for(int j = pituus; j <= 9; j++)
                System.out.print(" ");
            }
            //PVM
            else if(i == 3) {
              for(int j = pituus; j <= 9; j++)
                System.out.print(" ");
            }
            //TILA
            else {
              for(int j = pituus; j <= 9; j++)
                System.out.print(" ");
            } 
          } 
          System.out.print("| ");
        }
        System.out.println("");
      }
          
      tilaushistoria.close();
    }
    catch(SQLException e) {
      System.out.println("Virhe " + e);
    }
  }
}