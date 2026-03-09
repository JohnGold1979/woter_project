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

        //Итоги слева / справа по типам клиентов
        SaldoTypeDTO typeTotalFlat = saldoRepository.totalByTypes(month, year, stationId, 1);
        Double flatSaldoIn = typeTotalFlat.getRolledSaldoIn();
        Double flatSaldoOut = typeTotalFlat.getRolledSaldoOut();
        Double flatTotalCharged = typeTotalFlat.getTotalCharged();
        Double flatTotalPay = typeTotalFlat.getTotalPayd();

        model.addAttribute("totalFlatSaldoIn", flatSaldoIn);
        model.addAttribute("totalFlatSaldoOut", flatSaldoOut);
        model.addAttribute("totalFlatCharged", flatTotalCharged);
        model.addAttribute("totalFlatPayd", flatTotalPay);

        SaldoTypeDTO typeTotalPrivate = saldoRepository.totalByTypes(month, year, stationId, 2);
        Double privateSaldoIn = typeTotalFlat.getRolledSaldoIn();
        Double privateSaldoOut = typeTotalFlat.getRolledSaldoOut();
        Double privateTotalCharged = typeTotalFlat.getTotalCharged();
        Double privateTotalPay = typeTotalFlat.getTotalPayd();

        model.addAttribute("totalPrivateSaldoIn", privateSaldoIn);
        model.addAttribute("totalPrivateSaldoOut", privateSaldoOut);
        model.addAttribute("totalPrivateCharged", privateTotalCharged);
        model.addAttribute("totalPrivatePayd", privateTotalPay);

        SaldoTypeDTO typeTotalMeter = saldoRepository.totalByTypesMeter(month, year, stationId);
        Double materSaldoIn = typeTotalMeter.getRolledSaldoInMeter();
        Double materSaldoOut = typeTotalMeter.getRolledSaldoOutMeter();
        Double materTotalCharged = typeTotalMeter.getTotalChargedMeter();
        Double materTotalPay = typeTotalMeter.getTotalPaydMeter();

        model.addAttribute("totalMeterSaldoIn", materSaldoIn);
        model.addAttribute("totalMeterSaldoOut", materSaldoOut);
        model.addAttribute("totalMeterCharged", materTotalCharged);
        model.addAttribute("totalMeterPayd", materTotalPay);

        var totalSaldoIn = roundToTwoDecimals(flatSaldoIn + privateSaldoIn + materSaldoIn);
        var totalSaldoOut = roundToTwoDecimals(flatSaldoOut + privateSaldoOut + materSaldoOut);
        var totalCharged = roundToTwoDecimals(flatTotalCharged + privateTotalCharged + materTotalCharged);
        var totalPay = roundToTwoDecimals(flatTotalPay + privateTotalPay + materTotalPay);
        
        model.addAttribute("totalSaldoIn", totalSaldoIn);
        model.addAttribute("totalSaldoOut", totalSaldoOut);
        model.addAttribute("totalCharged", totalCharged);
        model.addAttribute("totalPayd", totalPay);


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

        // Основная таблица сальдо
        List<SaldoDTO> saldo = saldoRepository.findAll(month, year, stationId);
        model.addAttribute("saldos", saldo);

        // Название станции
        String stationName = saldoRepository.getStationNameById(stationId);
        model.addAttribute("stationName", stationName);
        model.addAttribute("stationId", stationId);

        // Общие итоги по всей станции
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

        // Итоги слева / справа по типам клиентов
        SaldoTypeDTO typeTotalFlat = saldoRepository.totalByTypes(month, year, stationId, 1);
        model.addAttribute("totalFlatSaldoIn", typeTotalFlat.getRolledSaldoIn());
        model.addAttribute("totalFlatSaldoOut", typeTotalFlat.getRolledSaldoOut());
        model.addAttribute("totalFlatCharged", typeTotalFlat.getTotalCharged());
        model.addAttribute("totalFlatPayd", typeTotalFlat.getTotalPayd());

        SaldoTypeDTO typeTotalPrivate = saldoRepository.totalByTypes(month, year, stationId, 2);
        model.addAttribute("totalPrivateCharged", typeTotalPrivate.getRolledSaldoIn());
        model.addAttribute("totalPrivatePayd", typeTotalPrivate.getRolledSaldoOut());
        model.addAttribute("totalPrivateSaldo", typeTotalPrivate.getTotalCharged());
        model.addAttribute("totalFlatPayd", typeTotalFlat.getTotalPayd());

        SaldoTypeDTO typeTotalMeter = saldoRepository.totalByTypesMeter(month, year, stationId);
        model.addAttribute("totalMeterSaldoIn", typeTotalMeter.getRolledSaldoInMeter());
        model.addAttribute("totalMeterSaldoOut", typeTotalMeter.getRolledSaldoOutMeter());
        model.addAttribute("totalMeterCharged", typeTotalMeter.getTotalChargedMeter());
        model.addAttribute("totalMeterPayd", typeTotalMeter.getTotalPaydMeter());

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

    public static double roundToTwoDecimals(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}