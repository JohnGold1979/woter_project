package com.example.WOTER.Controllers;

import com.example.WOTER.DTO.ClientDTO;
import com.example.WOTER.DTO.SaldoDTO;
import com.example.WOTER.DTO.SaldoTotalDTO;
import com.example.WOTER.DTO.SaldoTypeDTO;
import com.example.WOTER.Repository.SaldoRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/saldo")
public class SaldoController {

    private final SaldoRepository saldoRepository;

    public SaldoController(SaldoRepository saldoRepository) {
        this.saldoRepository = saldoRepository;
    }

    //@GetMapping("/saldo")
    @GetMapping
    public String getSaldo(@RequestParam(name = "month", required = false) Integer month,
                           @RequestParam(name = "year", required = false) Integer year,
                           Model model) {

        if (month == null) month = java.time.LocalDate.now().getMonthValue();
        if (year == null) year = java.time.LocalDate.now().getYear();
        model.addAttribute("saldos", List.of());
        return "saldo";
    }


    @GetMapping("/getBishSaldo") public String getBishSaldo(@RequestParam(name = "month", required = false) Integer month,
                                                            @RequestParam(name = "year", required = false) Integer year,
                                                            Model model){
        Integer stationId = 11;
        if (month == null) month = LocalDate.now().getMonthValue();
        if (year == null) year = LocalDate.now().getYear();


        // 1️⃣ Основная таблица сальдо
        List<SaldoDTO> saldo = saldoRepository.findAll(month, year, stationId);
        model.addAttribute("saldos", saldo);

        // 2️⃣ Название станции
        String stationName = saldoRepository.getStationNameById(stationId);
        model.addAttribute("stationName", stationName);
        model.addAttribute("stationId", stationId);

        // 3️⃣ Общие итоги по всей станции
        SaldoTotalDTO total = saldoRepository.totalSaldo(month, year, stationId);
        model.addAttribute("totalDebetIn", total.getDebetIn());
        model.addAttribute("totalCredetIn", total.getCredetIn());
        model.addAttribute("totalCharged", total.getChargedMoney());
        model.addAttribute("totalPaydIn", total.getPaydIn());
        model.addAttribute("totalTaxIn", total.getTaxIn());
        model.addAttribute("totalSubsidy", total.getSubsidy());
        model.addAttribute("totalTaxOut", total.getTaxOut());
        model.addAttribute("totalRemoval", total.getRemovalIn());
        model.addAttribute("totalDebetOut", total.getDebetOut());
        model.addAttribute("totalCredetOut", total.getCredetOut());
        model.addAttribute("totalPersonsOut", total.getPersonsOut());

        // 4️⃣ Итоги слева / справа по типам клиентов
        SaldoTypeDTO typeTotal = saldoRepository.totalByTypes(month, year, stationId);

        model.addAttribute("totalFlatCharged", typeTotal.getTotalFlatChargedFlat());
        model.addAttribute("totalFlatPayd", typeTotal.getTotalFlatPaydFlat());
        model.addAttribute("totalFlatSaldo", typeTotal.getRolledSaldoOutFlat());

        model.addAttribute("totalPrivateCharged", typeTotal.getPrivateCharged());
        model.addAttribute("totalPrivatePayd", typeTotal.getPrivatePayd());
        model.addAttribute("totalPrivateSaldo", typeTotal.getPrivateSaldo());

        model.addAttribute("totalMeterCharged", typeTotal.getMeterCharged());
        model.addAttribute("totalMeterPayd", typeTotal.getMeterPayd());
        model.addAttribute("totalMeterSaldo", typeTotal.getMeterSaldo());

        return "saldo";
    }

    @GetMapping("/getSaldoByStation")
    public String getSaldoByStation(@RequestParam(name = "month", required = false) Integer month,
                                    @RequestParam(name = "year", required = false) Integer year,
                                    @RequestParam("stationId") Integer stationId,
                                    Model model) {

        if (month == null) month = LocalDate.now().getMonthValue();
        if (year == null) year = LocalDate.now().getYear();
        if (stationId == null) stationId = 11;

        // 1️⃣ Основная таблица сальдо
        List<SaldoDTO> saldo = saldoRepository.findAll(month, year, stationId);
        model.addAttribute("saldos", saldo);

        // 2️⃣ Название станции
        String stationName = saldoRepository.getStationNameById(stationId);
        model.addAttribute("stationName", stationName);
        model.addAttribute("stationId", stationId);

        // 3️⃣ Общие итоги по всей станции
        SaldoTotalDTO total = saldoRepository.totalSaldo(month, year, stationId);
        model.addAttribute("totalDebetIn", total.getDebetIn());
        model.addAttribute("totalCredetIn", total.getCredetIn());
        model.addAttribute("totalCharged", total.getChargedMoney());
        model.addAttribute("totalPaydIn", total.getPaydIn());
        model.addAttribute("totalTaxIn", total.getTaxIn());
        model.addAttribute("totalSubsidy", total.getSubsidy());
        model.addAttribute("totalTaxOut", total.getTaxOut());
        model.addAttribute("totalRemoval", total.getRemovalIn());
        model.addAttribute("totalDebetOut", total.getDebetOut());
        model.addAttribute("totalCredetOut", total.getCredetOut());
        model.addAttribute("totalPersonsOut", total.getPersonsOut());

        // 4️⃣ Итоги слева / справа по типам клиентов
        SaldoTypeDTO typeTotal = saldoRepository.totalByTypes(month, year, stationId);

        model.addAttribute("totalFlatCharged", typeTotal.getTotalFlatChargedFlat());
        model.addAttribute("totalFlatPayd", typeTotal.getTotalFlatPaydFlat());
        model.addAttribute("totalFlatSaldo", typeTotal.getRolledSaldoOutFlat());

        model.addAttribute("totalPrivateCharged", typeTotal.getPrivateCharged());
        model.addAttribute("totalPrivatePayd", typeTotal.getPrivatePayd());
        model.addAttribute("totalPrivateSaldo", typeTotal.getPrivateSaldo());

        model.addAttribute("totalMeterCharged", typeTotal.getMeterCharged());
        model.addAttribute("totalMeterPayd", typeTotal.getMeterPayd());
        model.addAttribute("totalMeterSaldo", typeTotal.getMeterSaldo());

        return "saldo";
    }


    // ⚡ JSON-эндпоинт для fetch
    @GetMapping("/jumpMonth")
    public ResponseEntity<String> jumpMonth() {
        String result = saldoRepository.startJumpMonth();
        System.out.println("jumpMonth " + result);
        if (result == null || result.isEmpty()) {
            result = "Ошибка: " + result;
        }
        System.out.println("jumpMonth " + result);
        return ResponseEntity.ok(result);
    }
}