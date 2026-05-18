package com;

import com.cardio_generator.HealthDataSimulator;
import com.data_management.DataStorage;
import com.data_management.WebSocketDataReader;
import com.alerts.AlertGenerator;
import com.data_management.Patient;

public class Main {
    public static void main(String[] args) {
        try {
            // 1. Start the simulator (Server)
            // We override the args to force it to run a WebSocket server on port 8080
            String[] simulatorArgs = {"--patient-count", "5", "--output", "websocket:8080"};
            HealthDataSimulator simulator = HealthDataSimulator.getInstance();
            simulator.start(simulatorArgs);
            System.out.println("Started Simulator on WebSocket port 8080.");

            // 2. Give the server a second to boot up before the client connects
            Thread.sleep(1000);

            // 3. Initialize the Thread-Safe Data Storage
            DataStorage storage = DataStorage.getInstance();

            // 4. Initialize the WebSocket Client (Reader)
            WebSocketDataReader reader = new WebSocketDataReader("ws://localhost:8080");
            reader.readData(storage); // This connects asynchronously on a background thread

            // 5. Setup the Alert Generator
            AlertGenerator alertGenerator = new AlertGenerator(storage);

            // 6. The Real-Time Evaluation Loop
            System.out.println("Starting real-time alert evaluation...");
            while (true) {
                // Safely get all patients and evaluate them
                for (Patient patient : storage.getAllPatients()) {
                    alertGenerator.evaluateData(patient);
                }
                
                // Pause for 2 seconds before checking again to avoid maxing out the CPU
                Thread.sleep(2000); 
            }

        } catch (Exception e) {
            System.err.println("Critical System Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}