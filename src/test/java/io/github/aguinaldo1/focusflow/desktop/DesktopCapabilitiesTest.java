package io.github.aguinaldo1.focusflow.desktop;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DesktopCapabilitiesTest {

    @Test
    void shouldRejectSystemTrayWithoutGraphicalEnvironment() {

        assertFalse(
                DesktopCapabilities.canUseSystemTray(
                        false,
                        false,
                        true
                )
        );
    }

    @Test
    void shouldRejectSystemTrayInsideWsl() {

        assertFalse(
                DesktopCapabilities.canUseSystemTray(
                        true,
                        true,
                        true
                )
        );
    }

    @Test
    void shouldRejectUnsupportedSystemTray() {

        assertFalse(
                DesktopCapabilities.canUseSystemTray(
                        true,
                        false,
                        false
                )
        );
    }

    @Test
    void shouldAllowSystemTrayWhenEnvironmentSupportsIt() {

        assertTrue(
                DesktopCapabilities.canUseSystemTray(
                        true,
                        false,
                        true
                )
        );
    }
}
