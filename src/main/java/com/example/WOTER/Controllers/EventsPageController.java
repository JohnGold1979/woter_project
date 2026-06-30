package com.example.WOTER.Controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class EventsPageController {

    @GetMapping("/events")
    public String eventsPage() {
        return "events";
    }
}