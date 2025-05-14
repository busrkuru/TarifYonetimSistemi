package yazlab1.pkg1;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

public class TarifGuncelleFrm extends javax.swing.JFrame {

    private TarifController tarifController;
    private int tarifID;
    private JTextField tarifAdiField, kategoriField, hazirlamaSuresiField;
    private JTextArea talimatlarField;
    private JPanel malzemeCheckBoxPanel;
    private JButton kaydetButton, iptalButton;
    private List<JCheckBox> malzemeCheckBoxList;  
    private List<JTextField> malzemeMiktarFieldList;  
    private List<Malzeme> tumMalzemeler, tarifMalzemeleri;  

    public TarifGuncelleFrm(TarifController controller, int tarifID) {
        this.tarifController = controller;
        this.tarifID = tarifID;

        initComponents();
        loadTarifData();  
        loadMalzemeler(); 
    }

    private void initComponents() {
        setTitle("Tarif Güncelleme");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(null);

        JLabel tarifAdiLabel = new JLabel("Tarif Adı:");
        tarifAdiLabel.setBounds(20, 20, 100, 25);
        add(tarifAdiLabel);

        tarifAdiField = new JTextField();
        tarifAdiField.setBounds(150, 20, 200, 25);
        add(tarifAdiField);

        JLabel kategoriLabel = new JLabel("Kategori:");
        kategoriLabel.setBounds(20, 60, 100, 25);
        add(kategoriLabel);

        kategoriField = new JTextField();
        kategoriField.setBounds(150, 60, 200, 25);
        add(kategoriField);

        JLabel hazirlamaSuresiLabel = new JLabel("Hazırlama Süresi:");
        hazirlamaSuresiLabel.setBounds(20, 100, 100, 25);
        add(hazirlamaSuresiLabel);

        hazirlamaSuresiField = new JTextField();
        hazirlamaSuresiField.setBounds(150, 100, 200, 25);
        add(hazirlamaSuresiField);

        JLabel talimatlarLabel = new JLabel("Talimatlar:");
        talimatlarLabel.setBounds(20, 140, 100, 25);
        add(talimatlarLabel);

        talimatlarField = new JTextArea();
        talimatlarField.setBounds(150, 140, 200, 60);
        talimatlarField.setLineWrap(true);
        talimatlarField.setWrapStyleWord(true);
        add(talimatlarField);

        // Malzeme Checkbox Listesi
        malzemeCheckBoxPanel = new JPanel();
        malzemeCheckBoxPanel.setBorder(BorderFactory.createTitledBorder("Malzemeler"));
        malzemeCheckBoxPanel.setLayout(new BoxLayout(malzemeCheckBoxPanel, BoxLayout.Y_AXIS));
        JScrollPane malzemeScrollPane = new JScrollPane(malzemeCheckBoxPanel);
        malzemeScrollPane.setBounds(400, 20, 350, 400);  
        add(malzemeScrollPane);

        // Kaydet Butonu
        kaydetButton = new JButton("Kaydet");
        kaydetButton.setBounds(150, 500, 100, 30);
        add(kaydetButton);
        kaydetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                kaydetButtonActionPerformed(evt);
            }
        });

        iptalButton = new JButton("İptal");
        iptalButton.setBounds(260, 500, 100, 30);
        add(iptalButton);
        iptalButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                dispose();
            }
        });
    }

    private void loadTarifData() {
        Tarif tarif = tarifController.getTarifById(tarifID);
        if (tarif != null) {
            tarifAdiField.setText(tarif.getTarifAdi());
            kategoriField.setText(tarif.getKategori());
            hazirlamaSuresiField.setText(tarif.getHazirlamaSuresi());
            talimatlarField.setText(tarif.getTalimatlar());
        } else {
            JOptionPane.showMessageDialog(this, "Geçerli bir tarif bulunamadı.");
            dispose();
        }
    }

    private void loadMalzemeler() {
        malzemeCheckBoxList = new ArrayList<>();
        malzemeMiktarFieldList = new ArrayList<>();

        tumMalzemeler = tarifController.getAllMalzemeler(); 
        tarifMalzemeleri = tarifController.getTarifMalzemeleri(tarifID); 

        for (Malzeme malzeme : tumMalzemeler) {
            JPanel malzemePanel = new JPanel(new GridLayout(1, 3)); 

            JCheckBox checkBox = new JCheckBox(malzeme.getMalzemeAdi());
            JTextField miktarField = new JTextField(5); 
            JLabel birimLabel = new JLabel(malzeme.getBirim());  

            for (Malzeme tarifMalzeme : tarifMalzemeleri) {
                if (tarifMalzeme.getMalzemeAdi().equals(malzeme.getMalzemeAdi())) {
                    checkBox.setSelected(true);
                    miktarField.setText(String.valueOf(tarifMalzeme.getMiktar()));
                    break;
                }
            }

            malzemeCheckBoxList.add(checkBox);
            malzemeMiktarFieldList.add(miktarField);

            malzemePanel.add(checkBox);
            malzemePanel.add(new JLabel("Miktar:"));
            malzemePanel.add(miktarField);
            malzemePanel.add(birimLabel);  

            malzemeCheckBoxPanel.add(malzemePanel);
        }
        malzemeCheckBoxPanel.revalidate();
        malzemeCheckBoxPanel.repaint();
    }

    private void kaydetButtonActionPerformed(java.awt.event.ActionEvent evt) {
        String tarifAdi = tarifAdiField.getText().trim();
        String kategori = kategoriField.getText().trim();
        String hazirlamaSuresi = hazirlamaSuresiField.getText().trim();
        String talimatlar = talimatlarField.getText().trim();

        if (tarifAdi.isEmpty() || kategori.isEmpty() || hazirlamaSuresi.isEmpty() || talimatlar.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Lütfen tüm alanları doldurun.");
            return;
        }

        List<Malzeme> seciliMalzemeler = new ArrayList<>();  
        List<Malzeme> silinecekMalzemeler = new ArrayList<>();  
        List<Malzeme> eklenecekMalzemeler = new ArrayList<>();  

        for (int i = 0; i < malzemeCheckBoxList.size(); i++) {
            JCheckBox checkBox = malzemeCheckBoxList.get(i);
            JTextField miktarField = malzemeMiktarFieldList.get(i);

            if (checkBox.isSelected()) {
                 Malzeme malzeme = tarifController.getMalzemeByName(checkBox.getText());
                double miktar;
                try {
                    miktar = Double.parseDouble(miktarField.getText().trim());
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Geçerli bir miktar girin.");
                    return;
                }
                malzeme.setMiktar(miktar);

                 boolean alreadyInTarif = false;
                for (Malzeme tarifMalzeme : tarifMalzemeleri) {
                    if (tarifMalzeme.getMalzemeAdi().equals(malzeme.getMalzemeAdi())) {
                        if (tarifMalzeme.getMiktar() != miktar) {
                            tarifController.updateMalzemeMiktar(tarifID, malzeme);   
                        }
                        alreadyInTarif = true;
                        break;
                    }
                }
                if (!alreadyInTarif) {
                     eklenecekMalzemeler.add(malzeme);
                }

                seciliMalzemeler.add(malzeme);   
            } else {
                 for (Malzeme tarifMalzeme : tarifMalzemeleri) {
                    if (tarifMalzeme.getMalzemeAdi().equals(checkBox.getText())) {
                        silinecekMalzemeler.add(tarifMalzeme);
                        break;
                    }
                }
            }
        }

         for (Malzeme malzeme : silinecekMalzemeler) {
            tarifController.removeMalzemeFromTarif(tarifID, malzeme);
        }

        for (Malzeme malzeme : eklenecekMalzemeler) {
            tarifController.addMalzemeToTarif(tarifID, malzeme);
        }

        tarifController.updateTarif(tarifID, tarifAdi, hazirlamaSuresi, kategori, talimatlar, seciliMalzemeler);

        JOptionPane.showMessageDialog(this, "Tarif başarıyla güncellendi.");
        dispose();
    }
 
    
    @SuppressWarnings("unchecked")/*
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
*/
    public static void main(String args[]) {
        // Swing arayüzünü Event Dispatch Thread üzerinde çalıştırmak için invokeLater kullanılır.
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                // Örnek bir TarifController ve tarifID oluşturuyoruz
                TarifController tarifController = new TarifController();
                int tarifID = 1;  // Güncellemek istediğiniz tarifin ID'si

                // TarifGuncelleFrm formunu başlatıyoruz
                TarifGuncelleFrm form = new TarifGuncelleFrm(tarifController, tarifID);
                form.setVisible(true);  // Formun görünür hale getirilmesi
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
