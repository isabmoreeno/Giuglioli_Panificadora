package br.com.giuglioli.panificadora.stock;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;

class StockServiceTest {
    @Test
    void refusesMovementThatWouldMakeStockNegative() {
        JdbcTemplate db = mock(JdbcTemplate.class);
        when(db.queryForList(anyString(), any(Object[].class)))
            .thenReturn(List.of(Map.of("quantity", new BigDecimal("2.000"))));
        StockService service = new StockService(db);
        assertThrows(IllegalArgumentException.class, () -> service.move(
            7, "OUT", new BigDecimal("3"), "Consumo de produção"));
    }
}
