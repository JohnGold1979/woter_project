package com.example.WOTER.Services;

import com.example.WOTER.DTO.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReportAccountsService {

    private final JdbcTemplate jdbcTemplate;

    public ReportAccountsService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<ReportRowDTO> getHouseReport(int month, int year, int houseId) {
        String sql = """
           select flat,
           wc.pers_account as persAccount,
           wc.client_name as clientName,
           ((select street_name from wot_streets where id = (select street_id from wot_houses where id = wa.house_id))) ||
           ((select house from wot_houses where id = wa.house_id) ||' кв. '|| wa.FLAT) as address,
           ws.debet_in as debetIn,
           ws.credet_in as credetIn,
           ws.charged_money as chargedMoney,
           ws.tax_in as taxIn,
           ws.payd_in as paydIn,
           ws.tax_out as taxOut,
           ws.debet_out as debetOut,
           ws.credet_out as credetOut,
           ws.subsidy as subsidy,
           ws.removal_in as removalIn,
           ws.rem_tax_in as remTaxIn
            from wot_saldo ws, wot_clients wc, wot_address wa
            where ws.client_id =  wc.id
            and  ws.client_id  =  wa.client_id
            and ws.month_id = ?
            and ws.year_id  = ?
            and wa.house_id = ?
            and wc.client_type_id = 1
            and wc.status_id = 1
            AND ws.SYSTEM_ID = 1
            order by wa.house_id, wa.street_id, wc.pers_account
        """;

        return jdbcTemplate.query(sql, new Object[]{month, year, houseId}, (rs, rowNum) ->
                new ReportRowDTO(
                        rs.getString("flat"),
                        rs.getString("persAccount"),
                        rs.getString("clientName"),
                        rs.getString("address"),
                        rs.getBigDecimal("debetIn"),
                        rs.getBigDecimal("credetIn"),
                        rs.getBigDecimal("chargedMoney"),
                        rs.getBigDecimal("taxIn"),
                        rs.getBigDecimal("paydIn"),
                        rs.getBigDecimal("taxOut"),
                        rs.getBigDecimal("removalIn"),  // ✅ на своём месте
                        rs.getBigDecimal("remTaxIn"),   // ✅ на своём месте
                        rs.getBigDecimal("debetOut"),   // ✅ на своём месте
                        rs.getBigDecimal("credetOut"),  // ✅ на своём месте
                        rs.getBigDecimal("subsidy")     // ✅ теперь в конце
                )
        );
    }

    public List<ReportRowDTO> getPrivateReport( int month, int year, int streetId) {
        String sql = """
            select flat,
            wc.pers_account as persAccount,
            wc.client_name as clientName,
            ((select street_name from wot_streets where id = wa.STREET_ID) ||' д. '|| wa.FLAT )  as address,
            ws.debet_in as debetIn,
            ws.credet_in as credetIn,
            ws.charged_money as chargedMoney,
            ws.tax_in as taxIn,
            ws.payd_in as paydIn,
            ws.tax_out as taxOut,
            ws.debet_out as debetOut,
            ws.credet_out as credetOut,
            ws.subsidy as subsidy,
            ws.removal_in as removalIn,
            ws.rem_tax_in as remTaxIn
            from wot_saldo ws, wot_clients wc, wot_address wa
            where ws.client_id =  wc.id
              and  ws.client_id  =  wa.client_id
              and ws.month_id = ?
              and ws.year_id  = ?
              and wa.street_id = ?
              and wc.client_type_id = 2
              and wc.status_id = 1
              AND ws.SYSTEM_ID = 1
              order by wa.house_id, wa.street_id, wc.pers_account
        """;

        return jdbcTemplate.query(sql, new Object[]{month, year,  streetId}, (rs, rowNum) ->
                new ReportRowDTO(
                        rs.getString("flat"),
                        rs.getString("persAccount"),
                        rs.getString("clientName"),
                        rs.getString("address"),
                        rs.getBigDecimal("debetIn"),
                        rs.getBigDecimal("credetIn"),
                        rs.getBigDecimal("chargedMoney"),
                        rs.getBigDecimal("taxIn"),
                        rs.getBigDecimal("paydIn"),
                        rs.getBigDecimal("taxOut"),
                        rs.getBigDecimal("removalIn"),  // ✅ на своём месте
                        rs.getBigDecimal("remTaxIn"),   // ✅ на своём месте
                        rs.getBigDecimal("debetOut"),   // ✅ на своём месте
                        rs.getBigDecimal("credetOut"),  // ✅ на своём месте
                        rs.getBigDecimal("subsidy")     // ✅ теперь в конце
                )
        );
    }
//----------------------------------------------------------------------------------------------------------------------
    public List<HouseAllReportDTO> getHousesAllReport(int montId, int yearId){
        String sql = """
        select
           ((SELECT street_name FROM wot_streets WHERE id =
             (select wh.street_id from wot_houses wh where id = a.house_id))
            || ' ' ||
            (select wh.house from wot_houses wh where id = a.house_id)) as houseName,
           a.*
        from (
            select wa.house_id,
                   sum(ws.debet_in)  as debetIn,
                   sum(ws.credet_in) as credetIn,
                   sum(ws.charged_money) as chargedMoney,
                   sum(ws.tax_in)    as taxIn,
                   sum(ws.payd_in)   as paydIn,
                   sum(ws.tax_out)   as taxOut,
                   sum(ws.removal_in) as removalIn,
                   sum(ws.rem_tax_in) as remTaxIn,
                   sum(ws.debet_out) as debetOut,
                   sum(ws.credet_out) as credetOut,
                   sum(ws.subsidy)   as subsidy
            from wot_clients wc
            join wot_saldo ws on ws.client_id = wc.id
            join wot_address wa on ws.client_id = wa.client_id
            where wc.client_type_id = 1
              and wc.status_id = 1
              and ws.system_id = 1
              and ws.month_id = ?
              and ws.year_id = ?
            group by wa.house_id
            order by wa.house_id
        ) a
    """;

        return jdbcTemplate.query(sql, new Object[]{montId, yearId}, (rs, rowNum) ->
                new HouseAllReportDTO(
                        rs.getString("houseName"),
                        rs.getBigDecimal("debetIn"),
                        rs.getBigDecimal("credetIn"),
                        rs.getBigDecimal("chargedMoney"),
                        rs.getBigDecimal("taxIn"),
                        rs.getBigDecimal("paydIn"),
                        rs.getBigDecimal("taxOut"),
                        rs.getBigDecimal("removalIn"),
                        rs.getBigDecimal("remTaxIn"),
                        rs.getBigDecimal("debetOut"),
                        rs.getBigDecimal("credetOut"),
                        rs.getBigDecimal("subsidy")
                )
        );
    }
//----------------------------------------------------------------------------------------------------------------------
public List<StreetAllReportDTO> getStreetsAllReport(int montId, int yearId){
    String sql = """
        select
            (SELECT street_name FROM wot_streets WHERE id = a.street_id) as streentName,
              a.*
              from (
                               select wa.street_id,
                                      sum(ws.debet_in)  as debetIn,
                                      sum(ws.credet_in) as credetIn,
                                      sum(ws.charged_money) as chargedMoney,
                                      sum(ws.tax_in)    as taxIn,
                                      sum(ws.payd_in)   as paydIn,
                                      sum(ws.tax_out)   as taxOut,
                                      sum(ws.removal_in) as removalIn,
                                      sum(ws.rem_tax_in) as remTaxIn,
                                      sum(ws.debet_out) as debetOut,
                                      sum(ws.credet_out) as credetOut,
                                      sum(ws.subsidy)   as subsidy
                               from wot_clients wc
                               join wot_saldo ws on ws.client_id = wc.id
                               join wot_address wa on ws.client_id = wa.client_id
                               where wc.client_type_id = 2
                                 and wc.status_id = 1
                                 and ws.system_id = 1
                                 and ws.month_id = ?
                                 and ws.year_id = ?
                               group by wa.street_id
                               order by wa.street_id
              ) a
    """;

    return jdbcTemplate.query(sql, new Object[]{montId, yearId}, (rs, rowNum) ->
            new StreetAllReportDTO(
                    rs.getString("streentName"),
                    rs.getBigDecimal("debetIn"),
                    rs.getBigDecimal("credetIn"),
                    rs.getBigDecimal("chargedMoney"),
                    rs.getBigDecimal("taxIn"),
                    rs.getBigDecimal("paydIn"),
                    rs.getBigDecimal("taxOut"),
                    rs.getBigDecimal("removalIn"),
                    rs.getBigDecimal("remTaxIn"),
                    rs.getBigDecimal("debetOut"),
                    rs.getBigDecimal("credetOut"),
                    rs.getBigDecimal("subsidy")
            )
    );
}
//---------------------------------------------------------------------------------------------------------------------
public List<PayAllReportDTO> getPayAllReport(int montId, int yearId) {
           String sql = """ 
                 select a.days,
                        sum(a.amount) as amount,
                        sum(a.tax_in) as tax_in,
                        sum(a.pay_in) as pay_in
                 from
                 (select to_char(cp.pay_date_reestr,'dd') as days, cp.amount, cp.tax_in, cp.pay_in
                  from wot_clients_pay cp                  where cp.amount > 0
                  and cp.month_id = ?
                  and cp.year_id = ?
                  )a
                  group by a.days
                  order by a.days
                 """;
    return jdbcTemplate.query(sql, new Object[]{montId, yearId}, (rs, rowNum) ->
            new PayAllReportDTO(
                    rs.getString("days"),
                    rs.getBigDecimal("amount"),
                    rs.getBigDecimal("tax_in"),
                    rs.getBigDecimal("pay_in")
            )
    );
}
//----------------------------------------------------------------------------------------------------------------------
    public List<HouseDTO> getHouses() {
        String sql = """
          select a.id as houseId, a.houseName
                  from
                  (SELECT wh.*, (SELECT STREET_NAME FROM WOT_STREETS WHERE wh.STREET_ID = ID) || ' '|| WH.HOUSE AS houseName
                   FROM WOT_HOUSES wh order by wh.orders)a
        """;

        return jdbcTemplate.query(sql, new Object[]{}, (rs, rowNum) ->
                new HouseDTO(
                        rs.getInt("houseId"), rs.getString("houseName")
                )
        );
    }

    public List<StreetDTO> getStreets() {
        String sql = """
           SELECT ws.id as streetId,
                  ws.street_name as streetName
           FROM wot_streets ws
           where ws.system_id = 1
           order by ws.street_name
        """;

        return jdbcTemplate.query(sql, new Object[]{}, (rs, rowNum) ->
                new StreetDTO(
                        rs.getInt("streetId"), rs.getString("streetName")
                )
        );
    }


}
