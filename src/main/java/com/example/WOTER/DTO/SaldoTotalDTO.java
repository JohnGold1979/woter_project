package com.example.WOTER.DTO;
import lombok.Data;

@Data
public class SaldoTotalDTO {
    private Double debetIn;
    private Double credetIn;
    private Double chargedMoney;
    private Double paydIn;
    private Double taxIn;
    private Double subsidy;
    private Double taxOut;
    private Double removalIn;
    private Double debetOut;
    private Double credetOut;

    private Integer personsOut;
}
