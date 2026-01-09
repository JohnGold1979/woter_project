package com.example.WOTER.DTO;

import java.math.BigDecimal;

public class StreetAllReportDTO {
    private String streetName;
    private BigDecimal debetIn;
    private BigDecimal credetIn;
    private BigDecimal chargedMoney;
    private BigDecimal taxIn;
    private BigDecimal paydIn;
    private BigDecimal taxOut;
    private BigDecimal removalIn;
    private BigDecimal remTaxIn;
    private BigDecimal debetOut;
    private BigDecimal credetOut;
    private BigDecimal subsidy;

    public StreetAllReportDTO(String streetName,
                             BigDecimal debetIn,
                             BigDecimal credetIn,
                             BigDecimal chargedMoney,
                             BigDecimal taxIn,
                             BigDecimal paydIn,
                             BigDecimal taxOut,
                             BigDecimal removalIn,
                             BigDecimal remTaxIn,
                             BigDecimal debetOut,
                             BigDecimal credetOut,
                             BigDecimal subsidy) {
        this.streetName = streetName;
        this.debetIn = debetIn;
        this.credetIn = credetIn;
        this.chargedMoney = chargedMoney;
        this.taxIn = taxIn;
        this.paydIn = paydIn;
        this.taxOut = taxOut;
        this.removalIn = removalIn;
        this.remTaxIn = remTaxIn;
        this.debetOut = debetOut;
        this.credetOut = credetOut;
        this.subsidy = subsidy;
    }

    public String getStreetName() { return streetName; }
    public BigDecimal getDebetIn() { return debetIn; }
    public BigDecimal getCredetIn() { return credetIn; }
    public BigDecimal getChargedMoney() { return chargedMoney; }
    public BigDecimal getTaxIn() { return taxIn; }
    public BigDecimal getPaydIn() { return paydIn; }
    public BigDecimal getTaxOut() { return taxOut; }
    public BigDecimal getRemovalIn() { return removalIn; }
    public BigDecimal getRemTaxIn() { return remTaxIn; }
    public BigDecimal getDebetOut() { return debetOut; }
    public BigDecimal getCredetOut() { return credetOut; }
    public BigDecimal getSubsidy() { return subsidy; }
}
