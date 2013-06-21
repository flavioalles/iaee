package monitor;

import storage.HistoricalDatabase;
import usage.MonitoredData;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MonitoringMaster implements Runnable {

    // Socket connect to client
    private final Socket clientSock;
    // Historical database to save the monitored data.
    private final HistoricalDatabase hdb;
    // Get the input and output I/O streams from socket
    private ObjectInputStream ois;

    // Object to analyze the data contained in the monitored objects
    // private NodeInfoAnalyzer data;

    public MonitoringMaster(Socket clntSock, HistoricalDatabase _hdb) {
        this.clientSock = clntSock;
        this.hdb = _hdb;
        // this.data = new NodeInfoAnalyzer(gtrInterval);
        try {
            ois = new ObjectInputStream(this.clientSock.getInputStream());
        } catch (IOException e) {
            Logger.getLogger(MonitoringMaster.class.getName()).log(
                    Level.SEVERE, "Exception in echo protocol", e);
        }
    }

    void handleEchoClient(Socket client) throws IOException {
        try {
            MonitoredData mdata;
            // Receive until client closes connection
            while ((mdata = (MonitoredData) ois.readObject()) != null) {
                //System.out.println("Got received data. Ready to save.");
                this.hdb.saveOrUpdate(mdata);
                System.out.println("Monitored Data arrived at home from node: " + String.valueOf(mdata.getNodeUUID()));
            }

        }
        // catch (IOException ex) {
        // Logger.getLogger(MonitoringMaster.class.getName()).log(Level.SEVERE,
        // null, ex);
        // }
        catch (ClassNotFoundException ex) {
            Logger.getLogger(MonitoringMaster.class.getName()).log(
                    Level.SEVERE, null, ex);
        } finally {
            try {
                client.close();
            } catch (IOException e) {
                Logger.getLogger(MonitoringMaster.class.getName()).log(
                        Level.SEVERE, null, e);
            }
        }
    }

    public void run() {
        try {
            handleEchoClient(clientSock);
        } catch (IOException e) {
            System.out.println("One less client...");
        }
    }

}
