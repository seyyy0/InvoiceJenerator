import com.itextpdf.kernel.color.Color;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.border.Border;
import com.itextpdf.layout.border.SolidBorder;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.TextAlignment;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class InvoiceGeneratorMain extends Component {

    public static void btnDodajActionPerformed() {
        try {
            String mera = "---";

            if (InvoiceGeneratorGUI.tfProductName.getText().trim().isEmpty()) {
                throw new IllegalArgumentException("Unesite ime proizvoda.");
            }
            if (InvoiceGeneratorGUI.tfProductID.getText().trim().isEmpty() || Integer.parseInt(InvoiceGeneratorGUI.tfProductID.getText()) < 0) {
                throw new IllegalArgumentException("Unesite validan ID proizvoda.");
            }
            if (InvoiceGeneratorGUI.tfProductQuantity.getText().trim().isEmpty() || Double.parseDouble(InvoiceGeneratorGUI.tfProductQuantity.getText()) < 0) {
                throw new IllegalArgumentException("Unesite validnu količinu proizvoda.");
            }
            if (InvoiceGeneratorGUI.tfProductPrice.getText().trim().isEmpty() || Double.parseDouble(InvoiceGeneratorGUI.tfProductPrice.getText()) < 0) {
                throw new IllegalArgumentException("Unesite validnu cenu proizvoda.");
            }
            if (InvoiceGeneratorGUI.tfDiscount.getText().trim().isEmpty() || Double.parseDouble(InvoiceGeneratorGUI.tfDiscount.getText()) < 0) {
                throw new IllegalArgumentException("Unesite validan rabat.");
            }

            int selectedCount = 0;
            if (InvoiceGeneratorGUI.chkbxPiece.isSelected()) { mera = "Kom"; selectedCount++; }
            if (InvoiceGeneratorGUI.chkbxCm2.isSelected()) { mera = "cm²"; selectedCount++; }
            if (InvoiceGeneratorGUI.chkbxM2.isSelected()) { mera = "m²"; selectedCount++; }
            if (InvoiceGeneratorGUI.chkbxOther.isSelected()) { mera = "Ostalo"; selectedCount++; }
            if (selectedCount != 1) {
                throw new IllegalArgumentException("Izaberite tačno jednu jedinicu mere.");
            }

            String ime = InvoiceGeneratorGUI.tfProductName.getText().trim();
            int id = Integer.parseInt(InvoiceGeneratorGUI.tfProductID.getText());
            double kolicina = Double.parseDouble(InvoiceGeneratorGUI.tfProductQuantity.getText());
            double cena = Double.parseDouble(InvoiceGeneratorGUI.tfProductPrice.getText());
            double popust = Double.parseDouble(InvoiceGeneratorGUI.tfDiscount.getText());
            if (popust > 100) {
                throw new NumberFormatException("Unesite validan rabat (0% - 100%)");
            }

            double cenaSaPopustom = cena - (cena * popust / 100);
            double pdv = cenaSaPopustom * 0.2;
            double cenaSaPdv = cenaSaPopustom + pdv;
            double ukupno = cenaSaPdv * kolicina;

            String[] proizvod = {
                    Integer.toString(id),
                    ime,
                    mera,
                    Double.toString(kolicina),
                    String.format("%.2f", cena),
                    String.format("%.2f", popust),
                    String.format("%.2f", cenaSaPopustom),
                    String.format("%.2f", pdv),
                    String.format("%.2f", cenaSaPdv),
                    String.format("%.2f", ukupno)
            };

            InvoiceGeneratorGUI.tfProductName.setText("");
            InvoiceGeneratorGUI.tfProductID.setText("");
            InvoiceGeneratorGUI.tfProductQuantity.setText("");
            InvoiceGeneratorGUI.tfProductPrice.setText("");

            DefaultTableModel model = (DefaultTableModel) InvoiceGeneratorGUI.tblProducts.getModel();
            model.addRow(proizvod);

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(null, "Unesite validne numeričke vrednosti u sva polja brojeva!", "Greška", JOptionPane.ERROR_MESSAGE);
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage(), "Greška", JOptionPane.WARNING_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Došlo je do neočekivane greške: " + ex.getMessage(), "Greška", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    public static void btnUkloniActionPerformed() {
        DefaultTableModel model = (DefaultTableModel) InvoiceGeneratorGUI.tblProducts.getModel();
        if (InvoiceGeneratorGUI.tblProducts.getSelectedRowCount() == 1) {
            model.removeRow(InvoiceGeneratorGUI.tblProducts.getSelectedRow());
        } else {
            if (InvoiceGeneratorGUI.tblProducts.getRowCount() == 0) {
                JOptionPane.showMessageDialog(null, "Tabela je prazna.");
            } else {
                JOptionPane.showMessageDialog(null, "Izaberite proizvod za uklonjavanje.");
            }
        }
    }

    public static void btnGenerisiActionPerformed() {
        try {

            // Getting text from fields
            String buyerName = InvoiceGeneratorGUI.tfBuyerName.getText().trim();
            String buyerLocation = InvoiceGeneratorGUI.tfBuyerLocation.getText().trim();
            String buyerPIB = InvoiceGeneratorGUI.tfBuyerPIB.getText().trim();
            String buyerMIB = InvoiceGeneratorGUI.tfBuyerMIB.getText().trim();
            String invoiceNumber = InvoiceGeneratorGUI.tfInvoiceNumber.getText().trim();
            String documentName = InvoiceGeneratorGUI.tfDocumentName.getText().trim();

            String saleDate = InvoiceGeneratorGUI.tfSaleDate.getText().trim();
            String saleLocation = InvoiceGeneratorGUI.tfSaleLocation.getText().trim();
            String transactionDate = InvoiceGeneratorGUI.tfTransactionDate.getText().trim();
            String paymentDate = InvoiceGeneratorGUI.tfPaymentDate.getText().trim();
            String paymentMethod = InvoiceGeneratorGUI.tfPaymentMethod.getText().trim();

            DefaultTableModel model = (DefaultTableModel) InvoiceGeneratorGUI.tblProducts.getModel();

            ArrayList<String> proizvod = new ArrayList<>();

            for (int i = 0; i < model.getRowCount(); i++) {
                for (int j = 0; j < model.getColumnCount(); j++) {
                    proizvod.add(model.getValueAt(i, j).toString());
                }
            }

            System.out.println(proizvod);
            System.out.println("duzina" + proizvod.size());

            // iText Document setup
            String desktopPath;
            String os = System.getProperty("os.name").toLowerCase();
            if (os.contains("win")) {
                desktopPath = System.getProperty("user.home") + "\\Desktop\\" + documentName + ".pdf"; // Windows
            } else {
                desktopPath = System.getProperty("user.home") + "/Desktop/" + documentName + ".pdf"; // macOS/Linux
            }
            PdfWriter pdfWriter = new PdfWriter(desktopPath);
            PdfDocument pdfDocument = new PdfDocument(pdfWriter);
            pdfDocument.setDefaultPageSize(PageSize.A4);
            Document document = new Document(pdfDocument);

            // Header table setup/widths
            float threecol = 190f;
            float twocol = 285f;
            float twocol150 = twocol + 400f;
            float[] twoColWidth = {twocol150, twocol};
            float[] fullWidth = {threecol * 3};
            Table headerTable = new Table(twoColWidth);

            Cell prviCell = new Cell().setBorder(Border.NO_BORDER);
            prviCell.add(new Cell().add("IME KOMPANIJE").setFontSize(20f).setBorder(Border.NO_BORDER).setBold());
            prviCell.add(new Cell().add("MIB: 00000000, PIB: 00000000, \nTekuci racun: 123-123-123, \nAdresa, Adresa, \nemail: email@gmail.com").setFontSize(10f).setBorder(Border.NO_BORDER));
            headerTable.addCell(prviCell);
            Table nestedTable = new Table(new float[]{twocol / 2, twocol / 2 - 100f});
            nestedTable.addCell(new Cell().add("\n").setFontSize(10f).setBorder(Border.NO_BORDER));
            nestedTable.addCell(new Cell().add("\n\n").setFontSize(10f).setBorder(Border.NO_BORDER));
            nestedTable.addCell(getHeaderTextCell("Naziv dokumenta: ").setFontSize(11f));
            nestedTable.addCell(getHeaderTextCellValue(documentName).setFontSize(11f).setBorder(Border.NO_BORDER));
            nestedTable.addCell(getHeaderTextCell("Broj fakture: ").setFontSize(11f));
            nestedTable.addCell(getHeaderTextCellValue(invoiceNumber).setFontSize(11f).setBorder(Border.NO_BORDER));
            nestedTable.addCell(getHeaderTextCell("Datum fakture: ").setFontSize(11f));
            DateTimeFormatter fmtr = DateTimeFormatter.ofPattern("dd.MM.yyyy");
            LocalDateTime now = LocalDateTime.now();
            nestedTable.addCell(getHeaderTextCellValue(fmtr.format(now)).setFontSize(12f).setBorder(Border.NO_BORDER));
            headerTable.addCell(new Cell().add(nestedTable).setBorder(Border.NO_BORDER));
            headerTable.addCell(new Cell().add("").setBorder(Border.NO_BORDER));

            Border border = new SolidBorder(Color.GRAY, 1f / 2f);
            Table dividerLine = new Table(fullWidth);
            dividerLine.setBorder(border);

            document.add(headerTable);
            document.add(dividerLine);
            document.add(new Paragraph("\n").setFontSize(3f));

            Table twoColTable = new Table(twoColWidth);
            twoColTable.addCell(getBillingCell("Informacije prodaje"));
            twoColTable.addCell(getBillingCell("Informacije kupca"));
            twoColTable.setMarginBottom(4f);
            document.add(twoColTable);


            Table twoColTable2 = new Table(twoColWidth);
            twoColTable2.addCell(getCell10fLeft("Datum izdavanja otpremnice: " + saleDate, true));
            twoColTable2.addCell(getCell10fLeft("Kupac: " + buyerName, false));
            twoColTable2.addCell(getCell10fLeft("Mesto izdavanja otpremnice: " + saleLocation, true));
            twoColTable2.addCell(getCell10fLeft("Lokacija kupca: " + buyerLocation, false));
            twoColTable2.addCell(getCell10fLeft("Datum prometa robe: " + transactionDate, false));
            twoColTable2.addCell(getCell10fLeft("PIB: " + buyerPIB, false));
            twoColTable2.addCell(getCell10fLeft("Datum placanja: " + paymentDate, false));
            twoColTable2.addCell(getCell10fLeft("MIB: " + buyerMIB, false));
            twoColTable2.addCell(getCell10fLeft("Nacin placanja: " + paymentMethod, false));

            document.add(twoColTable2);

            document.add(new Paragraph("\n").setFontSize(3f));
            document.add(dividerLine);
            document.add(new Paragraph("\n").setFontSize(3f));
            document.add(new Paragraph("Proizvodi").setFontSize(12f).setBold());

            float[] tenColWidths = new float[]{100f, 100f, 100f, 100f, 100f, 100f, 100f, 100f, 100f, 100f};
            Table tenColumnTable = new Table(tenColWidths);

            tenColumnTable.addCell(new Cell().add("ID").setFontSize(8f).setFontColor(Color.WHITE).setBold().setBackgroundColor(Color.BLACK, 0.5f).setTextAlignment(TextAlignment.CENTER));
            tenColumnTable.addCell(new Cell().add("Artikal").setFontSize(8f).setFontColor(Color.WHITE).setBold().setBackgroundColor(Color.BLACK, 0.5f).setTextAlignment(TextAlignment.CENTER));
            tenColumnTable.addCell(new Cell().add("Jed. Mere").setFontSize(8f).setFontColor(Color.WHITE).setBold().setBackgroundColor(Color.BLACK, 0.5f).setTextAlignment(TextAlignment.CENTER));
            tenColumnTable.addCell(new Cell().add("Kol.").setFontSize(8f).setFontColor(Color.WHITE).setBold().setBackgroundColor(Color.BLACK, 0.5f).setTextAlignment(TextAlignment.CENTER));
            tenColumnTable.addCell(new Cell().add("Osnovica").setFontSize(8f).setFontColor(Color.WHITE).setBold().setBackgroundColor(Color.BLACK, 0.5f).setTextAlignment(TextAlignment.CENTER));
            tenColumnTable.addCell(new Cell().add("Rabat (%)").setFontSize(8f).setFontColor(Color.WHITE).setBold().setBackgroundColor(Color.BLACK, 0.5f).setTextAlignment(TextAlignment.CENTER));
            tenColumnTable.addCell(new Cell().add("Cena - rabat").setFontSize(7f).setFontColor(Color.WHITE).setBold().setBackgroundColor(Color.BLACK, 0.5f).setTextAlignment(TextAlignment.CENTER));
            tenColumnTable.addCell(new Cell().add("PDV").setFontSize(8f).setFontColor(Color.WHITE).setBold().setBackgroundColor(Color.BLACK, 0.5f).setTextAlignment(TextAlignment.CENTER));
            tenColumnTable.addCell(new Cell().add("Cena + PDV").setFontSize(8f).setFontColor(Color.WHITE).setBold().setBackgroundColor(Color.BLACK, 0.5f).setTextAlignment(TextAlignment.CENTER));
            tenColumnTable.addCell(new Cell().add("Iznos").setFontSize(8f).setFontColor(Color.WHITE).setBold().setBackgroundColor(Color.BLACK, 0.5f).setTextAlignment(TextAlignment.CENTER));

            double totalSum = 0;

            for (String info : proizvod) {
                tenColumnTable.addCell(new Cell().add(info)
                        .setBorder(Border.NO_BORDER)
                        .setBorderBottom(new SolidBorder(1f))
                        .setTextAlignment(TextAlignment.CENTER)
                        .setBackgroundColor(Color.WHITE)
                        .setFontSize(8f));
            }
            for (int i = 0; i < proizvod.size(); i++) {
                if (i % 10 == 9) {
                    totalSum += Double.parseDouble(proizvod.get(i));
                }
            }

            document.add(tenColumnTable);

            document.add(new Paragraph("Ukupni PDV: " + String.format("%.2f", totalSum * 0.2)).setFontSize(10f).setTextAlignment(TextAlignment.RIGHT).setBackgroundColor(Color.BLACK, 0.5f));
            document.add(new Paragraph("Ukupni iznos: " + String.format("%.2f", totalSum)).setFontSize(10f).setTextAlignment(TextAlignment.RIGHT).setBackgroundColor(Color.BLACK, 0.5f));

            PageSize ps = document.getPdfDocument().getDefaultPageSize();
            Paragraph lines = new Paragraph("________________              ________________              ________________");
            document.add(lines.setFixedPosition(document.getLeftMargin(), document.getBottomMargin() + 0f, ps.getWidth() - document.getLeftMargin() - document.getRightMargin()).setTextAlignment(TextAlignment.CENTER));
            Paragraph fields = new Paragraph("      Sastavio                                    Primio                               Odgovorno lice ");
            document.add(fields.setFixedPosition(document.getLeftMargin(), document.getBottomMargin() - 20f, ps.getWidth() - document.getLeftMargin() - document.getRightMargin()).setTextAlignment(TextAlignment.CENTER));

            document.close();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        try {
            new InvoiceGeneratorGUI();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static Cell getHeaderTextCellValue(String textValue) {
        return new Cell().add(textValue).setBold().setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.RIGHT);
    }

    static Cell getHeaderTextCell(String textValue) {
        return new Cell().add(textValue).setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.LEFT);
    }

    static Cell getBillingCell(String textValue) {
        return new Cell().add(textValue).setBold().setFontSize(12f).setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER);
    }

    static Cell getCell10fLeft(String textValue, boolean bold) {
        return new Cell().add(textValue).setFontSize(10f).setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER);
    }
}