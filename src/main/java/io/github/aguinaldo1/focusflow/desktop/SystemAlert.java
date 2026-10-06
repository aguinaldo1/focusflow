package io.github.aguinaldo1.focusflow.desktop;

import java.awt.Toolkit;
import java.io.IOException;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public final class SystemAlert {

    private static final String WINDOWS_SOUND_COMMAND =
            "$paths = @("
                    + "'C:\\Windows\\Media\\Windows Notify System Generic.wav',"
                    + "'C:\\Windows\\Media\\Windows Notify.wav',"
                    + "'C:\\Windows\\Media\\Alarm01.wav'"
                    + "); "
                    + "$sound = $paths "
                    + "| Where-Object { Test-Path $_ } "
                    + "| Select-Object -First 1; "
                    + "if ($sound) { "
                    + "(New-Object System.Media.SoundPlayer $sound).PlaySync(); "
                    + "exit 0 "
                    + "} "
                    + "exit 1";

    public void playTimerFinished() {

        Thread alertThread =
                new Thread(
                        this::playAlert,
                        "focusflow-system-alert"
                );

        alertThread.setDaemon(
                true
        );

        alertThread.start();
    }

    private void playAlert() {

        if (playWindowsNotificationSound()) {
            return;
        }

        Toolkit.getDefaultToolkit()
                .beep();
    }

    private boolean playWindowsNotificationSound() {

        if (!supportsWindowsInterop()) {
            return false;
        }

        try {

            Process process =
                    new ProcessBuilder(
                            "powershell.exe",
                            "-NoProfile",
                            "-NonInteractive",
                            "-Command",
                            WINDOWS_SOUND_COMMAND
                    )
                            .redirectOutput(
                                    ProcessBuilder.Redirect.DISCARD
                            )
                            .redirectError(
                                    ProcessBuilder.Redirect.DISCARD
                            )
                            .start();

            boolean finished =
                    process.waitFor(
                            5,
                            TimeUnit.SECONDS
                    );

            if (!finished) {

                process.destroyForcibly();

                return false;
            }

            return process.exitValue()
                    == 0;

        } catch (IOException exception) {

            return false;

        } catch (InterruptedException exception) {

            Thread.currentThread()
                    .interrupt();

            return false;
        }
    }

    private static boolean supportsWindowsInterop() {

        String operatingSystem =
                System.getProperty(
                        "os.name",
                        ""
                )
                        .toLowerCase(
                                Locale.ROOT
                        );

        if (
                operatingSystem.contains(
                        "windows"
                )
        ) {

            return true;
        }

        String wslDistribution =
                System.getenv(
                        "WSL_DISTRO_NAME"
                );

        return wslDistribution != null
                && !wslDistribution.isBlank();
    }
}
