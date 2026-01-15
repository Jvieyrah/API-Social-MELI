package com.meli.social;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
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

    @GetMapping(value = "/healthcheck", produces = MediaType.TEXT_HTML_VALUE)
    public ResponseEntity<String> healthcheckHtml() {
        Instant now = Instant.now();

        ComponentStatus overall = new ComponentStatus("UP", "🟢");
        ComponentStatus db = checkDb();

        MemoryInfo memory = buildMemoryInfo();

        String html = buildHealthcheckHtml(overall, db, memory, now);
        return ResponseEntity.ok(html);
    }

    @GetMapping("/healthcheck/json")
    public ResponseEntity<HealthResponse> healthcheckJson() {
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

    private String buildHealthcheckHtml(ComponentStatus overall, ComponentStatus db, MemoryInfo memory, Instant now) {
        String overallBadgeClass = "UP".equalsIgnoreCase(overall.status()) ? "badge--up" : "badge--down";
        String dbBadgeClass = "UP".equalsIgnoreCase(db.status()) ? "badge--up" : "badge--down";

        String memoryUsed = formatBytes(memory.usedBytes());
        String memoryMax = formatBytes(memory.maxBytes());
        String memoryPct = memory.maxBytes() > 0
                ? String.format("%.1f%%", (memory.usedBytes() * 100.0) / memory.maxBytes())
                : "n/a";

        String dbDetail = db.detail() == null ? "" : escapeHtml(db.detail());
        String nowText = escapeHtml(now.toString());

        return """
                <!doctype html>
                <html lang=\"pt-BR\">
                <head>
                  <meta charset=\"utf-8\" />
                  <meta name=\"viewport\" content=\"width=device-width, initial-scale=1\" />
                  <title>Healthcheck</title>
                  <style>
                    :root {
                      --bg: #0b1220;
                      --card: rgba(255,255,255,.06);
                      --border: rgba(255,255,255,.12);
                      --text: rgba(255,255,255,.92);
                      --muted: rgba(255,255,255,.72);
                      --up: #22c55e;
                      --down: #ef4444;
                      --accent: #60a5fa;
                    }
                    * { box-sizing: border-box; }
                    body {
                      margin: 0;
                      font-family: ui-sans-serif, system-ui, -apple-system, Segoe UI, Roboto, Helvetica, Arial, \"Apple Color Emoji\", \"Segoe UI Emoji\";
                      color: var(--text);
                      background:
                        radial-gradient(1000px 600px at 10%% 10%%, rgba(96,165,250,.18), transparent 55%%),
                        radial-gradient(900px 500px at 80%% 20%%, rgba(34,197,94,.14), transparent 55%%),
                        radial-gradient(900px 500px at 50%% 90%%, rgba(239,68,68,.10), transparent 55%%),
                        var(--bg);
                      min-height: 100vh;
                    }
                    .wrap { max-width: 980px; margin: 0 auto; padding: 28px 18px 60px; }
                    header {
                      display: flex;
                      align-items: baseline;
                      justify-content: space-between;
                      gap: 12px;
                      margin-bottom: 16px;
                    }
                    h1 { font-size: 22px; margin: 0; letter-spacing: .2px; }
                    .meta { color: var(--muted); font-size: 13px; }
                    .grid {
                      display: grid;
                      grid-template-columns: 1fr;
                      gap: 12px;
                    }
                    @media (min-width: 820px) {
                      .grid { grid-template-columns: 1fr 1fr; }
                    }
                    .card {
                      background: var(--card);
                      border: 1px solid var(--border);
                      border-radius: 16px;
                      padding: 16px 16px;
                      backdrop-filter: blur(10px);
                    }
                    .card h2 { margin: 0 0 10px; font-size: 14px; color: var(--muted); font-weight: 600; text-transform: uppercase; letter-spacing: .12em; }
                    .row { display: flex; align-items: center; justify-content: space-between; gap: 10px; padding: 8px 0; border-top: 1px dashed rgba(255,255,255,.10); }
                    .row:first-of-type { border-top: 0; }
                    .label { color: var(--muted); font-size: 13px; }
                    .value { font-size: 14px; font-weight: 600; }
                    .badge {
                      display: inline-flex;
                      align-items: center;
                      gap: 8px;
                      padding: 6px 10px;
                      border-radius: 999px;
                      border: 1px solid rgba(255,255,255,.16);
                      font-size: 12px;
                      font-weight: 700;
                      letter-spacing: .02em;
                    }
                    .badge--up { background: rgba(34,197,94,.14); border-color: rgba(34,197,94,.35); }
                    .badge--down { background: rgba(239,68,68,.14); border-color: rgba(239,68,68,.35); }
                    .pill {
                      display: inline-flex;
                      align-items: center;
                      gap: 8px;
                      padding: 8px 12px;
                      border-radius: 14px;
                      border: 1px solid rgba(96,165,250,.35);
                      background: rgba(96,165,250,.12);
                      font-size: 13px;
                      color: rgba(255,255,255,.90);
                    }
                    .sub { color: var(--muted); font-size: 12px; margin-top: 4px; }
                    .mono { font-family: ui-monospace, SFMono-Regular, Menlo, Monaco, Consolas, \"Liberation Mono\", \"Courier New\", monospace; }
                    footer { margin-top: 14px; color: var(--muted); font-size: 12px; }
                    a { color: var(--accent); text-decoration: none; }
                    a:hover { text-decoration: underline; }
                  </style>
                </head>
                <body>
                  <div class=\"wrap\">
                    <header>
                      <div>
                        <h1>Healthcheck</h1>
                        <div class=\"meta\">Serviço: <span class=\"mono\">%s</span></div>
                      </div>
                      <div class=\"pill\">
                        <span class=\"badge %s\">%s %s</span>
                      </div>
                    </header>

                    <div class=\"grid\">
                      <section class=\"card\">
                        <h2>Resumo</h2>
                        <div class=\"row\">
                          <div class=\"label\">Status geral</div>
                          <div class=\"value\"><span class=\"badge %s\">%s %s</span></div>
                        </div>
                        <div class=\"row\">
                          <div class=\"label\">Timestamp</div>
                          <div class=\"value mono\">%s</div>
                        </div>
                        <div class=\"row\">
                          <div class=\"label\">Actuator</div>
                          <div class=\"value\"><a href=\"/actuator/health\">/actuator/health</a></div>
                        </div>
                        <div class=\"row\">
                          <div class=\"label\">JSON</div>
                          <div class=\"value\"><a href=\"/healthcheck/json\">/healthcheck/json</a></div>
                        </div>
                      </section>

                      <section class=\"card\">
                        <h2>Dependências</h2>
                        <div class=\"row\">
                          <div>
                            <div class=\"label\">Banco de dados</div>
                            <div class=\"sub\">Conexão e validação em 2s</div>
                          </div>
                          <div class=\"value\"><span class=\"badge %s\">%s %s</span></div>
                        </div>
                        %s
                      </section>

                      <section class=\"card\">
                        <h2>Memória JVM</h2>
                        <div class=\"row\">
                          <div class=\"label\">Usada</div>
                          <div class=\"value mono\">%s</div>
                        </div>
                        <div class=\"row\">
                          <div class=\"label\">Máxima</div>
                          <div class=\"value mono\">%s</div>
                        </div>
                        <div class=\"row\">
                          <div class=\"label\">Ocupação</div>
                          <div class=\"value mono\">%s</div>
                        </div>
                      </section>

                      <section class=\"card\">
                        <h2>Como interpretar</h2>
                        <div class=\"row\">
                          <div class=\"label\">UP</div>
                          <div class=\"value\">Aplicação ok e dependências saudáveis</div>
                        </div>
                        <div class=\"row\">
                          <div class=\"label\">DOWN</div>
                          <div class=\"value\">Falha em dependência (ex.: DB). Ver detalhe.</div>
                        </div>
                      </section>
                    </div>

                    <footer>
                      Dica: use <span class=\"mono\">/health</span> para redirecionar ao actuator.
                    </footer>
                  </div>
                </body>
                </html>
                """.formatted(
                escapeHtml(this.serviceName),
                overallBadgeClass,
                escapeHtml(overall.indicator()),
                escapeHtml(overall.status()),
                overallBadgeClass,
                escapeHtml(overall.indicator()),
                escapeHtml(overall.status()),
                nowText,
                dbBadgeClass,
                escapeHtml(db.indicator()),
                escapeHtml(db.status()),
                dbDetail.isBlank()
                        ? ""
                        : """
                          <div class=\"row\">
                            <div class=\"label\">Detalhe</div>
                            <div class=\"value mono\">%s</div>
                          </div>
                        """.formatted(dbDetail),
                escapeHtml(memoryUsed),
                escapeHtml(memoryMax),
                escapeHtml(memoryPct)
        );
    }

    private String formatBytes(long bytes) {
        if (bytes < 0) {
            return "n/a";
        }
        String[] units = new String[]{"B", "KB", "MB", "GB", "TB"};
        double value = bytes;
        int unitIndex = 0;
        while (value >= 1024 && unitIndex < units.length - 1) {
            value /= 1024;
            unitIndex++;
        }
        if (unitIndex == 0) {
            return String.format("%d %s", bytes, units[unitIndex]);
        }
        return String.format("%.2f %s", value, units[unitIndex]);
    }

    private String escapeHtml(String value) {
        if (value == null) {
            return "";
        }
        return value
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
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
