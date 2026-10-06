package io.github.aguinaldo1.focusflow.persistence;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DatabaseSchemaMigrationTest {

    @TempDir
    Path temporaryDirectory;

    @Test
    void shouldMigrateLegacyDatabaseWithoutLosingObjective()
            throws Exception {

        Path databasePath =
                temporaryDirectory.resolve(
                        "legacy-focusflow.db"
                );

        createLegacyDatabase(
                databasePath
        );

        DatabaseManager databaseManager =
                new DatabaseManager(
                        databasePath
                );

        databaseManager.initialize();

        try (
                Connection connection =
                        databaseManager.openConnection();

                Statement statement =
                        connection.createStatement();

                ResultSet resultSet =
                        statement.executeQuery(
                                """
                                SELECT
                                    name,
                                    complexity,
                                    planned_focus_cycles
                                FROM objectives
                                WHERE id = 'legacy-objective'
                                """
                        )
        ) {

            resultSet.next();

            assertEquals(
                    "Curso Java",
                    resultSet.getString(
                            "name"
                    )
            );

            assertEquals(
                    "MEDIUM",
                    resultSet.getString(
                            "complexity"
                    )
            );

            assertEquals(
                    4,
                    resultSet.getInt(
                            "planned_focus_cycles"
                    )
            );
        }
    }

    private void createLegacyDatabase(
            Path databasePath
    ) throws Exception {

        String connectionUrl =
                "jdbc:sqlite:"
                        + databasePath
                                .toAbsolutePath();

        try (
                Connection connection =
                        DriverManager.getConnection(
                                connectionUrl
                        );

                Statement statement =
                        connection.createStatement()
        ) {

            statement.execute(
                    """
                    CREATE TABLE objectives (
                        id TEXT PRIMARY KEY,
                        name TEXT NOT NULL,
                        description TEXT NOT NULL,
                        status TEXT NOT NULL,
                        created_at TEXT NOT NULL,
                        closed_at TEXT,
                        pomodoro_phase TEXT NOT NULL,
                        pomodoro_status TEXT NOT NULL,
                        completed_focus_cycles INTEGER NOT NULL,
                        remaining_seconds INTEGER NOT NULL
                    )
                    """
            );

            statement.execute(
                    """
                    INSERT INTO objectives (
                        id,
                        name,
                        description,
                        status,
                        created_at,
                        closed_at,
                        pomodoro_phase,
                        pomodoro_status,
                        completed_focus_cycles,
                        remaining_seconds
                    )
                    VALUES (
                        'legacy-objective',
                        'Curso Java',
                        '',
                        'ACTIVE',
                        '2026-10-06T10:00:00Z',
                        NULL,
                        'FOCUS',
                        'IDLE',
                        0,
                        1500
                    )
                    """
            );
        }
    }
}
