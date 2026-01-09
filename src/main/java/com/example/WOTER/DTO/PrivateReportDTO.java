package com.example.WOTER.DTO;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

public class PrivateReportDTO {
    private Long id;
    private String flat;
    private String persAccount;
    private String clientName;
    private String addressPop;
    private String addressHo;

    private BigDecimal debetIn;
    private BigDecimal creditIn;
    private BigDecimal chargedMoney;
    private BigDecimal taxIn;
    private BigDecimal paidIn;
    private BigDecimal taxOut;
    private BigDecimal debetOut;
    private BigDecimal creditOut;
    private BigDecimal subsidyIn;
    private BigDecimal removalIn;
    private BigDecimal remTaxIn;

    private Integer monthId;
    private Integer yearId;
    private LocalDate dateCalc;

    // конструктор (можно перегрузить под разные варианты)
    public PrivateReportDTO(Long id, String flat, String persAccount, String clientName, String addressPop, String addressHo,
                            BigDecimal debetIn, BigDecimal creditIn, BigDecimal chargedMoney, BigDecimal taxIn,
                            BigDecimal paidIn, BigDecimal taxOut, BigDecimal debetOut, BigDecimal creditOut,
                            BigDecimal subsidyIn, BigDecimal removalIn, BigDecimal remTaxIn,
                            Integer monthId, Integer yearId, LocalDate dateCalc) {
        this.id = id;
        this.flat = flat;
        this.persAccount = persAccount;
        this.clientName = clientName;
        this.addressPop = addressPop;
        this.addressHo = addressHo;
        this.debetIn = debetIn;
        this.creditIn = creditIn;
        this.chargedMoney = chargedMoney;
        this.taxIn = taxIn;
        this.paidIn = paidIn;
        this.taxOut = taxOut;
        this.debetOut = debetOut;
        this.creditOut = creditOut;
        this.subsidyIn = subsidyIn;
        this.removalIn = removalIn;
        this.remTaxIn = remTaxIn;
        this.monthId = monthId;
        this.yearId = yearId;
        this.dateCalc = dateCalc;
    }

    // геттеры/сеттеры (можно сгенерировать через Lombok @Data)
    // ...
}
