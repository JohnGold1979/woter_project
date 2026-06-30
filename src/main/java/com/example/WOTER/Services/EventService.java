package com.example.WOTER.Services;

import com.example.WOTER.DTO.EventDTO;
import com.example.WOTER.Repository.EventRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class EventService {

    private final EventRepository eventRepository;

    public EventService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    public List<EventDTO> getRecentEvents() {
        LocalDateTime since = LocalDateTime.now().minusMinutes(10);
        return eventRepository.findRecentEvents(since);
    }

    public List<EventDTO> getEvents(LocalDateTime from, LocalDateTime to) {
        return eventRepository.findEvents(from, to);
    }
}