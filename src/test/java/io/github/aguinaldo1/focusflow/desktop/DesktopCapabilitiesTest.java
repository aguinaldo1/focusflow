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
                        true
                )
        );
    }

    @Test
    void shouldRejectUnsupportedSystemTray() {

        assertFalse(
                DesktopCapabilities.canUseSystemTray(
                        true,
                        false
                )
        );
    }

    @Test
    void shouldAllowSystemTrayWhenBothCapabilitiesAreAvailable() {

        assertTrue(
                DesktopCapabilities.canUseSystemTray(
                        true,
                        true
                )
        );
    }
}
