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
    private Integer StreetId;
    private String House;
    private Integer CntPers;
    private Integer CntPersFact;

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

    public Integer getStreetId() { return StreetId; }
    public void setStreetId(Integer StreetId) { this.StreetId = StreetId; }

    public String getHouse() { return House; }
    public void setHouse(String House) { this.House = House; }

    public Integer getCntPers() { return CntPers; }
    public void setCntPers(Integer CntPers) { this.CntPers = CntPers; }

    public Integer getCntPersFact() { return CntPersFact; }
    public void setCntPersFact(Integer CntPersFact) { this.CntPersFact = CntPersFact; }

}
