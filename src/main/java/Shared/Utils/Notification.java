package Shared.Utils;

import java.awt.*;

public class Notification {
    private static final String ICON_PATH = "youtubeicon.png";
    public static void sendNotification(String title , String description) {
        if (!SystemTray.isSupported()) {
            System.out.println("System tray not supported!");
            return;
        }

        Image image = Toolkit.getDefaultToolkit().getImage(ICON_PATH);

        TrayIcon trayIcon = new TrayIcon(image, "Tray Demo" , new PopupMenu());
        trayIcon.setImageAutoSize(true);
        trayIcon.setToolTip("System tray icon demo");

        SystemTray tray = SystemTray.getSystemTray();

        try {
            tray.add(trayIcon);
            trayIcon.displayMessage(title, description, TrayIcon.MessageType.INFO);
        } catch (AWTException e) {
            System.out.println("TrayIcon could not be added.");
            e.printStackTrace();
        }
    }
}
