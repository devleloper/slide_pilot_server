package com.devleloper.slidepilot.server;

import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Robot;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.io.InputStream;

import javax.microedition.io.StreamConnection;

public class ProcessConnectionThread implements Runnable {

    private StreamConnection mConnection;

    private static final int EXIT_CMD = -1;
    private static final int KEY_RIGHT = 1;
    private static final int KEY_LEFT = 2;

    Robot robot;

    private boolean threadStop = false;
    private InputStream inputStream;

    void setThreadStopper(boolean threadStop) {
        this.threadStop = threadStop;
        if (threadStop) {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
                if (mConnection != null) {
                    mConnection.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public ProcessConnectionThread(StreamConnection connection) {
        mConnection = connection;
    }

    @Override
    public void run() {
        try {
            inputStream = mConnection.openInputStream();
            System.out.println("waiting for input");

            StringBuffer stringBuf = new StringBuffer();
            while (true && !threadStop) {
                int command = inputStream.read();
                if (command == EXIT_CMD) {
                    System.out.println("finish process");
                    break;
                }

                char a = (char) command;
                if (a == '\n') {
                    processCommand(stringBuf.toString());
                    stringBuf = new StringBuffer();
                } else {
                    stringBuf.append(a);
                }
            }
        } catch (Exception e) {
            if (!threadStop) {
                e.printStackTrace();
            }
        }
    }

    private void processCommand(String command) {
        // Process the command received from the client
        try {
            System.out.println(command);
            if (robot == null) {
                robot = new Robot();
            }
            if (command.startsWith("*#*LC*@*")) {
                robot.mousePress(InputEvent.BUTTON1_MASK);
                robot.mouseRelease(InputEvent.BUTTON1_MASK);
                return;
            }
            if (command.startsWith("*#*ZOOM")) {
                robot.keyPress(KeyEvent.VK_CONTROL);
                double x2 = Double.parseDouble(command.substring(7, command.indexOf("*@*")));
                robot.mouseWheel((int) (x2 * 5));
                robot.keyRelease(KeyEvent.VK_CONTROL);
                return;
            }
            if (command.startsWith("*#*SCROLL")) {
                double x2 = Double.parseDouble(command.substring(9, command.indexOf("*@*")));
                robot.mouseWheel((int) (x2 * 5));
                return;
            }

            if (command.startsWith("*#*TYPE")) {
                String keys = command.substring(7, command.indexOf("*@*"));
                type(robot, keys);
                return;
            }
            if(command.startsWith("*#*esc*@*")) {
                robot.keyPress(KeyEvent.VK_ESCAPE);
                robot.keyRelease(KeyEvent.VK_ESCAPE);
                return;
            }
            if (command.startsWith("*#*Offset")) {
                Point p = MouseInfo.getPointerInfo().getLocation();
                int xi = p.x;
                int yi = p.y;
                double x2 = Double.parseDouble(command.substring(10, command.indexOf(",")));
                double y2 = Double.parseDouble(command.substring(command.indexOf(",") + 1, command.indexOf(")")));
                int xi1 = (int) (xi + (x2 * 5));
                int yi1 = (int) (yi + (y2 * 5));
                robot.mouseMove(xi1, yi1);
                return;
            }
            
            if (command.startsWith("*#*DRAGOffset")) {
                Point p = MouseInfo.getPointerInfo().getLocation();
                int xi = p.x;
                int yi = p.y;
                double x2 = Double.parseDouble(command.substring(14, command.indexOf(",")));
                double y2 = Double.parseDouble(command.substring(command.indexOf(",") + 1, command.indexOf(")")));
                int xi1 = (int) (xi + (x2 * 5));
                int yi1 = (int) (yi + (y2 * 5));
                robot.mouseMove(xi1, yi1);
                if(!dragStarted) {
                    robot.mousePress(InputEvent.BUTTON1_MASK);
                    dragStarted = true;
                }
                return;
            }
            if (command.startsWith("*#*DRAGENDED*@*")) {
                robot.mouseRelease(InputEvent.BUTTON1_MASK);
                return;
            }
            
            int xi1, yi1, xi, yi;
            if (command.startsWith("*#*JOYSTICK")) {
                double angle = Double.parseDouble(command.substring(11, command.indexOf(" ")));
                double distance = Double
                        .parseDouble(command.substring(command.indexOf(" ") + 1, command.indexOf("*@*")));
                double inRadians = Math.toRadians(angle);
                joystick.setRadians(inRadians);
            }
            if (command.startsWith("*#*RIGHT*@*")) {
                robot.keyPress(KeyEvent.VK_RIGHT);
                robot.keyRelease(KeyEvent.VK_RIGHT);
            }
            if (command.startsWith("*#*LEFT*@*")) {
                robot.keyPress(KeyEvent.VK_LEFT);
                robot.keyRelease(KeyEvent.VK_LEFT);
            }
            if (command.startsWith("*#*F5*@*")) {
                robot.keyPress(KeyEvent.VK_F5);
                robot.keyRelease(KeyEvent.VK_F5);
            }
            if (command.startsWith("*#*SHIFT+F5*@*")) {
                robot.keyPress(KeyEvent.VK_SHIFT);
                robot.keyPress(KeyEvent.VK_F5);
                robot.keyRelease(KeyEvent.VK_SHIFT);
                robot.keyRelease(KeyEvent.VK_F5);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void type(Robot robot, String keys) {
        for (char c : keys.toCharArray()) {
            int keyCode = KeyEvent.getExtendedKeyCodeForChar(c);
            if (KeyEvent.CHAR_UNDEFINED == keyCode) {
                throw new RuntimeException("Key code not found for character '" + c + "'");
            }
            robot.keyPress(keyCode);
            robot.delay(10);
            robot.keyRelease(keyCode);
            robot.delay(10);
        }
    }

    class Joystick extends Thread {
        double inRadians;

        public Joystick(double radians) {
            this.inRadians = radians;
        }

        void setRadians(double radians) {
            this.inRadians = radians;
        }

        @Override
        public void run() {
            while (true && !threadStop) {
                if (inRadians != 0) {
                    int xi1, yi1, xi, yi;
                    Point p = MouseInfo.getPointerInfo().getLocation();
                    xi = p.x;
                    yi = p.y;
                    xi1 = xi + (int) (2 * Math.sin(inRadians));
                    yi1 = yi - (int) (2 * Math.cos(inRadians));
                    robot.mouseMove(xi1, yi1);
                    try {
                        sleep(1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    Joystick joystick = new Joystick(0);
    boolean dragStarted = false;
}
