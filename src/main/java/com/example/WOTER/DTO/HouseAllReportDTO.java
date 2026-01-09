package com.example.WOTER.DTO;

import java.math.BigDecimal;

public class HouseAllReportDTO {
    private String houseName;
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

    public HouseAllReportDTO(String houseName,
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
        this.houseName = houseName;
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

    public String getHouseName() { return houseName; }
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

    public static class IndicationsDTO {
        private Long id;
        private Integer clientTypeId;
        private Integer counterInId;
        private String personalAccount;
        private String clientName;
        private Integer cntPersResult;
        private Long houseId;
        private Long streetId;
        private String flat;
        private String addressPop;
        private String addressHo;

        private Integer monthId;
        private Integer indication;
        private Integer m3;
        private BigDecimal tariff;
        private BigDecimal summa;
        private Integer yearId;

        // --- Getters & Setters ---
        public Long getId() {
            return id;
        }
        public void setId(Long id) {
            this.id = id;
        }

        public Integer getClientTypeId() {
            return clientTypeId;
        }
        public void setClientTypeId(Integer clientTypeId) {
            this.clientTypeId = clientTypeId;
        }

        public Integer getCounterInId() {
            return counterInId;
        }
        public void setCounterInId(Integer counterInId) {
            this.counterInId = counterInId;
        }

        public String getPersonalAccount() {
            return personalAccount;
        }
        public void setPersonalAccount(String personalAccount) {
            this.personalAccount = personalAccount;
        }

        public String getClientName() {
            return clientName;
        }
        public void setClientName(String clientName) {
            this.clientName = clientName;
        }

        public Integer getCntPersResult() {
            return cntPersResult;
        }
        public void setCntPersResult(Integer cntPersResult) {
            this.cntPersResult = cntPersResult;
        }

        public Long getHouseId() {
            return houseId;
        }
        public void setHouseId(Long houseId) {
            this.houseId = houseId;
        }

        public Long getStreetId() {
            return streetId;
        }
        public void setStreetId(Long streetId) {
            this.streetId = streetId;
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
        public void setYaerId(Integer yearId) {
            this.yearId = yearId;
        }

        public Integer getIndication() {
            return indication;
        }
        public void setIndication(Integer indication) {
            this.indication = indication;
        }

        public Integer getM3() {
            return m3;
        }
        public void setM3(Integer m3) {
            this.m3 = m3;
        }

        public BigDecimal getTariff() {
            return tariff;
        }
        public void setTariff(BigDecimal tariff) {
            this.tariff = tariff;
        }

        public BigDecimal getSumma() {
            return summa;
        }
        public void setSumma(BigDecimal summa) {
            this.summa = summa;
        }

    }
}
