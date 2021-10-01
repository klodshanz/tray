package tray;

import module.wallpaper.utilities.Config;
import tray.model.Dao;

import javax.swing.*;
import javax.swing.border.SoftBevelBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class TrayDatePauseWindow extends JFrame {

    JLabel time;
    private long timeStart = 0;
    private boolean running = false;
    private SimpleDateFormat sdf = new SimpleDateFormat("H:mm:ss");
    private Font font, fontHuge;

    public TrayDatePauseWindow() throws HeadlessException {

        timeStart = System.currentTimeMillis();

        loadFonts();

        JPanel panel = new JPanel(null);
        panel.setBackground(new Color(185,128,185));
        time = new JLabel("00:00:00", SwingConstants.CENTER);
        time.setFont(fontHuge);
        panel.add(time);

        JButton stop = new JButton("Start");
        stop.setBackground(new Color(250, 255, 111));
        stop.setFont(font);
        stop.setMargin(new Insets(20,20,20,20));
        stop.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new Dao().stamp(true);
                running=false;
                setVisible(false);
            }
        });
        panel.add(stop);

        JButton pause = new JButton("Snooze");
        pause.setBackground(new Color(250, 214, 255));
        pause.setFont(font);
        pause.setMargin(new Insets(20,20,20,20));
        pause.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setAlwaysOnTop(false);
                setExtendedState(JFrame.ICONIFIED);
                try { Thread.sleep(600000);} catch (Exception ex) {}
                setExtendedState(JFrame.MAXIMIZED_BOTH);
                setAlwaysOnTop(true);
                requestFocus();
            }
        });
        panel.add(pause);

        Dimension size = Toolkit.getDefaultToolkit().getScreenSize();
        int w;
        w=1200;
        time.setBounds(((int)size.getWidth()-w)/2,300,w,400);
        w=350;
        stop.setBounds(((int)size.getWidth()-w)/2 -7*w/10,760,w,80);
        stop.setFocusable(false);
        stop.setBorder(BorderFactory.createEmptyBorder());

        pause.setBounds(((int)size.getWidth()-w)/2 +7*w/10,760,w,80);
        pause.setFocusable(false);
        pause.setBorder(BorderFactory.createEmptyBorder());

        getContentPane().add(panel);

        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setUndecorated(true);
        setAlwaysOnTop(true);
    }

    public void run() {
        new Dao().stamp(false);
        setVisible(true);
        running = true;
        Date date = new Date();
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                while (running) {
                    long diff = System.currentTimeMillis()-timeStart;
                    final long hr = TimeUnit.MILLISECONDS.toHours(diff);
                    final long min = TimeUnit.MILLISECONDS.toMinutes(diff - TimeUnit.HOURS.toMillis(hr));
                    final long sec = TimeUnit.MILLISECONDS.toSeconds(diff - TimeUnit.HOURS.toMillis(hr) - TimeUnit.MINUTES.toMillis(min));
                    time.setText(String.format("%02d:%02d:%02d", hr, min, sec));
                    try {
                        Thread.sleep(500);
                    } catch (Exception ex) {
                        //ignore
                    }
                }
                dispose();
            }
        });
        t.start();
    }

    private void loadFonts() {
        try {
            InputStream is = getClass().getClassLoader().getResourceAsStream("fonts/Aller_Bd.ttf");
            Font base = Font.createFont(Font.TRUETYPE_FONT, is);
            is.close();
            font = base.deriveFont(42F);
            fontHuge = base.deriveFont(240F);
        } catch (Exception ex) {
            font = new Font("Trebuchet MS", Font.BOLD, 45);
            fontHuge = new Font("Trebuchet MS", Font.BOLD, 185);
        }
    }

}
