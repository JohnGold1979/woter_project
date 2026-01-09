package com.example.WOTER.Controllers;

import com.example.WOTER.DTO.ExcelPaymentDTO;
import com.example.WOTER.Repository.PaymentImportRepository;
import com.example.WOTER.Services.ExcelPaymentReader;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/upload")
public class ImportController {

    private final ExcelPaymentReader excelPaymentReader;
    private final PaymentImportRepository paymentImportRepository;

    public ImportController(ExcelPaymentReader reader, PaymentImportRepository repo) {
        this.excelPaymentReader = reader;
        this.paymentImportRepository = repo;
    }

    @PostMapping("/payments")
    public ResponseEntity<?> importPayments(@RequestParam("file") MultipartFile file) {
        try {
            List<ExcelPaymentDTO> payments = excelPaymentReader.readPayments(file.getInputStream(), file.getOriginalFilename());

            for (ExcelPaymentDTO p : payments) {
                paymentImportRepository.save(p);
            }

            return ResponseEntity.ok("Импортировано записей: " + payments.size());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Ошибка: " + e.getMessage());
        }
    }
}
