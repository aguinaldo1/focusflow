package io.github.aguinaldo1.focusflow.desktop;

import java.awt.GraphicsEnvironment;
import java.awt.SystemTray;

public final class DesktopCapabilities {

    private DesktopCapabilities() {
    }

    public static boolean isGraphicalEnvironmentAvailable() {
        return !GraphicsEnvironment.isHeadless();
    }

    public static boolean isSystemTrayAvailable() {

        if (!isGraphicalEnvironmentAvailable()) {
            return false;
        }

        return SystemTray.isSupported();
    }

    static boolean canUseSystemTray(
            boolean graphicalEnvironmentAvailable,
            boolean systemTraySupported
    ) {

        return graphicalEnvironmentAvailable
                && systemTraySupported;
    }
}
