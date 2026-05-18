package com.alerts;

import com.data_management.Patient;
import com.data_management.PatientRecord;
import java.util.ArrayList;
import java.util.List;

public class HypotensiveHypoxemiaStrategy implements AlertStrategy {
    private AlertFactory factory = new HypotensiveHypoxemiaAlertFactory();

    @Override
    public List<Alert> checkAlert(Patient patient) {
        List<Alert> alerts = new ArrayList<>();
        List<PatientRecord> records = patient.getRecords(0, Long.MAX_VALUE);

        List<PatientRecord> systolicRecords = new ArrayList<>();
        List<PatientRecord> saturationRecords = new ArrayList<>();

        // Separate records by type
        for (PatientRecord record : records) {
            if (record.getRecordType().equals("SystolicPressure")) systolicRecords.add(record);
            if (record.getRecordType().equals("Saturation")) saturationRecords.add(record);
        }

        // Check for combined condition
        for (PatientRecord bpRecord : systolicRecords) {
            if (bpRecord.getMeasurementValue() < 90.0) {
                // If BP is low, look for a Saturation drop within 1 minute (60,000 ms) of the BP reading
                for (PatientRecord satRecord : saturationRecords) {
                    if (Math.abs(satRecord.getTimestamp() - bpRecord.getTimestamp()) <= 60000) {
                        if (satRecord.getMeasurementValue() < 92.0) {
                            alerts.add(new BasicAlert(
                                String.valueOf(patient.getPatientId()), 
                                "Hypotensive Hypoxemia", 
                                bpRecord.getTimestamp()
                            ));
                            break; // Stop searching once we find a match for this specific BP reading
                        }
                    }
                }
            }
        }
        return alerts;
    }
}