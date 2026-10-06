package io.github.aguinaldo1.focusflow.desktop;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.RenderingHints;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.image.BufferedImage;
import java.util.Objects;

public final class SystemTrayIntegration implements AutoCloseable {

    private TrayIcon trayIcon;

    public boolean install(
            Runnable openAction,
            Runnable exitAction
    ) throws AWTException {

        Objects.requireNonNull(
                openAction,
                "openAction"
        );

        Objects.requireNonNull(
                exitAction,
                "exitAction"
        );

        if (!DesktopCapabilities.isSystemTrayAvailable()) {
            return false;
        }

        if (trayIcon != null) {
            return true;
        }

        PopupMenu menu =
                new PopupMenu();

        MenuItem openItem =
                new MenuItem(
                        "Abrir FocusFlow"
                );

        MenuItem exitItem =
                new MenuItem(
                        "Sair"
                );

        openItem.addActionListener(
                event -> openAction.run()
        );

        exitItem.addActionListener(
                event -> exitAction.run()
        );

        menu.add(openItem);
        menu.addSeparator();
        menu.add(exitItem);

        TrayIcon newTrayIcon =
                new TrayIcon(
                        createTrayImage(),
                        "FocusFlow",
                        menu
                );

        newTrayIcon.setImageAutoSize(true);

        newTrayIcon.addActionListener(
                event -> openAction.run()
        );

        SystemTray.getSystemTray()
                .add(newTrayIcon);

        trayIcon =
                newTrayIcon;

        return true;
    }

    public boolean isInstalled() {
        return trayIcon != null;
    }

    @Override
    public void close() {

        if (trayIcon == null) {
            return;
        }

        SystemTray.getSystemTray()
                .remove(trayIcon);

        trayIcon = null;
    }

    private static Image createTrayImage() {

        int size = 32;

        BufferedImage image =
                new BufferedImage(
                        size,
                        size,
                        BufferedImage.TYPE_INT_ARGB
                );

        Graphics2D graphics =
                image.createGraphics();

        try {

            graphics.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
            );

            graphics.setColor(
                    new Color(
                            35,
                            35,
                            35,
                            255
                    )
            );

            graphics.fillRoundRect(
                    1,
                    1,
                    30,
                    30,
                    8,
                    8
            );

            graphics.setColor(
                    Color.WHITE
            );

            graphics.setFont(
                    new Font(
                            Font.SANS_SERIF,
                            Font.BOLD,
                            20
                    )
            );

            graphics.drawString(
                    "F",
                    10,
                    23
            );

        } finally {
            graphics.dispose();
        }

        return image;
    }
}
