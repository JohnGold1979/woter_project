package com.example.WOTER.Controllers;

import com.example.WOTER.DTO.ExcelPaymentDTO;
import com.example.WOTER.Repository.EventRepository;
import com.example.WOTER.Repository.PaymentImportRepository;
import com.example.WOTER.Services.ExcelPaymentReader;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/upload")
public class ImportController {

    private final ExcelPaymentReader excelPaymentReader;
    private final PaymentImportRepository paymentImportRepository;
    private final EventRepository eventRepository;

    public ImportController(ExcelPaymentReader reader, PaymentImportRepository repo, EventRepository eventRepository) {
        this.excelPaymentReader = reader;
        this.paymentImportRepository = repo;
        this.eventRepository = eventRepository;
    }

    @PostMapping("/payments")
    public ResponseEntity<?> importPayments(@RequestParam("file") MultipartFile file) {
        try {
            List<ExcelPaymentDTO> payments = excelPaymentReader.readPayments(file.getInputStream(), file.getOriginalFilename());

            for (ExcelPaymentDTO p : payments) {
                paymentImportRepository.save(p);
            }

            // Логирование события о импорте
            eventRepository.saveEvent(
                "IMPORT",
                "Импорт оплат из файла: " + file.getOriginalFilename() + ", записей: " + payments.size(),
                LocalDateTime.now()
            );

            return ResponseEntity.ok("Импортировано записей: " + payments.size());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Ошибка: " + e.getMessage());
        }
    }
}
