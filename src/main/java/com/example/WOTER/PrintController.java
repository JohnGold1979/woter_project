package com.example.WOTER;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
@Controller
public class PrintController {

    private static final List<String> MONTHS = Arrays.asList(
            "Январь","Февраль","Март","Апрель","Май","Июнь",
            "Июль","Август","Сентябрь","Октябрь","Ноябрь","Декабрь"
    );

    @GetMapping("/print/select")
    public String selectPrint(
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Integer year,
            Model model) {

        LocalDate now = LocalDate.now();
        if (month == null) month = now.getMonthValue();
        if (year == null) year = now.getYear();

        // список годов (например последние 5 лет + текущий + +1)
        List<Integer> years = IntStream.rangeClosed(now.getYear() - 5, now.getYear() + 1)
                .boxed().collect(Collectors.toList());

        model.addAttribute("months", MONTHS);
        model.addAttribute("years", years);
        model.addAttribute("selectedMonth", month);
        model.addAttribute("selectedYear", year);

        return "print-select"; // templates/print-select.html
    }

    // Заглушки — страницы/эндпоинты для каждого типа печати.
    // Здесь можно генерировать PDF или рендерить форму с параметрами.

    @GetMapping("/print/wodometers")
    public String printWodometers(@RequestParam int month, @RequestParam int year, Model model) {
        model.addAttribute("month", month);
        model.addAttribute("year", year);

        return "print-wodometers"; // templates/print-wodometers.html
    }
}
