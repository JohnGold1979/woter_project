package com.example.WOTER.Controllers;

import com.example.WOTER.DTO.HouseAllReportDTO;
import com.example.WOTER.Repository.IndicationsRepository;
import com.example.WOTER.Services.ExcelImportService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class IndicationsController {
    private final IndicationsRepository indicationsRepository;
    private final ExcelImportService excelImportService;

    public IndicationsController(IndicationsRepository indicationsRepository,
                                 ExcelImportService excelImportService) {
        this.indicationsRepository = indicationsRepository;
        this.excelImportService = excelImportService;
    }

    @GetMapping("/indications")
    public String getPay(Model model) {
        List<HouseAllReportDTO.IndicationsDTO> payList = indicationsRepository.findAll(); // пока жёстко
        model.addAttribute("ind", payList);
        return "indication";
    }

    @GetMapping("/indications/{persAccount}/{year}")
    @ResponseBody
    public List<HouseAllReportDTO.IndicationsDTO> getIndication(
            @PathVariable String persAccount,
            @PathVariable int year) {
        return indicationsRepository.findByAccountAndYear(persAccount, year);
    }

   @PostMapping("/indications/import-excel")
   @ResponseBody
   public ResponseEntity<?> importExcel(
            @RequestParam("file") MultipartFile file,
            @RequestParam("month") int month,
            @RequestParam("year") int year) {

        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("Файл не выбран");
        }

        String fileName = file.getOriginalFilename();
        if (fileName == null || (!fileName.endsWith(".xlsx") && !fileName.endsWith(".xls"))) {
            return ResponseEntity.badRequest().body("Пожалуйста, загрузите файл Excel (.xlsx или .xls)");
        }

        try {
            ExcelImportService.ImportResult result = excelImportService.importIndicationsFromExcel(file, month, year);

            Map<String, Object> response = new HashMap<>();
            response.put("successCount", result.getSuccessCount());
            response.put("totalRows", result.getSuccessCount() + result.getErrors().size());

            if (result.hasErrors()) {
                response.put("errors", result.getErrors());
                response.put("message", String.format("Импортировано %d записей. Ошибок: %d",
                        result.getSuccessCount(), result.getErrors().size()));
                return ResponseEntity.status(206).body(response); // Partial Content
            } else {
                response.put("message", String.format("Успешно импортировано %d показаний", result.getSuccessCount()));
                return ResponseEntity.ok(response);
            }

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Ошибка при импорте: " + e.getMessage());
        }
    }

    @PostMapping("/indications/addind")
    public ResponseEntity<?> addPayment(@RequestBody HouseAllReportDTO.IndicationsDTO ind) {
        // Логируем то, что реально прилетает
        System.out.println("Получены показания: " + ind);

        // Проверка лицевого счёта
        if (ind.getPersonalAccount() == null || ind.getPersonalAccount().isBlank()) {
            return ResponseEntity
                    .badRequest()
                    .body("Лицевой счёт обязателен");
        }

        try {
            String mes = indicationsRepository.insertInd(ind);
            return ResponseEntity.ok(mes);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity
                    .status(500)
                    .body("Ошибка при добавлении оплаты: " + e.getMessage());
        }
    }

}
