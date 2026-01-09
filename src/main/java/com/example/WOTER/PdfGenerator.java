package com.example.WOTER;

import com.example.WOTER.DTO.ReceiptDTO;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.util.List;

@Component
public class PdfGenerator {

    public byte[] generate(List<ReceiptDTO> receipts, Integer month, Integer year) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, baos);
        document.open();

        int count = 0;
        PdfPTable table = new PdfPTable(2); // 2 колонки = 2 квитанции в ряд
        table.setWidthPercentage(100);

        for (ReceiptDTO r : receipts) {
            PdfPCell cell = new PdfPCell();
            cell.setPadding(5);

            Paragraph p = new Paragraph("Счет-квитанция\n");
            p.add("ФИО: " + r.getClientName() + "\n");
            p.add("Лицевой счет: " + r.getPersAccount() + "\n");
            p.add("Адрес: " + r.getAddressHo() + "\n");
            p.add("Начислено: " + r.getDebetOut() + "\n");
            p.add("К оплате: " + r.getSumma() + "\n");
            cell.addElement(p);

            table.addCell(cell);

            count++;
            if (count % 4 == 0) { // 4 квитанции на странице
                if (table.getRows().size() > 0) {
                    document.add(table);
                    document.newPage();
                }
                table = new PdfPTable(2);
                table.setWidthPercentage(100);
            }
        }

        if (table.getRows().size() > 0) {
            document.add(table);
        }

        document.close();
        return baos.toByteArray();
    }
}
