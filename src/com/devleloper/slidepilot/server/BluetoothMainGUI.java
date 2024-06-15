package com.devleloper.slidepilot.server;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class BluetoothMainGUI {
    public static void main(String[] args) {
        JFrame f = new JFrame();
        WaitThread waitThread = new WaitThread();

        JLabel currentStatus = new JLabel("Current Status: Stopped");
        currentStatus.setBounds(80, 40, 200, 40);
        f.add(currentStatus);

        JButton startServer = new JButton("Start");
        startServer.setBounds(80, 100, 100, 40);
        f.add(startServer);

        JButton stopServer = new JButton("Stop");
        stopServer.setBounds(200, 100, 100, 40);
        f.add(stopServer);
        stopServer.setEnabled(false);

        startServer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startServer.setEnabled(false);
                stopServer.setEnabled(true);
                currentStatus.setText("Current Status: Running");
                Thread thread = new Thread(waitThread);
                thread.start();
            }
        });

        stopServer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startServer.setEnabled(true);
                stopServer.setEnabled(false);
                currentStatus.setText("Current Status: Stopped");
                waitThread.setThreadStopper(true);
            }
        });

        f.setTitle("Slide Pilot Server");
        URL url = BluetoothMainGUI.class.getResource("/resources/icon.jpg");
        ImageIcon img = new ImageIcon(url);
        f.setIconImage(img.getImage());

        f.setSize(400, 200);
        f.setLayout(null);
        f.setVisible(true);
        f.setLocationRelativeTo(null);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}
