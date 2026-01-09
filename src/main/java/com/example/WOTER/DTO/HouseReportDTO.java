package com.example.WOTER.DTO;

import java.math.BigDecimal;

public class HouseReportDTO {
    private String houseName; // или streetName
    private BigDecimal debetIn;
    private BigDecimal creditIn;
    private BigDecimal chargedMoney;
    private BigDecimal taxIn;
    private BigDecimal paidIn;
    private BigDecimal taxOut;
    private BigDecimal removalIn;
    private BigDecimal remTaxIn;
    private BigDecimal debetOut;
    private BigDecimal creditOut;
    private BigDecimal subsidyIn;

    // конструктор
    public HouseReportDTO(String houseName, BigDecimal debetIn, BigDecimal creditIn, BigDecimal chargedMoney,
                          BigDecimal taxIn, BigDecimal paidIn, BigDecimal taxOut, BigDecimal removalIn,
                          BigDecimal remTaxIn, BigDecimal debetOut, BigDecimal creditOut, BigDecimal subsidyIn) {
        this.houseName = houseName;
        this.debetIn = debetIn;
        this.creditIn = creditIn;
        this.chargedMoney = chargedMoney;
        this.taxIn = taxIn;
        this.paidIn = paidIn;
        this.taxOut = taxOut;
        this.removalIn = removalIn;
        this.remTaxIn = remTaxIn;
        this.debetOut = debetOut;
        this.creditOut = creditOut;
        this.subsidyIn = subsidyIn;
    }

    // геттеры + сеттеры
    public String getHouseName() { return houseName; }
    public void setHouseName(String houseName) { this.houseName = houseName; }

    public BigDecimal getDebetIn() { return debetIn; }
    public void setDebetIn(BigDecimal debetIn) { this.debetIn = debetIn; }

    public BigDecimal getCreditIn() { return creditIn; }
    public void setCreditIn(BigDecimal creditIn) { this.creditIn = creditIn; }

    public BigDecimal getChargedMoney() { return chargedMoney; }
    public void setChargedMoney(BigDecimal chargedMoney) { this.chargedMoney = chargedMoney; }

    public BigDecimal getTaxIn() { return taxIn; }
    public void setTaxIn(BigDecimal taxIn) { this.taxIn = taxIn; }

    public BigDecimal getPaidIn() { return paidIn; }
    public void setPaidIn(BigDecimal paidIn) { this.paidIn = paidIn; }

    public BigDecimal getTaxOut() { return taxOut; }
    public void setTaxOut(BigDecimal taxOut) { this.taxOut = taxOut; }

    public BigDecimal getRemovalIn() { return removalIn; }
    public void setRemovalIn(BigDecimal removalIn) { this.removalIn = removalIn; }

    public BigDecimal getRemTaxIn() { return remTaxIn; }
    public void setRemTaxIn(BigDecimal remTaxIn) { this.remTaxIn = remTaxIn; }

    public BigDecimal getDebetOut() { return debetOut; }
    public void setDebetOut(BigDecimal debetOut) { this.debetOut = debetOut; }

    public BigDecimal getCreditOut() { return creditOut; }
    public void setCreditOut(BigDecimal creditOut) { this.creditOut = creditOut; }

    public BigDecimal getSubsidyIn() { return subsidyIn; }
    public void setSubsidyIn(BigDecimal subsidyIn) { this.subsidyIn = subsidyIn; }
}
