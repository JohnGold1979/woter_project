package com.example.WOTER.DTO;
import lombok.Data;

@Data
public class SaldoTypeDTO {
    // Квартиры
    private Double rolledSaldoInFlat;
    private Double rolledSaldoOutFlat;
    private Double totalFlatChargedFlat;
    private Double totalFlatPaydFlat;

    // Частные дома
    private Double privateCharged;
    private Double privatePayd;
    private Double privateSaldo;

    // Водомеры
    private Double meterCharged;
    private Double meterPayd;
    private Double meterSaldo;
}
