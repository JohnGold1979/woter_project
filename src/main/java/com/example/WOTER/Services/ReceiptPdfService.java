package com.example.WOTER.Services;

import com.example.WOTER.DTO.ReceiptDTO;
import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Service
public class ReceiptPdfService {

    private final Font boldFont = new Font(Font.HELVETICA, 10, Font.BOLD);
    private final Font normalFont = new Font(Font.HELVETICA, 9);

    public byte[] generatePdf(List<ReceiptDTO> receipts) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        // A4, альбомная ориентация
        Document document = new Document(PageSize.A4.rotate(), 10, 10, 10, 10);
        PdfWriter.getInstance(document, baos);
        document.open();

        int count = 0;
        PdfPTable grid = new PdfPTable(2); // 2 квитанции в ряд
        grid.setWidthPercentage(100);

        for (ReceiptDTO dto : receipts) {
            PdfPTable receipt = buildReceipt(dto);

            PdfPCell cell = new PdfPCell(receipt);
            cell.setPadding(5);
            cell.setBorder(Rectangle.NO_BORDER);
            grid.addCell(cell);

            count++;
            if (count % 4 == 0) { // на листе 4 квитанции (2x2)
                document.add(grid);
                document.newPage();
                grid = new PdfPTable(2);
                grid.setWidthPercentage(100);
            }
        }

        // Добавляем пустые ячейки, если квитанций меньше чем 4
        if (count % 4 != 0) {
            int emptyCells = 4 - (count % 4);
            for (int i = 0; i < emptyCells; i++) {
                PdfPCell empty = new PdfPCell(new Phrase(""));
                empty.setBorder(Rectangle.NO_BORDER);
                grid.addCell(empty);
            }
            document.add(grid);
        }

        document.close();
        return baos.toByteArray();
    }

    // Одна квитанция (header + body)
    private PdfPTable buildReceipt(ReceiptDTO dto) throws DocumentException {
        PdfPTable receipt = new PdfPTable(1);
        receipt.setWidthPercentage(100);

        // Header
        Paragraph header = new Paragraph();
        header.add(new Chunk("Месяц / год: " + dto.getMonthId() + " / " + dto.getYearId() + "   ", normalFont));
        header.add(new Chunk("(шт.): " + dto.getRownum() , normalFont));
        header.add(new Chunk("  Дата печати: " +
                new SimpleDateFormat("dd.MM.yyyy HH:mm").format(new Date()), normalFont));
       // header.add(Chunk.NEWLINE);

        PdfPCell headerCell = new PdfPCell();
        headerCell.addElement(header);
        headerCell.setBorder(Rectangle.NO_BORDER);
        headerCell.setPaddingBottom(4);
        receipt.addCell(headerCell);

        PdfPTable kvit_acc = new PdfPTable(1);
        kvit_acc.setWidthPercentage(100);


        kvit_acc.addCell(makeCell("ЭСЕП-БИЛДИРМЕ  СЧЕТ-ИЗВЕЩЕНИЕ", 1, Element.ALIGN_CENTER, true));
        kvit_acc.addCell(makeCell(
                "Энчилуу Эсеп / Лицевой счет: " + dto.getPersAccount() +
                        "\nФамилия: " + dto.getClientName() +
                        "\nДареги / Адрес: " + dto.getAddressHo(),
                1, Element.ALIGN_LEFT, true));
        PdfPTable charges_acc = buildCharges(dto);
        PdfPCell chargesCell_acc = new PdfPCell(charges_acc);
        chargesCell_acc.setPadding(5);
        kvit_acc.addCell(chargesCell_acc);

        PdfPCell bodyCell_acc = new PdfPCell(kvit_acc);
        bodyCell_acc.setBorder(Rectangle.NO_BORDER);
        receipt.addCell(bodyCell_acc);

        /////////////////////////////////////////////////////////////////////////////////////////////////////

       // PdfPCell dotted = new PdfPCell(new Phrase("......................................................................................................"));
       // dotted.setBorder(Rectangle.NO_BORDER);
       // dotted.setHorizontalAlignment(Element.ALIGN_CENTER);
       // receipt.addCell(dotted);

        PdfPTable kvit = new PdfPTable(1);
        kvit.setWidthPercentage(100);


        kvit.addCell(makeCell("ЭСЕП-ДУМУРЧОК   СЧЕТ-КВИТАНЦИЯ", 1, Element.ALIGN_CENTER, true));
        kvit.addCell(makeCell(
                "Энчилуу Эсеп / Лицевой счет: " + dto.getPersAccount() +
                        "\nФамилия: " + dto.getClientName() +
                        "\nДареги / Адрес: " + dto.getAddressHo() +
                        "\nМуздак суу тариф: " + dto.getTarif() +" Катталгандардын саны / Кол-во прописанных: " + dto.getPersonsOut() ,
                  1, Element.ALIGN_LEFT, true));
        PdfPTable charges = buildCharges(dto);
        PdfPCell chargesCell = new PdfPCell(charges);
        chargesCell.setPadding(4);
        kvit.addCell(chargesCell);

        kvit.addCell(makeCell(
                "Оплата производится только терминалах Раy24\n" +
                    "В случае неуплаты до 25 числа текущего месяца, подача холодной воды будет прекращена.",
                1, Element.ALIGN_LEFT, false));

        PdfPCell bodyCell = new PdfPCell(kvit);
        bodyCell.setBorder(Rectangle.NO_BORDER);
        receipt.addCell(bodyCell);

        return receipt;
    }

    // Таблица начислений
    private PdfPTable buildCharges(ReceiptDTO dto) {
        PdfPTable charges = new PdfPTable(new float[]{2, 2, 2, 2, 2, 2, 2, 2});
        charges.setWidthPercentage(100);

        charges.addCell(makeChargeHeader("Ай башындагы карыз Долг нач. мес"));
        charges.addCell(makeChargeHeader("Ашикча толом Предоплата"));
        charges.addCell(makeChargeHeader("Эсептелди Начислено"));
        charges.addCell(makeChargeHeader("Толонду Оплочено"));
        charges.addCell(makeChargeHeader("Субсидия"));
        charges.addCell(makeChargeHeader("Налог " + dto.getTaxPrcent()));
        charges.addCell(makeChargeHeader("Ай аягындагы Долг на конец мес"));
        charges.addCell(makeChargeHeader("Итого к оплате"));

        charges.addCell(makeChargeData(String.valueOf(dto.getDebetIn())));
        charges.addCell(makeChargeData(String.valueOf(dto.getCreditIn())));
        charges.addCell(makeChargeData(String.valueOf(dto.getChargedMoney())));
        charges.addCell(makeChargeData(String.valueOf(dto.getPaidIn())));
        charges.addCell(makeChargeData(String.valueOf(dto.getSubsidyIn())));
        charges.addCell(makeChargeData(String.valueOf(dto.getSummaTax())));
        charges.addCell(makeChargeData(String.valueOf(dto.getDebetOut())));
        charges.addCell(makeChargeData(String.valueOf(dto.getSumma())));

        return charges;
    }

    private PdfPCell makeChargeHeader(String text) {
        Font smallFont = new Font(Font.HELVETICA, 6, Font.BOLD); // шрифт 7pt, жирный
        PdfPCell cell = new PdfPCell(new Phrase(text, smallFont));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setPadding(3);
        return cell;
    }

    private PdfPCell makeChargeData(String text) {
        Font smallFont = new Font(Font.HELVETICA, 8, Font.NORMAL); // размер 8pt
        PdfPCell cell = new PdfPCell(new Phrase(text, smallFont));
        cell.setHorizontalAlignment(Element.ALIGN_RIGHT); // цифры лучше выравнивать вправо
        cell.setPadding(3);
        return cell;
    }
    private PdfPCell makeCell(String text, int colspan, int align, boolean bold) {
        Font font = bold ? boldFont : normalFont;
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setColspan(colspan);
        cell.setHorizontalAlignment(align);
        cell.setPadding(4);
        return cell;
    }
}
