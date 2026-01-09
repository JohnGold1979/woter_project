package com.example.WOTER.DTO;

public class HouseDTO {
    private int houseId;
    private String houseName;

    public HouseDTO() {
        // пустой конструктор обязателен для Spring / Jackson
    }

    public HouseDTO(int houseId, String houseName) {
        this.houseId = houseId;
        this.houseName = houseName;
    }

    public int getHouseId() {
        return houseId;
    }

    public void setHouseId(int houseId) {
        this.houseId = houseId;
    }

    public String getHouseName() {
        return houseName;
    }

    public void setHouseName(String houseName) {
        this.houseName = houseName;
    }
}
