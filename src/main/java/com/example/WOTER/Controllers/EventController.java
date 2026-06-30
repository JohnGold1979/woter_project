package com.example.WOTER.Controllers;

import com.example.WOTER.DTO.EventDTO;
import com.example.WOTER.Services.EventService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/events")
public class EventController {

    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @GetMapping("/recent")
    public ResponseEntity<List<EventDTO>> getRecentEvents() {
        List<EventDTO> events = eventService.getRecentEvents();
        return ResponseEntity.ok(events);
    }

    @GetMapping
    public ResponseEntity<List<EventDTO>> getEvents(@org.springframework.web.bind.annotation.RequestParam(required = false) String from,
                                                    @org.springframework.web.bind.annotation.RequestParam(required = false) String to) {
        java.time.LocalDateTime fromDt = parseDateTime(from, java.time.LocalDateTime.now().minusMonths(1));
        java.time.LocalDateTime toDt = parseDateTime(to, java.time.LocalDateTime.now());
        List<EventDTO> events = eventService.getEvents(fromDt, toDt);
        return ResponseEntity.ok(events);
    }

    private java.time.LocalDateTime parseDateTime(String value, java.time.LocalDateTime defaultVal) {
        if (value == null || value.isBlank()) return defaultVal;
        try {
            return java.time.LocalDateTime.parse(value);
        } catch (Exception e) {
            try {
                return java.time.LocalDate.parse(value).atStartOfDay();
            } catch (Exception ex) {
                return defaultVal;
            }
        }
    }
}