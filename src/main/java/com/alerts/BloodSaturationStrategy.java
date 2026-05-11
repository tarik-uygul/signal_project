package com.alerts;

import com.data_management.Patient;
import com.data_management.PatientRecord;
import java.util.ArrayList;
import java.util.List;

public class BloodSaturationStrategy implements AlertStrategy {

    private static final double SATURATION_THRESHOLD = 92.0;

    @Override
    public List<Alert> checkAlert(Patient patient) {
        List<Alert> alerts = new ArrayList<>();
        List<PatientRecord> records = patient.getRecords(0, Long.MAX_VALUE);
        List<PatientRecord> saturationRecords = new ArrayList<>();

        // 1. Filter for the correct label: "Saturation"
        for (PatientRecord record : records) {
            if (record.getRecordType().equals("Saturation")) {
                saturationRecords.add(record);
            }
        }

        // 2. Check for Low Saturation (< 92%)
        for (PatientRecord record : saturationRecords) {
            if (record.getMeasurementValue() < SATURATION_THRESHOLD) {
                alerts.add(new Alert(
                    String.valueOf(record.getPatientId()), 
                    "Low Saturation", 
                    record.getTimestamp()
                ));
            }
        }

        // 3. Check for Rapid Drop (5% or more within 10 minutes)
        long tenMinutesInMillis = 10 * 60 * 1000; // 10 minutes converted to milliseconds

        for (int i = 0; i < saturationRecords.size(); i++) {
            PatientRecord startRecord = saturationRecords.get(i);
            
            for (int j = i + 1; j < saturationRecords.size(); j++) {
                PatientRecord currentRecord = saturationRecords.get(j);
                
                // If the time difference is greater than 10 minutes, stop comparing with startRecord
                if (currentRecord.getTimestamp() - startRecord.getTimestamp() > tenMinutesInMillis) {
                    break; 
                }

                // If within 10 minutes, did it drop by 5% or more?
                if (startRecord.getMeasurementValue() - currentRecord.getMeasurementValue() >= 5.0) {
                    alerts.add(new Alert(
                        String.valueOf(currentRecord.getPatientId()), 
                        "Rapid Saturation Drop", 
                        currentRecord.getTimestamp()
                    ));
                    break; // Break to avoid triggering multiple alerts for the same drop window
                }
            }
        }

        return alerts;
    }
}