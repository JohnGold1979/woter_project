package com.example.WOTER.Controllers;

import com.example.WOTER.DTO.HouseAllReportDTO;
import com.example.WOTER.Repository.IndicationsRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class IndicationsController {


    private final IndicationsRepository indicationsRepository;

    public IndicationsController(IndicationsRepository indicationsRepository) {
        this.indicationsRepository = indicationsRepository;
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
