package io.github.aguinaldo1.focusflow.objective;

public enum ObjectiveComplexity {

    EASY(2),
    MEDIUM(4),
    HARD(6);

    private final int suggestedFocusCycles;

    ObjectiveComplexity(
            int suggestedFocusCycles
    ) {

        this.suggestedFocusCycles =
                suggestedFocusCycles;
    }

    public int suggestedFocusCycles() {
        return suggestedFocusCycles;
    }
}
