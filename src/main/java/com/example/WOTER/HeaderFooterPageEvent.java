package com.example.WOTER;

import com.lowagie.text.*;
import com.lowagie.text.pdf.*;

import java.text.SimpleDateFormat;
import java.util.Date;

public class HeaderFooterPageEvent extends PdfPageEventHelper {

    private final String title;
    private final String period;
    private final String printTime;

    private final Font titleFont;
    private final Font subFont;
    private final Font footerFont;

    public HeaderFooterPageEvent(String title, String period) {
        this.title = title;
        this.period = period;
        this.printTime = new SimpleDateFormat("dd.MM.yyyy HH:mm").format(new Date());

        try {
            // Грузим Unicode-шрифт
            BaseFont unicode = BaseFont.createFont(
                    "fonts/DejaVuSans.ttf",  // путь в resources
                    BaseFont.IDENTITY_H,
                    BaseFont.EMBEDDED
            );

            titleFont  = new Font(unicode, 12, Font.BOLD);
            subFont    = new Font(unicode, 10, Font.NORMAL);
            footerFont = new Font(unicode, 9, Font.NORMAL);

        } catch (Exception e) {
            throw new RuntimeException("Ошибка загрузки шрифта", e);
        }
    }

    @Override
    public void onEndPage(PdfWriter writer, Document document) {

        PdfContentByte cb = writer.getDirectContent();
        Rectangle page = writer.getPageSize();

        float left   = page.getLeft()   + document.leftMargin();
        float right  = page.getRight()  - document.rightMargin();
        float top    = page.getTop()    - document.topMargin();
        float bottom = page.getBottom() + document.bottomMargin();

        float centerX = (left + right) / 2;

        // --- Координаты ---
        float yTitle = top + 25f;
        float ySub   = top + 12f;
        float yLine  = top + 5f;

        // --- Заголовок ---
        ColumnText.showTextAligned(
                cb,
                Element.ALIGN_CENTER,
                new Phrase(title, titleFont),
                centerX,
                yTitle,
                0
        );

        // --- Подзаголовок ---
        ColumnText.showTextAligned(
                cb,
                Element.ALIGN_CENTER,
                new Phrase("Период: " + period + "   Печать: " + printTime, subFont),
                centerX,
                ySub,
                0
        );

        // --- Линия ---
        cb.saveState();
        cb.setLineWidth(0.6f);
        cb.moveTo(left, yLine);
        cb.lineTo(right, yLine);
        cb.stroke();
        cb.restoreState();

        // --- Номер страницы ---
        ColumnText.showTextAligned(
                cb,
                Element.ALIGN_CENTER,
                new Phrase("Стр. " + writer.getPageNumber(), footerFont),
                centerX,
                bottom - 20f,
                0
        );
    }
}
