package tray;

import common.Time;
import module.tracker.Mediator;
import module.wallpaper.utilities.Config;
import common.Log;
import tray.model.Dao;

import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import module.wallpaper.MainWallpaper;

public class TrayDateApplication {
    private TrayIcon trayIcon;
    private Mediator mediator;

    public TrayDateApplication(Mediator mediator) {
        this.mediator = mediator;
    }

    public void refresh() {
        TrayDateIconImage image = (TrayDateIconImage) trayIcon.getImage();
        if (image.shouldChange()) {
            trayIcon.setImage(new TrayDateIconImage());
            image.flush();
            trayIcon.displayMessage(
                    "New Day ! ! !",
                    "Just passed midnight.\nYou should stop working",
                    TrayIcon.MessageType.INFO
            );
        }
    }

    public void run() {
        if (SystemTray.isSupported()) {
            trayIcon = new TrayIcon(new TrayDateIconImage(), Config.getInstance().TOOLTIP + ", week is " + Time.getWeek());
            trayIcon.setImageAutoSize(true);
            trayIcon.setPopupMenu(getMenu(mediator.getMainWallpaper()));
            trayIcon.addMouseListener(new TrayMouseListener(mediator));

            try {
                SystemTray.getSystemTray().add(trayIcon);
            } catch (AWTException e) {
                System.err.println("TrayIcon could not be added.");
            }
        } else {
            Log.log("ERROR: System Tray is not supported");
        }
    }

    private PopupMenu getMenu(final MainWallpaper mainWallpaper) {
        PopupMenu popup = new PopupMenu();

        MenuItem item = new MenuItem("Edit Week");
        item.addActionListener(e -> {
            try {
                String currentWeek = Config.getInstance().getFilename(Config.getInstance().DIR_STAMPS, Time.getWeek());
                Runtime.getRuntime().exec("C:\\Program Files\\Sublime Text\\sublime_text.exe " + currentWeek);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
        popup.add(item);

        popup.addSeparator();

        MenuItem itemStampPair = new MenuItem("Stamp Pair");
        itemStampPair.addActionListener(e -> {
            try {
                Dao dao = new Dao();
                dao.stamp(false);
                dao.stamp(true);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
        popup.add(itemStampPair);

        MenuItem itemStampStop = new MenuItem("Stamp Stop");
        itemStampStop.addActionListener(e -> {
            try {
                new Dao().stamp(false);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
        popup.add(itemStampStop);

        MenuItem itemStampStart = new MenuItem("Stamp Start");
        itemStampStart.addActionListener(e -> {
            try {
                new Dao().stamp(true);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
        popup.add(itemStampStart);

        popup.addSeparator();

        item = new MenuItem("Wallpaper");
        item.addActionListener(e -> mainWallpaper.run());
        popup.add(item);
        item = new MenuItem("Wallpaper (force)");
        item.addActionListener(e -> mainWallpaper.runForce());
        popup.add(item);

        popup.addSeparator();

        item = new MenuItem("Shutdown");
        item.addActionListener(e -> {
            try {
                new Dao().stamp(false);
                Runtime rt = Runtime.getRuntime();
                rt.exec("shutdown.exe -s -t 0");
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
        popup.add(item);

        popup.addSeparator();

        item = new MenuItem("Exit");
        item.addActionListener(e -> {
            new Dao().stamp(false);
            System.exit(0);
        });
        popup.add(item);

        return popup;
    }
}