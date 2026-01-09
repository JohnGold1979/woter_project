package com.example.WOTER.Services;
import com.example.WOTER.DTO.ExcelPaymentDTO;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

@Service
public class ExcelPaymentReader {

    public List<ExcelPaymentDTO> readPayments(InputStream inputStream, String fileName) throws Exception {
        List<ExcelPaymentDTO> payments = new ArrayList<>();

        Workbook workbook;
        if (fileName.toLowerCase().endsWith(".xls")) {
            workbook = new HSSFWorkbook(inputStream); // старый Excel (2003-)
        } else if (fileName.toLowerCase().endsWith(".xlsx")) {
            workbook = new XSSFWorkbook(inputStream); // новый Excel (2007+)
        } else {
            throw new IllegalArgumentException("Неподдерживаемый формат файла: " + fileName);
        }

        try (workbook) {
            Sheet sheet = workbook.getSheetAt(0);

            // пропускаем первую строку (заголовки)
            for (int i = 4; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                ExcelPaymentDTO dto = new ExcelPaymentDTO();

                // № (пропускаем — не нужен)
                // Дата и время
                Cell dateCell = row.getCell(1);
               // System.out.println(dateCell);
                if (dateCell != null ){
                    if (dateCell.getCellType() == CellType.NUMERIC) {
                        // если Excel хранит дату как число
                        if (DateUtil.isCellDateFormatted(dateCell)) {
                            dto.setPayDate(dateCell.getDateCellValue()
                                    .toInstant()
                                    .atZone(ZoneId.systemDefault())
                                    .toLocalDateTime());
                        }
                    } else if (dateCell.getCellType() == CellType.STRING) {
                        // если дата хранится строкой
                        String dateStr = dateCell.getStringCellValue().trim();
                        if (!"ИТОГО:".equalsIgnoreCase(dateStr)) {
                            if (!dateStr.isEmpty()) {
                                try {
                                    dto.setPayDate(LocalDateTime.parse(
                                            dateStr,
                                            java.time.format.DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss")
                                    ));
                                } catch (Exception e) {
                                    System.out.println("⚠ Не удалось распарсить дату: " + dateStr);
                                }
                            }
                        } else {
                            continue;
                        }
                    }
                }
                // Лицевой счёт
                Cell persAcctCell = row.getCell(3);
                if (persAcctCell != null ){
                    dto.setPersAcc(persAcctCell.getStringCellValue().trim());
                    System.out.println(persAcctCell);
                }
                // Сумма
                Cell amountCell = row.getCell(5);
                System.out.println(amountCell);
                if (amountCell != null) {
                    dto.setAmount(BigDecimal.valueOf(amountCell.getNumericCellValue()));
                }
                // ⚡ лог для отладки (построчный вывод)
                System.out.print("Импорт: " + dto);
                payments.add(dto);
            }
        }

        return payments;
    }
}
