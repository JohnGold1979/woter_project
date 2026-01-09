package com.example.WOTER.Repository;

import com.example.WOTER.DTO.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class SaldoRepository {

    private final JdbcTemplate jdbcTemplate;

    public SaldoRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<SaldoDTO> findAll(int month, int year, int stationId) {
        String sql = """
            select row_number() OVER (ORDER BY wc.id) npp,
                   wa.flat,
                   wc.pers_account,
                   wc.client_name,
                   wc.client_type_id,
                   wa.house_id,
                   (select street_name from wot_streets where id = wa.street_id) || ' д. ' || wa.flat as address_pop,
                   (select street_name from wot_streets where id = (select street_id from wot_houses where id = wa.house_id)) ||
                   (select house from wot_houses where id = wa.house_id) || ' кв. ' || wa.flat as address_ho,
                   ws.debet_in, ws.credet_in, ws.charged_money, ws.tax_in, ws.payd_in,
                   ws.tax_out, ws.debet_out, ws.credet_out, ws.subsidy,
                   ws.date_calc, ws.month_id, ws.year_id, ws.removal_in, ws.rem_tax_in,
                   (ws.debet_out + round((ws.debet_out * (select tax_rate from wot_taxes where id = 1)), 2)) as summa,
                   ws.persons_out, ws.pers_fact_out, ws.pers_result_out, wc.client_type_id
            from wot_saldo ws
                     join wot_clients wc on ws.client_id = wc.id
                     join wot_address wa on ws.client_id = wa.client_id
            where ws.month_id = ?
            and ws.year_id = ?
            and wa.station_id = ?
            and wc.status_id = 1
            """;

        return jdbcTemplate.query(sql, new Object[]{month, year, stationId}, (rs, rowNum) -> {
            SaldoDTO dto = new SaldoDTO();
            dto.setNpp(rs.getLong("npp"));
            dto.setFlat(rs.getString("flat"));
            dto.setPersonalAccount(rs.getString("pers_account"));
            dto.setClientName(rs.getString("client_name"));
            dto.setClientTypeId(rs.getInt("client_type_id"));
            dto.setHouseId(rs.getLong("house_id"));
            dto.setAddressPop(rs.getString("address_pop"));
            dto.setAddressHo(rs.getString("address_ho"));
            dto.setDebetIn(rs.getDouble("debet_in"));
            dto.setCredetIn(rs.getDouble("credet_in"));
            dto.setChargedMoney(rs.getDouble("charged_money"));
            dto.setTaxIn(rs.getDouble("tax_in"));
            dto.setPaydIn(rs.getDouble("payd_in"));
            dto.setTaxOut(rs.getDouble("tax_out"));
            dto.setDebetOut(rs.getDouble("debet_out"));
            dto.setCredetOut(rs.getDouble("credet_out"));
            dto.setSubsidy(rs.getDouble("subsidy"));
            dto.setDateCalc(rs.getString("date_calc"));
            dto.setMonthId(rs.getInt("month_id"));
            dto.setYearId(rs.getInt("year_id"));
            dto.setRemovalIn(rs.getDouble("removal_in"));
            dto.setRemTaxIn(rs.getDouble("rem_tax_in"));
            dto.setSumma(rs.getDouble("summa"));
            dto.setPersonsOut(rs.getInt("persons_out"));
            dto.setPersFactOut(rs.getInt("pers_fact_out"));
            dto.setPersResultOut(rs.getInt("pers_result_out"));
            dto.setClientType(rs.getInt("client_type_id"));
            return dto;
        });
    }

    public String startJumpMonth() {
        String sql = "SELECT calc_jump_month()";
        return jdbcTemplate.queryForObject(sql, new Object[]{}, String.class);

    }

    public String getStationNameById(Integer stationId) {
        String sql = "SELECT station_name FROM wot_stations WHERE id = ?";
        return jdbcTemplate.queryForObject(sql, String.class, stationId);
    }

    public SaldoTotalDTO totalSaldo(int month, int year, int stationId) {
        String sql = """
         select 
             coalesce(round(sum(debet_in),2),0) as debet_in,
             coalesce(round(sum(credet_in),2),0) as credet_in,
             coalesce(round(sum(debet_out),2),0) as debet_out,
             coalesce(round(sum(credet_out),2),0) as credet_out,
             coalesce(sum(charged_money),0) as charged_money,
             coalesce(sum(payd_in),0) as payd_in,
             coalesce(sum(subsidy),0) as subsidy,
             coalesce(sum(tax_in),0) as tax_in,
             coalesce(sum(removal_in),0) as removal_in,
             coalesce(sum(tax_out),0) as tax_out
         from wot_saldo ws
         join wot_clients wc on ws.client_id = wc.id
         join wot_address wa on wa.client_id = wc.id
         where ws.month_id = ?
           and ws.year_id = ?
           and wa.station_id = ?
    """;

        return jdbcTemplate.queryForObject(sql, new Object[]{month, year, stationId}, (rs, rowNum) -> {
            SaldoTotalDTO dto = new SaldoTotalDTO();
            dto.setDebetIn(rs.getDouble("debet_in"));
            dto.setCredetIn(rs.getDouble("credet_in"));
            dto.setDebetOut(rs.getDouble("debet_out"));
            dto.setCredetOut(rs.getDouble("credet_out"));
            dto.setChargedMoney(rs.getDouble("charged_money"));
            dto.setPaydIn(rs.getDouble("payd_in"));
            dto.setSubsidy(rs.getDouble("subsidy"));
            dto.setTaxIn(rs.getDouble("tax_in"));
            dto.setRemovalIn(rs.getDouble("removal_in"));
            dto.setTaxOut(rs.getDouble("tax_out"));
            return dto;
        });
    }


    public SaldoTypeDTO totalByTypes(int month, int year, int stationId) {
        String sql = """
          select     (coalesce(round(sum(debet_in),2),0)  - coalesce(round(sum(credet_in),2),0))  as rolledSaldoInFlat,
                      coalesce(round(sum(debet_out),2),0) - coalesce(round(sum(credet_out),2),0) as rolledSaldoOutFlat,
                      coalesce(sum(charged_money),0) as totalFlatChargedFlat,
                      coalesce(sum(payd_in),0) as totalFlatPaydFlat
          from wot_saldo ws
          join wot_clients wc on ws.client_id = wc.id
          join wot_address wa on wa.client_id = wc.id
          where ws.month_id = ?
          and ws.year_id = ?
          and wa.station_id = ?
          and wc.client_type_id = 1
    """;

        return jdbcTemplate.queryForObject(sql, new Object[]{month, year, stationId}, (rs, rowNum) -> {
            SaldoTypeDTO dto = new SaldoTypeDTO();
            dto.setRolledSaldoInFlat(rs.getDouble("rolledSaldoInFlat"));
            dto.setRolledSaldoOutFlat(rs.getDouble("rolledSaldoOutFlat"));
            dto.setTotalFlatChargedFlat(rs.getDouble("totalFlatChargedFlat"));
            dto.setTotalFlatPaydFlat(rs.getDouble("totalFlatPaydFlat"));

            return dto;
        });
    }

}
