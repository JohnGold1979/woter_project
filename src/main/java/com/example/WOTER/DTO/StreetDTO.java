package com.example.WOTER.DTO;

public class StreetDTO {
    private int streetId;
    private String streetName;

    public StreetDTO() {
        // пустой конструктор обязателен для Spring / Jackson
    }

    public StreetDTO(int streetId, String streetName) {
        this.streetId= streetId;
        this.streetName = streetName;
    }

    public int getStreetId() {
        return streetId;
    }

    public void seStreetId(int streetId) {
        this.streetId = streetId;
    }

    public String getStreetName() {
        return streetName;
    }

    public void setStreetName(String streetName) {
        this.streetName = streetName;
    }
}
