package com.example.WOTER.DTO;

import java.math.BigDecimal;

public class PayAllReportDTO {
    private String days;
    private BigDecimal amount;
    private BigDecimal taxIn;
    private BigDecimal payIn;


    public PayAllReportDTO(String days,
                              BigDecimal amount,
                              BigDecimal taxIn,
                              BigDecimal payIn) {
        this.days = days;
        this.amount = amount;
        this.taxIn = taxIn;
        this.payIn = payIn;

    }

    public String getDays() { return days; }
    public BigDecimal getAmount() { return amount; }
    public BigDecimal getTaxIn() { return taxIn; }
    public BigDecimal getPayIn() { return payIn; }


}
