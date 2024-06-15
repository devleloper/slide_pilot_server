package com.devleloper.slidepilot.server;

import java.io.IOException;
import javax.bluetooth.DiscoveryAgent;
import javax.bluetooth.LocalDevice;
import javax.bluetooth.UUID;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
import javax.microedition.io.StreamConnectionNotifier;

public class WaitThread implements Runnable {

    private boolean threadStop = false;
    private StreamConnection connection = null;
    private StreamConnectionNotifier notifier;
    private ProcessConnectionThread processConnectionThread;

    void setThreadStopper(boolean threadStop) {
        this.threadStop = threadStop;
        if (processConnectionThread != null) {
            processConnectionThread.setThreadStopper(threadStop);
        }
        if (threadStop) {
            try {
                if (notifier != null) {
                    notifier.close();
                }
                if (connection != null) {
                    connection.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void run() {
        waitForConnection();
    }

    private void waitForConnection() {
        try {
            LocalDevice local = LocalDevice.getLocalDevice();
            if (local.getDiscoverable() != DiscoveryAgent.GIAC) {
                local.setDiscoverable(DiscoveryAgent.GIAC);
            }

            UUID uuid = new UUID(80087355); // "04c6093b-0000-1000-8000-00805f9b34fb"
            String url = "btspp://localhost:" + uuid.toString() + ";name=RemoteBluetooth";
            notifier = (StreamConnectionNotifier) Connector.open(url);

            System.out.println("waiting for connection...");
            connection = notifier.acceptAndOpen();
            processConnectionThread = new ProcessConnectionThread(connection);
            Thread processThread = new Thread(processConnectionThread);
            processThread.start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
