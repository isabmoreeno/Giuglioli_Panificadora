package br.com.giuglioli.panificadora.schedule;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

@RestController
@RequestMapping("/api/schedules")
public class ScheduleController {
    private final ScheduleService service;
    public ScheduleController(ScheduleService service) { this.service = service; }
    @GetMapping public Map<String,Object> get(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date, @RequestParam(defaultValue="Manhã") String period) { return service.get(date, period); }
    @PutMapping public Map<String,Object> save(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date, @RequestParam(defaultValue="Manhã") String period, @Valid @RequestBody SaveRequest request) { return service.save(date, period, request.employee(), request.lines()); }
    public record SaveRequest(String employee, @NotNull List<@Valid ScheduleService.Line> lines) {}
}
