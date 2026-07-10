package com.example.WOTER.DTO;

import java.math.BigDecimal;

public class CounterReportDTO {
    private String persAccount;
    private String clientName;
    private String address;
    private String clientType;
    private BigDecimal indication;
    private BigDecimal m3;
    private BigDecimal summa;

    public CounterReportDTO(String persAccount, String clientName, String address, 
                           String clientType, BigDecimal indication, BigDecimal m3, BigDecimal summa) {
        this.persAccount = persAccount;
        this.clientName = clientName;
        this.address = address;
        this.clientType = clientType;
        this.indication = indication;
        this.m3 = m3;
        this.summa = summa;
    }

    public String getPersAccount() { return persAccount; }
    public void setPersAccount(String persAccount) { this.persAccount = persAccount; }
    public String getClientName() { return clientName; }
    public void setClientName(String clientName) { this.clientName = clientName; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getClientType() { return clientType; }
    public void setClientType(String clientType) { this.clientType = clientType; }
    public BigDecimal getIndication() { return indication; }
    public void setIndication(BigDecimal indication) { this.indication = indication; }
    public BigDecimal getM3() { return m3; }
    public void setM3(BigDecimal m3) { this.m3 = m3; }
    public BigDecimal getSumma() { return summa; }
    public void setSumma(BigDecimal summa) { this.summa = summa; }
}