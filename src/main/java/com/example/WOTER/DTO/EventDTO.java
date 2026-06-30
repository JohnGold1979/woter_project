package com.example.WOTER.DTO;

import java.time.LocalDateTime;

public class EventDTO {
    private Long id;
    private String eventType; // PAYMENT, SUBSIDY, SALDO, etc.
    private String description;
    private LocalDateTime eventTime;

    public EventDTO() {
    }

    public EventDTO(Long id, String eventType, String description, LocalDateTime eventTime) {
        this.id = id;
        this.eventType = eventType;
        this.description = description;
        this.eventTime = eventTime;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getEventTime() {
        return eventTime;
    }

    public void setEventTime(LocalDateTime eventTime) {
        this.eventTime = eventTime;
    }
}