package com.example.WOTER;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ReportController {

    @GetMapping("/reports")
    public String reportsPage() {
        return "reports"; // вернет reports.html
    }
}
