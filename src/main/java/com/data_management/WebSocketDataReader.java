package com.data_management;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Reads real-time data from a WebSocket server and stores it in DataStorage.
 */
public class WebSocketDataReader extends WebSocketClient implements DataReader {
    private DataStorage dataStorage;

    public WebSocketDataReader(String serverUri) throws URISyntaxException {
        super(new URI(serverUri));
    }

    @Override
    public void readData(DataStorage dataStorage) throws IOException {
        this.dataStorage = dataStorage;
        // connect() runs asynchronously on a background thread
        this.connect(); 
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        System.out.println("✅ Connected to WebSocket server at: " + getURI());
    }

    @Override
    public void onMessage(String message) {
        try {
            // The simulator outputs strings like: "patientId,timestamp,label,data"
            String[] parts = message.split(",");
            if (parts.length == 4) {
                int patientId = Integer.parseInt(parts[0]);
                long timestamp = Long.parseLong(parts[1]);
                String label = parts[2];
                String dataStr = parts[3];

                // Handle percentage signs and textual alerts
                double measurementValue;
                if (dataStr.endsWith("%")) {
                    measurementValue = Double.parseDouble(dataStr.substring(0, dataStr.length() - 1));
                } else if (dataStr.equals("triggered")) {
                    measurementValue = 1.0;
                } else if (dataStr.equals("resolved")) {
                    measurementValue = 0.0;
                } else {
                    measurementValue = Double.parseDouble(dataStr);
                }

                if (dataStorage != null) {
                    dataStorage.addPatientData(patientId, measurementValue, label, timestamp);
                }
            } else {
                System.err.println("⚠️ Warning: Received malformed message format: " + message);
            }
        } catch (NumberFormatException e) {
            System.err.println("❌ Data format error in message: " + message + " | Reason: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("❌ Unexpected error processing message: " + message + " | Reason: " + e.getMessage());
        }
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        System.out.println("🔴 WebSocket connection closed. Reason: " + reason);
    }

    @Override
    public void onError(Exception ex) {
        System.err.println("❌ WebSocket error occurred: " + ex.getMessage());
    }
}