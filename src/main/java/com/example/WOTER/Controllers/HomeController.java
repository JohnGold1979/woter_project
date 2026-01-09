package com.example.WOTER.Controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/home")
    public String home() {
    return "home";


    //@GetMapping("/")
    //public String home() {
        // если хочешь чтобы адрес менялся
        // return "redirect:/clients";

        // если хочешь чтобы адрес оставался "/"
      //  return "forward:/clients";
    }
}