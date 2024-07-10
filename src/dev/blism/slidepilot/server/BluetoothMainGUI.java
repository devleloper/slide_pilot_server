package dev.blism.slidepilot.server;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import javax.swing.*;

public class BluetoothMainGUI {
    private static JTextArea logArea;
    private static JLabel currentStatus;

    public static void main(String[] args) {
        JFrame f = new JFrame();// creating instance of JFrame
        WaitThread waitThread = new WaitThread();

        currentStatus = new JLabel("Current Status: Stopped", SwingConstants.CENTER);
        
        logArea = new JTextArea();
        logArea.setEditable(false);
        JScrollPane logScrollPane = new JScrollPane(logArea);

        JButton startServer = new JButton("Start");
        JButton stopServer = new JButton("Stop");
        stopServer.setEnabled(false);

        startServer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startServer.setEnabled(false);
                stopServer.setEnabled(true);
                currentStatus.setText("Current Status: Running");
                updateLog("Server started, waiting for connection...");
                Thread waitThreadInstance = new Thread(waitThread);
                waitThreadInstance.start();
            }
        });

        stopServer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startServer.setEnabled(true);
                stopServer.setEnabled(false);
                currentStatus.setText("Current Status: Stopped");
                updateLog("Server stopped.");
                waitThread.setThreadStopper(true);
            }
        });

        // Layout setup
        f.setLayout(new BorderLayout());

        JPanel topPanel = new JPanel();
        topPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridx = 0;
        gbc.gridy = 0;
        topPanel.add(currentStatus, gbc);

        gbc.gridy = 1;
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        topPanel.add(startServer, gbc);

        gbc.gridx = 1;
        topPanel.add(stopServer, gbc);

        f.add(topPanel, BorderLayout.NORTH);
        f.add(logScrollPane, BorderLayout.CENTER);

        f.setTitle("Slide Pilot Server");

        URL url = BluetoothMainGUI.class.getResource("/resources/icon.png");
        ImageIcon img = new ImageIcon(url);
        f.setIconImage(img.getImage());

        f.setSize(400, 400);// 400 width and 400 height
        f.setMinimumSize(new Dimension(400, 400));
        f.setVisible(true);// making the frame visible
        f.setLocationRelativeTo(null);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public static void updateStatus(String status) {
        currentStatus.setText("Current Status: " + status);
        updateLog("Status changed to: " + status);
    }

    public static void updateLog(String message) {
        logArea.append(message + "\n");
    }
}
