package com.example.WOTER.Repository;
import com.example.WOTER.DTO.ExcelPaymentDTO;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class PaymentImportRepository {
    private final JdbcTemplate jdbcTemplate;

    public PaymentImportRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void save(ExcelPaymentDTO payment) {
        String sql = "SELECT insert_payment_reestr(?, ?, ?)";
        jdbcTemplate.queryForObject(sql,
                new Object[]{payment.getPayDate(), payment.getPersAcc(), payment.getAmount()},
                String.class);
    }
}