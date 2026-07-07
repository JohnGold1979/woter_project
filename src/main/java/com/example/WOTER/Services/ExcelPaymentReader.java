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

            // пропускаем первые 4 строки (заголовки)
            for (int i = 4; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                ExcelPaymentDTO dto = new ExcelPaymentDTO();

                // Дата и время
                Cell dateCell = row.getCell(1);
                if (dateCell == null) continue; // если нет даты, пропускаем строку
                
                // Пропускаем заголовки и служебные строки
                if (dateCell.getCellType() == CellType.STRING) {
                    String dateStr = dateCell.getStringCellValue().trim();
                    if (dateStr.isEmpty() || 
                        dateStr.contains("Дата и время") || 
                        dateStr.contains("ИТОГО:") ||
                        dateStr.matches(".*[a-zA-Zа-яА-ЯёЁ]+.*")) {
                        continue; // пропускаем всю строку
                    }
                }
                
                // Парсим дату
                if (dateCell.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(dateCell)) {
                    dto.setPayDate(dateCell.getDateCellValue()
                            .toInstant()
                            .atZone(ZoneId.systemDefault())
                            .toLocalDateTime());
                } else if (dateCell.getCellType() == CellType.STRING) {
                    String dateStr = dateCell.getStringCellValue().trim();
                    if (!dateStr.isEmpty() && !"ИТОГО:".equalsIgnoreCase(dateStr)) {
                        try {
                            dto.setPayDate(LocalDateTime.parse(
                                    dateStr,
                                    java.time.format.DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss")
                            ));
                        } catch (Exception e) {
                            System.out.println("⚠ Не удалось распарсить дату: " + dateStr);
                            continue; // если дату не распарсили, пропускаем строку
                        }
                    }
                }
                
                // Лицевой счёт
                Cell persAcctCell = row.getCell(3);
                if (persAcctCell != null && persAcctCell.getCellType() == CellType.STRING) {
                    dto.setPersAcc(persAcctCell.getStringCellValue().trim());
                }
                
                // Сумма - обрабатываем NUMERIC и STRING
                Cell amountCell = row.getCell(5);
                if (amountCell != null) {
                    if (amountCell.getCellType() == CellType.NUMERIC) {
                        dto.setAmount(BigDecimal.valueOf(amountCell.getNumericCellValue()));
                    } else if (amountCell.getCellType() == CellType.STRING) {
                        String amountStr = amountCell.getStringCellValue().trim();
                        if (!amountStr.isEmpty()) {
                            try {
                                dto.setAmount(new BigDecimal(amountStr.replace(",", ".")));
                            } catch (Exception e) {
                                System.out.println("⚠ Не удалось распарсить сумму: " + amountStr);
                            }
                        }
                    }
                }
                
                // ⚡ лог для отладки (построчный вывод)
                System.out.print("Импорт: " + dto);
                payments.add(dto);
            }
        }

        return payments;
    }
}
