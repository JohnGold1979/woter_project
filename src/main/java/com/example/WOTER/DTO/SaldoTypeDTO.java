package com.example.WOTER.DTO;
import lombok.Data;

@Data
public class SaldoTypeDTO {

    private Double rolledSaldoIn;
    private Double rolledSaldoOut;
    private Double totalCharged;
    private Double totalPayd;

   // Водомеры
    private Double rolledSaldoInMeter;
    private Double rolledSaldoOutMeter;
    private Double totalChargedMeter;
    private Double totalPaydMeter;
}
