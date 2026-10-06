package io.github.aguinaldo1.focusflow.persistence;

import io.github.aguinaldo1.focusflow.objective.Objective;
import io.github.aguinaldo1.focusflow.objective.ObjectiveManager;

import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

public final class ObjectiveLoader {

    private final SqliteObjectiveRepository repository;
    private final ObjectiveRestorer restorer;

    public ObjectiveLoader(
            SqliteObjectiveRepository repository,
            ObjectiveRestorer restorer
    ) {

        this.repository =
                Objects.requireNonNull(
                        repository,
                        "repository"
                );

        this.restorer =
                Objects.requireNonNull(
                        restorer,
                        "restorer"
                );
    }

    public ObjectiveManager load()
            throws SQLException {

        List<Objective> objectives =
                repository
                        .findAll()
                        .stream()
                        .map(
                                restorer::restore
                        )
                        .toList();

        return ObjectiveManager.restore(
                objectives
        );
    }
}
