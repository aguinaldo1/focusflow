package io.github.aguinaldo1.focusflow.desktop;

import java.awt.Toolkit;

public final class SystemAlert {

    public void playTimerFinished() {

        Toolkit.getDefaultToolkit()
                .beep();
    }
}
