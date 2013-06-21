package monitor;

import common.Utils;
import usage.MonitoredData;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MonitoringClient {

    public MonitoringClient(long _gatherInterval, String _servAddress,
                            int _servPort, String _sensorAddress) {
        new Gather(this, _gatherInterval, _sensorAddress);
        new Sender(this, _servAddress, _servPort);
    }

    private MonitoredData mData;
    private boolean valueSet = false;

    synchronized MonitoredData get() {
        if (!valueSet) {
            try {
                wait();
            } catch (InterruptedException e) {
                System.out.println("InterruptedException caught");
            }
        }
        //System.out.println("Available data being used.");
        valueSet = false;
        notify();
        return this.mData;
    }

    synchronized void put(MonitoredData data) {
        if (valueSet) {
            try {
                wait();
            } catch (InterruptedException e) {
                System.out.println("InterruptedException caught");
            }
        }
        this.mData = data;
        valueSet = true;
        //System.out.println("Gathered data available.");
        notify();
    }
}

class Gather implements Runnable {
    private final MonitoringClient client;

    private final NodeInfoGather gatherService;

    private long gatherInterval = 0;

    Gather(MonitoringClient client, long _gatherInterval, String _sensorAddress) {
        this.client = client;
        this.gatherInterval = _gatherInterval;
        this.gatherService = new NodeInfoGather(Utils.getNodeUUID(),
                _sensorAddress);
        new Thread(this, "Gather").start();
    }

    public void run() {
        MonitoredData tempData = null;
        while (true) {
            try {
                Thread.sleep(this.gatherInterval);

                tempData = gatherService.getSystemUsage();

            } catch (IOException ex) {
                Logger.getLogger(MonitoringClient.class.getName()).log(
                        Level.SEVERE, null, ex);
            } catch (InterruptedException ex) {
                Logger.getLogger(MonitoringClient.class.getName()).log(
                        Level.SEVERE, null, ex);
            }
            client.put(tempData);
        }
    }
}

class Sender implements Runnable {
    // Create socket that is connected to server on specified port
    private Socket socket;
    // Count the number of packages sent
    private int packageCount = 0;

    private ObjectOutputStream oos;

    private MonitoringClient client;

    Sender(MonitoringClient _client, String _server, int _servPort) {
        // check for parameters
        if (_client == null)
            throw new IllegalArgumentException(
                    "Parameter: <Client object> null.");
        if (!Utils.stringNotEmpty(_server))
            throw new IllegalArgumentException(
                    "Parameter: <Server Address> empty.");
        if (_servPort <= 0)
            throw new IllegalArgumentException(
                    "Parameter: <Server Port> empty.");

        this.client = _client;

        try {
            socket = new Socket(_server, _servPort);
            oos = new ObjectOutputStream(socket.getOutputStream());
            System.out.println("Connected to server...");
        } catch (UnknownHostException e) {
            Logger.getLogger(MonitoringClient.class.getName()).log(
                    Level.SEVERE, null, e);
        } catch (IOException e) {
            Logger.getLogger(MonitoringClient.class.getName()).log(
                    Level.SEVERE, null, e);
        }

        new Thread(this, "Sender").start();
    }

    public void run() {
        MonitoredData tempData;

        while (true) {
            if ((tempData = client.get()) == null)
                throw new IllegalArgumentException("Parameter: <Monitored Data> empty.");
            System.out.println("Data gathered. Sending...");
            try {
                oos.reset();
                oos.writeUnshared(tempData);
                oos.flush();
                this.packageCount++;
                System.out.println("Client sent the monitored data package: " + String.valueOf(this.packageCount));

            } catch (SocketException e) {
                System.out.println("Master service not available, closing client service...");
                break;
            } catch (IOException e) {
                Logger.getLogger(MonitoringClient.class.getName()).log(Level.SEVERE, null, e);
            }
        }
    }

    @Override
    protected void finalize() throws Throwable {
        // do finalization here
        // Close the socket and its streams
        socket.close();
        oos.close();

        super.finalize();
    }
}