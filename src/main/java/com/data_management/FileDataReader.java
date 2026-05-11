package com.data_management;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * Reads patient data from log files and stores it in the data storage system.
 */
public class FileDataReader implements DataReader {
    private String directoryPath;

    /**
     * Constructs a FileDataReader to read from the specified directory.
     * * @param directoryPath the path to the directory containing output data files
     */
    public FileDataReader(String directoryPath) {
        this.directoryPath = directoryPath;
    }

    @Override
    public void readData(DataStorage dataStorage) throws IOException {
        File dir = new File(directoryPath);
        if (!dir.exists() || !dir.isDirectory()) {
            throw new IOException("Directory does not exist or is not a directory: " + directoryPath);
        }

        // The simulator outputs separate .txt files for each label
        File[] files = dir.listFiles((d, name) -> name.endsWith(".txt"));
        if (files == null || files.length == 0) {
            System.out.println("No data files found in " + directoryPath);
            return;
        }

        for (File file : files) {
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = br.readLine()) != null) {
                    parseLineAndStore(line, dataStorage);
                }
            }
        }
    }

    /**
     * Parses a single line of log text and adds the data to storage.
     */
    private void parseLineAndStore(String line, DataStorage dataStorage) {
        try {
            String[] parts = line.split(", ");
            if (parts.length == 4) {
                int patientId = Integer.parseInt(parts[0].split(": ")[1]);
                long timestamp = Long.parseLong(parts[1].split(": ")[1]);
                String label = parts[2].split(": ")[1];
                String dataStr = parts[3].split(": ")[1];

                // Handle percentage signs (like in Saturation data e.g., "95%")
                double measurementValue;
                if (dataStr.endsWith("%")) {
                    measurementValue = Double.parseDouble(dataStr.substring(0, dataStr.length() - 1));
                } else if (dataStr.equals("triggered")) {
                    measurementValue = 1.0; // 1.0 = Triggered
                } else if (dataStr.equals("resolved")) {
                    measurementValue = 0.0; // 0.0 = Resolved
                } else {
                    measurementValue = Double.parseDouble(dataStr);
                }

                // Add to storage
                dataStorage.addPatientData(patientId, measurementValue, label, timestamp);
            }
        } catch (Exception e) {
            System.err.println("Error parsing line: " + line + " - " + e.getMessage());
        }
    }
}