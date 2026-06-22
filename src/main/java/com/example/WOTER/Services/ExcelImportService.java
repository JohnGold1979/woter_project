package com.example.WOTER.Services;

import com.example.WOTER.Repository.IndicationsRepository;
import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Service
public class ExcelImportService {

    private final IndicationsRepository indicationsRepository;

    public ExcelImportService(IndicationsRepository indicationsRepository) {
        this.indicationsRepository = indicationsRepository;
    }

    public ImportResult importIndicationsFromExcel(MultipartFile file, int month, int year) {
        List<ImportError> errors = new ArrayList<>();
        int successCount = 0;

        try (InputStream inputStream = file.getInputStream()) {
            Workbook workbook = WorkbookFactory.create(inputStream);
            Sheet sheet = workbook.getSheetAt(0);

            for (Row row : sheet) {
                // Пропускаем пустые строки
                if (row.getCell(0) == null || row.getCell(1) == null) continue;

                String personalAccount = getCellValueAsString(row.getCell(0));
                String indicationStr = getCellValueAsString(row.getCell(1));

                // Пропускаем заголовки если есть
                if (personalAccount.equalsIgnoreCase("Лицевой счет") ||
                        personalAccount.equalsIgnoreCase("pers_account") ||
                        personalAccount.isBlank()) continue;

                try {
                    int indication = Integer.parseInt(indicationStr.trim());

                    // Вызываем хранимую процедуру
                    String result = indicationsRepository.insertIndWithParams(
                            personalAccount, month, year, indication
                    );

                    successCount++;

                } catch (NumberFormatException e) {
                    errors.add(new ImportError(personalAccount, indicationStr,
                            "Неверный формат показаний"));
                } catch (Exception e) {
                    errors.add(new ImportError(personalAccount, indicationStr,
                            e.getMessage()));
                }
            }

            workbook.close();

        } catch (Exception e) {
            e.printStackTrace();
            return new ImportResult(0, List.of(new ImportError("", "",
                    "Ошибка чтения файла: " + e.getMessage())));
        }

        return new ImportResult(successCount, errors);
    }

    private String getCellValueAsString(Cell cell) {
        if (cell == null) return "";

        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue().trim();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getLocalDateTimeCellValue().toString();
                }
                // Для числовых значений, которые могут быть целыми
                double numValue = cell.getNumericCellValue();
                if (numValue == Math.floor(numValue)) {
                    return String.valueOf((long) numValue);
                }
                return String.valueOf(numValue);
            case FORMULA:
                return cell.getCellFormula();
            default:
                return "";
        }
    }

    // Класс для результата импорта
    public static class ImportResult {
        private final int successCount;
        private final List<ImportError> errors;

        public ImportResult(int successCount, List<ImportError> errors) {
            this.successCount = successCount;
            this.errors = errors;
        }

        public int getSuccessCount() { return successCount; }
        public List<ImportError> getErrors() { return errors; }
        public boolean hasErrors() { return !errors.isEmpty(); }
    }

    // Класс для ошибок импорта
    public static class ImportError {
        private final String personalAccount;
        private final String value;
        private final String message;

        public ImportError(String personalAccount, String value, String message) {
            this.personalAccount = personalAccount;
            this.value = value;
            this.message = message;
        }

        public String getPersonalAccount() { return personalAccount; }
        public String getValue() { return value; }
        public String getMessage() { return message; }
    }
}