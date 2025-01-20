// Model package
package com.invoice.model;

public class Invoice {
    private Buyer buyer;
    private Seller seller;
    private SaleInfo saleInfo;
    private List<Product> products = new ArrayList<>();
    private String invoiceNumber;
    private String documentName;
    private LocalDateTime generationDate;

    // Constructor, getters, setters
    public double calculateTotal() {
        return products.stream()
                .mapToDouble(Product::calculateTotal)
                .sum();
    }
}

public class Product {
    private int id;
    private String name;
    private UnitType unit;
    private double quantity;
    private double basePrice;
    private double discount;
    
    public double calculateTotal() {
        double priceAfterDiscount = basePrice * (1 - discount/100);
        double priceWithVAT = priceAfterDiscount * (1 + ConfigConstants.VAT_RATE);
        return priceWithVAT * quantity;
    }
}

public enum UnitType {
    PIECE("Kom"),
    CM2("cm²"),
    M2("m²"),
    OTHER("Ostalo");

    private final String display;
    
    UnitType(String display) {
        this.display = display;
    }
}

public class Buyer {
    private String name;
    private String location;
    private String pib;
    private String mib;
}

public class Seller {
    private String name;
    private String location;
    private String pib;
    private String mib;
    private String accountNumber;
}

public class SaleInfo {
    private LocalDate saleDate;
    private String location;
    private LocalDate transactionDate;
    private LocalDate paymentDate;
    private String paymentMethod;
    private String warehouseNumber;
}

// Service package
package com.invoice.service;

public interface ValidationService {
    void validateProduct(Product product) throws ValidationException;
    void validateInvoice(Invoice invoice) throws ValidationException;
}

public class ValidationServiceImpl implements ValidationService {
    @Override
    public void validateProduct(Product product) throws ValidationException {
        if (product.getName().trim().isEmpty()) {
            throw new ValidationException("Unesite ime proizvoda.");
        }
        if (product.getId() < 0) {
            throw new ValidationException("Unesite validan ID proizvoda.");
        }
        if (product.getQuantity() <= 0) {
            throw new ValidationException("Unesite validnu količinu proizvoda.");
        }
        if (product.getBasePrice() <= 0) {
            throw new ValidationException("Unesite validnu cenu proizvoda.");
        }
        if (product.getDiscount() < 0 || product.getDiscount() > 100) {
            throw new ValidationException("Unesite validan rabat (0% - 100%).");
        }
    }
}

public class PDFGeneratorService {
    public void generatePDF(Invoice invoice) throws IOException {
        String path = getDesktopPath(invoice.getDocumentName());
        try (PdfWriter writer = new PdfWriter(path)) {
            PdfDocument pdfDoc = new PdfDocument(writer);
            Document document = new Document(pdfDoc);
            
            addHeader(document, invoice);
            addBuyerInfo(document, invoice.getBuyer());
            addProductsTable(document, invoice.getProducts());
            addTotals(document, invoice);
            addSignatures(document);
        }
    }

    private void addHeader(Document document, Invoice invoice) {
        // Current header generation logic
    }
    // Other PDF generation methods
}

// UI package
package com.invoice.ui;

public class InvoiceGeneratorGUI extends JFrame {
    private final InvoiceService invoiceService;
    private final ValidationService validationService;
    private final ProductPanel productPanel;
    private final BuyerPanel buyerPanel;
    private final SaleInfoPanel saleInfoPanel;

    public InvoiceGeneratorGUI(InvoiceService invoiceService, 
                              ValidationService validationService) {
        this.invoiceService = invoiceService;
        this.validationService = validationService;
        
        setTitle("Generator Fakture");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 700);
        
        initializePanels();
        layoutComponents();
    }
}

public class ProductPanel extends JPanel {
    private final JTextField tfProductId;
    private final JTextField tfProductName;
    private final JTextField tfQuantity;
    private final JTextField tfPrice;
    private final JTextField tfDiscount;
    private final ButtonGroup unitGroup;
    private final ProductTableModel tableModel;
    
    public Product getProduct() throws ValidationException {
        Product product = new Product();
        // Fill product from fields
        return product;
    }
}

// Config package
package com.invoice.config;

public class ConfigConstants {
    public static final String COMPANY_NAME = "KERAMIKA BATO DOO";
    public static final String COMPANY_DETAILS = "MIB: 21975826, PIB: 114129861";
    public static final double VAT_RATE = 0.2;
    public static final String DEFAULT_CURRENCY = "RSD";
}

// Main class
package com.invoice;

public class InvoiceGeneratorApplication {
    public static void main(String[] args) {
        ValidationService validationService = new ValidationServiceImpl();
        PDFGeneratorService pdfService = new PDFGeneratorService();
        InvoiceService invoiceService = new InvoiceServiceImpl(validationService, pdfService);
        
        SwingUtilities.invokeLater(() -> {
            InvoiceGeneratorGUI gui = new InvoiceGeneratorGUI(invoiceService, validationService);
            gui.setVisible(true);
        });
    }
}
