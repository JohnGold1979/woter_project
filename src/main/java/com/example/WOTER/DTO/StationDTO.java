package com.example.WOTER.DTO;

public class StationDTO {

    private Long stationId;
    private String stationName;

    // геттеры и сеттеры
    public long getStationId() {
        return stationId;
    }

    public void setStationId(long stationId) {
        this.stationId = stationId;
    }

    public String getStationName() {
        return stationName;
    }

    public void setStationName(String stationName) {
        this.stationName = stationName;
    }
}
