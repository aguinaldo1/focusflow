package io.github.aguinaldo1.focusflow.desktop;

import java.awt.GraphicsEnvironment;
import java.awt.SystemTray;

public final class DesktopCapabilities {

    private DesktopCapabilities() {
    }

    public static boolean isGraphicalEnvironmentAvailable() {
        return !GraphicsEnvironment.isHeadless();
    }

    public static boolean isRunningOnWsl() {
        return System.getenv("WSL_DISTRO_NAME") != null
                || System.getenv("WSL_INTEROP") != null;
    }

    public static boolean isSystemTrayAvailable() {

        if (!isGraphicalEnvironmentAvailable()) {
            return false;
        }

        if (isRunningOnWsl()) {
            return false;
        }

        return SystemTray.isSupported();
    }

    static boolean canUseSystemTray(
            boolean graphicalEnvironmentAvailable,
            boolean runningOnWsl,
            boolean systemTraySupported
    ) {

        return graphicalEnvironmentAvailable
                && !runningOnWsl
                && systemTraySupported;
    }
}
