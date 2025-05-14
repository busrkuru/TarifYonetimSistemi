package yazlab1.pkg1;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

public class TarifEkleFrm extends javax.swing.JFrame {

    private JTextField tarifAdiField, hazirlamaSuresiField, kategoriField, talimatlarField;
    private JPanel malzemeCheckBoxPanel;
    private TarifController tarifController;
    private Runnable onSave;
    private List<JCheckBox> malzemeCheckBoxList;  
    private List<JTextField> malzemeMiktarFieldList;  // miktarları tutmak için liste

    public TarifEkleFrm(TarifController controller, Runnable onSaveCallback) {
        this.tarifController = controller;
        this.onSave = onSaveCallback;

        setTitle("Tarif Ekleme Formu");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);  

        JPanel tarifBilgiPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        tarifBilgiPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        tarifBilgiPanel.add(new JLabel("Tarif Adı:"));
        tarifAdiField = new JTextField();
        tarifBilgiPanel.add(tarifAdiField);

        tarifBilgiPanel.add(new JLabel("Kategori:"));
        kategoriField = new JTextField();
        tarifBilgiPanel.add(kategoriField);

        tarifBilgiPanel.add(new JLabel("Hazırlama Süresi:"));
        hazirlamaSuresiField = new JTextField();
        tarifBilgiPanel.add(hazirlamaSuresiField);

        tarifBilgiPanel.add(new JLabel("Talimatlar:"));
        talimatlarField = new JTextField();
        tarifBilgiPanel.add(talimatlarField);

        // malzeme checkbox
        malzemeCheckBoxPanel = new JPanel();
        malzemeCheckBoxPanel.setBorder(BorderFactory.createTitledBorder("Malzemeler"));
        malzemeCheckBoxPanel.setLayout(new BoxLayout(malzemeCheckBoxPanel, BoxLayout.Y_AXIS));

        loadMalzemeler();  

        JButton yeniMalzemeEkleButton = new JButton("Yeni Malzeme Ekle");
        yeniMalzemeEkleButton.addActionListener(e -> yeniMalzemeEkleFormuAc());

        JButton kaydetButton = new JButton("Kaydet");
        kaydetButton.addActionListener(e -> tarifKaydet());

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        mainPanel.add(tarifBilgiPanel, BorderLayout.NORTH);
        mainPanel.add(new JScrollPane(malzemeCheckBoxPanel), BorderLayout.CENTER);
        mainPanel.add(kaydetButton, BorderLayout.SOUTH);
        mainPanel.add(yeniMalzemeEkleButton, BorderLayout.EAST);

        add(mainPanel);
        setVisible(true);
    }

    private void loadMalzemeler() {
        malzemeCheckBoxList = new ArrayList<>();
        malzemeMiktarFieldList = new ArrayList<>();  

        List<Malzeme> malzemeler = tarifController.getAllMalzemeler(); 
        for (Malzeme malzeme : malzemeler) {
            JPanel malzemePanel = new JPanel(new GridLayout(1, 2));

            JCheckBox checkBox = new JCheckBox(malzeme.getMalzemeAdi());
            JTextField miktarField = new JTextField(5);  
            malzemeCheckBoxList.add(checkBox);
            malzemeMiktarFieldList.add(miktarField);

            malzemePanel.add(checkBox);
            malzemePanel.add(new JLabel("Miktar: "));
            malzemePanel.add(miktarField);

            malzemeCheckBoxPanel.add(malzemePanel);  
        }
        malzemeCheckBoxPanel.revalidate();  
        malzemeCheckBoxPanel.repaint();
    }

    private void yeniMalzemeEkleFormuAc() {
        JFrame yeniMalzemeFrame = new JFrame("Yeni Malzeme Ekle");
        yeniMalzemeFrame.setSize(400, 300);
        yeniMalzemeFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        yeniMalzemeFrame.setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridLayout(4, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JTextField malzemeAdiField = new JTextField();
        JTextField miktarField = new JTextField(); 
        JTextField birimField = new JTextField();
        JTextField birimFiyatField = new JTextField();

        panel.add(new JLabel("Malzeme Adı:"));
        panel.add(malzemeAdiField);
        panel.add(new JLabel("Miktar:"));  
        panel.add(miktarField);
        panel.add(new JLabel("Birim:"));
        panel.add(birimField);
        panel.add(new JLabel("Birim Fiyat:"));
        panel.add(birimFiyatField);

        JButton kaydetButton = new JButton("Kaydet");
        kaydetButton.addActionListener(e -> {
            String malzemeAdi = malzemeAdiField.getText().trim();
            double miktar = Double.parseDouble(miktarField.getText().trim());  
            String birim = birimField.getText().trim();
            double birimFiyat = Double.parseDouble(birimFiyatField.getText().trim());

            tarifController.addNewMalzeme(malzemeAdi, miktar, birim, birimFiyat);

            JPanel malzemePanel = new JPanel(new GridLayout(1, 2));
            JCheckBox newCheckBox = new JCheckBox(malzemeAdi);
            JTextField yeniMiktarField = new JTextField(5); 

            malzemeCheckBoxList.add(newCheckBox);
            malzemeMiktarFieldList.add(yeniMiktarField);  

            malzemePanel.add(newCheckBox);
            malzemePanel.add(new JLabel("Miktar:"));
            malzemePanel.add(yeniMiktarField); 

            malzemeCheckBoxPanel.add(malzemePanel);
            malzemeCheckBoxPanel.revalidate();
            malzemeCheckBoxPanel.repaint();

            yeniMalzemeFrame.dispose(); 
        });

        yeniMalzemeFrame.add(panel, BorderLayout.CENTER);
        yeniMalzemeFrame.add(kaydetButton, BorderLayout.SOUTH);
        yeniMalzemeFrame.setVisible(true);
    }

    private void tarifKaydet() {
        String tarifAdi = tarifAdiField.getText().trim();
        String kategori = kategoriField.getText().trim();
        String hazirlamaSuresi = hazirlamaSuresiField.getText().trim();
        String talimatlar = talimatlarField.getText().trim();

        if (tarifAdi.isEmpty() || kategori.isEmpty() || hazirlamaSuresi.isEmpty() || talimatlar.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Lütfen tüm alanları doldurun.");
            return;
        }

        if (tarifController.isTarifExists(tarifAdi)) {
            JOptionPane.showMessageDialog(this, "Bu tarif zaten mevcut.");
            return;
        }

        int tarifId = tarifController.addNewTarif(tarifAdi, hazirlamaSuresi, kategori, talimatlar);

        List<Malzeme> seciliMalzemeler = new ArrayList<>();
        for (int i = 0; i < malzemeCheckBoxList.size(); i++) {
            JCheckBox checkBox = malzemeCheckBoxList.get(i);
            JTextField miktarField = malzemeMiktarFieldList.get(i);

            if (checkBox.isSelected()) {
                Malzeme malzeme = tarifController.getMalzemeByName(checkBox.getText());
                double miktar = 0;
                try {
                    miktar = Double.parseDouble(miktarField.getText().trim());
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Geçerli bir miktar girin.");
                    return;
                }

                malzeme.setMiktar(miktar);
                seciliMalzemeler.add(malzeme);
            }
        }

        tarifController.addNewTarifWithIngredients(tarifId, seciliMalzemeler); 

        onSave.run();
        dispose();
    }

    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> {
            TarifController controller = new TarifController();
            new TarifEkleFrm(controller, () -> {
             
            }).setVisible(true);
        });
    }

    


    /*
    @SuppressWarnings("unchecked")
    private void initComponents() {
        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );

        pack();
    }
     */
 /*
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> {
            TarifController controller = new TarifController();
            new TarifEkleFrm(controller, () -> {
                // Kaydetme işleminden sonra yapılacaklar (örneğin: tabloyu yenilemek)
            }).setVisible(true);
        });
    }
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     */

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
