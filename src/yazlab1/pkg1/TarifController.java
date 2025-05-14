package yazlab1.pkg1;

import java.util.List;

public class TarifController {

    private TarifDAO tarifDAO;

    public TarifController() {
        tarifDAO = new TarifDAO();
    }

    public List<Tarif> getAllTarifler() {
        return tarifDAO.getTarifler();
    }

    /*
    public void addNewTarif(String tarifAdi, String hazirlamaSuresi, String kategori, String talimatlar) {
        Tarif tarif = new Tarif(tarifAdi, hazirlamaSuresi, kategori, talimatlar);
        tarifDAO.addTarif(tarif);
    }
     */
    public void updateTarif(int id, String tarifAdi, String hazirlamaSuresi, String kategori, String talimatlar) {
        Tarif tarif = new Tarif(id, tarifAdi, hazirlamaSuresi, kategori, talimatlar);
        tarifDAO.updateTarif(tarif);
    }

    public boolean deleteTarif(int tarifID) {
        return tarifDAO.deleteTarif(tarifID);
    }

    public double getTarifMaliyeti(int tarifID) {
        return tarifDAO.getTarifMaliyeti(tarifID);
    }

    /*
    public List<String> getTarifMalzemeleri(int tarifID) {
        return tarifDAO.getTarifMalzemeleri(tarifID);  
    }
    
    public List<String> getTarifMalzemeleri(int tarifID) {
    return tarifDAO.getTarifMalzemeleri(tarifID); 
}
     */
    public List<Malzeme> getTarifMalzemeleri(int tarifID) {
        return tarifDAO.getTarifMalzemeleri(tarifID);
    }

    public Tarif getTarifById(int id) {
        return tarifDAO.getTarifById(id);
    }

    
    // tarif var mı
    public boolean isTarifExists(String tarifAdi) {
        return tarifDAO.isTarifExists(tarifAdi);
    }

    // malzeme var mı
    public boolean isMalzemeExists(String malzemeAdi) {
        return tarifDAO.isMalzemeExists(malzemeAdi);
    }

    
    public void addNewMalzeme(String malzemeAdi, double miktar, String birim, double birimFiyat) {
        tarifDAO.addMalzeme(new Malzeme(malzemeAdi, miktar, birim, birimFiyat));
    }

    
    public Malzeme getMalzemeByName(String malzemeAdi) {
        return tarifDAO.getMalzemeByAdi(malzemeAdi);
    }

    public void addNewTarifWithIngredients(String tarifAdi, String hazirlamaSuresi, String kategori, String talimatlar, List<String> mevcutMalzemeler, List<String> yeniMalzemeListesi) {
        int tarifId = tarifDAO.addNewTarif(new Tarif(tarifAdi, hazirlamaSuresi, kategori, talimatlar));

        for (String malzeme : mevcutMalzemeler) {
            String[] data = malzeme.split(":");
            String malzemeAdi = data[0];
            double miktar = Double.parseDouble(data[1]);  
            Malzeme malzemeObj = tarifDAO.getMalzemeByAdi(malzemeAdi);

            if (malzemeObj != null) {
                tarifDAO.addTarifMalzemeIliskisi(tarifId, malzemeObj.getMalzemeId(), miktar);
            } else {
                System.out.println("Malzeme bulunamadı: " + malzemeAdi);
            }
        }

        for (String malzeme : yeniMalzemeListesi) {
            String[] data = malzeme.split(":");
            String malzemeAdi = data[0];
            double miktar = Double.parseDouble(data[1]);
            double birimFiyat = Double.parseDouble(data[2]);

            Malzeme yeniMalzeme = new Malzeme(malzemeAdi, miktar, "birim", birimFiyat);
            int yeniMalzemeId = tarifDAO.addMalzeme(yeniMalzeme);

            tarifDAO.addTarifMalzemeIliskisi(tarifId, yeniMalzemeId, miktar);
        }
    }

    public int addNewTarif(String tarifAdi, String hazirlamaSuresi, String kategori, String talimatlar) {
        Tarif tarif = new Tarif(tarifAdi, hazirlamaSuresi, kategori, talimatlar);
        return tarifDAO.addTarif(tarif);  
    }

    public void addNewTarifWithIngredients(int tarifId, List<Malzeme> malzemeler) {
        for (Malzeme malzeme : malzemeler) {
            if (malzeme == null) {
                System.out.println("Malzeme null, işlem yapılamıyor");
                continue;
            }

            System.out.println("Malzeme ID: " + malzeme.getMalzemeId());
            System.out.println("Malzeme Toplam Miktar: " + malzeme.getToplamMiktar());

            tarifDAO.addTarifMalzemeIliskisi(tarifId, malzeme.getMalzemeId(), malzeme.getToplamMiktar());
        }
    }

    public List<Malzeme> getAllMalzemeler() {
        return tarifDAO.getAllMalzemeler();  
    }

    public void updateTarifMalzemeleri(int tarifId, List<Malzeme> yeniMalzemeler) {
        List<Malzeme> mevcutMalzemeler = tarifDAO.getTarifMalzemeleri(tarifId);

        for (Malzeme mevcutMalzeme : mevcutMalzemeler) {
            boolean malzemeBulundu = false;
            for (Malzeme yeniMalzeme : yeniMalzemeler) {
                if (mevcutMalzeme.getMalzemeId() == yeniMalzeme.getMalzemeId()) {
                    malzemeBulundu = true;
                    if (mevcutMalzeme.getMiktar() != yeniMalzeme.getMiktar()) {
                        tarifDAO.updateMalzemeMiktar(tarifId, yeniMalzeme); 
                    }
                    break;
                }
            }

            if (!malzemeBulundu) {
                tarifDAO.removeMalzemeFromTarif(tarifId, mevcutMalzeme); 
            }
        }

        for (Malzeme yeniMalzeme : yeniMalzemeler) {
            boolean malzemeBulundu = false;
            for (Malzeme mevcutMalzeme : mevcutMalzemeler) {
                if (yeniMalzeme.getMalzemeId() == mevcutMalzeme.getMalzemeId()) {
                    malzemeBulundu = true;
                    break;
                }
            }

            if (!malzemeBulundu) {
                tarifDAO.addMalzemeToTarif(tarifId, yeniMalzeme); 
            }
        }
    }

    public void updateMalzemeMiktar(int tarifId, Malzeme malzeme) {
        tarifDAO.updateMalzemeMiktar(tarifId, malzeme);  
    }

    public void addMalzemeToTarif(int tarifId, Malzeme malzeme) {
        tarifDAO.addMalzemeToTarif(tarifId, malzeme); 
    }

    public void removeMalzemeFromTarif(int tarifId, Malzeme malzeme) {
        tarifDAO.removeMalzemeFromTarif(tarifId, malzeme);  
    }

    
    public void updateTarif(int id, String tarifAdi, String hazirlamaSuresi, String kategori, String talimatlar, List<Malzeme> seciliMalzemeler) {
        Tarif tarif = new Tarif(id, tarifAdi, hazirlamaSuresi, kategori, talimatlar);
        tarifDAO.updateTarif(tarif);
        tarifDAO.updateTarifMalzemeleri(id, seciliMalzemeler); 
    }
    

}
