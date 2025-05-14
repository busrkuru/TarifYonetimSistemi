package yazlab1.pkg1;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.PreparedStatement;

public class TarifDAO {

    private Connection conn;

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(
                "jdbc:sqlserver://localhost:1433;databaseName=TARIF_REHBERI;encrypt=false;trustServerCertificate=true;",
                "SA",
                "MyStrongPass123"
        );
    }

    private void closeConnection() throws SQLException {
        if (conn != null && !conn.isClosed()) {
            conn.close();
        }
    }

    public void updateTarif(Tarif tarif) {
        String query = "UPDATE Tarifler SET TarifAdi = ?, HazirlamaSuresi = ?, Kategori = ?, Talimatlar = ? WHERE TarifID = ?";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, tarif.getTarifAdi());
            pstmt.setString(2, tarif.getHazirlamaSuresi());
            pstmt.setString(3, tarif.getKategori());
            pstmt.setString(4, tarif.getTalimatlar());
            pstmt.setInt(5, tarif.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                closeConnection();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /*
    public void updateTarif(Tarif tarif) {
        String query = "UPDATE Tarifler SET TarifAdi = ?, HazirlamaSuresi = ?, Kategori = ?, Talimatlar = ? WHERE TarifID = ?";
        try (Connection conn = getConnection(); // Her seferinde bağlantı alıyoruz
                PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, tarif.getTarifAdi());
            pstmt.setString(2, tarif.getHazirlamaSuresi());
            pstmt.setString(3, tarif.getKategori());
            pstmt.setString(4, tarif.getTalimatlar());
            pstmt.setInt(5, tarif.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Tarif güncelleme hatası: " + e.getMessage());
        }
    }
     */
    public List<Tarif> getTarifler() {
        List<Tarif> tarifListesi = new ArrayList<>();
        try (Connection conn = getConnection()) {
            String query = "SELECT TarifID, TarifAdi, HazirlamaSuresi, Kategori, Talimatlar FROM Tarifler";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            while (rs.next()) {
                int id = rs.getInt("TarifID");
                String tarifAdi = rs.getString("TarifAdi");
                String hazirlamaSuresi = rs.getString("HazirlamaSuresi");
                String kategori = rs.getString("Kategori");
                String talimatlar = rs.getString("Talimatlar");

                double maliyet = getTarifMaliyeti(id);

                Tarif tarif = new Tarif(id, tarifAdi, hazirlamaSuresi, kategori, talimatlar);
                tarifListesi.add(tarif);

                System.out.println("Tarif: " + tarifAdi + ", Maliyet: " + maliyet);
            }
        } catch (SQLException e) {
            System.out.println("Veritabanı hatası: " + e.getMessage());
        }
        return tarifListesi;
    }

    public boolean deleteTarif(int tarifID) {
        Connection conn = null;
        try {
            conn = getConnection();
            conn.setAutoCommit(false);
            String deleteIliskilerQuery = "DELETE FROM TarifMalzemeIliskisi WHERE TarifID = ?";
            try (PreparedStatement pstmtIliskiler = conn.prepareStatement(deleteIliskilerQuery)) {
                pstmtIliskiler.setInt(1, tarifID);
                pstmtIliskiler.executeUpdate();
            }

            String deleteTarifQuery = "DELETE FROM Tarifler WHERE TarifID = ?";
            try (PreparedStatement pstmtTarif = conn.prepareStatement(deleteTarifQuery)) {
                pstmtTarif.setInt(1, tarifID);
                int affectedRows = pstmtTarif.executeUpdate();

                if (affectedRows > 0) {
                    conn.commit();
                    return true;
                } else {
                    conn.rollback();
                    return false;
                }
            }

        } catch (SQLException e) {
            System.out.println("Veritabanından silme hatası: " + e.getMessage());
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException rollbackEx) {
                    System.out.println("Rollback hatası: " + rollbackEx.getMessage());
                }
            }
            return false;
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException closeEx) {
                    System.out.println("Bağlantıyı kapatma hatası: " + closeEx.getMessage());
                }
            }
        }
    }

    public List<Tarif> searchTarifler(String searchTerm, String category) {
        List<Tarif> tarifListesi = new ArrayList<>();
        try (Connection conn = getConnection()) {
            String query = "SELECT TarifID, TarifAdi, HazirlamaSuresi, Kategori, Talimatlar FROM Tarifler WHERE TarifAdi LIKE ?";
            if (!category.equals("Tümü")) {
                query += " AND Kategori = ?";
            }

            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, "%" + searchTerm + "%");

            if (!category.equals("Tümü")) {
                pstmt.setString(2, category);
            }

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("TarifID");
                String tarifAdi = rs.getString("TarifAdi");
                String hazirlamaSuresi = rs.getString("HazirlamaSuresi");
                String kategori = rs.getString("Kategori");
                String talimatlar = rs.getString("Talimatlar");
                Tarif tarif = new Tarif(id, tarifAdi, hazirlamaSuresi, kategori, talimatlar);
                tarifListesi.add(tarif);
            }
        } catch (SQLException e) {
            System.out.println("Tarif arama hatası: " + e.getMessage());
        }
        return tarifListesi;
    }

    public List<Malzeme> getTarifMalzemeleri(int tarifID) {
        List<Malzeme> malzemeler = new ArrayList<>();
        String query = "SELECT m.MalzemeID, m.MalzemeAdi, tmi.MalzemeMiktar, m.MalzemeBirim, m.BirimFiyat "
                + "FROM Malzemeler m "
                + "JOIN TarifMalzemeIliskisi tmi ON m.MalzemeID = tmi.MalzemeID "
                + "WHERE tmi.TarifID = ?";

        try (Connection conn = getConnection();
                PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, tarifID);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                int malzemeId = rs.getInt("MalzemeID");
                String malzemeAdi = rs.getString("MalzemeAdi");
                double miktar = rs.getDouble("MalzemeMiktar");
                String birim = rs.getString("MalzemeBirim");
                double birimFiyat = rs.getDouble("BirimFiyat");

                Malzeme malzeme = new Malzeme(malzemeAdi, miktar, birim, birimFiyat, malzemeId);
                malzemeler.add(malzeme);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return malzemeler;
    }

    public Tarif getTarifById(int id) {
        Tarif tarif = null;
        String query = "SELECT TarifID, TarifAdi, HazirlamaSuresi, Kategori, Talimatlar FROM Tarifler WHERE TarifID = ?";
        try (Connection conn = getConnection();
                PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                String tarifAdi = rs.getString("TarifAdi");
                String hazirlamaSuresi = rs.getString("HazirlamaSuresi");
                String kategori = rs.getString("Kategori");
                String talimatlar = rs.getString("Talimatlar");
                tarif = new Tarif(id, tarifAdi, hazirlamaSuresi, kategori, talimatlar);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tarif;
    }

    public void updateMalzemeMiktar(int malzemeId, double miktar) {
        String query = "UPDATE Malzemeler SET ToplamMiktar = ToplamMiktar + ? WHERE MalzemeID = ?";
        try (Connection conn = getConnection();
                PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setDouble(1, miktar);
            pstmt.setInt(2, malzemeId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean isTarifExists(String tarifAdi) {
        String query = "SELECT COUNT(*) FROM Tarifler WHERE TarifAdi = ?";
        try (Connection conn = getConnection();
                PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, tarifAdi);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean isMalzemeExists(String malzemeAdi) {
        String query = "SELECT COUNT(*) FROM Malzemeler WHERE LOWER(MalzemeAdi) = LOWER(?)";
        try (Connection conn = getConnection();
                PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, malzemeAdi);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void updateTarif(int tarifId, String tarifAdi, String hazirlamaSuresi, String kategori, String talimatlar) {
        String sql = "UPDATE tarifler SET tarif_adi = ?, hazirlama_suresi = ?, kategori = ?, talimatlar = ? WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, tarifAdi);
            pstmt.setString(2, hazirlamaSuresi);
            pstmt.setString(3, kategori);
            pstmt.setString(4, talimatlar);
            pstmt.setInt(5, tarifId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Yeni malzeme ekler
    public int addMalzeme(Malzeme malzeme) {
        String query = "INSERT INTO Malzemeler (MalzemeAdi, ToplamMiktar, MalzemeBirim, BirimFiyat) OUTPUT INSERTED.MalzemeID VALUES (?, ?, ?, ?)";
        try (Connection conn = getConnection();
                PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, malzeme.getMalzemeAdi());
            pstmt.setDouble(2, malzeme.getToplamMiktar());
            pstmt.setString(3, malzeme.getBirim());
            pstmt.setDouble(4, malzeme.getBirimFiyat());
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public Malzeme getMalzemeByAdi(String malzemeAdi) {
        String query = "SELECT MalzemeID, MalzemeAdi, ToplamMiktar, MalzemeBirim, BirimFiyat FROM Malzemeler WHERE LOWER(MalzemeAdi) = LOWER(?)";
        try (Connection conn = getConnection();
                PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, malzemeAdi);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                int malzemeId = rs.getInt("MalzemeID");
                String malzemeAdiDb = rs.getString("MalzemeAdi");
                double toplamMiktar = rs.getDouble("ToplamMiktar");
                String birim = rs.getString("MalzemeBirim");
                double birimFiyat = rs.getDouble("BirimFiyat");
                return new Malzeme(malzemeAdiDb, toplamMiktar, birim, birimFiyat, malzemeId);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public int addTarif(Tarif tarif) {
        int tarifId = 0;
        String query = "INSERT INTO Tarifler (TarifAdi, HazirlamaSuresi, Kategori, Talimatlar) OUTPUT INSERTED.TarifID VALUES (?, ?, ?, ?)";
        try (Connection conn = getConnection();
                PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, tarif.getTarifAdi());
            pstmt.setString(2, tarif.getHazirlamaSuresi());
            pstmt.setString(3, tarif.getKategori());
            pstmt.setString(4, tarif.getTalimatlar());
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                tarifId = rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tarifId;
    }

    /*
    public void addTarifMalzemeIliskisi(int tarifId, int malzemeId, double miktar) {
        String query = "INSERT INTO TarifMalzemeIliskisi (TarifID, MalzemeID, Miktar) VALUES (?, ?, ?)";
        try (Connection conn = getConnection();
                PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, tarifId);
            pstmt.setInt(2, malzemeId);
            pstmt.setDouble(3, miktar);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
     */
    public int addNewTarif(Tarif tarif) {
        int tarifId = 0;
        String query = "INSERT INTO Tarifler (TarifAdi, HazirlamaSuresi, Kategori, Talimatlar) OUTPUT INSERTED.TarifID VALUES (?, ?, ?, ?)";
        try (Connection conn = getConnection();
                PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, tarif.getTarifAdi());
            pstmt.setString(2, tarif.getHazirlamaSuresi());
            pstmt.setString(3, tarif.getKategori());
            pstmt.setString(4, tarif.getTalimatlar());
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                tarifId = rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tarifId;
    }

    public void addTarifMalzemeIliskisi(int tarifId, int malzemeId, double miktar) {
        String query = "INSERT INTO TarifMalzemeIliskisi (TarifID, MalzemeID, MalzemeMiktar) VALUES (?, ?, ?)";
        try (Connection conn = getConnection();
                PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, tarifId);
            pstmt.setInt(2, malzemeId);
            pstmt.setDouble(3, miktar);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addNewMalzeme(String malzemeAdi, double miktar, String birim, double birimFiyat) {
        String query = "INSERT INTO Malzemeler (MalzemeAdi, ToplamMiktar, MalzemeBirim, BirimFiyat) VALUES (?, ?, ?, ?)";
        try (Connection conn = getConnection();
                PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, malzemeAdi);
            pstmt.setDouble(2, miktar);
            pstmt.setString(3, birim);
            pstmt.setDouble(4, birimFiyat);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Malzeme> getAllMalzemeler() {
        List<Malzeme> malzemeler = new ArrayList<>();
        String query = "SELECT MalzemeID, MalzemeAdi, ToplamMiktar, MalzemeBirim, BirimFiyat FROM Malzemeler";

        try (Connection conn = getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                int id = rs.getInt("MalzemeID");
                String malzemeAdi = rs.getString("MalzemeAdi");
                double miktar = rs.getDouble("ToplamMiktar");
                String birim = rs.getString("MalzemeBirim");
                double birimFiyat = rs.getDouble("BirimFiyat");

                Malzeme malzeme = new Malzeme(malzemeAdi, miktar, birim, birimFiyat, id);
                malzemeler.add(malzeme);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return malzemeler;
    }

    public void addNewTarifWithIngredients(int tarifId, List<Malzeme> malzemeler) {
        String query = "INSERT INTO TarifMalzemeIliskisi (TarifID, MalzemeID, MalzemeMiktar) VALUES (?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            for (Malzeme malzeme : malzemeler) {
                pstmt.setInt(1, tarifId);
                pstmt.setInt(2, malzeme.getMalzemeId());
                pstmt.setDouble(3, malzeme.getMiktar());
                pstmt.addBatch();
            }
            pstmt.executeBatch();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public double getTarifMaliyeti(int tarifId) {
        double toplamMaliyet = 0.0;
        String query = "SELECT SUM(m.BirimFiyat * tmi.MalzemeMiktar) AS ToplamMaliyet "
                + "FROM Malzemeler m "
                + "JOIN TarifMalzemeIliskisi tmi ON m.MalzemeID = tmi.MalzemeID "
                + "WHERE tmi.TarifID = ?";
        try (Connection conn = getConnection();
                PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, tarifId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                toplamMaliyet = rs.getDouble("ToplamMaliyet");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return toplamMaliyet;
    }

    public void updateMalzemeMiktar(int tarifId, Malzeme malzeme) {
        String sql = "UPDATE TarifMalzemeIliskisi SET miktar = ? WHERE tarif_id = ? AND malzeme_id = ?";

        try (Connection conn = getConnection();
                PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setDouble(1, malzeme.getMiktar());
            statement.setInt(2, tarifId);
            statement.setInt(3, malzeme.getMalzemeId());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addMalzemeToTarif(int tarifId, Malzeme malzeme) {
        String sql = "INSERT INTO TarifMalzemeIliskisi (tarif_id, malzeme_id, miktar) VALUES (?, ?, ?)";

        try (Connection conn = getConnection();
                PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setInt(1, tarifId);
            statement.setInt(2, malzeme.getMalzemeId());
            statement.setDouble(3, malzeme.getMiktar());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void removeMalzemeFromTarif(int tarifId, Malzeme malzeme) {
        String sql = "DELETE FROM TarifMalzemeIliskisi WHERE tarif_id = ? AND malzeme_id = ?";

        try (Connection conn = getConnection();
                PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setInt(1, tarifId);
            statement.setInt(2, malzeme.getMalzemeId());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Malzeme getMalzemeByName(String malzemeAdi) {
        String sql = "SELECT MalzemeID, MalzemeAdi, ToplamMiktar, MalzemeBirim, BirimFiyat FROM Malzemeler WHERE MalzemeAdi = ?";
        Malzeme malzeme = null;
        try (Connection conn = getConnection();
                PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setString(1, malzemeAdi);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                int id = rs.getInt("MalzemeID");
                String malzemeAdiDb = rs.getString("MalzemeAdi");
                double miktar = rs.getDouble("ToplamMiktar");
                String birim = rs.getString("MalzemeBirim");
                double birimFiyat = rs.getDouble("BirimFiyat");

                malzeme = new Malzeme(malzemeAdiDb, miktar, birim, birimFiyat, id);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return malzeme;
    }

    public void updateTarifMalzemeleri(int tarifId, List<Malzeme> seciliMalzemeler) {
        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false);
            String selectQuery = "SELECT MalzemeID FROM TarifMalzemeIliskisi WHERE TarifID = ?";
            PreparedStatement selectStmt = conn.prepareStatement(selectQuery);
            selectStmt.setInt(1, tarifId);
            ResultSet rs = selectStmt.executeQuery();

            List<Integer> mevcutMalzemeIds = new ArrayList<>();
            while (rs.next()) {
                mevcutMalzemeIds.add(rs.getInt("MalzemeID"));
            }
            String deleteQuery = "DELETE FROM TarifMalzemeIliskisi WHERE TarifID = ? AND MalzemeID = ?";
            PreparedStatement deleteStmt = conn.prepareStatement(deleteQuery);

            for (Integer malzemeId : mevcutMalzemeIds) {
                boolean malzemeBulundu = false;
                for (Malzeme seciliMalzeme : seciliMalzemeler) {
                    if (malzemeId == seciliMalzeme.getMalzemeId()) {
                        malzemeBulundu = true;
                        break;
                    }
                }
                if (!malzemeBulundu) {
                    deleteStmt.setInt(1, tarifId);
                    deleteStmt.setInt(2, malzemeId);
                    deleteStmt.addBatch(); 
                }
            }
            deleteStmt.executeBatch();

            String insertQuery = "INSERT INTO TarifMalzemeIliskisi (TarifID, MalzemeID, MalzemeMiktar) VALUES (?, ?, ?)";
            PreparedStatement insertStmt = conn.prepareStatement(insertQuery);

            for (Malzeme seciliMalzeme : seciliMalzemeler) {
                if (!mevcutMalzemeIds.contains(seciliMalzeme.getMalzemeId())) {
                    insertStmt.setInt(1, tarifId);
                    insertStmt.setInt(2, seciliMalzeme.getMalzemeId());
                    insertStmt.setDouble(3, seciliMalzeme.getMiktar()); 
                    insertStmt.addBatch(); 
                }
            }
            insertStmt.executeBatch(); 

            conn.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
