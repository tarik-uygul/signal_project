package com.alerts;

import com.data_management.Patient;
import com.data_management.PatientRecord;
import java.util.ArrayList;
import java.util.List;

public class ECGStrategy implements AlertStrategy {
    private AlertFactory factory = new ECGAlertFactory();
    private static final int WINDOW_SIZE = 50; // The sliding window size (e.g., last 50 readings)
    private static final double PEAK_MULTIPLIER = 1.5; // Trigger if the peak is 50% above the average

    @Override
    public List<Alert> checkAlert(Patient patient) {
        List<Alert> alerts = new ArrayList<>();
        List<PatientRecord> ecgRecords = new ArrayList<>();

        for (PatientRecord record : patient.getRecords(0, Long.MAX_VALUE)) {
            if (record.getRecordType().equals("ECG")) {
                ecgRecords.add(record);
            }
        }

        // We need enough data to form a window before we can calculate an average
        if (ecgRecords.size() < WINDOW_SIZE) return alerts;

        // Calculate initial window sum
        double windowSum = 0;
        for (int i = 0; i < WINDOW_SIZE; i++) {
            windowSum += ecgRecords.get(i).getMeasurementValue();
        }

        // Slide the window across the remaining records
        for (int i = WINDOW_SIZE; i < ecgRecords.size(); i++) {
            double currentAverage = windowSum / WINDOW_SIZE;
            double currentVal = ecgRecords.get(i).getMeasurementValue();

            // Check if the current value is an abnormal peak
            // (Using absolute value in case the baseline fluctuates around 0)
            if (Math.abs(currentVal) > Math.abs(currentAverage * PEAK_MULTIPLIER)) {
                alerts.add(factory.createAlert(
                    String.valueOf(patient.getPatientId()), 
                    "Abnormal ECG Peak", 
                    ecgRecords.get(i).getTimestamp()
                ));
            }

            // Slide window: remove oldest value, add newest value
            windowSum -= ecgRecords.get(i - WINDOW_SIZE).getMeasurementValue();
            windowSum += currentVal;
        }

        return alerts;
    }
}