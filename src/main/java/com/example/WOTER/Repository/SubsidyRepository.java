package com.example.WOTER.Repository;

import com.example.WOTER.DTO.PayDTO;
import com.example.WOTER.DTO.SubsidyDTO;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

@Repository
public class SubsidyRepository {

    private final JdbcTemplate jdbcTemplate;

    public SubsidyRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<SubsidyDTO> findAll(int month, int year) {
        String sql = """
                select WP.ID as id,
                       wc.ID as clientId,
                       wc.CLIENT_TYPE_ID as client_type_id,
                       wc.COUNTER_IN_ID as counterIn,
                       wc.PERS_ACCOUNT as persAcc,
                       wc.CLIENT_NAME as clientName,
                       (wc.CNT_PERS + wc.CNT_PERS_FACT) AS cnt_pers_result,
                       ((select street_name from wot_streets where id = wa.STREET_ID) ||' кв. '|| wa.FLAT ) as address_pop,
                       ((select street_name from wot_streets where id = (select street_id from wot_houses where id = wa.house_id))) || ((select house from wot_houses where id = wa.house_id) ||' кв. '|| wa.FLAT) as address_ho,
                       wp.month_id,
                       wp.year_id,
                       wp.pay_in,
                       wp.tax_in,
                       wp.AMOUNT,
                       wp.pay_date
                  from WOT_CLIENTS_SUBSIDY wp
                  join wot_clients wc on wp.client_id = wc.id
                  join WOT_ADDRESS wa on wa.client_id = wc.id
                 where wp.month_id = ?
                   and wp.year_id = ?
                   and wp.SYSTEM_ID = 1
                 order by wp.pay_date
            """;

        return jdbcTemplate.query(sql, new Object[]{month, year}, (rs, rowNum) -> mapRowToSubsidyDTO(rs));
    }

    private SubsidyDTO mapRowToSubsidyDTO(ResultSet rs) throws SQLException {
        SubsidyDTO dto = new SubsidyDTO();
        dto.setId(rs.getLong("id"));
        dto.setClientId(rs.getLong("clientId"));
        dto.setClientTypeId(rs.getInt("client_type_id"));
        dto.setCounterIn(rs.getInt("counterIn"));
        dto.setPersAcc(rs.getString("persAcc"));
        dto.setClientName(rs.getString("clientName"));
        dto.setCntPersResult(rs.getInt("cnt_pers_result"));
        dto.setAddressPop(rs.getString("address_pop"));
        dto.setAddressHo(rs.getString("address_ho"));
        dto.setMonthId(rs.getInt("month_id"));
        dto.setYearId(rs.getInt("year_id"));
        dto.setPaydIn(rs.getBigDecimal("pay_in"));
        dto.setTaxIn(rs.getBigDecimal("tax_in"));
        dto.setAmount(rs.getBigDecimal("amount"));
        Timestamp ts = rs.getTimestamp("pay_date");
        if (ts != null) {
            dto.setDatePayd(ts.toLocalDateTime());
        }
        return dto;
    }

    public SubsidyDTO totalSubsidy(int month, int year) {
        String sql = """
       select  sum(ws.amount) totalAmount
       from  wot_clients_subsidy ws
       where ws.month_id = ?
       and ws.year_id = ?
       and ws.amount > 0
       and ws.system_id  = 1
    """;
        return jdbcTemplate.queryForObject(sql, new Object[]{month, year}, (rs, rowNum) -> {
            SubsidyDTO dto = new SubsidyDTO();
            dto.setTotalAmount(rs.getBigDecimal("totalAmount"));
            return dto;
        });
    }
}
