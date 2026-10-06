package io.github.aguinaldo1.focusflow.persistence;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Objects;

public final class DatabaseManager {

    private final Path databasePath;
    private final String connectionUrl;

    public DatabaseManager(
            Path databasePath
    ) {

        Objects.requireNonNull(
                databasePath,
                "databasePath"
        );

        this.databasePath =
                databasePath
                        .toAbsolutePath()
                        .normalize();

        this.connectionUrl =
                "jdbc:sqlite:"
                        + this.databasePath;
    }

    public Connection openConnection()
            throws SQLException {

        return DriverManager.getConnection(
                connectionUrl
        );
    }

    public void initialize()
            throws SQLException {

        createDatabaseDirectory();

        try (
                Connection connection =
                        openConnection()
        ) {

            createObjectivesTable(
                    connection
            );

            ensureColumn(
                    connection,
                    "complexity",
                    "TEXT NOT NULL DEFAULT 'MEDIUM'"
            );

            ensureColumn(
                    connection,
                    "planned_focus_cycles",
                    "INTEGER NOT NULL DEFAULT 4"
            );
        }
    }

    private void createObjectivesTable(
            Connection connection
    ) throws SQLException {

        try (
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
                        complexity TEXT NOT NULL DEFAULT 'MEDIUM',
                        planned_focus_cycles INTEGER NOT NULL DEFAULT 4,
                        pomodoro_phase TEXT NOT NULL,
                        pomodoro_status TEXT NOT NULL,
                        completed_focus_cycles INTEGER NOT NULL,
                        remaining_seconds INTEGER NOT NULL
                    )
                    """
            );
        }
    }

    private void ensureColumn(
            Connection connection,
            String columnName,
            String columnDefinition
    ) throws SQLException {

        if (
                columnExists(
                        connection,
                        columnName
                )
        ) {
            return;
        }

        try (
                Statement statement =
                        connection.createStatement()
        ) {

            statement.execute(
                    "ALTER TABLE objectives ADD COLUMN "
                            + columnName
                            + " "
                            + columnDefinition
            );
        }
    }

    private boolean columnExists(
            Connection connection,
            String columnName
    ) throws SQLException {

        try (
                Statement statement =
                        connection.createStatement();

                ResultSet resultSet =
                        statement.executeQuery(
                                "PRAGMA table_info(objectives)"
                        )
        ) {

            while (resultSet.next()) {

                if (
                        columnName.equalsIgnoreCase(
                                resultSet.getString(
                                        "name"
                                )
                        )
                ) {

                    return true;
                }
            }
        }

        return false;
    }

    private void createDatabaseDirectory()
            throws SQLException {

        Path parent =
                databasePath.getParent();

        if (parent == null) {
            return;
        }

        try {

            Files.createDirectories(
                    parent
            );

        } catch (IOException exception) {

            throw new SQLException(
                    "Could not create database directory.",
                    exception
            );
        }
    }
}
