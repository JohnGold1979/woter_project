package com.example.WOTER.Repository;

import com.example.WOTER.DTO.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ClientRepository {

    private final JdbcTemplate jdbcTemplate;

    public ClientRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<ClientDTO> findAll() {
        String sql = """
                 select wc.id::bigint,
                       wa.flat as flat,
                        wc.pers_account as pers_account,
                        wc.client_name as client_name,
                        (wc.cnt_pers + wc.cnt_pers_fact) as cnt_pers_result,
                        CASE
                          WHEN wa.street_id IS NULL THEN s2.street_name || ' ' || h.house || ' кв. ' || wa.flat
                          ELSE s.street_name || ' д. ' || wa.flat
                        END AS address,
                        wc.client_type_id,
                        wc.counter_in_id
                        from wot_clients wc
                        left join wot_address wa on wa.client_id = wc.id
                        left join wot_streets s on s.id = wa.street_id
                        left join wot_houses h on h.id = wa.house_id
                        left join wot_streets s2 on s2.id = h.street_id
                         where wc.system_id = 1
                         and wc.status_id <> 3
                         order by wc.client_type_id, wc.id
                """;

        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            ClientDTO dto = new ClientDTO();
            dto.setId(rs.getObject("id", Long.class));   // ✅ вместо getLong()
            dto.setFlat(rs.getString("flat"));
            dto.setPersonalAccount(rs.getString("pers_account"));
            dto.setClientName(rs.getString("client_name"));
            dto.setCntPersResult(rs.getInt("cnt_pers_result"));
            dto.setAddress(rs.getString("address"));
            dto.setClientType(rs.getInt("client_type_id"));
            dto.setCounterInId(rs.getInt("counter_in_id"));
            return dto;
        });
    }

    public String getStationNameById(Integer stationId) {
        String sql = "SELECT station_name FROM wot_stations WHERE id = ?";
        return jdbcTemplate.queryForObject(sql, String.class, stationId);
    }

    public List<ClientDTO> getByStation(Integer stationId) {
        String sql = """
        select wc.id::bigint,
               wa.flat as flat,
               wc.pers_account as pers_account,
               wc.client_name as client_name,
               (wc.cnt_pers + wc.cnt_pers_fact) as cnt_pers_result,
               CASE
                 WHEN wa.street_id IS NULL THEN s2.street_name || ' ' || h.house || ' кв. ' || wa.flat
                 ELSE s.street_name || ' д. ' || wa.flat
               END AS address,
               wc.client_type_id,
               wc.counter_in_id
          from wot_clients wc
          left join wot_address wa on wa.client_id = wc.id
          left join wot_streets s on s.id = wa.street_id
          left join wot_houses h on h.id = wa.house_id
          left join wot_streets s2 on s2.id = h.street_id
         where wc.system_id = 2
           and wa.station_id = ?
           and wc.status_id <> 3
         order by wc.client_type_id, wc.id
    """;

        return jdbcTemplate.query(sql, new Object[]{stationId}, (rs, rowNum) -> {
            ClientDTO dto = new ClientDTO();
            dto.setId(rs.getObject("id", Long.class));
            dto.setFlat(rs.getString("flat"));
            dto.setPersonalAccount(rs.getString("pers_account"));
            dto.setClientName(rs.getString("client_name"));
            dto.setCntPersResult(rs.getInt("cnt_pers_result"));
            dto.setAddress(rs.getString("address"));
            dto.setClientType(rs.getInt("client_type_id"));
            dto.setCounterInId(rs.getInt("counter_in_id"));
            return dto;
        });
    }

    public ClientDTO findByPersAcc(String account) {
        String sql = """
            select wc.id::bigint,
                   wa.flat,
                   wc.pers_account,
                   wc.client_name,
                   (wc.cnt_pers + wc.cnt_pers_fact) as cnt_pers_result,
                   s2.street_name || ' ' || h.house || ' кв. ' || wa.flat as address,
                   wc.client_type_id
            from wot_clients wc
            left join wot_address wa on wa.client_id = wc.id
            left join wot_houses h on h.id = wa.house_id
            left join wot_streets s2 on s2.id = h.street_id
            where wc.pers_account = ?
            """;

        return jdbcTemplate.queryForObject(sql, new Object[]{account}, (rs, rowNum) -> {
            ClientDTO dto = new ClientDTO();
            dto.setId(rs.getLong("id"));
            dto.setFlat(rs.getString("flat"));
            dto.setPersonalAccount(rs.getString("pers_account"));
            dto.setClientName(rs.getString("client_name"));
            dto.setCntPersResult(rs.getInt("cnt_pers_result"));
            dto.setAddress(rs.getString("address"));
            dto.setClientType(rs.getInt("client_type_id"));
            return dto;
        });
    }

    public List<StreetDTO> getAllStreets(String account) {
        String sql = """
         SELECT ws.id as streetId, ws.street_name as streetName
         FROM wot_streets ws
         where ws.station_id = (
               select wa.station_id
               from wot_clients wc, wot_address wa
               where wa.client_id  = wc.id
               and wc.pers_account = ?
               )
         order by ws.street_name
        """;
        return jdbcTemplate.query(sql, new Object[]{account}, (rs, rowNum) ->
                new StreetDTO(
                        rs.getInt("streetId"), rs.getString("streetName")
                )
        );
    }

    public List<StationDTO> getSatationsAll() {
        String sql = """ 
                     select s.id station_id, s.station_name
                     from wot_stations s
                     where s.system_id = 2
                     order by s.station_name
                     """;

        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            StationDTO dto = new StationDTO();
            dto.setStationId(rs.getLong("station_id"));
            dto.setStationName(rs.getString("station_name"));
            return dto;
        });
    }

    public TaxDTO getActiveTax() {
        String sql = "select tax_rate from wot_taxes where status_id = 1 limit 1";

        return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> {
            TaxDTO dto = new TaxDTO();
            dto.setTaxRate(rs.getDouble("tax_rate"));
            return dto;
        });

    }

    public PeriodDTO findLastOpenPeriod() {
        String sql = """
        select ws.month_id, ws.year_id
        from wot_saldo ws
        where ws.closed = 1
        order by ws.year_id desc, ws.month_id desc
        limit 1
        """;

        return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> {
            PeriodDTO dto = new PeriodDTO();
            dto.setMonthId(rs.getInt("month_id"));
            dto.setYearId(rs.getInt("year_id"));
            return dto;
        });
    }

    public String insertPayment(PaymentDTO payment) {
        String sql = "SELECT insert_payment(?, ?, ?)";
        return jdbcTemplate.queryForObject(
                sql,
                new Object[]{payment.getPersAcc(), payment.getAmount(), payment.getTax()},
                String.class
        );

    }

    public String insertPaymentSub(PaymentDTO payment) {
        System.out.println("Пришло " + payment.getPersAcc());
        System.out.println("Пришло " + payment.getAmount());
        String sql = "SELECT insert_payment_sub(?, ?)";
        return jdbcTemplate.queryForObject(
                sql,
                new Object[]{payment.getPersAcc(), payment.getAmount()},
                String.class
        );

    }
}