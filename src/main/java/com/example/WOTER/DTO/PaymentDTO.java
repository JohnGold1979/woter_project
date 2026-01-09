package com.example.WOTER.DTO;

import java.math.BigDecimal;

public class PaymentDTO {
        private String persAcc;
        private BigDecimal amount;
        private BigDecimal tax;

        public void setPersAcc(String persAcc) {
                this.persAcc = persAcc;
        }
        public String getPersAcc() {
                return persAcc;
        }
        public BigDecimal getAmount() { return amount; }

        public void setAmount(BigDecimal amount) { this.amount = amount; }
        public BigDecimal getTax() { return tax; }

        public void setTax(BigDecimal tax) { this.tax = tax; }


}
