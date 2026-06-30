package com.example.WOTER.DTO;

public class HouseDTO {
    private int houseId;
    private String house;
    private String houseName;
    private int streetId;
    private String streetName;
    private int stationId;

    public HouseDTO() {
        // пустой конструктор обязателен для Spring / Jackson
    }

    public HouseDTO(int houseId, String house) {
        this.houseId = houseId;
        this.house = house;
    }

    public int getHouseId() {
        return houseId;
    }

    public void setHouseId(int houseId) {
        this.houseId = houseId;
    }

    public String getHouse() {
        return house;
    }

    public void setHouse(String house) {
        this.house = house;
    }

    public int getStreetId() {
        return streetId;
    }

    public void setStreetId(int streetId) {
        this.streetId = streetId;
    }

    public String getStreetName() {
        return streetName;
    }

    public void setStreetName(String streetName) {
        this.streetName = streetName;
    }

    public int getStationId() {
        return stationId;
    }

    public void setStationId(int stationId) {
        this.stationId = stationId;
    }

    public String getHouseName() {
        return houseName;
    }

    public void setHouseName(String houseName) {
        this.houseName = houseName;
    }
}
