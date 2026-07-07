package com.example.WOTER.DTO;

public class DislocationDTO {
    private String name;      // street name or house name
    private Integer peopleCount;

    public DislocationDTO(String name, Integer peopleCount) {
        this.name = name;
        this.peopleCount = peopleCount;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getPeopleCount() {
        return peopleCount;
    }

    public void setPeopleCount(Integer peopleCount) {
        this.peopleCount = peopleCount;
    }
}