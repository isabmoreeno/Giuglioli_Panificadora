package br.com.giuglioli.panificadora.schedule;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;

class ScheduleServiceTest {
    @Test
    void rejectsNegativeQuantities() {
        JdbcTemplate db = mock(JdbcTemplate.class);
        when(db.queryForList(anyString(), any(Object[].class))).thenReturn(List.of());
        ScheduleService service = new ScheduleService(db);
        assertThrows(IllegalArgumentException.class, () -> service.save(
            LocalDate.of(2026, 1, 1), "Manhã", "Funcionário",
            List.of(new ScheduleService.Line(1, 1, -1))));
    }
}
