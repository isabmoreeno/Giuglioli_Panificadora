package br.com.giuglioli.panificadora.api;

import java.util.Map;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class HealthController {
    private final JdbcTemplate db;
    public HealthController(JdbcTemplate db) { this.db = db; }
    @GetMapping("/health") public Map<String, String> health() { return Map.of("status", "ok"); }
    @GetMapping("/dashboard") public Map<String,Long> dashboard() {
        return Map.of("schedulesToday", db.queryForObject("select count(*) from schedules where delivery_date=(now() at time zone 'America/Sao_Paulo')::date", Long.class),
            "activeProducts", db.queryForObject("select count(*) from products where active", Long.class),
            "activeCustomers", db.queryForObject("select count(*) from customers where active", Long.class));
    }
}
