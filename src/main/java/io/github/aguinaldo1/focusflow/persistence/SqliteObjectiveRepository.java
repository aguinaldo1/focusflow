package io.github.aguinaldo1.focusflow.persistence;

import io.github.aguinaldo1.focusflow.objective.Objective;
import io.github.aguinaldo1.focusflow.objective.ObjectiveStatus;
import io.github.aguinaldo1.focusflow.pomodoro.PomodoroPhase;
import io.github.aguinaldo1.focusflow.pomodoro.PomodoroSnapshot;
import io.github.aguinaldo1.focusflow.pomodoro.PomodoroStatus;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public final class SqliteObjectiveRepository {

    private final DatabaseManager databaseManager;

    public SqliteObjectiveRepository(
            DatabaseManager databaseManager
    ) {

        this.databaseManager =
                Objects.requireNonNull(
                        databaseManager,
                        "databaseManager"
                );
    }

    public void save(
            Objective objective
    ) throws SQLException {

        Objects.requireNonNull(
                objective,
                "objective"
        );

        PomodoroSnapshot snapshot =
                objective
                        .getTimer()
                        .snapshot();

        String sql =
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
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                ON CONFLICT(id) DO UPDATE SET
                    name = excluded.name,
                    description = excluded.description,
                    status = excluded.status,
                    created_at = excluded.created_at,
                    closed_at = excluded.closed_at,
                    pomodoro_phase = excluded.pomodoro_phase,
                    pomodoro_status = excluded.pomodoro_status,
                    completed_focus_cycles =
                        excluded.completed_focus_cycles,
                    remaining_seconds =
                        excluded.remaining_seconds
                """;

        try (
                Connection connection =
                        databaseManager.openConnection();

                PreparedStatement statement =
                        connection.prepareStatement(
                                sql
                        )
        ) {

            statement.setString(
                    1,
                    objective
                            .getId()
                            .toString()
            );

            statement.setString(
                    2,
                    objective.getName()
            );

            statement.setString(
                    3,
                    objective.getDescription()
            );

            statement.setString(
                    4,
                    objective
                            .getStatus()
                            .name()
            );

            statement.setString(
                    5,
                    objective
                            .getCreatedAt()
                            .toString()
            );

            if (objective.getClosedAt() == null) {

                statement.setNull(
                        6,
                        Types.VARCHAR
                );

            } else {

                statement.setString(
                        6,
                        objective
                                .getClosedAt()
                                .toString()
                );
            }

            statement.setString(
                    7,
                    snapshot
                            .phase()
                            .name()
            );

            statement.setString(
                    8,
                    snapshot
                            .status()
                            .name()
            );

            statement.setInt(
                    9,
                    snapshot
                            .completedFocusCycles()
            );

            statement.setLong(
                    10,
                    snapshot
                            .remainingTime()
                            .toSeconds()
            );

            statement.executeUpdate();
        }
    }

    public List<PersistedObjective> findAll()
            throws SQLException {

        String sql =
                """
                SELECT
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
                FROM objectives
                ORDER BY created_at
                """;

        List<PersistedObjective> objectives =
                new ArrayList<>();

        try (
                Connection connection =
                        databaseManager.openConnection();

                PreparedStatement statement =
                        connection.prepareStatement(
                                sql
                        );

                ResultSet resultSet =
                        statement.executeQuery()
        ) {

            while (resultSet.next()) {

                objectives.add(
                        mapObjective(
                                resultSet
                        )
                );
            }
        }

        return List.copyOf(
                objectives
        );
    }

    private PersistedObjective mapObjective(
            ResultSet resultSet
    ) throws SQLException {

        String closedAtValue =
                resultSet.getString(
                        "closed_at"
                );

        Instant closedAt =
                closedAtValue == null
                        ? null
                        : Instant.parse(
                                closedAtValue
                        );

        return new PersistedObjective(
                UUID.fromString(
                        resultSet.getString(
                                "id"
                        )
                ),
                resultSet.getString(
                        "name"
                ),
                resultSet.getString(
                        "description"
                ),
                ObjectiveStatus.valueOf(
                        resultSet.getString(
                                "status"
                        )
                ),
                Instant.parse(
                        resultSet.getString(
                                "created_at"
                        )
                ),
                closedAt,
                PomodoroPhase.valueOf(
                        resultSet.getString(
                                "pomodoro_phase"
                        )
                ),
                PomodoroStatus.valueOf(
                        resultSet.getString(
                                "pomodoro_status"
                        )
                ),
                resultSet.getInt(
                        "completed_focus_cycles"
                ),
                resultSet.getLong(
                        "remaining_seconds"
                )
        );
    }
}
