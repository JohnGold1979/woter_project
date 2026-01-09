package com.example.WOTER.DTO;

public class ClientDTO {

    private Long id;
    private String flat;
    private String PersonalAccount;
    private String ClientName;
    private Integer CntPersResult;
    private String Address;
    private Integer ClientType;
    private Integer CounterInId;

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getFlat() {
        return flat;
    }
    public void setFlat(String flat) {
        this.flat = flat;
    }
    public String getPersonalAccount() {
        return PersonalAccount;
    }
    public void setPersonalAccount(String PersonalAccount) {
        this.PersonalAccount = PersonalAccount;
    }
    public String getClientName() {
        return ClientName;
    }
    public void setClientName(String ClientName) {
        this.ClientName = ClientName;
    }

    public Integer getCntPersResult() {
        return CntPersResult;
    }
    public void setCntPersResult(Integer CntPersResult) {
        this.CntPersResult = CntPersResult;
    }
    public String getAddress() {
        return Address;
    }
    public void setAddress(String Address) {
        this.Address = Address;
    }

    public Integer getClientType() { return ClientType; }
    public void setClientType(Integer ClientType) { this.ClientType = ClientType; }

    public Integer getCounterInId() { return CounterInId; }
    public void setCounterInId(Integer CounterInId) { this.CounterInId = CounterInId; }

}
