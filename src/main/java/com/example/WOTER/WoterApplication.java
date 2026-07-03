package com.example.WOTER;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@SpringBootApplication
public class WoterApplication {

	public static void main(String[] args) {
		SpringApplication.run(WoterApplication.class, args);
	}
}

@Component
class DatabaseInitializer {

	private final JdbcTemplate jdbcTemplate;

	public DatabaseInitializer(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	@EventListener(ApplicationReadyEvent.class)
	public void initDatabase() {
		try {
			String sql = """
				CREATE TABLE IF NOT EXISTS wot_events (
					id BIGSERIAL PRIMARY KEY,
					event_type VARCHAR(50) NOT NULL,
					description TEXT NOT NULL,
					event_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
				)
			""";
			jdbcTemplate.execute(sql);
			System.out.println("wot_events table initialized");
		} catch (Exception e) {
			System.err.println("Could not create wot_events table: " + e.getMessage());
		}
	}
}
