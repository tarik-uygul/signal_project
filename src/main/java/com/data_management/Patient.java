package com.data_management;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a patient and manages their medical records.
 * This class stores patient-specific data, allowing for the addition and
 * retrieval
 * of medical records based on specified criteria.
 */
// Inside Patient.java
import java.util.Collections; // ADD THIS

public class Patient {
    private int patientId;
    private List<PatientRecord> patientRecords;

    public Patient(int patientId) {
        this.patientId = patientId;
        // CHANGE to a thread-safe synchronized list
        this.patientRecords = Collections.synchronizedList(new ArrayList<>()); 
    }
    
    public int getPatientId() {
        return patientId;
    }

    public void addRecord(double measurementValue, String recordType, long timestamp) {
        PatientRecord record = new PatientRecord(this.patientId, measurementValue, recordType, timestamp);
        this.patientRecords.add(record);
    }

    public List<PatientRecord> getRecords(long startTime, long endTime) {
        List<PatientRecord> filteredRecords = new ArrayList<>();
        // When iterating over a synchronizedList, you MUST put it in a synchronized block
        synchronized (this.patientRecords) { 
            for (PatientRecord record : this.patientRecords) {
                if (record.getTimestamp() >= startTime && record.getTimestamp() <= endTime) {
                    filteredRecords.add(record);
                }
            }
        }
        return filteredRecords;
    }
}
