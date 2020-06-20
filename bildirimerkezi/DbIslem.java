package bildirimerkezi;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import static java.time.LocalDate.now;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Date;
import java.util.HashMap;
import java.util.Set;
import javax.swing.*;

public class DbIslem {

    private Connection con = null;
    private Statement st = null;
    String dizim[][];
    HashMap<Integer, String> alanlıHakem;
    String dizi[][];
    private String driver = "com.mysql.cj.jdbc.Driver";
    private String path = "jdbc:mysql://localhost:3306/";
    private String dbName = "bildirimerkezi";
    private String username = "fkendir";
    private String password = "";

    private DbIslem() {
        try {
            Class.forName(driver).newInstance();
            con = DriverManager.getConnection(path + dbName, username, password);
            st = con.createStatement();
            System.out.println("Bağlantı sağlandı");
        } catch (SQLException ex) {
            System.out.println("Olamaz SQL hatası oluştu. şte hatanız: " + ex.getMessage());
        } catch (Exception ex) {
            System.out.println("Olamaz tanımsız bir hata oluştu. şte hatanız: " + ex.getMessage());
        }
    }
    private static DbIslem dbObject;
    //singleton tasarım deseni

    public static DbIslem returnObject() {
        if (dbObject == null) {
            dbObject = new DbIslem();
        }
        return dbObject;
    }

    public ResultSet user; //kullanici bilgilerini tutmak

    //pencerelerin ustundeki label'lar icin
    public String isim;
    public String kurum;

    //kullanici girisi
    public int login(String username, String password) {
        String query = "select * from bildirimerkezi.yazar where yazar.eposta"
                + " = '" + username + "' and yazar.sifre = '" + password + "' and hakemmi = false";
        try {
            user = st.executeQuery(query);
            if (user.next()) {
                isim = "Sayın " + user.getString(2).toUpperCase() + " " + user.getString(3).toUpperCase() + ""
                        + " Bildiri Merkezi Sistemine Hoşgeldiniz...";
                kurum = user.getString(5).toUpperCase();
                return 1;
            } else {
                query = "select * from bildirimerkezi.yazar where yazar.eposta "
                        + "= '" + username + "' and yazar.sifre = '" + password + "' and hakemmi = true";
                user = st.executeQuery(query);
                if (user.next()) {
                    isim = "Sayın " + user.getString(2).toUpperCase() + " " + user.getString(3).toUpperCase() + " "
                            + "Bildiri Merkezi Sistemine Hoşgeldiniz...";
                    kurum = user.getString(5).toUpperCase();
                    return 2;
                } else {
                    query = "select * from bildirimerkezi.okb where okb.eposta "
                            + "= '" + username + "' and okb.sifre = '" + password + "';";
                    user = st.executeQuery(query);
                    if (user.next()) {
                        isim = "Sayın " + user.getString(2).toUpperCase() + " " + user.getString(3).toUpperCase() + " "
                                + "Bildiri Merkezi Sistemine Hoşgeldiniz...";
                        return 3;
                    }
                }
            }
        } catch (Exception ex) {
            System.out.println("Sorgu hatası: " + ex.getMessage());
        }
        return 0;
    }

    //Yazar tablosu icin
    public void tblFiil() throws SQLException {

        try {
            ArrayList<String> yzr = new ArrayList<>();
            Statement sb = con.createStatement();
            ResultSet vt = sb.executeQuery("Select * from bildiri where idyazar =" + Integer.parseInt(dbObject.user.getString(1)) + "; ");

            while (vt.next()) {
                yzr.add(vt.getString(1));
            }
            vt = sb.executeQuery("Select * from bildiri where idyazar =" + Integer.parseInt(dbObject.user.getString(1)) + "; ");
            dizim = new String[yzr.size()][];
            int i = 0;
            while (vt.next()) {
                dizim[i] = new String[]{
                    vt.getString(1), vt.getString(3), vt.getString(5), vt.getString(6), vt.getString(9), vt.getString(10), vt.getString(7)
                };
                i++;
            }

        } catch (SQLException ex) {
            Logger.getLogger(DbIslem.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    //okb ekrani onay bekleyen bildiriler
    public void okbOnayBekleyenTblFill() {
        try {
            ArrayList<String> yzr = new ArrayList<>();
            Statement sb = con.createStatement();
            ResultSet vt = sb.executeQuery("Select * from bildiri where onay = '0' and puan is not null; ");
            while (vt.next()) {
                String arg[] = vt.getString(10).split("-");
                String argv[] = vt.getString(12).split("--");
                if (arg.length == argv.length) {
                    yzr.add(vt.getString(1));
                }
            }
            vt = sb.executeQuery("Select * from bildiri where onay = '0' and puan is not null; ");
            dizi = new String[yzr.size()][];
            int i = 0;
            while (vt.next()) {
                String arg[] = vt.getString(10).split("-");
                String argv[] = vt.getString(12).split("--");
                if (arg.length == argv.length) {
                    dizi[i] = new String[]{
                        vt.getString(1), vt.getString(3), vt.getString(4), vt.getString(5), vt.getString(6)
                    };
                }
                i++;
            }

        } catch (SQLException ex) {
            Logger.getLogger(DbIslem.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    //okb ekrani hakem bekleyen bildiriler
    public void okbHakemBekleyenTblFill() {

        try {
            ArrayList<String> yzr = new ArrayList<>();
            Statement sb = con.createStatement();
            ResultSet vt = sb.executeQuery("Select * from bildiri where onay = '0' and hakemler is  null; ");
            while (vt.next()) {
                yzr.add(vt.getString(1));
            }
            vt = sb.executeQuery("Select * from bildiri where onay = '0' and hakemler is null; ");
            dizim = new String[yzr.size()][];
            int i = 0;
            while (vt.next()) {
                dizim[i] = new String[]{
                    vt.getString(1), vt.getString(3), vt.getString(4), vt.getString(5), vt.getString(6)
                };
                i++;
            }

        } catch (SQLException ex) {
            Logger.getLogger(DbIslem.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    //hakemleri el ile atamak(okb)
    public boolean elleAta(int id) {
        alanlıHakemAtama(id);
        Set<Integer> hakem = alanlıHakem.keySet();
        ArrayList<String> dummy = new ArrayList<>();

        hakem.forEach((v) -> {
            dummy.add(v.toString());
        });
        if (dummy.size() < 3) {
            return false;
        }
        String temp = dummy.get(0);
        for (int i = 1; i < 3; i++) {
            temp = temp + "--" + dummy.get(i);
        }
        
        try {
            Statement smts = con.createStatement();
            String query = "update  bildiri set hakemler = '" + temp + "' where idbildiri = '" + bildiri.getString(1) + "' ";
            smts.execute(query);
        } catch (SQLException ex) {
            Logger.getLogger(DbIslem.class.getName()).log(Level.SEVERE, null, ex);
        }
        return true;

    }

    //yazar ekrani onay bekleyen bildiriler
    public void onayBekleyen() throws SQLException {

        try {
            ArrayList<String> yzr = new ArrayList<>();
            Statement sb = con.createStatement();
            ResultSet vt = sb.executeQuery("Select * from bildiri where idyazar =" +
                    Integer.parseInt(dbObject.user.getString(1)) + " and onay = '0'; ");

            while (vt.next()) {
                yzr.add(vt.getString(1));
            }
            vt = sb.executeQuery("Select * from bildiri where idyazar =" +
                    Integer.parseInt(dbObject.user.getString(1)) + "  and onay = '0'; ");
            dizi = new String[yzr.size()][];
            int i = 0;
            while (vt.next()) {
                dizi[i] = new String[]{
                    vt.getString(1), vt.getString(3), vt.getString(5), vt.getString(6), 
                    vt.getString(9), vt.getString(10), vt.getString(7)
                };
                i++;
            }

        } catch (SQLException ex) {
            Logger.getLogger(DbIslem.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    //bildiriye atanan hakemleri kaydetme
    public void kaydet() {
        Set<Integer> hakem = alanlıHakem.keySet();
        ArrayList<String> dummy = new ArrayList<>();
        hakem.forEach((v) -> {
            dummy.add(v.toString());
        });
        String temp = dummy.get(0);
        for (int i = 1; i < dummy.size(); i++) {
            temp = temp + "--" + dummy.get(i);
        }
        try {
            Statement smts = con.createStatement();
            String query = "update  bildiri set hakemler = '" + temp + "' where idbildiri = '" + bildiri.getString(1) + "' ";
            smts.execute(query);
        } catch (SQLException ex) {
            Logger.getLogger(DbIslem.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    //bildiriye uygun hakemlerin getirilmesi
    public void alanlıHakemAtama(int id) {
        try {
            alanlıHakem = new HashMap<>();
            Statement sg = con.createStatement();
            ResultSet vr = sg.executeQuery("Select alanlar, idyazar from bildiri  where idbildiri = " + id + " ;");
            vr.next();
            int yazarid = Integer.parseInt(vr.getString(2));
            String dummy[] = vr.getString(1).split("--");
            for (int i = 0; i < dummy.length; i++) {
                vr = sg.executeQuery("Select * from yazar where alan = '" + dummy[i] + "' and hakemmi = '1' and idyazar <>" + yazarid + "");
                while (vr.next()) {
                    alanlıHakem.put(Integer.parseInt(vr.getString(1)), vr.getString(2).toUpperCase() + " " + vr.getString(3).toUpperCase());
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(DbIslem.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    //yazar ekleme
    public void insertYazar(String isim, String soyisim, String eposta, String kurum, String alan, String sifre) {
        try {
            Statement smt = con.createStatement();
            String query = "insert into bildirimerkezi.yazar (ad, soyad, eposta, kurum, alan, hakemmi, sifre) "
                    + "values('" + isim + "','" + soyisim + "','" + eposta + "','" + kurum + "','" + alan + "',0,'" + sifre + "');";
            smt.execute(query);
        } catch (SQLException ex) {
            System.out.println("Hata oluştu: " + ex.getMessage());
        }
    }

    //hakem ekleme
    public void insertHakem(String isim, String soyisim, String eposta, String kurum, String alan, String sifre) {
        try {
            Statement smt = con.createStatement();
            String query = "insert into bildirimerkezi.yazar (ad, soyad, eposta, kurum, alan, hakemmi, sifre) "
                    + "values('" + isim + "','" + soyisim + "','" + eposta + "','" + kurum + "','" + alan + "',1,'" + sifre + "');";
            smt.execute(query);
        } catch (SQLException ex) {
            System.out.println("Hata oluştu: " + ex.getMessage());
        }

    }

    ResultSet bildiri; //bildiri ayarları icin cagrilan bildiri

    //gerekli bildiriyi getirme
    public void getBildiri(int id) {
        try {
            Statement smt = con.createStatement();
            String query = "select * from bildiri where idbildiri = " + id + ";";
            bildiri = smt.executeQuery(query);
            bildiri.next();
        } catch (SQLException ex) {
            System.out.println("hata oluştu: " + ex.getMessage());
        }
    }

    //yazarin onaylanan bildirileri
    public void setOnaylandimi(int id) {
        try {
            Statement smts = con.createStatement();
            String query = "update  bildiri set onay = '1' where idbildiri = '" + id + "' ";
            smts.execute(query);
        } catch (SQLException ex) {
            Logger.getLogger(DbIslem.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    //yazar bilgilerini guncelleme 
    public void yazarGuncelle(String isim, String soyisim, String eposta, String kurum, String alan, String sifre) {
        try {
            Statement smt = con.createStatement();
            String query = "UPDATE `bildirimerkezi`.`yazar` SET ad = '" + isim + "', "
                    + "soyad ='" + soyisim + "', eposta = '" + eposta + "',"
                    + " kurum = '" + kurum + "',alan = '" + alan + "',sifre = '" + sifre + "' "
                    + "WHERE (`idyazar` = " + Integer.parseInt(user.getString(1)) + ");";
            smt.execute(query);
        } catch (SQLException ex) {
            System.out.println("hata oluştu: " + ex.getMessage());
        }
    }

    //yazar-hakem son giris bilgisi
    public void sonGiris(int id) {
        try {
            int month, day, year;
            String newDateFormat;
            Calendar cld = Calendar.getInstance();
            month = cld.get(Calendar.MONTH) + 1;
            day = cld.get(Calendar.DAY_OF_MONTH);
            year = cld.get(Calendar.YEAR);
            newDateFormat = "yyyy.MM.dd";
            Statement smt = con.createStatement();
            DateTimeFormatter showDate = DateTimeFormatter.ofPattern(newDateFormat);
            String query = "update yazar set songiris = '" + showDate.format(LocalDate.of(year, month, day)) + "' where idyazar = " + id + "; ";
            smt.execute(query);
        } catch (SQLException ex) {
            System.out.println("hata oluştu: " + ex.getMessage());
        }
    }

    //okb son giris bilgisi
    public void okbSonGiris() {
        try {
            int month, day, year;
            String newDateFormat;
            Calendar cld = Calendar.getInstance();
            month = cld.get(Calendar.MONTH) + 1;
            day = cld.get(Calendar.DAY_OF_MONTH);
            year = cld.get(Calendar.YEAR);
            newDateFormat = "yyyy.MM.dd";
            Statement smt = con.createStatement();
            DateTimeFormatter showDate = DateTimeFormatter.ofPattern(newDateFormat);
            String query = "update okb set songiris = '" + showDate.format(LocalDate.of(year, month, day)) + "'; ";
            smt.execute(query);
        } catch (SQLException ex) {
            System.out.println("hata oluştu: " + ex.getMessage());
        }
    }

    //bildiri guncelleme
    public void bildiriGuncelle(int id, String baslik, String ozet, String anahtar, String alan, String bildiri) {
        try {
            Statement smt = con.createStatement();
            String query = "UPDATE bildiri SET baslik = '" + baslik + "', "
                    + "bildiriozeti ='" + ozet + "', anahtarKelime = '" + anahtar + "',"
                    + " alanlar = '" + alan + "', bildiri = '" + bildiri + "'  WHERE (idbildiri = " + id + ");";
            smt.execute(query);
        } catch (SQLException ex) {
            System.out.println("hata oluştu: " + ex.getMessage());
        }

    }

    public boolean varMı;//yazarin ayni isimde bildirisi var mi

    ResultSet zamanLI; //bildirimi gonderilecek bildiriler

    //yazar kisisine gelen bildirimler
    public void yazarBildirim(int id) {

        try {

            Statement smt = con.createStatement();
            String query = "select * from bildiri where idyazar = " + id + " and onay ='1' "
                    + "and datediff(onayTarihi, (select songiris from yazar where idyazar = " + id + ")) >= 0 ;";
            zamanLI = smt.executeQuery(query);

        } catch (Exception ex) {
            System.out.println("hata oluştu: " + ex.getMessage());
        }

    }

    ResultSet bildiri_hakem; //hakem atama vakti gelen bildiriler
    //hakem atamasi icin bekleyen bildiriler

    public void okbBildirimHakem() {
        try {
            Statement smt = con.createStatement();
            String query = "select * from bildiri where datediff(onayTarihi, (select songiris from okb)) >= 3 and onay = '0' and hakemler is null ;";
            bildiri_hakem = smt.executeQuery(query);
        } catch (Exception ex) {
            System.out.println("hata oluştu: " + ex.getMessage());
        }
    }

    //onay icin bekleyen bildiriler
    public void okbBildirimOnay() {
        try {
            Statement smt = con.createStatement();
            String query = "select * from bildiri where datediff(onayTarihi, (select songiris from okb)) = 0 and onay = '0' ;";
            zamanLI = smt.executeQuery(query);
        } catch (Exception ex) {
            System.out.println("hata oluştu: " + ex.getMessage());
        }
    }

    ResultSet bildiri_yazar;//bildiriye ait yazar bilgileri
    //yazar  bilgiler

    public void okbBildiriYazar(int id) {
        try {
            Statement smt = con.createStatement();
            String query = "select * from yazar where idyazar = " + id + ";";
            bildiri_yazar = smt.executeQuery(query);
            bildiri_yazar.next();
        } catch (SQLException ex) {
            System.out.println("hata oluştu: " + ex.getMessage());
        }
    }

    //yazarin ayni baslikta bildirisi var mi
    public boolean control(String baslik) {
        try {
            Statement smt = con.createStatement();
            String query = "select * from bildiri where idyazar = " + Integer.parseInt(user.getString(1)) + " and baslik = '" + baslik + "';";
            ResultSet rs = smt.executeQuery(query);
            if (rs.next()) {
                return true;
            }
        } catch (SQLException ex) {
            Logger.getLogger(DbIslem.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public DefaultListModel alanlar() {
        DefaultListModel dim = new DefaultListModel();
        dim.addElement("Yayın yapabileceğiniz alanlar");
        String query = "select distinct alan from yazar where hakemmi = '1';";
        ResultSet rs;
        Statement smt;
        try {
            smt = con.createStatement();
            rs = smt.executeQuery(query);
            while (rs.next()) {
                dim.addElement(rs.getString(1));
            }
        } catch (SQLException ex) {
            Logger.getLogger(DbIslem.class.getName()).log(Level.SEVERE, null, ex);
        }
        return dim;
    }

    //bildiri ekleme
    public void addBildiri(String baslik, String ozet, String key, String alan, String bildiri) {
        try {
            Statement smt = con.createStatement();

            String query = "insert into bildiri ( idyazar, baslik, bildiriOzeti, anahtarKelime, alanlar, bildiri, bildiricol, tarih, puan, onay, onayTarihi, hakemler)\n"
                    + "values(" + Integer.parseInt(user.getString(1)) + ",'" + baslik + "','" + ozet + "','" + key + "','" + alan + "','" + bildiri + "','" + "" + "'  ,DATE_FORMAT(now(),\"%Y-%m-%d\"),"
                    + "null ,0,date_add(DATE_FORMAT(now(), \"%Y-%m-%d\"),interval 10 day),null);";
            smt.execute(query);
        } catch (SQLException ex) {
            Logger.getLogger(DbIslem.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    ArrayList<Integer> bildiris = new ArrayList<>();//bildiri id'leri
    String bil[][];//giris yapan hakemin gorus bildirecegi bildiriler
    //hakeme atanan bildiriler

    public void hakemBildiri() {
        try {
            Statement smt = con.createStatement();
            String query = "select *from bildiri where onay = '0' and hakemler is not null;";
            ResultSet ars = smt.executeQuery(query);
            String temp[];
            while (ars.next()) {
                temp = ars.getString("hakemler").split("--");
                for (int i = 0; i < temp.length; i++) {
                    if (Integer.parseInt(temp[i]) == Integer.parseInt(user.getString(1))) {
                        bildiris.add(Integer.parseInt(ars.getString(1)));
                        break;
                    }
                }
                bil = new String[bildiris.size()][];
            }
            for (int i = 0; i < bildiris.size(); i++) {
                ars = smt.executeQuery("select * from bildiri where idbildiri = " + bildiris.get(i) + "");
                ars.next();
                bil[i] = new String[]{
                    ars.getString(1),
                    ars.getString(3),
                    ars.getString(4),
                    ars.getString(5),
                    ars.getString(7),
                    ars.getString(9)
                };
            }
        } catch (SQLException ex) {
            Logger.getLogger(DbIslem.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void gorusAndPuan(int id, String gorus, String puan) {
        try {
            Statement smt = con.createStatement();
            String query = "update bildiri set bildiricol = '" + gorus + "' , puan = '" + puan + "' where idbildiri =" + id + ";";
            smt.execute(query);
        } catch (SQLException ex) {
            Logger.getLogger(DbIslem.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public boolean userControl(String mail) {
        ResultSet rs;
        Statement smt;
        String query = "select * from yazar where eposta = '" + mail + "'";
        try {
            smt = con.createStatement();
            rs = smt.executeQuery(query);
            if (rs.next()) {
                return false;
            }
        } catch (SQLException ex) {
            Logger.getLogger(DbIslem.class.getName()).log(Level.SEVERE, null, ex);
        }
        return true;
    }

}
