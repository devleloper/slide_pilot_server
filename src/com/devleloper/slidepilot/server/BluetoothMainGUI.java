package com.devleloper.slidepilot.server;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.net.URL;
import javax.swing.*;

public class BluetoothMainGUI {
    private static JTextArea logArea;
    private static JLabel currentStatus;

    public static void main(String[] args) {
        JFrame f = new JFrame();// creating instance of JFrame
        WaitThread waitThread = new WaitThread();

        currentStatus = new JLabel("Current Status: Stopped");
        currentStatus.setBounds(80, 40, 200, 40);// x axis, y axis, width, height
        f.add(currentStatus);

        logArea = new JTextArea();
        logArea.setEditable(false);
        JScrollPane logScrollPane = new JScrollPane(logArea);
        logScrollPane.setBounds(20, 150, 350, 200);
        f.add(logScrollPane);

        JButton startServer = new JButton("Start");// creating instance of JButton
        startServer.setBounds(80, 100, 100, 40);// x axis, y axis, width, height
        f.add(startServer);// adding button in JFrame

        JButton stopServer = new JButton("Stop");// creating instance of JButton
        stopServer.setBounds(200, 100, 100, 40);// x axis, y axis, width, height
        f.add(stopServer);// adding button in JFrame
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

        f.setTitle("Slide Pilot Server");

        URL url = BluetoothMainGUI.class.getResource("/resources/icon.jpg");
        ImageIcon img = new ImageIcon(url);
        f.setIconImage(img.getImage());

        f.setSize(400, 400);// 400 width and 400 height
        f.setLayout(null);// using no layout managers
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

    public static void highlightPointer() {
        try {
            Robot screenRobot;
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            GraphicsDevice[] gs = ge.getScreenDevices();
            for (GraphicsDevice gd : gs) {
                GraphicsConfiguration gc = gd.getDefaultConfiguration();
                BufferedImage bi = gc.createCompatibleImage(gc.getBounds().width, gc.getBounds().height, Transparency.TRANSLUCENT);
                Graphics2D g = bi.createGraphics();
                g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f)); // Прозрачность фона
                g.setColor(Color.BLACK);
                g.fillRect(0, 0, bi.getWidth(), bi.getHeight());

               
                Point cursor = MouseInfo.getPointerInfo().getLocation();
                int radius = 50;
                int diameter = radius * 2;
                int centerX = cursor.x;
                int centerY = cursor.y;

                g.setComposite(AlphaComposite.getInstance(AlphaComposite.CLEAR, 1.0f));
                g.fillOval(centerX - radius, centerY - radius, diameter, diameter);

                g.dispose();
                try {
                    screenRobot = new Robot(gd);
                    screenRobot.createScreenCapture(gc.getBounds()).getGraphics().drawImage(bi, 0, 0, null);
                } catch (AWTException e) {
                    e.printStackTrace();
                }
            }

            
            try {
                Thread.sleep(3000); 
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            
            for (GraphicsDevice gd : gs) {
                GraphicsConfiguration gc = gd.getDefaultConfiguration();
                try {
                    screenRobot = new Robot(gd);
                    BufferedImage screenImage = screenRobot.createScreenCapture(gc.getBounds());
                    screenRobot.createScreenCapture(gc.getBounds()).getGraphics().drawImage(screenImage, 0, 0, null);
                } catch (AWTException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
