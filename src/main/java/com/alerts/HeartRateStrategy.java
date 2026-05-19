package com.alerts;

import com.data_management.Patient;
import com.data_management.PatientRecord;
import java.util.ArrayList;
import java.util.List;

public class HeartRateStrategy implements AlertStrategy {
    private AlertFactory factory = new HeartRateAlertFactory();
    private static final int WINDOW_SIZE = 50; 
    private static final double PEAK_MULTIPLIER = 1.5; 

    @Override
    public List<Alert> checkAlert(Patient patient) {
        List<Alert> alerts = new ArrayList<>();
        List<PatientRecord> HeartRateRecords = new ArrayList<>();

        for (PatientRecord record : patient.getRecords(0, Long.MAX_VALUE)) {
            if (record.getRecordType().equals("HeartRate")) {
                HeartRateRecords.add(record);
            }
        }

        if (HeartRateRecords.size() < WINDOW_SIZE) return alerts;

        double windowSum = 0;
        for (int i = 0; i < WINDOW_SIZE; i++) {
            windowSum += HeartRateRecords.get(i).getMeasurementValue();
        }

        for (int i = WINDOW_SIZE; i < HeartRateRecords.size(); i++) {
            double currentAverage = windowSum / WINDOW_SIZE;
            double currentVal = HeartRateRecords.get(i).getMeasurementValue();

            if (Math.abs(currentVal) > Math.abs(currentAverage * PEAK_MULTIPLIER)) {
                alerts.add(factory.createAlert(
                    String.valueOf(patient.getPatientId()), 
                    "Abnormal HeartRate Peak", 
                    HeartRateRecords.get(i).getTimestamp()
                ));
            }

            windowSum -= HeartRateRecords.get(i - WINDOW_SIZE).getMeasurementValue();
            windowSum += currentVal;
        }

        return alerts;
    }
}