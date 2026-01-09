package com.example.WOTER.Services;

import com.example.WOTER.DTO.ReceiptDTO;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Service
public class IndicationsPdfService {

    private final Font boldFont = new Font(Font.HELVETICA, 10, Font.BOLD);
    private final Font normalFont = new Font(Font.HELVETICA, 9);

    public byte[] indicationsPdf(List<ReceiptDTO> receipts) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

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

    private PdfPTable buildReceipt(ReceiptDTO dto) throws DocumentException {
        PdfPTable receipt = new PdfPTable(1);
        receipt.setWidthPercentage(100);

        // === HEADER ===
        Paragraph header = new Paragraph();
        header.add(new Chunk("Месяц / год: " + dto.getMonthId() + " / " + dto.getYearId() + "   ", normalFont));
        header.add(new Chunk("(шт.): " + dto.getRownum(), normalFont));
        header.add(new Chunk("  Дата печати: " +
                new SimpleDateFormat("dd.MM.yyyy HH:mm").format(new Date()), normalFont));

        PdfPCell headerCell = new PdfPCell();
        headerCell.addElement(header);
        headerCell.setBorder(Rectangle.NO_BORDER);
        headerCell.setPaddingBottom(4);
        receipt.addCell(headerCell);

        // === СЧЕТ-ИЗВЕЩЕНИЕ ===
        PdfPTable noticeTable = new PdfPTable(1);
        noticeTable.setWidthPercentage(100);
        noticeTable.addCell(makeCell("ЭСЕП-БИЛДИРМЕ  СЧЕТ-ИЗВЕЩЕНИЕ", 1, Element.ALIGN_CENTER, true));
        noticeTable.addCell(makeCell(
                "Энчилуу Эсеп / Лицевой счет: " + dto.getPersAccount() +
                        "\nФамилия: " + dto.getClientName() +
                        "\nДареги / Адрес: " +  dto.getAddress(),
                1, Element.ALIGN_LEFT, true));

        // Таблица показаний
        PdfPTable indTable1 = indCharges(dto);
        PdfPCell indCell1 = new PdfPCell(indTable1);
        indCell1.setPadding(5);
        noticeTable.addCell(indCell1);

        // Таблица начислений
        PdfPTable chargesTable1 = buildCharges(dto);
        PdfPCell chargesCell1 = new PdfPCell(chargesTable1);
        chargesCell1.setPadding(5);
        noticeTable.addCell(chargesCell1);

        PdfPCell bodyCellNotice = new PdfPCell(noticeTable);
        bodyCellNotice.setBorder(Rectangle.NO_BORDER);
        receipt.addCell(bodyCellNotice);

        // === СЧЕТ-КВИТАНЦИЯ ===
        PdfPTable kvitTable = new PdfPTable(1);
        kvitTable.setWidthPercentage(100);
        kvitTable.addCell(makeCell("ЭСЕП-ДУМУРЧОК   СЧЕТ-КВИТАНЦИЯ", 1, Element.ALIGN_CENTER, true));
        kvitTable.addCell(makeCell(
                "Энчилуу Эсеп / Лицевой счет: " + dto.getPersAccount() +
                        "\nФамилия: " + dto.getClientName() +
                        "\nДареги / Адрес: " + dto.getAddress() +
                        "\nМуздак суу тариф: " + dto.getTarif(),

                1, Element.ALIGN_LEFT, true));

        // Таблица показаний (вторая копия)
        PdfPTable indTable2 = indCharges(dto);
        PdfPCell indCell2 = new PdfPCell(indTable2);
        indCell2.setPadding(5);
        kvitTable.addCell(indCell2);

        // Таблица начислений
        PdfPTable chargesTable2 = buildCharges(dto);
        PdfPCell chargesCell2 = new PdfPCell(chargesTable2);
        chargesCell2.setPadding(4);
        kvitTable.addCell(chargesCell2);

        kvitTable.addCell(makeCell(
                "Оплата производится только в терминалах Pay24.\nТелефон для справок: 92-65-33, 92-70-64 \n"+
                        "В случае неуплаты до 25 числа текущего месяца, подача холодной воды будет прекращена. \n" +
                     "ВНИМАНИЕ! Показания принимаются по тел 92-70-64, 92-70-64",
                1, Element.ALIGN_LEFT, false));

        PdfPCell bodyCellKvit = new PdfPCell(kvitTable);
        bodyCellKvit.setBorder(Rectangle.NO_BORDER);
        receipt.addCell(bodyCellKvit);

        return receipt;
    }

    // === Таблица начислений ===
    private PdfPTable buildCharges(ReceiptDTO dto) {
        PdfPTable charges = new PdfPTable(new float[]{2, 2, 2, 2, 2, 2, 2, 2});
        charges.setWidthPercentage(100);

        charges.addCell(makeChargeHeader("Ай башындагы карыз\nДолг нач. мес"));
        charges.addCell(makeChargeHeader("Ашикча толом\nПредоплата"));
        charges.addCell(makeChargeHeader("Эсептелди\nНачислено"));
        charges.addCell(makeChargeHeader("Толонду\nОплачено"));
        charges.addCell(makeChargeHeader("Субсидия"));
        charges.addCell(makeChargeHeader("Налог " + dto.getTaxPrcent()));
        charges.addCell(makeChargeHeader("Ай аягындагы карыз\nДолг на конец мес"));
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

    // === Таблица показаний (расход воды) ===
    private PdfPTable indCharges(ReceiptDTO dto) {
        PdfPTable ind = new PdfPTable(new float[]{2, 2, 2, 2, 2});
        ind.setWidthPercentage(100);

        ind.addCell(makeChargeHeader("Дата показаний"));
        ind.addCell(makeChargeHeader("Корсоткуч\nСтарые"));
        ind.addCell(makeChargeHeader("Корсоткуч\nНовые"));
        ind.addCell(makeChargeHeader("Расход, м³"));
        ind.addCell(makeChargeHeader("Сумма"));

        // Здесь можно будет вставить реальные показания
        ind.addCell(makeChargeData(dto.getMonthId() + "." + dto.getYearId())); // можно подставить dateCurr
        ind.addCell(makeChargeData(dto.getIndicationPrev() != null ? dto.getIndicationPrev().toString() : ""));
        ind.addCell(makeChargeData(dto.getIndicationCurr() != null ? dto.getIndicationCurr().toString() : ""));
        ind.addCell(makeChargeData(dto.getM3() != null ? dto.getM3().toString() : ""));
        ind.addCell(makeChargeData(dto.getSumIndication() != null ? dto.getSumIndication().toString() : ""));
        ind.addCell(makeChargeData(String.valueOf(dto.getSumma())));

        return ind;
    }

    private PdfPCell makeChargeHeader(String text) {
        Font smallFont = new Font(Font.HELVETICA, 6, Font.BOLD);
        PdfPCell cell = new PdfPCell(new Phrase(text, smallFont));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setPadding(3);
        return cell;
    }

    private PdfPCell makeChargeData(String text) {
        Font smallFont = new Font(Font.HELVETICA, 8, Font.NORMAL);
        PdfPCell cell = new PdfPCell(new Phrase(text, smallFont));
        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
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
