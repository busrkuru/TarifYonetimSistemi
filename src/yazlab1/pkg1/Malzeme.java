package yazlab1.pkg1;

public class Malzeme {
    private int malzemeId;  
    private String malzemeAdi;
    private double birimFiyat;
    private double miktar;
    private String birim;  
    
    
    public Malzeme(String malzemeAdi, double miktar, String birim, double birimFiyat, int malzemeId) {
        this.malzemeAdi = malzemeAdi;
        this.miktar = miktar;
        this.birim = birim;
        this.birimFiyat = birimFiyat;
        this.malzemeId = malzemeId;
    }
    
    public Malzeme(String malzemeAdi, double miktar, String birim, double birimFiyat) {
        this.malzemeAdi = malzemeAdi;
        this.miktar = miktar;
        this.birim = birim;
        this.birimFiyat = birimFiyat;
    }

    public Malzeme(String malzemeAdi, double birimFiyat) {
        this.malzemeAdi = malzemeAdi;
        this.birimFiyat = birimFiyat;
    }

    public int getMalzemeId() {
        return malzemeId;
    }

    public void setMalzemeId(int malzemeId) {
        this.malzemeId = malzemeId;
    }

    public String getMalzemeAdi() {
        return malzemeAdi;
    }

    public void setMalzemeAdi(String malzemeAdi) {
        this.malzemeAdi = malzemeAdi;
    }

    public double getBirimFiyat() {
        return birimFiyat;
    }

    public void setBirimFiyat(double birimFiyat) {
        this.birimFiyat = birimFiyat;
    }

    public double getMiktar() {
        return miktar;
    }

    public void setMiktar(double miktar) {
        this.miktar = miktar;
    }

    public String getBirim() {
        return birim;
    }

    public void setBirim(String birim) {
        this.birim = birim;
    }

    public double getToplamMiktar() {
        return this.miktar;  
    }

    @Override
    public String toString() {
        return malzemeAdi + " (Miktar: " + miktar + " " + birim + ", Birim Fiyat: " + birimFiyat + ")";
    }
    
}