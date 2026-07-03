package com.example.WOTER.Repository;

import com.example.WOTER.DTO.EventDTO;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Repository
public class EventRepository {

    private final JdbcTemplate jdbcTemplate;

    public EventRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<EventDTO> findRecentEvents(LocalDateTime since) {
        String sql = """
            SELECT 
                'PAYMENT' as event_type,
                'Оплата: лицевой ' || wc.pers_account || ' сумма ' || cp.amount as description,
                cp.pay_date_reestr as event_time
            FROM wot_clients_pay cp
            JOIN wot_clients wc ON wc.id = cp.client_id
            WHERE cp.pay_date_reestr >= ?
            UNION ALL
            SELECT 
                'SALDO' as event_type,
                'Сальдо рассчитано за ' || ws.month_id || '/' || ws.year_id as description,
                ws.date_calc as event_time
            FROM wot_saldo ws
            WHERE ws.date_calc >= ?
            UNION ALL
            SELECT 
                we.event_type,
                we.description,
                we.event_time
            FROM wot_events we
            WHERE we.event_time >= ?
            ORDER BY event_time DESC
            LIMIT 50
        """;

        return jdbcTemplate.query(sql, new Object[]{since, since, since}, (rs, rowNum) -> {
            EventDTO dto = new EventDTO();
            dto.setEventType(rs.getString("event_type"));
            dto.setDescription(rs.getString("description"));
            dto.setEventTime(rs.getTimestamp("event_time").toLocalDateTime());
            return dto;
        });
    }

    public List<EventDTO> findEvents(LocalDateTime from, LocalDateTime to) {
        String sql = """
            SELECT 
                'PAYMENT' as event_type,
                'Оплата: лицевой ' || wc.pers_account || ' сумма ' || cp.amount as description,
                cp.pay_date_reestr as event_time
            FROM wot_clients_pay cp
            JOIN wot_clients wc ON wc.id = cp.client_id
            WHERE cp.pay_date_reestr BETWEEN ? AND ?
            UNION ALL
            SELECT 
                'SALDO' as event_type,
                'Сальдо рассчитано за ' || ws.month_id || '/' || ws.year_id as description,
                ws.date_calc as event_time
            FROM wot_saldo ws
            WHERE ws.date_calc BETWEEN ? AND ?
            UNION ALL
            SELECT 
                we.event_type,
                we.description,
                we.event_time
            FROM wot_events we
            WHERE we.event_time BETWEEN ? AND ?
            ORDER BY event_time DESC
        """;

        return jdbcTemplate.query(sql, new Object[]{from, to, from, to, from, to}, (rs, rowNum) -> {
            EventDTO dto = new EventDTO();
            dto.setEventType(rs.getString("event_type"));
            dto.setDescription(rs.getString("description"));
            dto.setEventTime(rs.getTimestamp("event_time").toLocalDateTime());
            return dto;
        });
    }

    public void saveEvent(String eventType, String description, LocalDateTime eventTime) {
        String sql = "INSERT INTO wot_events (event_type, description, event_time) VALUES (?, ?, ?)";
        jdbcTemplate.update(sql, eventType, description, eventTime);
    }
}
