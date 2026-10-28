package br.com.giuglioli.panificadora.schedule;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

@Service
public class ScheduleService {
    private final JdbcTemplate db;
    public ScheduleService(JdbcTemplate db) { this.db = db; }

    public Map<String,Object> get(LocalDate date, String period) {
        var head = db.queryForList("select id, delivery_date, period, status, updated_at from schedules where delivery_date=? and period=?", date, period);
        if (head.isEmpty()) return Map.of("date", date, "period", period, "status", "OPEN", "customers", db.queryForList("select id,name,city from customers where active order by city,name"), "products", db.queryForList("select id,name,category,unit,price from products where active order by name"), "lines", List.of());
        long id = ((Number)head.get(0).get("id")).longValue();
        return Map.of("schedule", head.get(0), "customers", db.queryForList("select id,name,city from customers where active order by city,name"), "products", db.queryForList("select id,name,category,unit,price from products where active order by name"), "lines", db.queryForList("select customer_id,product_id,quantity from schedule_lines where schedule_id=?", id), "audit", db.queryForList("select employee_label,changed_at,summary from schedule_audit where schedule_id=? order by changed_at desc limit 10", id));
    }

    @Transactional
    public Map<String,Object> save(LocalDate date, String period, String employee, List<Line> lines) {
        var existing = db.queryForList("select id from schedules where delivery_date=? and period=? for update", date, period);
        long id;
        if (existing.isEmpty()) {
            db.update("insert into schedules(delivery_date,period) values (?,?)", date, period);
            id = db.queryForObject("select id from schedules where delivery_date=? and period=?", Long.class, date, period);
        } else { id = ((Number)existing.get(0).get("id")).longValue(); db.update("delete from schedule_lines where schedule_id=?", id); }
        for (Line line : lines) {
            if (line.quantity() < 0) throw new IllegalArgumentException("A quantidade não pode ser negativa.");
            db.update("insert into schedule_lines(schedule_id,customer_id,product_id,quantity) values (?,?,?,?)", id, line.customerId(), line.productId(), line.quantity());
        }
        db.update("update schedules set updated_at=now() where id=?", id);
        db.update("insert into schedule_audit(schedule_id,employee_label,summary) values (?,?,?)", id, employee == null || employee.isBlank() ? "Funcionário" : employee, "Programação salva com " + lines.size() + " linhas.");
        return Map.of("id", id, "date", date, "period", period, "savedLines", lines.size());
    }

    public record Line(@Positive long customerId, @Positive long productId, @PositiveOrZero int quantity) {}
}
