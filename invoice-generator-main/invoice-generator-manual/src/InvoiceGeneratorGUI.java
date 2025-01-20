import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class InvoiceGeneratorGUI extends JFrame {
    // Panels
    public JPanel mainPanel;
    public JPanel sellerPanel, buyerPanel, salePanel, soldPanel;
    public JScrollPane scrollPaneTable;

    // Seller info fields
    public static JTextField tfSellerName;
    public static JTextField tfSellerLocation;
    public static JTextField tfSellerPIB;
    public static JTextField tfSellerMIB;
    public static JTextField tfSellerAccount;
    public static JTextField tfInvoiceNumber;
    public static JTextField tfDocumentName;
    public JTextField tfWarehouseNumber;
    public static JButton btnSaveSeller = new JButton();
    public JButton btnLoadSeller = new JButton();

    // Buyer info fields
    public static JTextField tfBuyerName, tfBuyerLocation, tfBuyerPIB, tfBuyerMIB;

    // Sale info fields
    public static JTextField tfSaleDate;
    public static JTextField tfSaleLocation;
    public static JTextField tfTransactionDate;
    public static JTextField tfPaymentDate;
    public static JTextField tfPaymentMethod;

    // Product info fields
    public static JTextField tfProductID, tfProductName, tfProductQuantity, tfProductPrice, tfDiscount;
    public static JPanel panelCheckBox;
    public static JCheckBox chkbxPiece, chkbxCm2, chkbxM2, chkbxOther;
    public static JButton btnAddProduct, btnRemoveProduct;
    public static JTable tblProducts;

    public JButton btnGenerate;


    Font font = new Font("Arial", Font.PLAIN, 16);
    Font fontTitle = new Font("Arial", Font.BOLD, 20);

    public InvoiceGeneratorGUI() {
        UIManager.put("Label.font", font);
        UIManager.put("TextField.font", font);
        UIManager.put("Button.font", font);
        UIManager.put("Table.font", font);
        UIManager.put("TableHeader.font", font);

        setTitle("Generator Fakture");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 700);
        setLocationRelativeTo(null);

        mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        setupBuyerInfo();
        setupSaleInfo();
        setupSoldInfo();

        JScrollPane scrollMain = new JScrollPane(mainPanel);
        scrollMain.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollMain.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        add(scrollMain);
        setVisible(true);
    }

    private void setupSellerInfo(String dataPath) {
        sellerPanel = createPanel("Informacije o prodavcu");
        GridBagConstraints gbc = createGridBagConstraints();

        addLabelAndField(sellerPanel, gbc, "Ime prodavca:", tfSellerName = new JTextField(10), 0);
        addLabelAndField(sellerPanel, gbc, "Lokacija prodavca:", tfSellerLocation = new JTextField(10), 1);
        addLabelAndField(sellerPanel, gbc, "PIB:", tfSellerPIB = new JTextField(10), 2);
        addLabelAndField(sellerPanel, gbc, "MIB:", tfSellerMIB = new JTextField(10), 3);
        addLabelAndField(sellerPanel, gbc, "Račun:", tfSellerAccount = new JTextField(10), 4);

        mainPanel.add(sellerPanel);
    }

    private void setupBuyerInfo() {
        buyerPanel = createPanel("Informacije o kupcu");
        GridBagConstraints gbc = createGridBagConstraints();

        addLabelAndField(buyerPanel, gbc, "Ime kupca:", tfBuyerName = new JTextField(10), 0);
        addLabelAndField(buyerPanel, gbc, "Lokacija kupca:", tfBuyerLocation = new JTextField(10), 1);
        addLabelAndField(buyerPanel, gbc, "PIB:", tfBuyerPIB = new JTextField(10), 2);
        addLabelAndField(buyerPanel, gbc, "MIB:", tfBuyerMIB = new JTextField(10), 3);
        addLabelAndField(buyerPanel, gbc, "Broj fakture:", tfInvoiceNumber = new JTextField(10), 4);
        addLabelAndField(buyerPanel, gbc, "Ime fakture (dokumenta)", tfDocumentName = new JTextField(10), 5);

        mainPanel.add(buyerPanel);
    }

    private void setupSaleInfo() {
        salePanel = createPanel("Informacije o prodaji");
        GridBagConstraints gbc = createGridBagConstraints();

        addLabelAndField(salePanel, gbc, "Datum prodaje:", tfSaleDate = new JTextField(10), 0);
        addLabelAndField(salePanel, gbc, "Lokacija prodaje:", tfSaleLocation = new JTextField(10), 1);
        addLabelAndField(salePanel, gbc, "Datum transakcije:", tfTransactionDate = new JTextField(10), 2);
        addLabelAndField(salePanel, gbc, "Datum plačanja:", tfPaymentDate = new JTextField(10), 3);
        addLabelAndField(salePanel, gbc, "Metod plačanja:", tfPaymentMethod = new JTextField(10), 4);
        addLabelAndField(salePanel, gbc, "Broj skladišta:", tfWarehouseNumber = new JTextField(10), 5);

        mainPanel.add(salePanel);
    }

    private void setupSoldInfo() {
        soldPanel = createPanel("Prodate stavke");
        GridBagConstraints gbc = createGridBagConstraints();

        addLabelAndField(soldPanel, gbc, "ID proizvoda:", tfProductID = new JTextField(10), 0);
        addLabelAndField(soldPanel, gbc, "Ime proizvoda:", tfProductName = new JTextField(10), 1);
        addLabelAndField(soldPanel, gbc, "Količina:", tfProductQuantity = new JTextField(10), 2);
        addLabelAndField(soldPanel, gbc, "Cena (osnovica):", tfProductPrice = new JTextField(10), 3);
        addLabelAndField(soldPanel, gbc, "Rabat:", tfDiscount = new JTextField(10), 4);

        panelCheckBox = new JPanel(new FlowLayout(FlowLayout.LEFT));
        chkbxPiece = new JCheckBox("Komad");
        chkbxCm2 = new JCheckBox("cm²");
        chkbxM2 = new JCheckBox("m²");
        chkbxOther = new JCheckBox("Ostalo");
        panelCheckBox.add(chkbxPiece);
        panelCheckBox.add(chkbxCm2);
        panelCheckBox.add(chkbxM2);
        panelCheckBox.add(chkbxOther);
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        soldPanel.add(panelCheckBox, gbc);

        addButton(soldPanel, gbc, "Dodaj", btnAddProduct = new JButton(), 0, 6);
        addButton(soldPanel, gbc, "Ukloni", btnRemoveProduct = new JButton(), 1, 6);
        btnAddProduct.addActionListener(e -> InvoiceGeneratorMain.btnDodajActionPerformed());
        btnRemoveProduct.addActionListener(e -> InvoiceGeneratorMain.btnUkloniActionPerformed());

        DefaultTableModel tblModel = new DefaultTableModel(
                new String[]{"ID", "Artikal", "Jed. mere", "Kolicina", "Cena bez PDV", "Popust", "Cena - popust", "PDV", "Cena sa PDV", "Iznos"}, 0
        );
        tblProducts = new JTable(tblModel);
        tblProducts.setFillsViewportHeight(true);
        scrollPaneTable = new JScrollPane(tblProducts);
        scrollPaneTable.setPreferredSize(new Dimension(1300, 150));

        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.BOTH;
        soldPanel.add(scrollPaneTable, gbc);

        addButton(soldPanel, gbc, "Generiši", btnGenerate = new JButton(), 0, 8);
        btnGenerate.addActionListener(e -> InvoiceGeneratorMain.btnGenerisiActionPerformed());

        mainPanel.add(soldPanel);
    }

    private JPanel createPanel(String title) {

        JPanel panel = new JPanel(new GridBagLayout());
        TitledBorder border = BorderFactory.createTitledBorder(title);
        border.setTitleFont(fontTitle);
        panel.setBorder(border);
        return panel;
    }

    private GridBagConstraints createGridBagConstraints() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        return gbc;
    }

    private void addLabelAndField(JPanel panel, GridBagConstraints gbc, String labelText, JTextField field, int y) {
        gbc.gridx = 0;
        gbc.gridy = y;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(new JLabel(labelText), gbc);

        gbc.gridx = 1;
        panel.add(field, gbc);
    }

    private void addButton(JPanel panel, GridBagConstraints gbc, String btnText, JButton btn, int x, int y) {
        gbc.gridx = x;
        gbc.gridy = y;
        gbc.weightx = 0.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.CENTER;

        btn.setText(btnText);
        panel.add(btn, gbc);
    }
}
