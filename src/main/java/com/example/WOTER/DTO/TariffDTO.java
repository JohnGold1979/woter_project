package com.example.WOTER.DTO;

public class TariffDTO {
    private Integer tariffId;
    private String tariffName;
    private Double tariffRate;
    private Integer statusId;

    public TariffDTO() {
    }

    public TariffDTO(Integer tariffId, String tariffName, Double tariffRate, Integer statusId) {
        this.tariffId = tariffId;
        this.tariffName = tariffName;
        this.tariffRate = tariffRate;
        this.statusId = statusId;
    }

    public Integer getTariffId() {
        return tariffId;
    }

    public void setTariffId(Integer tariffId) {
        this.tariffId = tariffId;
    }

    public String getTariffName() {
        return tariffName;
    }

    public void setTariffName(String tariffName) {
        this.tariffName = tariffName;
    }

    public Double getTariffRate() {
        return tariffRate;
    }

    public void setTariffRate(Double tariffRate) {
        this.tariffRate = tariffRate;
    }

    public Integer getStatusId() {
        return statusId;
    }

    public void setStatusId(Integer statusId) {
        this.statusId = statusId;
    }
}