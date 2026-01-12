package com.meli.social;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

import javax.sql.DataSource;
import java.sql.Connection;
import java.time.Instant;

@RestController
public class HealthController {

    private final String serviceName;
    private final DataSource dataSource;

    public HealthController(
            @Value("${spring.application.name:social}") String serviceName,
            DataSource dataSource
    ) {
        this.serviceName = serviceName;
        this.dataSource = dataSource;
    }

    @GetMapping("/healthcheck")
    public ResponseEntity<HealthResponse> health() {
        Instant now = Instant.now();

        ComponentStatus overall = new ComponentStatus("UP", "🟢");
        ComponentStatus db = checkDb();

        MemoryInfo memory = buildMemoryInfo();

        HealthResponse response = new HealthResponse(
                overall.status(),
                overall.indicator(),
                this.serviceName,
                now,
                db,
                memory
        );

        return ResponseEntity.ok(response);
    }

    private ComponentStatus checkDb() {
        try (Connection connection = dataSource.getConnection()) {
            boolean valid = connection.isValid(2);
            if (valid) {
                return new ComponentStatus("UP", "🟢");
            }
            return new ComponentStatus("DOWN", "🔴");
        } catch (Exception ex) {
            String message = ex.getMessage();
            if (message == null || message.isBlank()) {
                message = ex.getClass().getSimpleName();
            }
            return new ComponentStatus("DOWN", "🔴", message);
        }
    }

    @GetMapping("/health")
    public RedirectView healthActuator() {
        return new RedirectView("/actuator/health");
    }

    private MemoryInfo buildMemoryInfo() {
        Runtime runtime = Runtime.getRuntime();
        long max = runtime.maxMemory();
        long used = runtime.totalMemory() - runtime.freeMemory();
        return new MemoryInfo(used, max);
    }

    public record HealthResponse(
            String status,
            String indicator,
            String service,
            Instant timestamp,
            ComponentStatus db,
            MemoryInfo memory
    ) {
    }

    public record ComponentStatus(String status, String indicator, String detail) {
        public ComponentStatus(String status, String indicator) {
            this(status, indicator, null);
        }
    }

    public record MemoryInfo(long usedBytes, long maxBytes) {
    }
}
