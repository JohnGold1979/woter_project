package com.example.WOTER.DTO;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class SubsidyDTO {
    private Long id;
    private Long clientId;
    private Integer clientTypeId;
    private Integer counterIn;
    private String persAcc;
    private String clientName;
    private Integer cntPersResult;
    private String invoiceNumb;
    private String addressPop;
    private String addressHo;
    private Integer monthId;
    private Integer yearId;
    private BigDecimal paydIn;
    private BigDecimal taxIn;
    private BigDecimal amount;
    private LocalDateTime datePayd;
    private BigDecimal totalAmount;

    // --- геттеры и сеттеры ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getClientId() { return clientId; }
    public void setClientId(Long clientId) { this.clientId = clientId; }

    public Integer getClientTypeId() { return clientTypeId; }
    public void setClientTypeId(Integer clientTypeId) { this.clientTypeId = clientTypeId; }

    public Integer getCounterIn() { return counterIn; }
    public void setCounterIn(Integer counterIn) { this.counterIn = counterIn; }

    public String getPersAcc() { return persAcc; }
    public void setPersAcc(String persAcc) { this.persAcc = persAcc; }

    public String getClientName() { return clientName; }
    public void setClientName(String clientName) { this.clientName = clientName; }

    public Integer getCntPersResult() { return cntPersResult; }
    public void setCntPersResult(Integer cntPersResult) { this.cntPersResult = cntPersResult; }

    public String getInvoiceNumb() { return invoiceNumb; }
    public void setInvoiceNumb(String invoiceNumb) { this.invoiceNumb = invoiceNumb; }

    public String getAddressPop() { return addressPop; }
    public void setAddressPop(String addressPop) { this.addressPop = addressPop; }

    public String getAddressHo() { return addressHo; }
    public void setAddressHo(String addressHo) { this.addressHo = addressHo; }

    public Integer getMonthId() { return monthId; }
    public void setMonthId(Integer monthId) { this.monthId = monthId; }

    public Integer getYearId() { return yearId; }
    public void setYearId(Integer yearId) { this.yearId = yearId; }

    public BigDecimal getPaydIn() { return paydIn; }
    public void setPaydIn(BigDecimal paydIn) { this.paydIn = paydIn; }

    public BigDecimal getTaxIn() { return taxIn; }
    public void setTaxIn(BigDecimal taxIn) { this.taxIn = taxIn; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public LocalDateTime getDatePayd() { return datePayd; }
    public void setDatePayd(LocalDateTime datePayd) { this.datePayd = datePayd; }

    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
}
