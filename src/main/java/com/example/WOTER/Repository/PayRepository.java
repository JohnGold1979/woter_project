package com.example.WOTER.Repository;

import com.example.WOTER.DTO.PayDTO;
import com.example.WOTER.DTO.SaldoDTO;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class PayRepository {

    private final JdbcTemplate jdbcTemplate;

    public PayRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<PayDTO> findAll(int month, int year) {
        String sql = """
                select wc.ID,
                       wc.CLIENT_TYPE_ID  as clientType,
                       wc.COUNTER_IN_ID   as counterId,
                       wc.PERS_ACCOUNT    as persAccount,
                       wc.CLIENT_NAME     as clientName,
                       wa.FLAT            as flat,
                       ((select street_name from wot_streets where id = wa.STREET_ID) ||' кв. '|| wa.FLAT ) as addressPop,
                       ((select street_name from wot_streets where id = (select street_id from wot_houses where id = wa.house_id))) ||
                       ((select house from wot_houses where id = wa.house_id) ||' кв. '|| wa.FLAT) as addressHo,
                       wp.month_id,
                       wp.year_id,
                       wp.tax_in,
                       wp.AMOUNT,
                       to_char(wp.PAY_DATE,'dd.mm.yyyy hh24:mi:ss')PAY_DATE,
                       to_char(wp.pay_date_reestr,'dd.mm.yyyy hh24:mi:ss')pay_date_reestr,
                       wp.pay_in
                 from Wot_Clients_Pay wp
                 join wot_clients wc on wp.client_id = wc.id
                 join WOT_ADDRESS wa on wa.client_id = wc.id
                 where wp.month_id = ?
                   and wp.year_id = ?
                   and wp.SYSTEM_ID = 1
                 order by wp.pay_date_reestr
                """;

        return jdbcTemplate.query(sql, new Object[]{month, year}, (rs, rowNum) -> mapRowToPayDTO(rs));
    }

    private PayDTO mapRowToPayDTO(ResultSet rs) throws SQLException {
        PayDTO dto = new PayDTO();
        dto.setId(rs.getLong("ID"));
        dto.setClientType(rs.getInt("clientType"));
        dto.setCounterId(rs.getInt("counterId"));
        dto.setPersAccount(rs.getString("persAccount"));
        dto.setClientName(rs.getString("clientName"));
        dto.setFlat(rs.getString("flat"));
        dto.setAddressPop(rs.getString("addressPop"));
        dto.setAddressHo(rs.getString("addressHo"));
        dto.setMonthId(rs.getInt("month_id"));
        dto.setYearId(rs.getInt("year_id"));
        dto.setTaxIn(rs.getBigDecimal("tax_in"));
        dto.setAmount(rs.getBigDecimal("AMOUNT"));
        dto.setPayDate(rs.getString("PAY_DATE"));
        dto.setPayDateReestr(rs.getString("pay_date_reestr"));
        dto.setPayIn(rs.getBigDecimal("pay_in"));

        return dto;
    }

    public PayDTO totalPay(int month, int year) {
        String sql = """
        select  sum(cp.amount) totalAmount,
                sum(cp.tax_in) totalTaxIn,
                sum(cp.pay_in) totalPayIn
        from wot_clients_pay cp
        where cp.month_id = ?
          and cp.year_id = ?
          and cp.amount > 0
          and cp.system_id  = 1
    """;

        return jdbcTemplate.queryForObject(sql, new Object[]{month, year}, (rs, rowNum) -> {
            PayDTO dto = new PayDTO();
            dto.setTotalAmount(rs.getBigDecimal("totalAmount"));
            dto.setTotalTaxIn(rs.getBigDecimal("totalTaxIn"));
            dto.setTotalPayIn(rs.getBigDecimal("totalPayIn"));
            return dto;
        });
    }
}
