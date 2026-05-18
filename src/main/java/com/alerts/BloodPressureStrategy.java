package com.alerts;

import com.data_management.Patient;
import com.data_management.PatientRecord;
import java.util.ArrayList;
import java.util.List;

public class BloodPressureStrategy implements AlertStrategy {
    // 1. Instantiate the factory
    private AlertFactory factory = new BloodPressureAlertFactory();

    @Override
    public List<Alert> checkAlert(Patient patient) {
        List<Alert> alerts = new ArrayList<>();
        List<PatientRecord> records = patient.getRecords(0, Long.MAX_VALUE);
        
        List<PatientRecord> systolicRecords = new ArrayList<>();
        List<PatientRecord> diastolicRecords = new ArrayList<>();

        for (PatientRecord record : records) {
            if (record.getRecordType().equals("SystolicPressure")) systolicRecords.add(record);
            if (record.getRecordType().equals("DiastolicPressure")) diastolicRecords.add(record);
        }

        checkThresholds(systolicRecords, 180, 90, "SystolicPressure Critical Threshold", alerts);
        checkThresholds(diastolicRecords, 120, 60, "DiastolicPressure Critical Threshold", alerts);
        checkTrends(systolicRecords, "Systolic", alerts);
        checkTrends(diastolicRecords, "Diastolic", alerts);

        return alerts;
    }

    private void checkThresholds(List<PatientRecord> records, double max, double min, String condition, List<Alert> alerts) {
        for (PatientRecord record : records) {
            if (record.getMeasurementValue() > max || record.getMeasurementValue() < min) {
                // 2. Use the factory to create the alert!
                alerts.add(factory.createAlert(String.valueOf(record.getPatientId()), condition, record.getTimestamp()));
            }
        }
    }

    private void checkTrends(List<PatientRecord> records, String type, List<Alert> alerts) {
        if (records.size() < 3) return;

        for (int i = 0; i <= records.size() - 3; i++) {
            double v1 = records.get(i).getMeasurementValue();
            double v2 = records.get(i+1).getMeasurementValue();
            double v3 = records.get(i+2).getMeasurementValue();

            if (v2 - v1 > 10 && v3 - v2 > 10) {
                // 3. Use the factory here too
                alerts.add(factory.createAlert(String.valueOf(records.get(i+2).getPatientId()), type + " Trend Increasing", records.get(i+2).getTimestamp()));
            } else if (v1 - v2 > 10 && v2 - v3 > 10) {
                alerts.add(factory.createAlert(String.valueOf(records.get(i+2).getPatientId()), type + " Trend Decreasing", records.get(i+2).getTimestamp()));
            }
        }
    }
}