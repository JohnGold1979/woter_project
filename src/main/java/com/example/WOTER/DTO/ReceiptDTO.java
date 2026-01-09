package com.example.WOTER.DTO;

public class ReceiptDTO {
    private Long id;
    private String flat;
    private String persAccount;
    private String clientName;
    private Integer clientTypeId;
    private String addressPop;
    private String addressHo;
    private Double debetIn;
    private Double creditIn;
    private Double chargedMoney;
    private Double taxIn;
    private Double paidIn;
    private Double taxOut;
    private Double debetOut;
    private Double creditOut;
    private Double subsidyIn;
    private Integer monthId;
    private Integer yearId;
    private Double summa;
    private Integer personsOut;
    private Integer persFactOut;
    private Integer persResultOut;
    private Double summaTax;
    private Double tax;
    private String taxPrcent;
    private Integer rownum;
    private Double tarif;
    private String address;
    private String monthPrev;
    private String monthCurr;
    private String indicationPrev;
    private String indicationCurr;
    private Integer m3;
    private Double sumIndication;


    // Пустой конструктор (обязателен для Spring/JPA)
    public ReceiptDTO() {}

    // Полный конструктор
    public ReceiptDTO(Long id, String flat, String persAccount, String clientName,
                      Integer clientTypeId, String addressPop, String addressHo,
                      Double debetIn, Double creditIn, Double chargedMoney,
                      Double taxIn, Double paidIn, Double taxOut, Double debetOut,
                      Double creditOut, Double subsidyIn, Integer monthId,
                      Integer yearId, Double summa, Integer personsOut,
                      Integer persFactOut, Integer persResultOut, Double summaTax, Double tax,
                      String taxPrcent, Integer rownum, Double tarif, String address,
                      String monthPrev, String monthCurr, String indicationPrev, String indicationCurr,
                      Integer m3, Double sumIndication) {
        this.id = id;
        this.flat = flat;
        this.persAccount = persAccount;
        this.clientName = clientName;
        this.clientTypeId = clientTypeId;
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
        this.monthId = monthId;
        this.yearId = yearId;
        this.summa = summa;
        this.personsOut = personsOut;
        this.persFactOut = persFactOut;
        this.persResultOut = persResultOut;
        this.summaTax = summaTax;
        this.tax = tax;
        this.taxPrcent = taxPrcent;
        this.rownum = rownum;
        this.tarif = tarif;
        this.address = address;
        this.monthPrev = monthPrev;
        this.monthCurr = monthCurr;
        this.indicationPrev = indicationPrev;
        this.indicationCurr = indicationCurr;
        this.m3 = m3;
        this.sumIndication = sumIndication;
    }

    // --- Геттеры и сеттеры ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getFlat() { return flat; }
    public void setFlat(String flat) { this.flat = flat; }

    public String getPersAccount() { return persAccount; }
    public void setPersAccount(String persAccount) { this.persAccount = persAccount; }

    public String getClientName() { return clientName; }
    public void setClientName(String clientName) { this.clientName = clientName; }

    public Integer getClientTypeId() { return clientTypeId; }
    public void setClientTypeId(Integer clientTypeId) { this.clientTypeId = clientTypeId; }

    public String getAddressPop() { return addressPop; }
    public void setAddressPop(String addressPop) { this.addressPop = addressPop; }

    public String getAddressHo() { return addressHo; }
    public void setAddressHo(String addressHo) { this.addressHo = addressHo; }

    public Double getDebetIn() { return debetIn; }
    public void setDebetIn(Double debetIn) { this.debetIn = debetIn; }

    public Double getCreditIn() { return creditIn; }
    public void setCreditIn(Double creditIn) { this.creditIn = creditIn; }

    public Double getChargedMoney() { return chargedMoney; }
    public void setChargedMoney(Double chargedMoney) { this.chargedMoney = chargedMoney; }

    public Double getTaxIn() { return taxIn; }
    public void setTaxIn(Double taxIn) { this.taxIn = taxIn; }

    public Double getPaidIn() { return paidIn; }
    public void setPaidIn(Double paidIn) { this.paidIn = paidIn; }

    public Double getTaxOut() { return taxOut; }
    public void setTaxOut(Double taxOut) { this.taxOut = taxOut; }

    public Double getDebetOut() { return debetOut; }
    public void setDebetOut(Double debetOut) { this.debetOut = debetOut; }

    public Double getCreditOut() { return creditOut; }
    public void setCreditOut(Double creditOut) { this.creditOut = creditOut; }

    public Double getSubsidyIn() { return subsidyIn; }
    public void setSubsidyIn(Double subsidyIn) { this.subsidyIn = subsidyIn; }

    public Integer getMonthId() { return monthId; }
    public void setMonthId(Integer monthId) { this.monthId = monthId; }

    public Integer getYearId() { return yearId; }
    public void setYearId(Integer yearId) { this.yearId = yearId; }

    public Double getSumma() { return summa; }
    public void setSumma(Double summa) { this.summa = summa; }

    public Integer getPersonsOut() { return personsOut; }
    public void setPersonsOut(Integer personsOut) { this.personsOut = personsOut; }

    public Integer getPersFactOut() { return persFactOut; }
    public void setPersFactOut(Integer persFactOut) { this.persFactOut = persFactOut; }

    public Integer getPersResultOut() { return persResultOut; }
    public void setPersResultOut(Integer persResultOut) { this.persResultOut = persResultOut; }

    public Double getSummaTax() { return summaTax; }
    public void setSummaTax(Double summaTax) { this.summaTax = summaTax; }

    public Double getTax() { return tax; }
    public void setTax(Double tax) { this.tax = tax; }

    public String getTaxPrcent() { return taxPrcent; }
    public void setTaxPrcent(String taxPrcent) { this.taxPrcent = taxPrcent; }

    public Integer getRownum() { return rownum; }
    public void setRownum(Integer rownum) { this.rownum = rownum; }

    public Double getTarif() { return tarif; }
    public void setTarif(Double tarif) { this.tarif = tarif; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getMonthPrev() { return monthPrev; }
    public void setMonthPrev(String monthPrev) { this.monthPrev = monthPrev; }

    public String getMonthCurr() { return monthCurr; }
    public void setMonthCurr(String monthCurr) { this.monthCurr = monthCurr; }

    public String getIndicationPrev() { return indicationPrev; }
    public void setIndicationPrev(String indicationPrev) { this.indicationPrev = indicationPrev; }

    public String getIndicationCurr() { return indicationCurr; }
    public void setIndicationCurr(String indicationCurr) { this.indicationCurr = indicationCurr; }

    public Integer getM3() { return m3; }
    public void setM3(Integer m3) { this.m3 = m3; }

    public Double getSumIndication() { return sumIndication; }
    public void setSumIndication(Double sumIndication) { this.sumIndication = sumIndication; }

}
