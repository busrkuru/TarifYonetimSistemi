package yazlab1.pkg1;
public class Tarif {
    private int id;  
    private String tarifAdi;
    private String hazirlamaSuresi;
    private String kategori;  
    private String talimatlar;  

   
    public Tarif(int id, String tarifAdi, String hazirlamaSuresi, String kategori, String talimatlar) {
        this.id = id;
        this.tarifAdi = tarifAdi;
        this.hazirlamaSuresi = hazirlamaSuresi;
        this.kategori = kategori;
        this.talimatlar = talimatlar;
    }

    
    public Tarif(String tarifAdi, String hazirlamaSuresi, String kategori, String talimatlar) {
        this.tarifAdi = tarifAdi;
        this.hazirlamaSuresi = hazirlamaSuresi;
        this.kategori = kategori;
        this.talimatlar = talimatlar;
    }

   
    public int getId() {
        return id;
    }

    public String getTarifAdi() {
        return tarifAdi;
    }

    public String getHazirlamaSuresi() {
        return hazirlamaSuresi;
    }

    public String getKategori() {
        return kategori;
    }

    public String getTalimatlar() {
        return talimatlar;
    }

    }