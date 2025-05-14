package yazlab1.pkg1;

import javax.swing.JPanel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.text.DecimalFormat;

public class TarifListesiFrame extends javax.swing.JFrame {

    private JTextField tarifAdField, minMaliyetField, maxMaliyetField, minMalzemeField;
    private JComboBox<String> kategoriComboBox, sortCriteriaComboBox, sortOrderComboBox;

    public TarifListesiFrame() {
        tarifController = new TarifController();
        setupComponents();
        loadTarifler();
    }

     private void setupComponents() {
        setTitle("Tarif Listesi");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        

        JMenuBar jMenuBar = new JMenuBar();
        JMenu jMenu1 = new JMenu("İşlemler");

        JMenuItem jMenuItem1 = new JMenuItem("Tarif Ekle");
        JMenuItem jMenuItem2 = new JMenuItem("Tarif Sil");
        JMenuItem jMenuItem3 = new JMenuItem("Tarif Güncelle");

        jMenuItem1.addActionListener(e -> {
            TarifEkleFrm tarifEkleFrm = new TarifEkleFrm(tarifController, this::loadTarifler);
            tarifEkleFrm.setVisible(true);
        });

        jMenuItem2.addActionListener(e -> {
            int selectedRow = JTable.getSelectedRow();
            if (selectedRow != -1) {
                int tarifID = (int) tableModel.getValueAt(selectedRow, 0);
                if (tarifController.deleteTarif(tarifID)) {
                    loadTarifler();
                } else {
                    JOptionPane.showMessageDialog(null, "Silme işlemi başarısız oldu.");
                }
            } else {
                JOptionPane.showMessageDialog(null, "Lütfen silmek için bir tarif seçin.");
            }
        });

        // Tarif Güncelle butonu
        /*
        jMenuItem3.addActionListener(e -> {
            int selectedRow = JTable.getSelectedRow();
            if (selectedRow != -1) {
                int tarifID = (int) tableModel.getValueAt(selectedRow, 0);
                String tarifAdi = JOptionPane.showInputDialog(null, "Yeni Tarif Adı:", tableModel.getValueAt(selectedRow, 1));
                String hazirlamaSuresi = JOptionPane.showInputDialog(null, "Yeni Hazırlama Süresi:", tableModel.getValueAt(selectedRow, 2));
                String kategori = JOptionPane.showInputDialog(null, "Yeni Kategori:", tableModel.getValueAt(selectedRow, 3));
                if (tarifAdi != null && hazirlamaSuresi != null && kategori != null) {
                    tarifController.updateTarif(tarifID, tarifAdi, hazirlamaSuresi, kategori, "");
                    loadTarifler();
                } else {
                    JOptionPane.showMessageDialog(null, "Lütfen tüm alanları doldurun.");
                }
            } else {
                JOptionPane.showMessageDialog(null, "Lütfen bir tarif seçin!");
            }
        });
         */
        jMenuItem3.addActionListener(e -> {
            int selectedRow = JTable.getSelectedRow();
            if (selectedRow != -1) {
                int tarifID = (int) tableModel.getValueAt(selectedRow, 0);
                TarifGuncelleFrm tarifGuncelleFrm = new TarifGuncelleFrm(tarifController, tarifID);
                tarifGuncelleFrm.setVisible(true);
            } else {
                JOptionPane.showMessageDialog(null, "Lütfen güncellemek için bir tarif seçin!");
            }
        });

        jMenu1.add(jMenuItem1);
        jMenu1.add(jMenuItem2);
        jMenu1.add(jMenuItem3);
        jMenuBar.add(jMenu1);
        setJMenuBar(jMenuBar);

        tableModel = new DefaultTableModel(new String[]{"ID", "Tarif Adı", "Hazırlama Süresi", "Maliyet", "Eşleşme Yüzdesi"}, 0);
        JTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(JTable);

        JPanel searchPanel = new JPanel(new GridLayout(2, 2));
        tarifAdField = new JTextField(15);
        JButton tarifAraButton = new JButton("Ara");
        tarifAraButton.addActionListener(e -> searchTarifler());

        malzemeTextField = new JTextField(15);
        JButton malzemeyeGoreAraButton = new JButton("Malzemeye Göre Ara");
        malzemeyeGoreAraButton.addActionListener(e -> searchByIngredients());

        searchPanel.add(new JLabel("Tarif Adı:"));
        searchPanel.add(tarifAdField);
        searchPanel.add(tarifAraButton);
        searchPanel.add(new JLabel("Malzeme:"));
        searchPanel.add(malzemeTextField);
        searchPanel.add(malzemeyeGoreAraButton);

        JPanel sortAndFilterPanel = new JPanel(new GridLayout(1, 6));
        sortCriteriaComboBox = new JComboBox<>(new String[]{"Hazırlama Süresi", "Tarif Maliyeti"});
        sortOrderComboBox = new JComboBox<>(new String[]{"Artan", "Azalan"});
        JButton sortButton = new JButton("Sırala");

        sortButton.addActionListener(e -> {
            String criteria = sortCriteriaComboBox.getSelectedItem().toString();
            boolean ascending = sortOrderComboBox.getSelectedItem().toString().equals("Artan");
            sortByCriteria(criteria, ascending);
        });

        kategoriComboBox = new JComboBox<>(new String[]{"Tümü", "Ana Yemek", "Tatlı", "Çorba", "Salata", "Aperatif"});
        minMaliyetField = new JTextField(5);
        maxMaliyetField = new JTextField(5);
        JButton filtreleButton = new JButton("Filtrele");

        filtreleButton.addActionListener(e -> filterTarifler(kategoriComboBox, minMaliyetField, maxMaliyetField));

        sortAndFilterPanel.add(new JLabel("Sıralama Kriteri:"));
        sortAndFilterPanel.add(sortCriteriaComboBox);
        sortAndFilterPanel.add(sortOrderComboBox);
        sortAndFilterPanel.add(sortButton);
        sortAndFilterPanel.add(new JLabel("Kategori:"));
        sortAndFilterPanel.add(kategoriComboBox);
        sortAndFilterPanel.add(new JLabel("Min Maliyet:"));
        sortAndFilterPanel.add(minMaliyetField);
        sortAndFilterPanel.add(new JLabel("Max Maliyet:"));
        sortAndFilterPanel.add(maxMaliyetField);
        sortAndFilterPanel.add(filtreleButton);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(searchPanel, BorderLayout.NORTH);
        mainPanel.add(sortAndFilterPanel, BorderLayout.SOUTH);

        add(mainPanel, BorderLayout.NORTH);

        JTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int selectedRow = JTable.getSelectedRow();
                if (selectedRow != -1) {
                    try {
                        int tarifID = Integer.parseInt(JTable.getValueAt(selectedRow, 0).toString());
                        Tarif selectedTarif = tarifController.getTarifById(tarifID);

                        List<Malzeme> malzemeler = tarifController.getTarifMalzemeleri(tarifID);

                        StringBuilder malzemeDetaylari = new StringBuilder();
                        double toplamMaliyet = 0;

                        for (Malzeme malzeme : malzemeler) {
                            double maliyet = malzeme.getMiktar() * malzeme.getBirimFiyat();
                            toplamMaliyet += maliyet;

                            malzemeDetaylari.append(malzeme.getMalzemeAdi())
                                    .append(" - Miktar: ").append(malzeme.getMiktar())
                                    .append(" ").append(malzeme.getBirim())
                                    .append(", Birim Fiyat: ").append(malzeme.getBirimFiyat())
                                    .append(", Toplam: ").append(String.format("%.2f", maliyet))
                                    .append(" TL\n");
                        }

                        String detaylar = "Tarif Adı: " + selectedTarif.getTarifAdi() + "\n"
                                + "Hazırlama Süresi: " + selectedTarif.getHazirlamaSuresi() + "\n"
                                + "Kategori: " + selectedTarif.getKategori() + "\n"
                                + "Talimatlar: " + selectedTarif.getTalimatlar() + "\n\n"
                                + "Malzemeler:\n" + malzemeDetaylari.toString()
                                + "\nToplam Maliyet: " + String.format("%.2f", toplamMaliyet) + " TL";
                        JOptionPane.showMessageDialog(null, detaylar, "Tarif Detayları", JOptionPane.INFORMATION_MESSAGE);
                    } catch (NumberFormatException ex) {
                        System.out.println("ID formatı hatalı: " + ex.getMessage());
                    }
                }
            }
        });

        add(scrollPane, BorderLayout.CENTER);

        setVisible(true);
    }

    private void searchTarifler() {
        String searchTerm = tarifAdField.getText().trim().toLowerCase();
        tableModel.setRowCount(0);
        List<Tarif> tarifler = tarifController.getAllTarifler();

        for (Tarif tarif : tarifler) {
            if (tarif.getTarifAdi().toLowerCase().contains(searchTerm)) {
                double maliyet = tarifController.getTarifMaliyeti(tarif.getId());
                tableModel.addRow(new Object[]{tarif.getId(), tarif.getTarifAdi(), tarif.getHazirlamaSuresi(), maliyet});
            }
        }

        if (tableModel.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "Eşleşen tarif bulunamadı.");
        }
    }

    private void sortByCriteria(String criteria, boolean ascending) {
        switch (criteria) {
            case "Hazırlama Süresi":
                sortByPreparationTime(ascending);
                break;
            case "Tarif Maliyeti":
                sortByCost(ascending);
                break;
        }
    }

    private void filterTarifler(JComboBox<String> kategoriComboBox, JTextField minMaliyetField, JTextField maxMaliyetField) {
        String selectedCategory = kategoriComboBox.getSelectedItem().toString();
        String minMaliyetText = minMaliyetField.getText().trim();
        String maxMaliyetText = maxMaliyetField.getText().trim();

        // Verileri kontrol et
        double minMaliyet = minMaliyetText.isEmpty() ? 0 : Double.parseDouble(minMaliyetText);
        double maxMaliyet = maxMaliyetText.isEmpty() ? Double.MAX_VALUE : Double.parseDouble(maxMaliyetText);

        List<Tarif> tarifler = tarifController.getAllTarifler();
        tableModel.setRowCount(0);

        for (Tarif tarif : tarifler) {
            double maliyet = tarifController.getTarifMaliyeti(tarif.getId());

            if ((selectedCategory.equals("Tümü") || tarif.getKategori().equalsIgnoreCase(selectedCategory))
                    && maliyet >= minMaliyet && maliyet <= maxMaliyet) {
                tableModel.addRow(new Object[]{tarif.getId(), tarif.getTarifAdi(), tarif.getHazirlamaSuresi(), maliyet});
            }
        }

        if (tableModel.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "Filtreye uygun tarif bulunamadı.");
        } else {
            JOptionPane.showMessageDialog(this, "Filtreleme işlemi tamamlandı.");
        }
    }

    private void searchByIngredients() {
        String girilenMalzemeMetni = malzemeTextField.getText().trim();

        if (girilenMalzemeMetni.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Lütfen bir malzeme girin!");
            return;
        }

        String[] girilenMalzemeler = girilenMalzemeMetni.split(",");
        List<Tarif> tarifler = tarifController.getAllTarifler();
        List<Object[]> tarifEslesmeListesi = new ArrayList<>();

        for (Tarif tarif : tarifler) {
            List<Malzeme> tarifMalzemeleri = tarifController.getTarifMalzemeleri(tarif.getId());
            double eslesmeYuzdesi = calculateEslesmeYuzdesi(girilenMalzemeler, tarifMalzemeleri);

            if (eslesmeYuzdesi > 0) {
                double maliyet = tarifController.getTarifMaliyeti(tarif.getId());
                tarifEslesmeListesi.add(new Object[]{tarif.getId(), tarif.getTarifAdi(), tarif.getHazirlamaSuresi(), maliyet, eslesmeYuzdesi});
            }
        }
        tarifEslesmeListesi.sort((a, b) -> Double.compare((double) b[4], (double) a[4]));

        tableModel.setRowCount(0);
        for (Object[] tarifData : tarifEslesmeListesi) {
            tableModel.addRow(tarifData);
        }

        if (tableModel.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "Eşleşen tarif bulunamadı.");
        } else {
            JOptionPane.showMessageDialog(this, "Malzemeye göre arama tamamlandı.");
        }
    }

    private double calculateEslesmeYuzdesi(String[] girilenMalzemeler, List<Malzeme> tarifMalzemeleri) {
        int eslesenMalzemeSayisi = 0;
        int tarifMalzemeSayisi = tarifMalzemeleri.size();

        for (Malzeme malzeme : tarifMalzemeleri) {
            if (Arrays.asList(girilenMalzemeler).contains(malzeme.getMalzemeAdi().trim().toLowerCase())) {
                eslesenMalzemeSayisi++;
            }
        }
        return ((double) eslesenMalzemeSayisi / tarifMalzemeSayisi) * 100;
    }

    private void searchTarifler(String searchTerm) {
        searchTerm = searchTerm.toLowerCase().trim();

        String selectedCategory = JComboBox.getSelectedItem().toString();

        List<Tarif> tarifler = tarifController.getAllTarifler();
        tableModel.setRowCount(0);

        for (Tarif tarif : tarifler) {
            if (tarif.getTarifAdi().toLowerCase().contains(searchTerm)
                    && (selectedCategory.equals("Tümü") || tarif.getKategori().equalsIgnoreCase(selectedCategory))) {

                double maliyet = tarifController.getTarifMaliyeti(tarif.getId());
                tableModel.addRow(new Object[]{tarif.getId(), tarif.getTarifAdi(), tarif.getHazirlamaSuresi(), maliyet});
            }
        }
        if (tableModel.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "Eşleşen tarif bulunamadı.");
        } else {
            JOptionPane.showMessageDialog(this, "Arama işlemi tamamlandı.");
        }
    }

    private void loadTarifler() {
        List<Tarif> tarifler = tarifController.getAllTarifler();
        tableModel.setRowCount(0);
        for (Tarif tarif : tarifler) {
            double maliyet = tarifController.getTarifMaliyeti(tarif.getId());
            tableModel.addRow(new Object[]{tarif.getId(), tarif.getTarifAdi(), tarif.getHazirlamaSuresi(), maliyet});
        }
    }

    //SIRALAMA İŞLEMLERİ
    private void sortByPreparationTime(boolean ascending) {
        List<Tarif> tarifler = tarifController.getAllTarifler();
        tarifler.sort((t1, t2) -> {
            int time1 = Integer.parseInt(t1.getHazirlamaSuresi().replaceAll("[^0-9]", ""));
            int time2 = Integer.parseInt(t2.getHazirlamaSuresi().replaceAll("[^0-9]", ""));
            return ascending ? Integer.compare(time1, time2) : Integer.compare(time2, time1);
        });
        loadSortedTarifler(tarifler);
    }

// maliyete göre 
    private void sortByCost(boolean ascending) {
        List<Tarif> tarifler = tarifController.getAllTarifler();
        tarifler.sort((t1, t2) -> {
            double cost1 = tarifController.getTarifMaliyeti(t1.getId());
            double cost2 = tarifController.getTarifMaliyeti(t2.getId());
            return ascending ? Double.compare(cost1, cost2) : Double.compare(cost2, cost1);
        });
        loadSortedTarifler(tarifler);
    }

// malzeme sayısına göre 
    private void sortByIngredientCount() {
        List<Tarif> tarifler = tarifController.getAllTarifler();
        tarifler.sort((t1, t2) -> Integer.compare(tarifController.getTarifMalzemeleri(t2.getId()).size(),
                tarifController.getTarifMalzemeleri(t1.getId()).size()));
        loadSortedTarifler(tarifler);
    }

//tabloya ekleme işlemi
    private void loadSortedTarifler(List<Tarif> sortedTarifler) {
        tableModel.setRowCount(0);
        for (Tarif tarif : sortedTarifler) {
            double maliyet = tarifController.getTarifMaliyeti(tarif.getId());
            tableModel.addRow(new Object[]{tarif.getId(), tarif.getTarifAdi(), tarif.getHazirlamaSuresi(), maliyet});
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        JTable = new javax.swing.JTable();
        JTextField = new javax.swing.JTextField();
        JComboBox = new javax.swing.JComboBox<>();
        JButton = new javax.swing.JButton();
        malzemeTextField = new javax.swing.JTextField();
        malzemeyeGoreAraButton = new javax.swing.JButton();
        JPanel = new javax.swing.JPanel();
        JMenuBar = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenu3 = new javax.swing.JMenu();
        jMenuItem1 = new javax.swing.JMenuItem();
        jMenuItem2 = new javax.swing.JMenuItem();
        jMenuItem3 = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        JTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane1.setViewportView(JTable);

        JTextField.setText("Arama Kutusu");

        JComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        JButton.setText("Ara");

        malzemeTextField.setText("malzeme giriş");

        malzemeyeGoreAraButton.setText("malzeme buton");
        malzemeyeGoreAraButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                malzemeyeGoreAraButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout JPanelLayout = new javax.swing.GroupLayout(JPanel);
        JPanel.setLayout(JPanelLayout);
        JPanelLayout.setHorizontalGroup(
            JPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );
        JPanelLayout.setVerticalGroup(
            JPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );

        jMenu1.setText("File");

        jMenu3.setText("İşlemler");

        jMenuItem1.setText("Tarif Ekle");
        jMenu3.add(jMenuItem1);

        jMenuItem2.setText("Tarif Sil");
        jMenuItem2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem2ActionPerformed(evt);
            }
        });
        jMenu3.add(jMenuItem2);

        jMenuItem3.setText("Tarif Güncelle");
        jMenuItem3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem3ActionPerformed(evt);
            }
        });
        jMenu3.add(jMenuItem3);

        jMenu1.add(jMenu3);

        JMenuBar.add(jMenu1);

        jMenu2.setText("Edit");
        JMenuBar.add(jMenu2);

        setJMenuBar(JMenuBar);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(45, 45, 45)
                        .addComponent(JTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 137, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(50, 50, 50)
                        .addComponent(JButton))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 378, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(99, 99, 99)
                                .addComponent(malzemeTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(89, 89, 89)
                                .addComponent(malzemeyeGoreAraButton))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(JPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(17, 17, 17))))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(56, 56, 56)
                        .addComponent(JComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(67, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 329, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(46, 46, 46)
                        .addComponent(JTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 99, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(56, 56, 56)
                        .addComponent(malzemeTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(35, 35, 35)
                        .addComponent(malzemeyeGoreAraButton)
                        .addGap(54, 54, 54)
                        .addComponent(JPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(JButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)))
                .addComponent(JComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(19, 19, 19))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents


    private void malzemeyeGoreAraButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_malzemeyeGoreAraButtonActionPerformed
        searchByIngredients(); // Malzemeye göre arama fonksiyonunu çağır
    }//GEN-LAST:event_malzemeyeGoreAraButtonActionPerformed

    private void jMenuItem2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jMenuItem2ActionPerformed

    private void jMenuItem3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem3ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jMenuItem3ActionPerformed

    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new TarifListesiFrame().setVisible(true);
            }
        });
    }
    private DefaultTableModel tableModel;
    private TarifController tarifController;

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton JButton;
    private javax.swing.JComboBox<String> JComboBox;
    private javax.swing.JMenuBar JMenuBar;
    private javax.swing.JPanel JPanel;
    private javax.swing.JTable JTable;
    private javax.swing.JTextField JTextField;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenu jMenu3;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JMenuItem jMenuItem3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextField malzemeTextField;
    private javax.swing.JButton malzemeyeGoreAraButton;
    // End of variables declaration//GEN-END:variables
}
