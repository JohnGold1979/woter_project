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
                   CASE
                     WHEN wa.street_id IS NULL THEN wa.flat
                     ELSE wa.flat
                   end as flat,
                   wc.pers_account,
                   wc.client_name,
                   (wc.cnt_pers + wc.cnt_pers_fact) as cnt_pers_result,
                   CASE
                     WHEN wa.street_id IS NULL THEN s2.street_name || ' ' || h.house || ' кв. ' || wa.flat
                     ELSE s.street_name || ' д. ' || wa.flat
                   END AS address,
                   wc.client_type_id,
                   wc.counter_in_id,
                   CASE
                     WHEN wa.street_id IS NULL THEN h.street_id
                     ELSE wa.street_id
                   END as street_id,
                   CASE
                     WHEN wa.street_id IS NULL THEN h.house
                     ELSE ''
                   END as house,
                   wc.cnt_pers,
                   wc.cnt_pers_fact
            from wot_clients wc
            left join wot_address wa on wa.client_id = wc.id
            left join wot_houses h on h.id = wa.house_id
            left join wot_streets s on s.id = wa.street_id
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
            dto.setCounterInId(rs.getInt("counter_in_id"));
            dto.setStreetId(rs.getInt("street_id"));
            dto.setHouse(rs.getString("house"));
            dto.setCntPers(rs.getInt("cnt_pers"));
            dto.setCntPersFact(rs.getInt("cnt_pers_fact"));
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

    public Long saveClient(java.util.Map<String, Object> data) {
        String sql = """
        INSERT INTO wot_clients (
            pers_account, 
            client_name, 
            client_type_id, 
            counter_in_id,
            cnt_pers,
            cnt_pers_fact,
            create_date,
            system_id,
            status_id
        )
        VALUES (?, ?, ?, ?, ?, ?, CURRENT_DATE, 2, 1)
        RETURNING id
        """;

        String persAcc = (String) data.get("personalAccount");
        String clientName = (String) data.get("clientName");
        Integer clientType = (Integer) data.get("clientType");
        Integer counterIn = (Integer) data.get("counterIn");
        Integer cntPersons = (Integer) data.get("cntPersons");
        Integer cntPersonsFact = (Integer) data.get("cntPersonsFact");

        return jdbcTemplate.queryForObject(
                sql,
                new Object[]{
                    persAcc,
                    clientName,
                    clientType,
                    counterIn,
                    cntPersons != null ? cntPersons : 0,
                    cntPersonsFact != null ? cntPersonsFact : 0
                },
                Long.class
        );
    }

    public void saveClientAddress(Long clientId, java.util.Map<String, Object> data) {
        Integer streetId = (Integer) data.get("streetId");
        String house = (String) data.get("house");
        String flat = (String) data.get("flat");

        // Получаем station_id из улицы
        Integer stationId = jdbcTemplate.queryForObject(
                "SELECT station_id FROM wot_streets WHERE id = ?",
                new Object[]{streetId},
                Integer.class
        );

        String sql = """
        INSERT INTO wot_address (
            client_id,
            street_id,
            house_id,
            flat,
            station_id,
            system_id,
            status_id
        )
        VALUES (?, ?, NULL, ?, ?, 2, 1)
        """;

        jdbcTemplate.update(sql, clientId, streetId, flat, stationId);
    }

    public void updateClient(java.util.Map<String, Object> data) {
        String persAcc = (String) data.get("personalAccount");
        String clientName = (String) data.get("clientName");
        Integer clientType = (Integer) data.get("clientType");
        Integer counterIn = (Integer) data.get("counterIn");
        Integer cntPersons = (Integer) data.get("cntPersons");
        Integer cntPersonsFact = (Integer) data.get("cntPersonsFact");
        Integer streetId = (Integer) data.get("streetId");
        String house = (String) data.get("house");
        String flat = (String) data.get("flat");

        // Обновляем данные клиента
        String sql = """
        UPDATE wot_clients
        SET client_name = ?,
            client_type_id = ?,
            counter_in_id = ?,
            cnt_pers = COALESCE(?, cnt_pers),
            cnt_pers_fact = COALESCE(?, cnt_pers_fact)
        WHERE pers_account = ?
        """;

        jdbcTemplate.update(sql, clientName, clientType, counterIn,
                cntPersons, cntPersonsFact, persAcc);

        // Проверяем есть ли адрес
        Integer addressCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM wot_address WHERE client_id = (SELECT id FROM wot_clients WHERE pers_account = ?)",
                new Object[]{persAcc},
                Integer.class
        );

        Integer stationId = jdbcTemplate.queryForObject(
                "SELECT station_id FROM wot_streets WHERE id = ?",
                new Object[]{streetId},
                Integer.class
        );

        if (addressCount > 0) {
            // Обновляем адрес
            String updateAddress = """
            UPDATE wot_address
            SET street_id = ?,
                flat = ?,
                station_id = ?
            WHERE client_id = (SELECT id FROM wot_clients WHERE pers_account = ?)
            """;
            jdbcTemplate.update(updateAddress, streetId, flat, stationId, persAcc);
        } else {
            // Создаём адрес
            String insertAddress = """
            INSERT INTO wot_address (
                client_id, street_id, house_id, flat, station_id, system_id, status_id
            )
            VALUES (
                (SELECT id FROM wot_clients WHERE pers_account = ?),
                ?, NULL, ?, ?, 2, 1
            )
            """;
            jdbcTemplate.update(insertAddress, persAcc, streetId, flat, stationId);
        }
    }

    // ========== Справочник: Станции ==========
    public java.util.List<StationDTO> getAllStations() {
        String sql = "SELECT id as station_id, station_name FROM wot_stations ORDER BY station_name";
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            StationDTO dto = new StationDTO();
            dto.setStationId(rs.getLong("station_id"));
            dto.setStationName(rs.getString("station_name"));
            return dto;
        });
    }

    public void saveStation(String stationName) {
        String sql = "INSERT INTO wot_stations (station_name, system_id, status_id) VALUES (?, 2, 1)";
        jdbcTemplate.update(sql, stationName);
    }

    public void updateStation(Long stationId, String stationName) {
        String sql = "UPDATE wot_stations SET station_name = ? WHERE id = ?";
        jdbcTemplate.update(sql, stationName, stationId);
    }

    public void deleteStation(Long stationId) {
        String sql = "DELETE FROM wot_stations WHERE id = ?";
        jdbcTemplate.update(sql, stationId);
    }

    // ========== Справочник: Улицы ==========
    public java.util.List<StreetDTO> getAllStreetsAll() {
        String sql = """
            SELECT ws.id as street_id, ws.street_name, ws.station_id, s.station_name
            FROM wot_streets ws
            LEFT JOIN wot_stations s ON s.id = ws.station_id
            ORDER BY ws.street_name
        """;
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            StreetDTO dto = new StreetDTO();
            dto.setStreetId(rs.getInt("street_id"));
            dto.setStreetName(rs.getString("street_name"));
            return dto;
        });
    }

    public void saveStreet(String streetName, Integer stationId) {
        String sql = "INSERT INTO wot_streets (street_name, station_id, system_id, status_id) VALUES (?, ?, 2, 1)";
        jdbcTemplate.update(sql, streetName, stationId);
    }

    public void updateStreet(Integer streetId, String streetName, Integer stationId) {
        String sql = "UPDATE wot_streets SET street_name = ?, station_id = ? WHERE id = ?";
        jdbcTemplate.update(sql, streetName, stationId, streetId);
    }

    public void deleteStreet(Integer streetId) {
        String sql = "DELETE FROM wot_streets WHERE id = ?";
        jdbcTemplate.update(sql, streetId);
    }

    // ========== Справочник: Дома ==========
    public java.util.List<HouseDTO> getAllHousesAll() {
        String sql = """
            SELECT h.id as house_id, h.house, h.street_id,
                   s.street_name || ' ' || h.house as house_name, s.station_id
            FROM wot_houses h
            LEFT JOIN wot_streets s ON s.id = h.street_id
            ORDER BY s.street_name, h.house
        """;
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            HouseDTO dto = new HouseDTO();
            dto.setHouseId(rs.getInt("house_id"));
            dto.setHouse(rs.getString("house"));
            dto.setStreetId(rs.getInt("street_id"));
            dto.setStreetName(rs.getString("house_name"));
            dto.setStationId(rs.getInt("station_id"));
            dto.setHouseName(rs.getString("house_name"));
            return dto;
        });
    }

    public void saveHouse(String house, Integer streetId, Integer stationId) {
        String sql = "INSERT INTO wot_houses (house, street_id, station_id, system_id, status_id) VALUES (?, ?, ?, 2, 1)";
        jdbcTemplate.update(sql, house, streetId, stationId);
    }

    public void updateHouse(Integer houseId, String house, Integer streetId, Integer stationId) {
        String sql = "UPDATE wot_houses SET house = ?, street_id = ?, station_id = ? WHERE id = ?";
        jdbcTemplate.update(sql, house, streetId, stationId, houseId);
    }

    public void deleteHouse(Integer houseId) {
        String sql = "DELETE FROM wot_houses WHERE id = ?";
        jdbcTemplate.update(sql, houseId);
    }

    // ========== Справочник: Тарифы ==========
    public java.util.List<TariffDTO> getAllTariffs() {
        String sql = "SELECT id as tariff_id, tarif_name, tarif as tariff_rate, tarif_status_id as status_id FROM wot_tariffs ORDER BY tarif_name";
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            TariffDTO dto = new TariffDTO();
            dto.setTariffId(rs.getInt("tariff_id"));
            dto.setTariffName(rs.getString("tarif_name"));
            dto.setTariffRate(rs.getDouble("tariff_rate"));
            dto.setStatusId(rs.getInt("status_id"));
            return dto;
        });
    }

    public void saveTariff(String tariffName, Double tariffRate, Integer statusId) {
        String sql = "INSERT INTO wot_tariffs (tarif_name, tarif, tarif_status_id) VALUES (?, ?, ?)";
        jdbcTemplate.update(sql, tariffName, tariffRate, statusId);
    }

    public void updateTariff(Integer tariffId, String tariffName, Double tariffRate, Integer statusId) {
        String sql = "UPDATE wot_tariffs SET tarif_name = ?, tarif = ?, tarif_status_id = ? WHERE id = ?";
        jdbcTemplate.update(sql, tariffName, tariffRate, statusId, tariffId);
    }

    public void deleteTariff(Integer tariffId) {
        String sql = "DELETE FROM wot_tariffs WHERE id = ?";
        jdbcTemplate.update(sql, tariffId);
    }
}
