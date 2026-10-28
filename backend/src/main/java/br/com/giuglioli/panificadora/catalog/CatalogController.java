package br.com.giuglioli.panificadora.catalog;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

@RestController @RequestMapping("/api")
public class CatalogController {
    private final JdbcTemplate db;
    public CatalogController(JdbcTemplate db) { this.db = db; }
    @GetMapping("/products") public List<Map<String,Object>> products() { return db.queryForList("select * from products where active order by name"); }
    @PostMapping("/products") public Map<String,Object> addProduct(@Valid @RequestBody Product p) { db.update("insert into products(name,category,unit,price,cost) values(?,?,?,?,?)", p.name(), p.category(), p.unit(), p.price(), p.cost()); return Map.of("saved", true); }
    @PutMapping("/products/{id}") public Map<String,Object> editProduct(@PathVariable long id, @Valid @RequestBody Product p) { db.update("update products set name=?,category=?,unit=?,price=?,cost=?,active=? where id=?", p.name(),p.category(),p.unit(),p.price(),p.cost(),p.active(),id); return Map.of("saved", true); }
    @GetMapping("/customers") public List<Map<String,Object>> customers() { return db.queryForList("select * from customers order by city,name"); }
    @PostMapping("/customers") public Map<String,Object> addCustomer(@Valid @RequestBody Customer c) { db.update("insert into customers(name,city) values (?,?)", c.name(),c.city()); return Map.of("saved", true); }
    public record Product(@NotBlank String name, @NotBlank String category, @NotBlank String unit, @NotNull @PositiveOrZero BigDecimal price, @NotNull @PositiveOrZero BigDecimal cost, boolean active) {}
    public record Customer(@NotBlank String name, @NotBlank String city) {}
}
