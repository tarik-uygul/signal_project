package com.data_management;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap; // NEW IMPORT

public class DataStorage {
    private static DataStorage instance; 
    // CHANGE to ConcurrentHashMap for thread safety
    private Map<Integer, Patient> patientMap; 

    private DataStorage() {
        this.patientMap = new ConcurrentHashMap<>(); // CHANGED HERE
    }

    public static synchronized DataStorage getInstance() {
        if (instance == null) {
            instance = new DataStorage();
        }
        return instance;
    }

    public void addPatientData(int patientId, double measurementValue, String recordType, long timestamp) {
        // computeIfAbsent is thread-safe and prevents race conditions when creating new patients
        Patient patient = patientMap.computeIfAbsent(patientId, k -> new Patient(patientId));
        patient.addRecord(measurementValue, recordType, timestamp);
    }

    public List<PatientRecord> getRecords(int patientId, long startTime, long endTime) {
        Patient patient = patientMap.get(patientId);
        if (patient != null) {
            return patient.getRecords(startTime, endTime);
        }
        return new ArrayList<>(); 
    }

    public List<Patient> getAllPatients() {
        return new ArrayList<>(patientMap.values());
    }
}
