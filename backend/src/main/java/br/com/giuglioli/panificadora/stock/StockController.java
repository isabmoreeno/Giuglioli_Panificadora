package br.com.giuglioli.panificadora.stock;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

@RestController @RequestMapping("/api/ingredients")
public class StockController {
    private final JdbcTemplate db;
    private final StockService service;
    public StockController(JdbcTemplate db, StockService service) { this.db = db; this.service = service; }
    @GetMapping public List<Map<String,Object>> list() { return db.queryForList("select * from ingredients where active order by name"); }
    @PostMapping public Map<String,Object> create(@Valid @RequestBody IngredientRequest request) {
        db.update("insert into ingredients(name,unit,quantity,minimum_quantity) values (?,?,?,?)", request.name(),request.unit(),request.quantity(),request.minimumQuantity());
        return Map.of("saved", true);
    }
    @PostMapping("/{id}/movements") public Map<String,Object> move(@PathVariable long id, @Valid @RequestBody MovementRequest request) { return service.move(id,request.type(),request.quantity(),request.reason()); }
    public record IngredientRequest(@NotBlank String name,@NotBlank String unit,@NotNull @PositiveOrZero BigDecimal quantity,@NotNull @PositiveOrZero BigDecimal minimumQuantity) {}
    public record MovementRequest(@NotBlank String type,@NotNull @PositiveOrZero BigDecimal quantity,@NotBlank String reason) {}
}

@Service
class StockService {
    private final JdbcTemplate db;
    StockService(JdbcTemplate db) { this.db=db; }
    @Transactional public Map<String,Object> move(long id,String type,BigDecimal quantity,String reason) {
        if (!List.of("IN","OUT").contains(type) || quantity==null || quantity.signum()<=0 || reason==null || reason.isBlank())
            throw new IllegalArgumentException("Informe tipo, quantidade positiva e motivo para a movimentação.");
        var row=db.queryForList("select quantity from ingredients where id=? and active for update",id);
        if(row.isEmpty()) throw new IllegalArgumentException("Ingrediente não encontrado.");
        BigDecimal current=(BigDecimal)row.get(0).get("quantity");
        BigDecimal next="IN".equals(type)?current.add(quantity):current.subtract(quantity);
        if(next.signum()<0) throw new IllegalArgumentException("O consumo não pode deixar o estoque negativo.");
        db.update("update ingredients set quantity=? where id=?",next,id);
        db.update("insert into stock_movements(ingredient_id,movement_type,quantity,reason) values (?,?,?,?)",id,type,quantity,reason.trim());
        return Map.of("ingredientId",id,"quantity",next);
    }
}
