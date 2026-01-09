package com.example.WOTER.DTO;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ExcelPaymentDTO {
    private LocalDateTime payDate;
    private String persAcc;
    private BigDecimal amount;

    // геттеры и сеттеры
    public LocalDateTime getPayDate() { return payDate; }
    public void setPayDate(LocalDateTime payDate) { this.payDate = payDate; }

    public String getPersAcc() { return persAcc; }
    public void setPersAcc(String persAcc) { this.persAcc = persAcc; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
}
