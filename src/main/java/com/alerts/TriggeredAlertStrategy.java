package com.alerts;

import com.data_management.Patient;
import com.data_management.PatientRecord;
import java.util.ArrayList;
import java.util.List;

public class TriggeredAlertStrategy implements AlertStrategy {
        private AlertFactory factory = new TriggeredAlertFactory();

    @Override
    public List<Alert> checkAlert(Patient patient) {
        List<Alert> alerts = new ArrayList<>();
        List<PatientRecord> records = patient.getRecords(0, Long.MAX_VALUE);

        for (PatientRecord record : records) {
            // We mapped "triggered" to 1.0 in the DataReader
            if (record.getRecordType().equals("Alert") && record.getMeasurementValue() == 1.0) {
                alerts.add(new BasicAlert(
                    String.valueOf(patient.getPatientId()), 
                    "Manual Alert Triggered", 
                    record.getTimestamp()
                ));
            }
        }
        return alerts;
    }
}
