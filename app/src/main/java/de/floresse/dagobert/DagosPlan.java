package de.floresse.dagobert;

import android.util.Log;
/*
import de.floresse.util.Amount;
*/
public class DagosPlan {

    private Amount Kontostand;
    private Amount Miete;
    private Amount Strom;
    private Amount Telefon;
    private Amount Konto;
    private Amount Zeitung;
    private Amount Versicherung;
    private Amount Rechnungen;
    private Amount TVGeb;
    private Amount SonstAus1;
    private Amount SonstAus2;
    private Integer        FaMiete;
    private Integer        FaStrom;
    private Integer        FaTelefon;
    private Integer        FaKonto;
    private Integer        FaZeitung;
    private Integer        FaVersicherung;
    private String     erlRechnungen;
    private String     erlMiete;
    private String     erlStrom;
    private String     erlTelefon;
    private String     erlKonto;
    private String     erlZeitung;
    private String     erlVersicherung;
    private String     erlTVGeb;
    private String     erlGehalt;
    private String     erlGutschriften;
    private String     erlSonstAus1;
    private String     erlSonstAus2;

    private Amount KontostandNeu;
    private Amount Gehalt;
    private Amount Gutschriften;
    private Amount SonstEin1;
    private Amount SonstEin2;
    private Integer        FaGehalt;
    private Integer        DisplayedChild;
    private Integer        FaGutschriften;
    private String     erlSonstEin1;
    private String     erlSonstEin2;
    private String     erlBares;

    private Amount SummeAus;
    private Amount SummeEin;

    private Amount Bares;
    private Amount Tage;
    private Amount Satz;
    private Amount Bedarf;
    private Amount Kopfhau;

    DagosPlan () {
        Kontostand     = new Amount("0");
        Miete          = new Amount("0");
        Strom          = new Amount("0");
        Telefon        = new Amount("0");
        Konto          = new Amount("0");
        Zeitung        = new Amount("0");
        Versicherung   = new Amount("0");
        Rechnungen     = new Amount("0");
        TVGeb          = new Amount("0");
        SonstAus1      = new Amount("0");
        SonstAus2      = new Amount("0");
        FaMiete        = 1;
        FaStrom        = 1;
        FaTelefon      = 1;
        FaKonto        = 1;
        FaZeitung      = 1;
        FaVersicherung = 1;
        erlMiete       = new String("");
        erlStrom       = new String("");
        erlTelefon     = new String("");
        erlKonto       = new String("");
        erlZeitung     = new String("");
        erlVersicherung  = new String("");
        erlRechnungen  = new String("");
        erlTVGeb       = new String("");
        erlSonstAus1   = new String("");
        erlSonstAus2   = new String("");
        KontostandNeu  = new Amount("0");
        Gehalt         = new Amount("0");
        Gutschriften   = new Amount("0");
        SonstEin1      = new Amount("0");
        SonstEin2      = new Amount("0");
        FaGehalt       = 1;
        FaGutschriften = 1;
        erlGehalt       = new String("");
        erlGutschriften  = new String("");
        erlSonstEin1   = new String("");
        erlSonstEin2   = new String("");
        erlBares       = new String("");
        Bares          = new Amount("0");
        Tage           = new Amount("0");
        Satz           = new Amount("0");
        DisplayedChild = 0;
        setAlles();
    }

    DagosPlan (DagosPlan source) {
        this();
        Amount.setBlankzero(false);
        setKontostand     (source.getKontostand());
        setMiete          (source.getMiete());
        setStrom          (source.getStrom());
        setTelefon        (source.getTelefon());
        setKonto          (source.getKonto());
        setZeitung        (source.getZeitung());
        setVersicherung   (source.getVersicherung());
        setRechnungen     (source.getRechnungen());
        setTVGeb          (source.getTVGeb());
        setSonstAus1      (source.getSonstAus1());
        setSonstAus2      (source.getSonstAus2());
        setFaMiete        (source.getFaMiete());
        setFaStrom        (source.getFaStrom());
        setFaTelefon      (source.getFaTelefon());
        setFaKonto        (source.getFaKonto());
        setFaZeitung      (source.getFaZeitung());
        setFaVersicherung (source.getFaVersicherung());
        seterlMiete       (source.geterlMiete());
        seterlStrom       (source.geterlStrom());
        seterlTelefon     (source.geterlTelefon());
        seterlKonto       (source.geterlKonto());
        seterlZeitung     (source.geterlZeitung());
        seterlVersicherung  (source.geterlVersicherung());
        seterlRechnungen  (source.geterlRechnungen());
        seterlTVGeb       (source.geterlTVGeb());
        seterlSonstAus1   (source.geterlSonstAus1());
        seterlSonstAus2   (source.geterlSonstAus2());
        setKontostandNeu  (source.getKontostandNeu());
        setGehalt         (source.getGehalt());
        setGutschriften   (source.getGutschriften());
        setSonstEin1      (source.getSonstEin1());
        setSonstEin2      (source.getSonstEin2());
        setFaGehalt       (source.getFaGehalt());
        setFaGutschriften (source.getFaGutschriften());
        seterlGehalt       (source.geterlGehalt());
        seterlGutschriften  (source.geterlGutschriften());
        seterlSonstEin1   (source.geterlSonstEin1());
        seterlSonstEin2   (source.geterlSonstEin2());
        seterlBares       (source.geterlBares());
        setBares          (source.getBares());
        setTage           (source.getTage());
        setSatz           (source.getSatz());
        setDisplayedChild (source.getDisplayedChild());
        setAlles();
        Amount.setBlankzero(true);
    }

    public boolean equals(DagosPlan source) {
        //Log.i(MainActivity.LogTAG, " equals ");
        if (getKontostand().equals	  (source.getKontostand()) &&
                getMiete().equals         (source.getMiete()) &&
                getStrom().equals         (source.getStrom()) &&
                getTelefon().equals       (source.getTelefon()) &&
                getKonto().equals         (source.getKonto()) &&
                getZeitung().equals       (source.getZeitung()) &&
                getVersicherung().equals  (source.getVersicherung()) &&
                getRechnungen().equals    (source.getRechnungen()) &&
                getTVGeb().equals         (source.getTVGeb()) &&
                getSonstAus1().equals     (source.getSonstAus1()) &&
                getSonstAus2().equals     (source.getSonstAus2()) &&
                getFaMiete().equals       (source.getFaMiete()) &&
                getFaStrom().equals       (source.getFaStrom()) &&
                getFaTelefon().equals     (source.getFaTelefon()) &&
                getFaKonto().equals       (source.getFaKonto()) &&
                getFaZeitung().equals     (source.getFaZeitung()) &&
                getFaVersicherung().equals(source.getFaVersicherung()) &&
                geterlMiete().equals      (source.geterlMiete()) &&
                geterlStrom().equals      (source.geterlStrom()) &&
                geterlTelefon().equals    (source.geterlTelefon()) &&
                geterlKonto().equals      (source.geterlKonto()) &&
                geterlZeitung().equals    (source.geterlZeitung()) &&
                geterlVersicherung().equals (source.geterlVersicherung()) &&
                geterlRechnungen().equals (source.geterlRechnungen()) &&
                geterlTVGeb().equals      (source.geterlTVGeb()) &&
                geterlSonstAus1().equals  (source.geterlSonstAus1()) &&
                geterlSonstAus2().equals  (source.geterlSonstAus2()) &&
                getKontostandNeu().equals (source.getKontostandNeu()) &&
                getGehalt().equals        (source.getGehalt()) &&
                getGutschriften().equals  (source.getGutschriften()) &&
                getSonstEin1().equals     (source.getSonstEin1()) &&
                getSonstEin2().equals     (source.getSonstEin2()) &&
                getFaGehalt().equals      (source.getFaGehalt()) &&
                getFaGutschriften().equals(source.getFaGutschriften()) &&
                geterlGehalt().equals     (source.geterlGehalt()) &&
                geterlGutschriften().equals (source.geterlGutschriften()) &&
                geterlSonstEin1().equals  (source.geterlSonstEin1()) &&
                geterlSonstEin2().equals  (source.geterlSonstEin2()) &&
                geterlBares().equals      (source.geterlBares()) &&
                getBares().equals         (source.getBares()) &&
                getTage().equals          (source.getTage()) &&
                getSatz().equals          (source.getSatz()) &&
                getDisplayedChild().equals (source.getDisplayedChild())
        ) {
            return true;
        } else {
            return false;
        }
    }

    public void setKontostand (String s) {
        Kontostand = new Amount(s);
        setAlles();
    }

    public void setMiete (String s) {
        Miete = new Amount(s);
        setAlles();
    }

    public void setStrom (String s) {
        Strom = new Amount(s);
        setAlles();
    }

    public void setTelefon (String s) {
        Telefon = new Amount(s);
        setAlles();
    }

    public void setKonto (String s) {
        Konto = new Amount(s);
        setAlles();
    }

    public void setZeitung (String s) {
        Zeitung = new Amount(s);
        setAlles();
    }

    public void setVersicherung (String s) {
        Versicherung = new Amount(s);
        setAlles();
    }

    public void setRechnungen (String s) {
        Rechnungen = new Amount(s);
        setAlles();
    }

    public void setTVGeb (String s) {
        TVGeb = new Amount(s);
        setAlles();
    }

    public void setSonstAus1 (String s) {
        SonstAus1 = new Amount(s);
        setAlles();
    }

    public void setSonstAus2 (String s) {
        SonstAus2 = new Amount(s);
        setAlles();
    }

    public void setFaMiete(Integer i) {
        FaMiete = i;
        setAlles();
    }

    public void setFaStrom(Integer i) {
        FaStrom = i;
        setAlles();
    }

    public void setFaTelefon(Integer i) {
        FaTelefon = i;
        setAlles();
    }

    public void setFaKonto(Integer i) {
        FaKonto = i;
        setAlles();
    }

    public void setFaZeitung(Integer i) {
        FaZeitung = i;
        setAlles();
    }

    public void setFaVersicherung(Integer i) {
        FaVersicherung = i;
        setAlles();
    }

    public void setKontostandNeu (String s) {
        KontostandNeu = new Amount(s);
        setAlles();
    }

    public void setGehalt (String s) {
        Gehalt = new Amount(s);
        setAlles();
    }

    public void setGutschriften (String s) {
        Gutschriften = new Amount(s);
        setAlles();
    }

    public void setSonstEin1 (String s) {
        SonstEin1 = new Amount(s);
        setAlles();
    }

    public void setSonstEin2 (String s) {
        SonstEin2 = new Amount(s);
        setAlles();
    }

    public void setFaGehalt(Integer i) {
        FaGehalt = i;
        setAlles();
    }

    public void setBares(String s) {
        Bares = new Amount(s);
        setAlles();
    }

    public void setFaGutschriften(Integer i) {
        FaGutschriften = i;
        setAlles();
    }

    public void setTage(String s) {
        Tage = new Amount(s) ;
        setAlles();
    }

    public void setSatz(String s) {
        Satz = new Amount(s) ;
        setAlles();
    }

    public void setDisplayedChild(Integer i) {
        DisplayedChild = i;
        setAlles();
    }

    public void seterlMiete(String s) {
        erlMiete = s;
    }

    public void seterlStrom(String s) {
        erlStrom = s;
    }

    public void seterlTelefon(String s) {
        erlTelefon = s;
    }

    public void seterlKonto(String s) {
        erlKonto = s;
    }

    public void seterlZeitung(String s) {
        erlZeitung = s;
    }

    public void seterlVersicherung(String s) {
        erlVersicherung = s;
    }

    public void seterlRechnungen(String s) {
        erlRechnungen = s;
    }

    public void seterlTVGeb(String s) {
        erlTVGeb = s;
    }

    public void seterlSonstAus1(String s) {
        erlSonstAus1 = s;
    }

    public void seterlSonstAus2(String s) {
        erlSonstAus2 = s;
    }

    public void seterlGehalt(String s) {
        erlGehalt = s;
    }

    public void seterlGutschriften(String s) {
        erlGutschriften = s;
    }

    public void seterlSonstEin1(String s) {
        erlSonstEin1 = s;
    }

    public void seterlSonstEin2(String s) {
        erlSonstEin2 = s;
    }

    public void seterlBares(String s) {
        erlBares = s;
    }

    private void setAusgaben() {
        Double sum = (Miete.doubleValue() * FaMiete)
                + (Strom.doubleValue() * FaStrom)
                + (Telefon.doubleValue() * FaTelefon)
                + (Konto.doubleValue() * FaKonto)
                + (Zeitung.doubleValue() * FaZeitung)
                + (Versicherung.doubleValue() * FaVersicherung)
                + Rechnungen.doubleValue()
                + TVGeb.doubleValue() + SonstAus1.doubleValue() + SonstAus2.doubleValue();

        SummeAus = new Amount(sum);
    }

    private void setEinnahmen() {
        Double sum = (Gehalt.doubleValue() * FaGehalt)
                + (Gutschriften.doubleValue() * FaGutschriften)
                + SonstEin1.doubleValue() + SonstEin2.doubleValue();

        //SummeEin = new Amount(sum).setScale(2,BigDecimal.ROUND_HALF_UP);
        SummeEin = new Amount(sum);
    }

    public void setBedarf() {
        Bedarf = new Amount(Tage.doubleValue() * Satz.doubleValue());
    }

    public void setKopfhau() {
        Double sum = Kontostand.doubleValue()
                - SummeAus.doubleValue()
                + SummeEin.doubleValue()
                + Bares.doubleValue()
                - Bedarf.doubleValue()
                - KontostandNeu.doubleValue();

        Kopfhau = new Amount(sum);
    }

    public void setAlles() {
        setAusgaben();
        setEinnahmen();
        setBedarf();
        setKopfhau();
    }

    public String getKontostand () {
        return Kontostand.toString();
    }

    public String getMiete () {
        return Miete.toString();
    }

    public String getStrom () {
        return Strom.toString();
    }

    public String getTelefon () {
        return Telefon.toString();
    }

    public String getKonto () {
        return Konto.toString();
    }

    public String getZeitung () {
        return Zeitung.toString();
    }

    public String getVersicherung () {
        return Versicherung.toString();
    }

    public String getRechnungen () {
        return Rechnungen.toString();
    }

    public String getTVGeb () {
        return TVGeb.toString();
    }

    public String getSonstAus1 () {
        return SonstAus1.toString();
    }

    public String getSonstAus2 () {
        return SonstAus2.toString();
    }

    public String getKontostandNeu () {
        return KontostandNeu.toString();
    }

    public String getGehalt () {
        return Gehalt.toString();
    }

    public String getGutschriften () {
        return Gutschriften.toString();
    }

    public String getSonstEin1 () {
        return SonstEin1.toString();
    }

    public String getSonstEin2 () {
        return SonstEin2.toString();
    }

    public String getBares () {
        return Bares.toString();
    }

    public String getTage () {
        return Tage.toString();
    }

    public String getSatz () {
        return Satz.toString();
    }

    public String getBedarf () {
        return Bedarf.toString();
    }

    public String getKopfhau () {
        return Kopfhau.toString();
    }

    public String getSummeAus () {
        return SummeAus.toString();
    }

    public String getSummeEin () {
        return SummeEin.toString();
    }

    public Integer getFaMiete() {
        return(FaMiete);
    }

    public Integer getFaStrom() {
        return(FaStrom);
    }

    public Integer getFaTelefon() {
        return(FaTelefon);
    }

    public Integer getFaKonto() {
        return(FaKonto);
    }

    public Integer getFaZeitung() {
        return (FaZeitung);
    }

    public Integer getFaVersicherung() {
        return (FaVersicherung);
    }

    public Integer getFaGehalt() {
        return (FaGehalt);
    }

    public Integer getFaGutschriften() {
        return (FaGutschriften);
    }

    public String geterlMiete() {
        return (erlMiete);
    }

    public String geterlStrom() {
        return (erlStrom);
    }

    public String geterlTelefon() {
        return (erlTelefon);
    }

    public String geterlKonto() {
        return (erlKonto);
    }

    public String geterlZeitung() {
        return (erlZeitung);
    }

    public String geterlVersicherung() {
        return (erlVersicherung);
    }

    public String geterlRechnungen() {
        return (erlRechnungen);
    }

    public String geterlTVGeb() {
        return (erlTVGeb);
    }

    public String geterlSonstAus1() {
        return (erlSonstAus1);
    }

    public String geterlSonstAus2() {
        return (erlSonstAus2);
    }

    public String geterlGehalt() {
        return (erlGehalt);
    }

    public String geterlGutschriften() {
        return (erlGutschriften);
    }

    public String geterlSonstEin1() {
        return (erlSonstEin1);
    }

    public String geterlSonstEin2() {
        return (erlSonstEin2);
    }

    public String geterlBares() {
        return (erlBares);
    }

    public Integer getDisplayedChild() {
        return (DisplayedChild);
    }

    public void minus1() {
        Bares = new Amount(Bares.doubleValue() - Satz.doubleValue());
        Tage = new Amount (Tage.doubleValue()-1);
    }

}
