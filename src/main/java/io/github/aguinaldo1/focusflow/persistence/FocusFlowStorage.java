package io.github.aguinaldo1.focusflow.persistence;

import io.github.aguinaldo1.focusflow.objective.Objective;
import io.github.aguinaldo1.focusflow.objective.ObjectiveManager;

import java.nio.file.Path;
import java.sql.SQLException;
import java.util.Objects;

public final class FocusFlowStorage {

    private final DatabaseManager databaseManager;
    private final SqliteObjectiveRepository repository;
    private final ObjectiveLoader loader;

    public FocusFlowStorage(
            Path databasePath
    ) {

        this.databaseManager =
                new DatabaseManager(
                        Objects.requireNonNull(
                                databasePath,
                                "databasePath"
                        )
                );

        this.repository =
                new SqliteObjectiveRepository(
                        databaseManager
                );

        this.loader =
                new ObjectiveLoader(
                        repository,
                        new ObjectiveRestorer()
                );
    }

    public ObjectiveManager load()
            throws SQLException {

        databaseManager.initialize();

        return loader.load();
    }

    public void save(
            ObjectiveManager manager
    ) throws SQLException {

        Objects.requireNonNull(
                manager,
                "manager"
        );

        databaseManager.initialize();

        for (
                Objective objective
                        : manager.getAllObjectives()
        ) {

            repository.save(
                    objective
            );
        }
    }
}
