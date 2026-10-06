package io.github.aguinaldo1.focusflow.persistence;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.sql.Connection;
import java.sql.ResultSet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DatabaseManagerTest {

    @TempDir
    Path temporaryDirectory;

    @Test
    void shouldCreateObjectivesTable()
            throws Exception {

        Path databasePath =
                temporaryDirectory.resolve(
                        "focusflow-test.db"
                );

        DatabaseManager databaseManager =
                new DatabaseManager(
                        databasePath
                );

        databaseManager.initialize();

        assertTrue(
                databasePath.toFile()
                        .exists()
        );

        try (
                Connection connection =
                        databaseManager.openConnection();

                ResultSet tables =
                        connection
                                .getMetaData()
                                .getTables(
                                        null,
                                        null,
                                        "objectives",
                                        null
                                )
        ) {

            assertTrue(
                    tables.next()
            );

            assertEquals(
                    "objectives",
                    tables.getString(
                            "TABLE_NAME"
                    )
            );
        }
    }

    @Test
    void shouldAllowInitializationMoreThanOnce()
            throws Exception {

        Path databasePath =
                temporaryDirectory.resolve(
                        "focusflow-test.db"
                );

        DatabaseManager databaseManager =
                new DatabaseManager(
                        databasePath
                );

        databaseManager.initialize();
        databaseManager.initialize();

        try (
                Connection connection =
                        databaseManager.openConnection();

                ResultSet tables =
                        connection
                                .getMetaData()
                                .getTables(
                                        null,
                                        null,
                                        "objectives",
                                        null
                                )
        ) {

            assertTrue(
                    tables.next()
            );
        }
    }
}
