package io.github.aguinaldo1.focusflow.persistence;

import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Objects;

public final class DatabaseManager {

    private final String connectionUrl;

    public DatabaseManager(
            Path databasePath
    ) {

        Objects.requireNonNull(
                databasePath,
                "databasePath"
        );

        this.connectionUrl =
                "jdbc:sqlite:"
                        + databasePath.toAbsolutePath();
    }

    public Connection openConnection()
            throws SQLException {

        return DriverManager.getConnection(
                connectionUrl
        );
    }

    public void initialize()
            throws SQLException {

        try (
                Connection connection =
                        openConnection();

                Statement statement =
                        connection.createStatement()
        ) {

            statement.execute(
                    """
                    CREATE TABLE IF NOT EXISTS objectives (
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
        }
    }
}
