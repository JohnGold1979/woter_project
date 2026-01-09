package com.example.WOTER.Controllers;


import com.example.WOTER.DTO.PayDTO;
import com.example.WOTER.DTO.SubsidyDTO;
import com.example.WOTER.Repository.SubsidyRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
public class SubsidyController {
        private final SubsidyRepository subsidyRepository;

        public SubsidyController(SubsidyRepository subsidyRepository) {
            this.subsidyRepository = subsidyRepository;
        }

        @GetMapping("/subsidy")
        public String getPay(@RequestParam(name = "month", required = false) Integer month,
                             @RequestParam(name = "year", required = false) Integer year,
                             Model model) {

            if (month == null) month = java.time.LocalDate.now().getMonthValue();
            if (year == null) year = java.time.LocalDate.now().getYear();

            List<SubsidyDTO> payList = subsidyRepository.findAll(month, year); // пока жёстко
            model.addAttribute("subsidys", payList);
            return "subsidy";
        }

    @GetMapping("/subsidy/totalSubsidy")
    @ResponseBody
    public SubsidyDTO totalSubsidy(@RequestParam(name = "month", required = false) Integer month,
                               @RequestParam(name = "year", required = false) Integer year,
                               Model model) {

        if (month == null) month = java.time.LocalDate.now().getMonthValue();
        if (year == null) year = java.time.LocalDate.now().getYear();
        return subsidyRepository.totalSubsidy(month, year);
    }

}
