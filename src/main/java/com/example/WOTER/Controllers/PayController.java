package com.example.WOTER.Controllers;

import com.example.WOTER.DTO.PayDTO;
import com.example.WOTER.Repository.PayRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
public class PayController {
    private final PayRepository payRepository;

    public PayController(PayRepository payRepository) {
        this.payRepository = payRepository;
    }

    @GetMapping("/pay")
    public String getPay(@RequestParam(name = "month", required = false) Integer month,
                         @RequestParam(name = "year", required = false) Integer year,
                         Model model)
    {
        if (month == null) month = java.time.LocalDate.now().getMonthValue();
        if (year == null) year = java.time.LocalDate.now().getYear();

        System.out.println("Period " + month + " " + year);

        List<PayDTO> payList = payRepository.findAll(month, year);
        model.addAttribute("pays", payList);
        return "pay";
    }

    @GetMapping("/pay/totalPay")
    @ResponseBody
    public PayDTO totalPay(@RequestParam(name = "month", required = false) Integer month,
                           @RequestParam(name = "year", required = false) Integer year,
                           Model model) {

        if (month == null) month = java.time.LocalDate.now().getMonthValue();
        if (year == null) year = java.time.LocalDate.now().getYear();
       return payRepository.totalPay(month, year);
    }

}
