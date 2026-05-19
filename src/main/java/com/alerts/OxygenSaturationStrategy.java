package com.alerts;

import com.data_management.Patient;
import com.data_management.PatientRecord;
import java.util.ArrayList;
import java.util.List;

public class OxygenSaturationStrategy implements AlertStrategy {

    // FIX: Using the correct factory here!
    private AlertFactory factory = new BloodOxygenAlertFactory();
    private static final double SATURATION_THRESHOLD = 92.0;

    @Override
    public List<Alert> checkAlert(Patient patient) {
        List<Alert> alerts = new ArrayList<>();
        List<PatientRecord> records = patient.getRecords(0, Long.MAX_VALUE);
        List<PatientRecord> saturationRecords = new ArrayList<>();

        for (PatientRecord record : records) {
            if (record.getRecordType().equals("Saturation")) {
                saturationRecords.add(record);
            }
        }

        for (PatientRecord record : saturationRecords) {
            if (record.getMeasurementValue() < SATURATION_THRESHOLD) {
                alerts.add(factory.createAlert(
                    String.valueOf(record.getPatientId()), 
                    "Low Saturation", 
                    record.getTimestamp()
                ));
            }
        }

        long tenMinutesInMillis = 10 * 60 * 1000; 

        for (int i = 0; i < saturationRecords.size(); i++) {
            PatientRecord startRecord = saturationRecords.get(i);
            for (int j = i + 1; j < saturationRecords.size(); j++) {
                PatientRecord currentRecord = saturationRecords.get(j);
                if (currentRecord.getTimestamp() - startRecord.getTimestamp() > tenMinutesInMillis) {
                    break; 
                }
                if (startRecord.getMeasurementValue() - currentRecord.getMeasurementValue() >= 5.0) {
                    alerts.add(factory.createAlert(
                        String.valueOf(currentRecord.getPatientId()), 
                        "Rapid Saturation Drop", 
                        currentRecord.getTimestamp()
                    ));
                    break; 
                }
            }
        }
        return alerts;
    }
}