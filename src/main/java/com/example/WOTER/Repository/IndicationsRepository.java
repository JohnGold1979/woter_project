package com.example.WOTER.Repository;

import com.example.WOTER.DTO.HouseAllReportDTO;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class IndicationsRepository {

    private final JdbcTemplate jdbcTemplate;

    public IndicationsRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<HouseAllReportDTO.IndicationsDTO> findAll() {
        String sql = """
               SELECT wc.id,
                      wc.client_type_id,
                      wc.counter_in_id,
                      wc.pers_account,
                      wc.client_name,
                      (wc.CNT_PERS + wc.CNT_PERS_FACT) AS CNT_PERS_RESULT,
                      wa.HOUSE_ID,
                      wa.STREET_ID,
                      wa.FLAT,
                      ((select street_name from wot_streets where id = wa.STREET_ID) ||' д. '|| wa.FLAT ) as address_pop,
                      ((select street_name from wot_streets where id = (select street_id from wot_houses where id = wa.house_id))) ||
                      ((select house from wot_houses where id = wa.house_id) ||' кв. '|| wa.FLAT) as address_ho
                FROM Wot_Clients wc
                LEFT JOIN wot_address wa on wa.client_id = wc.id
                WHERE wc.system_id = 1
                  AND wc.COUNTER_IN_ID = 1
                ORDER BY wc.CLIENT_TYPE_ID, wa.house_id
              """;

        // пока month/year не используются, но оставляем параметры для будущего
        return jdbcTemplate.query(sql, (rs, rowNum) -> mapRowToIndicationsDTO(rs));
    }

    private HouseAllReportDTO.IndicationsDTO mapRowToIndicationsDTO(ResultSet rs) throws SQLException {
        HouseAllReportDTO.IndicationsDTO dto = new HouseAllReportDTO.IndicationsDTO();
        dto.setId(rs.getLong("id"));
        dto.setClientTypeId(rs.getInt("client_type_id"));
        dto.setCounterInId(rs.getInt("counter_in_id"));
        dto.setPersonalAccount(rs.getString("pers_account"));
        dto.setClientName(rs.getString("client_name"));
        dto.setCntPersResult(rs.getInt("cnt_pers_result"));
        dto.setHouseId(rs.getLong("house_id"));
        dto.setStreetId(rs.getLong("street_id"));
        dto.setFlat(rs.getString("flat"));
        dto.setAddressPop(rs.getString("address_pop"));
        dto.setAddressHo(rs.getString("address_ho"));
        return dto;
    }


    public List<HouseAllReportDTO.IndicationsDTO> findByAccountAndYear(String persAccount, int year) {
        String sql = """
               SELECT
                month_id,
                indication,
                m3,
                tarif,
                summa
               FROM wot_clients_counters_ind wi, wot_clients wc
               WHERE wi.client_id = wc.id
                 and wc.counter_in_id = 1
                 and wc.pers_account = ?
                 and wi.year_id = ?
               ORDER BY month_id
              """;

        return jdbcTemplate.query(sql,
                new Object[]{persAccount, year},
                (rs, rowNum) -> {
                    HouseAllReportDTO.IndicationsDTO dto = new HouseAllReportDTO.IndicationsDTO();
                    dto.setMonthId(rs.getInt("month_id"));
                    dto.setIndication(rs.getInt("indication"));
                    dto.setM3(rs.getInt("m3"));
                    dto.setTariff(rs.getBigDecimal("tarif"));
                    dto.setSumma(rs.getBigDecimal("summa"));
                    return dto;
                });
    }

    public String insertInd(HouseAllReportDTO.IndicationsDTO ind) {
        System.out.println("Пришло" + ind.getPersonalAccount());
        System.out.println("Пришло" + ind.getMonthId());
        System.out.println("Пришло" + ind.getYearId());
        System.out.println("Пришло" + ind.getM3());
        String sql = "SELECT ins_indications(?, ?, ?, ?);";
        return jdbcTemplate.queryForObject(
                sql,
                new Object[]{ind.getPersonalAccount(), ind.getMonthId(),  ind.getYearId(),  ind.getM3()},
                String.class
        );

    }

}
