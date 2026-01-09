package com.example.WOTER.DTO;

import java.math.BigDecimal;
import java.util.Date;

public class PayDTO {
    private Long id;
    private Integer clientType;
    private Integer counterId;
    private String persAccount;
    private String clientName;
    private String flat;
    private String addressPop;
    private String addressHo;
    private Integer monthId;
    private Integer yearId;
    private BigDecimal taxIn;
    private BigDecimal amount;
    private String payDate;

    private BigDecimal payIn;
    private String payDateReestr;
    private BigDecimal totalAmount;
    private BigDecimal totalTaxIn;
    private BigDecimal totalPayIn;


    // ==== Getters and Setters ====
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public Integer getClientType() {
        return clientType;
    }
    public void setClientType(Integer clientType) {
        this.clientType = clientType;
    }
    public Integer getCounterId() {
        return counterId;
    }
    public void setCounterId(Integer counterId) {
        this.counterId = counterId;
    }

    public String getPersAccount() {
        return persAccount;
    }
    public void setPersAccount(String persAccount) {
        this.persAccount = persAccount;
    }

    public String getClientName() {
        return clientName;
    }
    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getFlat() {
        return flat;
    }
    public void setFlat(String flat) {
        this.flat = flat;
    }

    public String getAddressPop() {
        return addressPop;
    }
    public void setAddressPop(String addressPop) {
        this.addressPop = addressPop;
    }

    public String getAddressHo() {
        return addressHo;
    }
    public void setAddressHo(String addressHo) {
        this.addressHo = addressHo;
    }

    public Integer getMonthId() {
        return monthId;
    }
    public void setMonthId(Integer monthId) {
        this.monthId = monthId;
    }

    public Integer getYearId() {
        return yearId;
    }
    public void setYearId(Integer yearId) {
        this.yearId = yearId;
    }

    public BigDecimal getTaxIn() {
        return taxIn;
    }
    public void setTaxIn(BigDecimal taxIn) {
        this.taxIn = taxIn;
    }

    public BigDecimal getAmount() {
        return amount;
    }
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
    public String getPayDate() {
        return payDate;
    }
    public void setPayDate(String payDate) {
        this.payDate = payDate;
    }
    public BigDecimal getPayIn() {
        return payIn;
    }
    public void setPayIn(BigDecimal payIn) {
        this.payIn = payIn;
    }
    public String getPayDateReestr() { return payDateReestr;  }
    public void setPayDateReestr(String payDateReestr) {
        this.payDateReestr = payDateReestr;
    }
    public BigDecimal getTotalAmount() {
        return totalAmount;
    }
    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }
    public BigDecimal getTotalTaxIn() {
        return totalTaxIn;
    }
    public void setTotalTaxIn(BigDecimal totalTaxIn) {
        this.totalTaxIn = totalTaxIn;
    }
    public BigDecimal getTotalPayIn() {
        return totalPayIn;
    }
    public void setTotalPayIn(BigDecimal totalPayIn) {
        this.totalPayIn = totalPayIn;
    }

}
