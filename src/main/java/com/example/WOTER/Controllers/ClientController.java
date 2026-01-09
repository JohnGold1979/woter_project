package com.example.WOTER.Controllers;

import com.example.WOTER.DTO.*;
import com.example.WOTER.Repository.ClientRepository;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.math.BigDecimal;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/clients")
public class ClientController {

    private final ClientRepository clientRepository;
    private static final Logger logger = LoggerFactory.getLogger(ClientController.class);
    public ClientController(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    // HTML-страница со списком
    @GetMapping
    public String getClients(Model model) {
        model.addAttribute("clients", List.of()); // пустой список
        return "clients"; // шаблон templates/clients.html
    }

    @GetMapping("/allStations")
    @ResponseBody
    public ResponseEntity<?> getAllStations() {
        try {
            List<StationDTO> stations = clientRepository.getSatationsAll();
            logger.info("📡 Отправлено {} станций через /stations/allStations", stations.size());
            return ResponseEntity.ok(stations);
        } catch (Exception e) {
            logger.error("❌ Ошибка при запросе /stations/allStations: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError()
                    .body("Ошибка при загрузке станций: " + e.getMessage());
        }
    }

    @GetMapping("/getByStation")
    public String getClientsPage(@RequestParam("stationId") Integer stationId, Model model) {
        System.out.println("Пришло stationId = " + stationId);
        // список клиентов станции
        List<ClientDTO> clients = clientRepository.getByStation(stationId);
        model.addAttribute("clients", clients);

        // получить название станции
        String stationName = clientRepository.getStationNameById(stationId);
        model.addAttribute("stationName", stationName);
        model.addAttribute("stationId", stationId);

        return "clients";  // вернёт шаблон templates/clients.html
    }

    @GetMapping("/getBishClients")
    public String getBishClients(Model model) {
        Integer stationId = 11;
        List<ClientDTO> clients = clientRepository.findAll();
        model.addAttribute("clients", clients);

        String stationName = clientRepository.getStationNameById(stationId);
        model.addAttribute("stationName", stationName);
        model.addAttribute("stationId", stationId);
        return "clients"; // templates/clients.html
    }


    @GetMapping("/streets/findByPersAcc")
    @ResponseBody
    public ResponseEntity<?> findByPersAcc(@RequestParam String persAcc) {
        System.out.println("Пришло persAcc = " + persAcc);

        List<StreetDTO> streets = clientRepository.getAllStreets(persAcc);

        return ResponseEntity.ok(streets);
    }



    // ⚡ JSON-эндпоинт для fetch
    // Поиск по лицевому счёту
    @GetMapping("/{persAcc}")
    public ResponseEntity<ClientDTO> searchPersAcc(@PathVariable("persAcc") String persAcc) {
        System.out.println("Пришло persAcc = " + persAcc);
        ClientDTO client = clientRepository.findByPersAcc(persAcc);
        return client == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(client);
    }

    // ⚡ API для расчёта налога
    @GetMapping("/calc")
    public ResponseEntity<Map<String, Double>> calcTax(@RequestParam("amount") double amount) {
        TaxDTO tax = clientRepository.getActiveTax();

        double taxBasa = amount / (1 + tax.getTaxRate());
        double taxOut = amount - taxBasa;

        Map<String, Double> response = new HashMap<>();
        response.put("tax", taxOut);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/period/last")
    @ResponseBody
    public ResponseEntity<PeriodDTO> getLastOpenPeriod() {
        PeriodDTO period = clientRepository.findLastOpenPeriod();
        return ResponseEntity.ok(period);
    }

    @PostMapping("/payments")
    public ResponseEntity<?> addPayment(@RequestBody PaymentDTO payment) {
        // Логируем то, что реально прилетает
        System.out.println("Получен платёж: " + payment);

        // Проверка лицевого счёта
        if (payment.getPersAcc() == null || payment.getPersAcc().isBlank()) {
            return ResponseEntity
                    .badRequest()
                    .body("Лицевой счёт обязателен");
        }

        // Проверка суммы
        if (payment.getAmount() == null || payment.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            return ResponseEntity
                    .badRequest()
                    .body("Сумма должна быть больше 0");
        }

        try {
            String mes = clientRepository.insertPayment(payment);
            return ResponseEntity.ok(mes);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity
                    .status(500)
                    .body("Ошибка при добавлении оплаты: " + e.getMessage());
        }
    }

    @PostMapping("/paymentsSub")
    public ResponseEntity<?> addPaymentSub(@RequestBody PaymentDTO payment) {
        // Логируем то, что реально прилетает
        System.out.println("Получен платёж: " + payment);

        // Проверка лицевого счёта
        if (payment.getPersAcc() == null || payment.getPersAcc().isBlank()) {
            return ResponseEntity
                    .badRequest()
                    .body("Лицевой счёт обязателен");
        }

        // Проверка суммы
        if (payment.getAmount() == null || payment.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            return ResponseEntity
                    .badRequest()
                    .body("Сумма должна быть больше 0");
        }

        try {
            String mes = clientRepository.insertPaymentSub(payment);
            return ResponseEntity.ok(mes);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity
                    .status(500)
                    .body("Ошибка при добавлении оплаты: " + e.getMessage());
        }
    }

}
