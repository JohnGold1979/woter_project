package com.example.WOTER.Controllers;

import com.example.WOTER.DTO.HouseDTO;
import com.example.WOTER.DTO.StationDTO;
import com.example.WOTER.DTO.StreetDTO;
import com.example.WOTER.DTO.TariffDTO;
import com.example.WOTER.Repository.ClientRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/dictionary")
public class DictionaryController {

    private final ClientRepository clientRepository;

    public DictionaryController(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    @GetMapping("/{type}")
    public String dictionaryPage(@PathVariable String type, Model model) {
        model.addAttribute("pageType", type);
        String title;
        switch (type) {
            case "stations" -> title = "Станции";
            case "streets" -> title = "Улицы";
            case "houses" -> title = "Дома";
            case "tariffs" -> title = "Тарифы";
            default -> title = "Справочник";
        }
        model.addAttribute("title", title);
        return "dictionary";
    }

    @GetMapping("/list/stations")
    @ResponseBody
    public List<StationDTO> getStations() {
        return clientRepository.getAllStations();
    }

    @GetMapping("/list/streets")
    @ResponseBody
    public List<StreetDTO> getStreets() {
        return clientRepository.getAllStreetsAll();
    }

    @GetMapping("/list/houses")
    @ResponseBody
    public List<HouseDTO> getHouses() {
        return clientRepository.getAllHousesAll();
    }

    @GetMapping("/list/tariffs")
    @ResponseBody
    public List<TariffDTO> getTariffs() {
        return clientRepository.getAllTariffs();
    }

    // ===== Stations =====
    @PostMapping("/stations/save")
    @ResponseBody
    public ResponseEntity<?> saveStation(@RequestBody Map<String, String> data) {
        try {
            clientRepository.saveStation(data.get("stationName"));
            return ResponseEntity.ok("Станция добавлена");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Ошибка: " + e.getMessage());
        }
    }

    @PostMapping("/stations/update")
    @ResponseBody
    public ResponseEntity<?> updateStation(@RequestBody Map<String, Object> data) {
        try {
            Long id = Long.parseLong(data.get("stationId").toString());
            String name = data.get("stationName").toString();
            clientRepository.updateStation(id, name);
            return ResponseEntity.ok("Станция обновлена");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Ошибка: " + e.getMessage());
        }
    }

    @DeleteMapping("/stations/delete/{id}")
    @ResponseBody
    public ResponseEntity<?> deleteStation(@PathVariable Long id) {
        try {
            clientRepository.deleteStation(id);
            return ResponseEntity.ok("Станция удалена");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Ошибка: " + e.getMessage());
        }
    }

    // ===== Streets =====
    @PostMapping("/streets/save")
    @ResponseBody
    public ResponseEntity<?> saveStreet(@RequestBody Map<String, Object> data) {
        try {
            String name = data.get("streetName").toString();
            Integer stationId = Integer.parseInt(data.get("stationId").toString());
            clientRepository.saveStreet(name, stationId);
            return ResponseEntity.ok("Улица добавлена");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Ошибка: " + e.getMessage());
        }
    }

    @PostMapping("/streets/update")
    @ResponseBody
    public ResponseEntity<?> updateStreet(@RequestBody Map<String, Object> data) {
        try {
            Integer streetId = Integer.parseInt(data.get("streetId").toString());
            String name = data.get("streetName").toString();
            Integer stationId = Integer.parseInt(data.get("stationId").toString());
            clientRepository.updateStreet(streetId, name, stationId);
            return ResponseEntity.ok("Улица обновлена");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Ошибка: " + e.getMessage());
        }
    }

    @DeleteMapping("/streets/delete/{id}")
    @ResponseBody
    public ResponseEntity<?> deleteStreet(@PathVariable Integer id) {
        try {
            clientRepository.deleteStreet(id);
            return ResponseEntity.ok("Улица удалена");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Ошибка: " + e.getMessage());
        }
    }

    // ===== Houses =====
    @PostMapping("/houses/save")
    @ResponseBody
    public ResponseEntity<?> saveHouse(@RequestBody Map<String, Object> data) {
        try {
            String house = data.get("house").toString();
            Integer streetId = Integer.parseInt(data.get("streetId").toString());
            Integer stationId = Integer.parseInt(data.get("stationId").toString());
            clientRepository.saveHouse(house, streetId, stationId);
            return ResponseEntity.ok("Дом добавлен");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Ошибка: " + e.getMessage());
        }
    }

    @PostMapping("/houses/update")
    @ResponseBody
    public ResponseEntity<?> updateHouse(@RequestBody Map<String, Object> data) {
        try {
            Integer houseId = Integer.parseInt(data.get("houseId").toString());
            String house = data.get("house").toString();
            Integer streetId = Integer.parseInt(data.get("streetId").toString());
            Integer stationId = Integer.parseInt(data.get("stationId").toString());
            clientRepository.updateHouse(houseId, house, streetId, stationId);
            return ResponseEntity.ok("Дом обновлён");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Ошибка: " + e.getMessage());
        }
    }

    @DeleteMapping("/houses/delete/{id}")
    @ResponseBody
    public ResponseEntity<?> deleteHouse(@PathVariable Integer id) {
        try {
            clientRepository.deleteHouse(id);
            return ResponseEntity.ok("Дом удалён");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Ошибка: " + e.getMessage());
        }
    }

    // ===== Tariffs =====
    @PostMapping("/tariffs/save")
    @ResponseBody
    public ResponseEntity<?> saveTariff(@RequestBody Map<String, Object> data) {
        try {
            String name = data.get("tariffName").toString();
            Double rate = Double.parseDouble(data.get("tariffRate").toString());
            Integer status = Integer.parseInt(data.get("statusId").toString());
            clientRepository.saveTariff(name, rate, status);
            return ResponseEntity.ok("Тариф добавлен");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Ошибка: " + e.getMessage());
        }
    }

    @PostMapping("/tariffs/update")
    @ResponseBody
    public ResponseEntity<?> updateTariff(@RequestBody Map<String, Object> data) {
        try {
            Integer id = Integer.parseInt(data.get("tariffId").toString());
            String name = data.get("tariffName").toString();
            Double rate = Double.parseDouble(data.get("tariffRate").toString());
            Integer status = Integer.parseInt(data.get("statusId").toString());
            clientRepository.updateTariff(id, name, rate, status);
            return ResponseEntity.ok("Тариф обновлён");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Ошибка: " + e.getMessage());
        }
    }

    @DeleteMapping("/tariffs/delete/{id}")
    @ResponseBody
    public ResponseEntity<?> deleteTariff(@PathVariable Integer id) {
        try {
            clientRepository.deleteTariff(id);
            return ResponseEntity.ok("Тариф удалён");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Ошибка: " + e.getMessage());
        }
    }
}