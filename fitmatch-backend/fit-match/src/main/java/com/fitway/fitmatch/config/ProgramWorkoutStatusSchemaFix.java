package com.fitway.fitmatch.config;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class ProgramWorkoutStatusSchemaFix implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(ProgramWorkoutStatusSchemaFix.class);
    private final JdbcTemplate jdbcTemplate;

    @Override
    public void run(ApplicationArguments args) {
        try {
            dropOldProgramWorkoutStatusConstraints();
            dropOldProgramWorkoutStatusIndexes();
            jdbcTemplate.execute("CREATE UNIQUE INDEX IF NOT EXISTS uk_program_workout_status_program_sequence " +
                    "ON PROGRAM_WORKOUT_STATUS(PROGRAM_ID, SEQUENCE)");
        } catch (Exception ex) {
            log.warn("Could not run program workout status schema fix: {}", ex.getMessage());
        }
    }

    private void dropOldProgramWorkoutStatusConstraints() {
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(
                "SELECT CONSTRAINT_NAME, COLUMN_NAME FROM INFORMATION_SCHEMA.CONSTRAINT_COLUMNS " +
                        "WHERE TABLE_NAME = 'PROGRAM_WORKOUT_STATUS' ORDER BY CONSTRAINT_NAME, ORDINAL_POSITION");

        Map<String, Set<String>> constraints = new LinkedHashMap<>();
        for (Map<String, Object> row : rows) {
            String constraintName = (String) row.get("CONSTRAINT_NAME");
            String columnName = ((String) row.get("COLUMN_NAME")).toUpperCase();
            constraints.computeIfAbsent(constraintName, k -> new LinkedHashSet<>()).add(columnName);
        }

        for (Map.Entry<String, Set<String>> entry : constraints.entrySet()) {
            Set<String> columns = entry.getValue();
            if (columns.contains("PROGRAM_ID") && columns.contains("WORKOUT_ID")) {
                String constraintName = entry.getKey();
                jdbcTemplate.execute("ALTER TABLE PROGRAM_WORKOUT_STATUS DROP CONSTRAINT IF EXISTS " + quoteIdentifier(constraintName));
                log.info("Dropped old constraint {} from PROGRAM_WORKOUT_STATUS", constraintName);
            }
        }
    }

    private void dropOldProgramWorkoutStatusIndexes() {
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(
                "SELECT INDEX_NAME, COLUMN_NAME FROM INFORMATION_SCHEMA.INDEXES " +
                        "WHERE TABLE_NAME = 'PROGRAM_WORKOUT_STATUS' ORDER BY INDEX_NAME, ORDINAL_POSITION");

        Map<String, Set<String>> indexes = new LinkedHashMap<>();
        for (Map<String, Object> row : rows) {
            String indexName = (String) row.get("INDEX_NAME");
            String columnName = ((String) row.get("COLUMN_NAME")).toUpperCase();
            indexes.computeIfAbsent(indexName, k -> new LinkedHashSet<>()).add(columnName);
        }

        for (Map.Entry<String, Set<String>> entry : indexes.entrySet()) {
            Set<String> columns = entry.getValue();
            if (columns.contains("PROGRAM_ID") && columns.contains("WORKOUT_ID")) {
                String indexName = entry.getKey();
                jdbcTemplate.execute("DROP INDEX IF EXISTS " + quoteIdentifier(indexName));
                log.info("Dropped old index {} from PROGRAM_WORKOUT_STATUS", indexName);
            }
        }
    }

    private String quoteIdentifier(String identifier) {
        return "\"" + identifier.replace("\"", "\"\"") + "\"";
    }
}
