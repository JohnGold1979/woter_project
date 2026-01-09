package com.example.WOTER.DTO;

import java.math.BigDecimal;

public class ReportRowDTO {

    private String flat;
    private String persAccount;
    private String clientName;
    private String address;
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


    // Конструктор для агрегированных отчётов
    public ReportRowDTO(String flat, String persAccount,String clientName,String address, BigDecimal debetIn, BigDecimal credetIn, BigDecimal chargedMoney,
                        BigDecimal taxIn, BigDecimal paydIn, BigDecimal taxOut,
                        BigDecimal removalIn, BigDecimal remTaxIn, BigDecimal debetOut,
                        BigDecimal credetOut, BigDecimal subsidy) {
        this.flat = flat;
        this.persAccount = persAccount;
        this.clientName = clientName;
        this.address = address;
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


    public String getFlat() { return flat; }
    public void setFlat(String flat) { this.flat = flat; }
    public String getPersAccount() { return persAccount; }
    public void setPersAccount(String persAccount) { this.persAccount = persAccount; }

    public String getClientName() { return clientName; }
    public void setClientName(String clientName) { this.clientName = clientName; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    // геттеры + сеттеры
    public BigDecimal getDebetIn() { return debetIn; }
    public void setDebetIn(BigDecimal debetIn) { this.debetIn = debetIn; }

    public BigDecimal getCredetIn() { return credetIn; }
    public void setCredetIn(BigDecimal credetIn) { this.credetIn = credetIn; }

    public BigDecimal getChargedMoney() { return chargedMoney; }
    public void setChargedMoney(BigDecimal chargedMoney) { this.chargedMoney = chargedMoney; }

    public BigDecimal getTaxIn() { return taxIn; }
    public void setTaxIn(BigDecimal taxIn) { this.taxIn = taxIn; }

    public BigDecimal getPaydIn() { return paydIn; }
    public void setPaydIn(BigDecimal paydIn) { this.paydIn = paydIn; }

    public BigDecimal getTaxOut() { return taxOut; }
    public void setTaxOut(BigDecimal taxOut) { this.taxOut = taxOut; }

    public BigDecimal getRemovalIn() { return removalIn; }
    public void setRemovalIn(BigDecimal removalIn) { this.removalIn = removalIn; }

    public BigDecimal getRemTaxIn() { return remTaxIn; }
    public void setRemTaxIn(BigDecimal remTaxIn) { this.remTaxIn = remTaxIn; }

    public BigDecimal getDebetOut() { return debetOut; }
    public void setDebetOut(BigDecimal debetOut) { this.debetOut = debetOut; }

    public BigDecimal getCredetOut() { return credetOut; }
    public void setCredetOut(BigDecimal credetOut) { this.credetOut = credetOut; }

    public BigDecimal getSubsidy() { return subsidy; }
    public void setSubsidy(BigDecimal subsidy) { this.subsidy = subsidy; }





}
