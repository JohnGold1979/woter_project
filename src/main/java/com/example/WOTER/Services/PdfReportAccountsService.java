package com.example.WOTER.Services;

import com.example.WOTER.DTO.*;
import com.example.WOTER.HeaderFooterPageEvent;
import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Service
public class PdfReportAccountsService {

    private final ReportAccountsService reportAccountsService;

    public PdfReportAccountsService(ReportAccountsService reportAccountsService) {
        this.reportAccountsService = reportAccountsService;
    }

    public void generateCombinedReport(
            List<HouseDTO> houses,
            int month,
            int year,
            ReportAccountsService reportService,
            OutputStream out
    ) throws DocumentException {

        Document document = new Document(PageSize.A4.rotate(), 30, 30, 60, 30);
        PdfWriter writer = PdfWriter.getInstance(document, out);

        String period = String.format("%02d.%d", month, year);
        writer.setPageEvent(new HeaderFooterPageEvent(
                "ОБОРОТНАЯ ВЕДОМОСТЬ ПО ЛИЦЕВЫМ СЧЕТАМ квартирного сектора (ЗА ХОЛОДНУЮ ВОДУ)",
                period
        ));
        document.open();

        Font titleFont = new Font(Font.HELVETICA, 14, Font.BOLD);

        // === Общие суммы по всем домам ===
        BigDecimal totalDebetInAll = BigDecimal.ZERO;
        BigDecimal totalCredetInAll = BigDecimal.ZERO;
        BigDecimal totalChargedAll = BigDecimal.ZERO;
        BigDecimal totalTaxAll = BigDecimal.ZERO;
        BigDecimal totalPaidAll = BigDecimal.ZERO;
        BigDecimal totalRemovalAll = BigDecimal.ZERO;
        BigDecimal totalDebetOutAll = BigDecimal.ZERO;
        BigDecimal totalCredetOutAll = BigDecimal.ZERO;

        for (HouseDTO house : houses) {
            int houseId = house.getHouseId();
            String houseName = house.getHouseName();

            List<ReportRowDTO> rows = reportService.getHouseReport(month, year, houseId);
            if (rows.isEmpty()) continue;
            // ====== Заголовок ======
            Paragraph title = new Paragraph(
                    "Отчёт по дому: " + houseName + " (" + month + "/" + year + ")", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);

            Paragraph spacer = new Paragraph(" ");
            spacer.setSpacingBefore(1f);
            document.add(spacer);

            PdfPTable table = new PdfPTable(12);
            table.setWidthPercentage(100);
            table.setWidths(new float[]{2f, 4f, 13f, 10f, 5f, 5f, 5f, 5f, 5f, 5f, 5f, 5f});

            // Заголовки
            String[] headers = {"Квартира","Лицевой счёт","ФИО","Адрес",
                    "Дебет нач.","Кредит нач.","Начислено","Налог 3%",
                    "Оплачено","Субсидии","Дебет кон.","Кредит кон."};
            for (String h : headers) table.addCell(makeChargeHeader(h));
            table.setHeaderRows(1);

            // === Локальные суммы ===
            BigDecimal sumDebetIn = BigDecimal.ZERO;
            BigDecimal sumCredetIn = BigDecimal.ZERO;
            BigDecimal sumCharged = BigDecimal.ZERO;
            BigDecimal sumTaxIn = BigDecimal.ZERO;
            BigDecimal sumPaid = BigDecimal.ZERO;
            BigDecimal sumRemoval = BigDecimal.ZERO;
            BigDecimal sumDebetOut = BigDecimal.ZERO;
            BigDecimal sumCredetOut = BigDecimal.ZERO;

            for (ReportRowDTO row : rows) {
                table.addCell(makeChargeDataInfo(String.valueOf(row.getFlat())));
                table.addCell(makeChargeDataInfo(row.getPersAccount()));
                table.addCell(makeChargeDataInfo(row.getClientName()));
                table.addCell(makeChargeDataInfo(row.getAddress()));
                table.addCell(makeChargeData(toStr(row.getDebetIn())));
                table.addCell(makeChargeData(toStr(row.getCredetIn())));
                table.addCell(makeChargeData(toStr(row.getChargedMoney())));
                table.addCell(makeChargeData(toStr(row.getTaxIn())));
                table.addCell(makeChargeData(toStr(row.getPaydIn())));
                table.addCell(makeChargeData(toStr(row.getSubsidy())));
                table.addCell(makeChargeData(toStr(row.getDebetOut())));
                table.addCell(makeChargeData(toStr(row.getCredetOut())));

                // суммирование
                sumDebetIn = sumDebetIn.add(nvl(row.getDebetIn()));
                sumCredetIn = sumCredetIn.add(nvl(row.getCredetIn()));
                sumCharged = sumCharged.add(nvl(row.getChargedMoney()));
                sumTaxIn = sumTaxIn.add(nvl(row.getTaxIn()));
                sumPaid = sumPaid.add(nvl(row.getPaydIn()));
                sumRemoval = sumRemoval.add(nvl(row.getSubsidy()));
                sumDebetOut = sumDebetOut.add(nvl(row.getDebetOut()));
                sumCredetOut = sumCredetOut.add(nvl(row.getCredetOut()));
            }

            // === Итог по дому ===
            PdfPCell totalCell = new PdfPCell(new Phrase("ИТОГО по дому", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9)));
            totalCell.setColspan(4);
            totalCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            totalCell.setBackgroundColor(new Color(230, 230, 230));
            table.addCell(totalCell);

            table.addCell(makeTotalCell(sumDebetIn));
            table.addCell(makeTotalCell(sumCredetIn));
            table.addCell(makeTotalCell(sumCharged));
            table.addCell(makeTotalCell(sumTaxIn));
            table.addCell(makeTotalCell(sumPaid));
            table.addCell(makeTotalCell(sumRemoval));
            table.addCell(makeTotalCell(sumDebetOut));
            table.addCell(makeTotalCell(sumCredetOut));

            document.add(table);
            document.add(new Paragraph("\n"));

            // === Добавляем в общие итоги ===
            totalDebetInAll = totalDebetInAll.add(sumDebetIn);
            totalCredetInAll = totalCredetInAll.add(sumCredetIn);
            totalChargedAll = totalChargedAll.add(sumCharged);
            totalTaxAll = totalTaxAll.add(sumTaxIn);
            totalPaidAll = totalPaidAll.add(sumPaid);
            totalRemovalAll = totalRemovalAll.add(sumRemoval);
            totalDebetOutAll = totalDebetOutAll.add(sumDebetOut);
            totalCredetOutAll = totalCredetOutAll.add(sumCredetOut);
        }

        // === Итоговая страница по всем домам ===
        document.newPage();

        Paragraph totalTitle = new Paragraph("Сводные итоги по всем домам", titleFont);
        totalTitle.setAlignment(Element.ALIGN_CENTER);
        document.add(totalTitle);
        document.add(new Paragraph("\n"));

        PdfPTable totalTable = new PdfPTable(8);
        totalTable.setWidthPercentage(100);
        totalTable.setWidths(new float[]{5f,5f,5f,5f,5f,5f,5f,5f});

        String[] totalHeaders = {"Дебет нач.","Кредит нач.","Начислено","Налог 3%",
                "Оплачено","Субсидии","Дебет кон.","Кредит кон."};
        for (String h : totalHeaders) totalTable.addCell(makeChargeHeader(h));

        totalTable.addCell(makeTotalCell(totalDebetInAll));
        totalTable.addCell(makeTotalCell(totalCredetInAll));
        totalTable.addCell(makeTotalCell(totalChargedAll));
        totalTable.addCell(makeTotalCell(totalTaxAll));
        totalTable.addCell(makeTotalCell(totalPaidAll));
        totalTable.addCell(makeTotalCell(totalRemovalAll));
        totalTable.addCell(makeTotalCell(totalDebetOutAll));
        totalTable.addCell(makeTotalCell(totalCredetOutAll));

        // --- Свернутое сальдо ---
        PdfPTable rollUpTable = new PdfPTable(2);
        rollUpTable.setWidthPercentage(100);
        rollUpTable.setWidths(new float[]{50f, 50f});

        String[] rollUpHeaders = {"Свернутое сальдо начало", "Свернутое сальдо конец"};
        for (String h : rollUpHeaders) rollUpTable.addCell(makeChargeHeader(h));

       // Вычисляем свернутое сальдо (BigDecimal → BigDecimal)
        BigDecimal rollUpStart = totalDebetInAll.subtract(totalCredetInAll);
        BigDecimal rollUpEnd = totalDebetOutAll.subtract(totalCredetOutAll);

        rollUpTable.addCell(makeTotalCell(rollUpStart));
        rollUpTable.addCell(makeTotalCell(rollUpEnd));

        document.add(totalTable);
        document.add(rollUpTable);
        document.close();
    }
//----------------------------------------------------------------------------------------------------------------------
public void generatePrivateReport(
        List<StreetDTO> streets,
        int month,
        int year,
        ReportAccountsService reportService,
        OutputStream out
) throws DocumentException {

    Document document = new Document(PageSize.A4.rotate(), 30, 30, 60, 30);
    PdfWriter writer = PdfWriter.getInstance(document, out);

    String period = String.format("%02d.%d", month, year);
    writer.setPageEvent(new HeaderFooterPageEvent(
            "ОБОРОТНАЯ ВЕДОМОСТЬ ПО ЛИЦЕВЫМ СЧЕТАМ частного сектора (ЗА ХОЛОДНУЮ ВОДУ)",
            period
    ));
    document.open();

    Font titleFont = new Font(Font.HELVETICA, 14, Font.BOLD);

    // === Общие суммы по всем домам ===
    BigDecimal totalDebetInAll = BigDecimal.ZERO;
    BigDecimal totalCredetInAll = BigDecimal.ZERO;
    BigDecimal totalChargedAll = BigDecimal.ZERO;
    BigDecimal totalTaxAll = BigDecimal.ZERO;
    BigDecimal totalPaidAll = BigDecimal.ZERO;
    BigDecimal totalRemovalAll = BigDecimal.ZERO;
    BigDecimal totalDebetOutAll = BigDecimal.ZERO;
    BigDecimal totalCredetOutAll = BigDecimal.ZERO;

    for (StreetDTO street : streets) {
        int steetId = street.getStreetId();
        String streetName = street.getStreetName();

        List<ReportRowDTO> rows = reportService.getPrivateReport(month, year, steetId);
        if (rows.isEmpty()) continue;

        // ====== Заголовок ======
        Paragraph title = new Paragraph(
                "Отчёт по улице: " + streetName + " (" + month + "/" + year + ")", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);

        PdfPTable table = new PdfPTable(12);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{2f, 4f, 13f, 10f, 5f, 5f, 5f, 5f, 5f, 5f, 5f, 5f});

        // Заголовки
        String[] headers = {"Квартира","Лицевой счёт","ФИО","Адрес",
                "Дебет нач.","Кредит нач.","Начислено","Налог 3%",
                "Оплачено","Субсидии","Дебет кон.","Кредит кон."};
        for (String h : headers) table.addCell(makeChargeHeader(h));
        table.setHeaderRows(1);

        // === Локальные суммы ===
        BigDecimal sumDebetIn = BigDecimal.ZERO;
        BigDecimal sumCredetIn = BigDecimal.ZERO;
        BigDecimal sumCharged = BigDecimal.ZERO;
        BigDecimal sumTaxIn = BigDecimal.ZERO;
        BigDecimal sumPaid = BigDecimal.ZERO;
        BigDecimal sumRemoval = BigDecimal.ZERO;
        BigDecimal sumDebetOut = BigDecimal.ZERO;
        BigDecimal sumCredetOut = BigDecimal.ZERO;

        for (ReportRowDTO row : rows) {
            table.addCell(makeChargeDataInfo(String.valueOf(row.getFlat())));
            table.addCell(makeChargeDataInfo(row.getPersAccount()));
            table.addCell(makeChargeDataInfo(row.getClientName()));
            table.addCell(makeChargeDataInfo(row.getAddress()));
            table.addCell(makeChargeData(toStr(row.getDebetIn())));
            table.addCell(makeChargeData(toStr(row.getCredetIn())));
            table.addCell(makeChargeData(toStr(row.getChargedMoney())));
            table.addCell(makeChargeData(toStr(row.getTaxIn())));
            table.addCell(makeChargeData(toStr(row.getPaydIn())));
            table.addCell(makeChargeData(toStr(row.getSubsidy())));
            table.addCell(makeChargeData(toStr(row.getDebetOut())));
            table.addCell(makeChargeData(toStr(row.getCredetOut())));

            // суммирование
            sumDebetIn = sumDebetIn.add(nvl(row.getDebetIn()));
            sumCredetIn = sumCredetIn.add(nvl(row.getCredetIn()));
            sumCharged = sumCharged.add(nvl(row.getChargedMoney()));
            sumTaxIn = sumTaxIn.add(nvl(row.getTaxIn()));
            sumPaid = sumPaid.add(nvl(row.getPaydIn()));
            sumRemoval = sumRemoval.add(nvl(row.getSubsidy()));
            sumDebetOut = sumDebetOut.add(nvl(row.getDebetOut()));
            sumCredetOut = sumCredetOut.add(nvl(row.getCredetOut()));
        }

        // === Итог по дому ===
        PdfPCell totalCell = new PdfPCell(new Phrase("ИТОГО по улице", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9)));
        totalCell.setColspan(4);
        totalCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        totalCell.setBackgroundColor(new Color(230, 230, 230));
        table.addCell(totalCell);

        table.addCell(makeTotalCell(sumDebetIn));
        table.addCell(makeTotalCell(sumCredetIn));
        table.addCell(makeTotalCell(sumCharged));
        table.addCell(makeTotalCell(sumTaxIn));
        table.addCell(makeTotalCell(sumPaid));
        table.addCell(makeTotalCell(sumRemoval));
        table.addCell(makeTotalCell(sumDebetOut));
        table.addCell(makeTotalCell(sumCredetOut));

        document.add(table);
        document.add(new Paragraph("\n"));

        // === Добавляем в общие итоги ===
        totalDebetInAll = totalDebetInAll.add(sumDebetIn);
        totalCredetInAll = totalCredetInAll.add(sumCredetIn);
        totalChargedAll = totalChargedAll.add(sumCharged);
        totalTaxAll = totalTaxAll.add(sumTaxIn);
        totalPaidAll = totalPaidAll.add(sumPaid);
        totalRemovalAll = totalRemovalAll.add(sumRemoval);
        totalDebetOutAll = totalDebetOutAll.add(sumDebetOut);
        totalCredetOutAll = totalCredetOutAll.add(sumCredetOut);
    }

    // === Итоговая страница по всем домам ===
    document.newPage();

    Paragraph totalTitle = new Paragraph("Сводные итоги по всем улицам", titleFont);
    totalTitle.setAlignment(Element.ALIGN_CENTER);
    document.add(totalTitle);
    document.add(new Paragraph("\n"));

    PdfPTable totalTable = new PdfPTable(8);
    totalTable.setWidthPercentage(100);
    totalTable.setWidths(new float[]{5f,5f,5f,5f,5f,5f,5f,5f});

    String[] totalHeaders = {"Дебет нач.","Кредит нач.","Начислено","Налог 3%",
            "Оплачено","Субсидии","Дебет кон.","Кредит кон."};
    for (String h : totalHeaders) totalTable.addCell(makeChargeHeader(h));

    totalTable.addCell(makeTotalCell(totalDebetInAll));
    totalTable.addCell(makeTotalCell(totalCredetInAll));
    totalTable.addCell(makeTotalCell(totalChargedAll));
    totalTable.addCell(makeTotalCell(totalTaxAll));
    totalTable.addCell(makeTotalCell(totalPaidAll));
    totalTable.addCell(makeTotalCell(totalRemovalAll));
    totalTable.addCell(makeTotalCell(totalDebetOutAll));
    totalTable.addCell(makeTotalCell(totalCredetOutAll));

    // --- Свернутое сальдо ---
    PdfPTable rollUpTable = new PdfPTable(2);
    rollUpTable.setWidthPercentage(100);
    rollUpTable.setWidths(new float[]{50f, 50f});

    String[] rollUpHeaders = {"Свернутое сальдо начало", "Свернутое сальдо конец"};
    for (String h : rollUpHeaders) rollUpTable.addCell(makeChargeHeader(h));

// Вычисляем свернутое сальдо (BigDecimal → BigDecimal)
    BigDecimal rollUpStart = totalDebetInAll.subtract(totalCredetInAll);
    BigDecimal rollUpEnd = totalDebetOutAll.subtract(totalCredetOutAll);

    rollUpTable.addCell(makeTotalCell(rollUpStart));
    rollUpTable.addCell(makeTotalCell(rollUpEnd));

    document.add(totalTable);
    document.add(rollUpTable);
    document.close();
}
//----------------------------------------------------------------------------------------------------------------------
public void generateAllHousesReport(
        int month,
        int year,
        ReportAccountsService reportService,
        OutputStream out
) throws DocumentException {
    Document document = new Document(PageSize.A4.rotate(), 30, 30, 60, 30);
    PdfWriter writer = PdfWriter.getInstance(document, out);

    String period = String.format("%02d.%d", month, year);
    writer.setPageEvent(new HeaderFooterPageEvent(
            "ОБОРОТНАЯ ВЕДОМОСТЬ ПО ДОМАМ квартирного сектора (ЗА ХОЛОДНУЮ ВОДУ)",
            period
    ));
    document.open();

    // === таблица ===
    PdfPTable table = new PdfPTable(9);
    table.setWidthPercentage(100);
    table.setWidths(new float[]{15f, 5f, 5f, 5f, 5f, 5f, 5f, 5f, 5f});

    // Заголовки
    String[] headers = {
            "Наименование дома",
            "Дебет нач.","Кредит нач.","Начислено","Налог 3%",
            "Оплачено","Субсидии","Дебет кон.","Кредит кон."
    };
    for (String h : headers)
        table.addCell(makeChargeHeader(h));

    table.setHeaderRows(1);

    List<HouseAllReportDTO> rows = reportService.getHousesAllReport(month, year);

    if (rows == null || rows.isEmpty()) {
        document.add(new Paragraph("Нет данных за указанный период.", new Font(Font.HELVETICA, 12, Font.BOLD)));
        document.close();
        return;
    }

    // === Локальные суммы ===
    BigDecimal sumDebetIn = BigDecimal.ZERO;
    BigDecimal sumCredetIn = BigDecimal.ZERO;
    BigDecimal sumCharged = BigDecimal.ZERO;
    BigDecimal sumTaxIn = BigDecimal.ZERO;
    BigDecimal sumPaid = BigDecimal.ZERO;
    BigDecimal sumSubsidy = BigDecimal.ZERO;
    BigDecimal sumDebetOut = BigDecimal.ZERO;
    BigDecimal sumCredetOut = BigDecimal.ZERO;

    // === Цикл по строкам ===
    for (HouseAllReportDTO row : rows) {
        table.addCell(makeChargeDataInfo(row.getHouseName())); // тут, если это строка, добавь flat / name
        table.addCell(makeChargeData(toStr(row.getDebetIn())));
        table.addCell(makeChargeData(toStr(row.getCredetIn())));
        table.addCell(makeChargeData(toStr(row.getChargedMoney())));
        table.addCell(makeChargeData(toStr(row.getTaxIn())));
        table.addCell(makeChargeData(toStr(row.getPaydIn())));
        table.addCell(makeChargeData(toStr(row.getSubsidy())));
        table.addCell(makeChargeData(toStr(row.getDebetOut())));
        table.addCell(makeChargeData(toStr(row.getCredetOut())));


        // суммирование
        sumDebetIn = sumDebetIn.add(nvl(row.getDebetIn()));
        sumCredetIn = sumCredetIn.add(nvl(row.getCredetIn()));
        sumCharged = sumCharged.add(nvl(row.getChargedMoney()));
        sumTaxIn = sumTaxIn.add(nvl(row.getTaxIn()));
        sumPaid = sumPaid.add(nvl(row.getPaydIn()));
        sumSubsidy = sumSubsidy.add(nvl(row.getSubsidy()));
        sumDebetOut = sumDebetOut.add(nvl(row.getDebetOut()));
        sumCredetOut = sumCredetOut.add(nvl(row.getCredetOut()));
    }

    // === Добавляем итоговую строку ===
    // === Добавляем итоговую строку ===
    table.addCell(makeTotalCell2("ИТОГО:"));
    table.addCell(makeTotalCell2(toStr2(sumDebetIn)));
    table.addCell(makeTotalCell2(toStr2(sumCredetIn)));
    table.addCell(makeTotalCell2(toStr2(sumCharged)));
    table.addCell(makeTotalCell2(toStr2(sumTaxIn)));
    table.addCell(makeTotalCell2(toStr(sumPaid)));
    table.addCell(makeTotalCell2(toStr(sumSubsidy)));
    table.addCell(makeTotalCell2(toStr(sumDebetOut)));
    table.addCell(makeTotalCell2(toStr(sumCredetOut)));

    // ✅ добавляем таблицу в документ
    document.add(table);

    document.close();
}

//----------------------------------------------------------------------------------------------------------------------
public void generateAllStreetsReport(
        int month,
        int year,
        ReportAccountsService reportService,
        OutputStream out
) throws DocumentException {
    Document document = new Document(PageSize.A4.rotate(), 30, 30, 60, 30);
    PdfWriter writer = PdfWriter.getInstance(document, out);

    String period = String.format("%02d.%d", month, year);
    writer.setPageEvent(new HeaderFooterPageEvent(
            "ОБОРОТНАЯ ВЕДОМОСТЬ ПО Улицам (ЗА ХОЛОДНУЮ ВОДУ)",
            period
    ));
    document.open();

    // === таблица ===
    PdfPTable table = new PdfPTable(9);
    table.setWidthPercentage(100);
    table.setWidths(new float[]{15f, 5f, 5f, 5f, 5f, 5f, 5f, 5f, 5f});

    // Заголовки
    String[] headers = {
            "Наименование улицы",
            "Дебет нач.","Кредит нач.","Начислено","Налог 3%",
            "Оплачено","Субсидии","Дебет кон.","Кредит кон."
    };
    for (String h : headers)
        table.addCell(makeChargeHeader(h));

    table.setHeaderRows(1);

    List<StreetAllReportDTO> rows = reportService.getStreetsAllReport(month, year);

    if (rows == null || rows.isEmpty()) {
        document.add(new Paragraph("Нет данных за указанный период.", new Font(Font.HELVETICA, 12, Font.BOLD)));
        document.close();
        return;
    }

    // === Локальные суммы ===
    BigDecimal sumDebetIn = BigDecimal.ZERO;
    BigDecimal sumCredetIn = BigDecimal.ZERO;
    BigDecimal sumCharged = BigDecimal.ZERO;
    BigDecimal sumTaxIn = BigDecimal.ZERO;
    BigDecimal sumPaid = BigDecimal.ZERO;
    BigDecimal sumSubsidy = BigDecimal.ZERO;
    BigDecimal sumDebetOut = BigDecimal.ZERO;
    BigDecimal sumCredetOut = BigDecimal.ZERO;

    // === Цикл по строкам ===
    for (StreetAllReportDTO row : rows) {
        table.addCell(makeChargeDataInfo(row.getStreetName())); // тут, если это строка, добавь flat / name
        table.addCell(makeChargeData(toStr(row.getDebetIn())));
        table.addCell(makeChargeData(toStr(row.getCredetIn())));
        table.addCell(makeChargeData(toStr(row.getChargedMoney())));
        table.addCell(makeChargeData(toStr(row.getTaxIn())));
        table.addCell(makeChargeData(toStr(row.getPaydIn())));
        table.addCell(makeChargeData(toStr(row.getSubsidy())));
        table.addCell(makeChargeData(toStr(row.getDebetOut())));
        table.addCell(makeChargeData(toStr(row.getCredetOut())));

        // суммирование
        sumDebetIn = sumDebetIn.add(nvl(row.getDebetIn()));
        sumCredetIn = sumCredetIn.add(nvl(row.getCredetIn()));
        sumCharged = sumCharged.add(nvl(row.getChargedMoney()));
        sumTaxIn = sumTaxIn.add(nvl(row.getTaxIn()));
        sumPaid = sumPaid.add(nvl(row.getPaydIn()));
        sumSubsidy = sumSubsidy.add(nvl(row.getSubsidy()));
        sumDebetOut = sumDebetOut.add(nvl(row.getDebetOut()));
        sumCredetOut = sumCredetOut.add(nvl(row.getCredetOut()));
    }

    // === Добавляем итоговую строку ===
    // === Добавляем итоговую строку ===
    table.addCell(makeTotalCell2("ИТОГО:"));
    table.addCell(makeTotalCell2(toStr2(sumDebetIn)));
    table.addCell(makeTotalCell2(toStr2(sumCredetIn)));
    table.addCell(makeTotalCell2(toStr2(sumCharged)));
    table.addCell(makeTotalCell2(toStr2(sumTaxIn)));
    table.addCell(makeTotalCell2(toStr(sumPaid)));
    table.addCell(makeTotalCell2(toStr(sumSubsidy)));
    table.addCell(makeTotalCell2(toStr(sumDebetOut)));
    table.addCell(makeTotalCell2(toStr(sumCredetOut)));

    // ✅ добавляем таблицу в документ
    document.add(table);

    document.close();
}
//------------- Отчет по оплатм ----------------------------------------------------------------------------------------
    public void generateAllPaysReport(
            int month,
            int year,
            ReportAccountsService reportService,
            OutputStream out
    ) throws DocumentException {
        Document document = new Document(PageSize.A4, 30, 30, 60, 30);
        PdfWriter writer = PdfWriter.getInstance(document, out);

        String period = String.format("%02d.%d", month, year);
        writer.setPageEvent(new HeaderFooterPageEvent(
                "Отчет по оплатам за период " + month + ' ' + year,
                period
        ));
        document.open();

        // создаём страницу, чтобы PageEvent точно сработал
        //document.newPage();

       // гарантированный фиксированный отступ
        Paragraph spacer = new Paragraph(" ");
        spacer.setSpacingBefore(5f);
        document.add(spacer);


        // === таблица ===
        PdfPTable table = new PdfPTable(4);
        table.setWidthPercentage(100);
        table.setSpacingBefore(20f);
        table.setWidths(new float[]{5f, 5f, 5f, 5f});

        // Заголовки
        String[] headers = {
                "Дни", "Сумма в реестре","Налог","Сумма без налога"
        };
        for (String h : headers)
            table.addCell(makeChargeHeader(h));

        table.setHeaderRows(1);

        List<PayAllReportDTO> rows = reportService.getPayAllReport(month, year);

        if (rows == null || rows.isEmpty()) {
            document.add(new Paragraph("Нет данных за указанный период.", new Font(Font.HELVETICA, 12, Font.BOLD)));
            document.close();
            return;
        }

        // === Локальные суммы ===
        BigDecimal sumAmount = BigDecimal.ZERO;
        BigDecimal sumTaxIn = BigDecimal.ZERO;
        BigDecimal sumPayIn = BigDecimal.ZERO;


        // === Цикл по строкам ===
        for (PayAllReportDTO row : rows) {
            table.addCell(makeChargeDataInfo(row.getDays())); // тут, если это строка, добавь flat / name
            table.addCell(makeChargeData(toStr(row.getAmount())));
            table.addCell(makeChargeData(toStr(row.getTaxIn())));
            table.addCell(makeChargeData(toStr(row.getPayIn())));

            // суммирование
            sumAmount = sumAmount.add(nvl(row.getAmount()));
            sumTaxIn = sumTaxIn.add(nvl(row.getTaxIn()));
            sumPayIn = sumPayIn.add(nvl(row.getPayIn()));

        }

        // === Добавляем итоговую строку ===
        // === Добавляем итоговую строку ===
        table.addCell(makeTotalCell2("ИТОГО:"));
        table.addCell(makeTotalCell2(toStr2(sumAmount)));
        table.addCell(makeTotalCell2(toStr2(sumTaxIn)));
        table.addCell(makeTotalCell2(toStr2(sumPayIn)));

        // ✅ добавляем таблицу в документ
        document.add(table);

        document.close();
    }
//----------------------------------------------------------------------------------------------------------------------
    // ===== Вспомогательные методы =====
    private String toStr2(BigDecimal value) {
        return value == null ? "" : value.setScale(2, RoundingMode.HALF_UP).toPlainString();
    }
    private String toStr(BigDecimal val) {
        return val == null ? "" : val.toPlainString();
    }
    private PdfPCell makeChargeData(String text) {
        Font f = new Font(Font.HELVETICA, 8, Font.NORMAL);
        PdfPCell cell = new PdfPCell(new Phrase(text, f));
        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cell.setPadding(3);
        return cell;
    }

    private PdfPCell makeChargeDataInfo(String text) {
        Font f = new Font(Font.HELVETICA, 8, Font.BOLD);
        PdfPCell cell = new PdfPCell(new Phrase(text, f));
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell.setPadding(3);
        return cell;
    }

    private PdfPCell makeChargeHeader(String text) {
        Font f = new Font(Font.HELVETICA, 8, Font.BOLD);
        PdfPCell cell = new PdfPCell(new Phrase(text, f));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setPadding(3);
        return cell;
    }

    private PdfPCell makeTotalCell(BigDecimal val) {
        Font f = new Font(Font.HELVETICA, 8, Font.BOLD);
        PdfPCell cell = new PdfPCell(new Phrase(toStr(val), f));
        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cell.setBackgroundColor(new Color(230, 230, 230));
        cell.setPadding(3);
        return cell;
    }

    private PdfPCell makeTotalCell2(String text) {
        Font font = new Font(Font.HELVETICA, 10, Font.BOLD);
        PdfPCell cell = new PdfPCell(new Phrase(text == null ? "" : text, font));
        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cell.setBackgroundColor(new Color(230, 230, 230));
        return cell;
    }
    private BigDecimal nvl(BigDecimal val) {
        return val == null ? BigDecimal.ZERO : val;
    }

    // ======= Класс для вывода номеров страниц =======
    static class PageFooter extends PdfPageEventHelper {
        private final Font f = new Font(Font.HELVETICA, 8, Font.NORMAL);

        @Override
        public void onEndPage(PdfWriter writer, Document document) {
            PdfContentByte cb = writer.getDirectContent();
            ColumnText.showTextAligned(
                    cb,
                    Element.ALIGN_CENTER,
                    new Phrase(String.format("Страница %d", writer.getPageNumber()), f),
                    (document.right() - document.left()) / 2 + document.leftMargin(),
                    document.bottom() - 10,
                    0
            );
        }
    }
}
