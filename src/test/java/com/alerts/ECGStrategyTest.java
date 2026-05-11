package com.alerts;

import com.data_management.Patient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class ECGStrategyTest {
    private ECGStrategy strategy;
    private Patient patient;

    @BeforeEach
    void setUp() {
        strategy = new ECGStrategy();
        patient = new Patient(1);
    }

    @Test
    void testInsufficientData() {
        // Add only 10 records (window size is 50)
        for (int i = 0; i < 10; i++) {
            patient.addRecord(1.0, "ECG", i * 1000L);
        }
        List<Alert> alerts = strategy.checkAlert(patient);
        assertTrue(alerts.isEmpty(), "No alert should trigger without a full sliding window.");
    }

    @Test
    void testAbnormalECGPeak() {
        // Add 50 normal records to establish an average of 1.0
        for (int i = 0; i < 50; i++) {
            patient.addRecord(1.0, "ECG", i * 1000L);
        }
        
        // Add an abnormal peak (1.5x the average of 1.0 is 1.5, so 2.0 should trigger)
        patient.addRecord(2.0, "ECG", 50000L);

        List<Alert> alerts = strategy.checkAlert(patient);
        assertEquals(1, alerts.size(), "An abnormal peak should trigger an alert.");
        assertEquals("Abnormal ECG Peak", alerts.get(0).getCondition());
    }

    @Test
    void testNormalECGFluctuations() {
        // Add 60 records that fluctuate mildly around 1.0
        for (int i = 0; i < 60; i++) {
            double val = 1.0 + (i % 2 == 0 ? 0.1 : -0.1); // Flips between 1.1 and 0.9
            patient.addRecord(val, "ECG", i * 1000L);
        }

        List<Alert> alerts = strategy.checkAlert(patient);
        assertTrue(alerts.isEmpty(), "Normal fluctuations should not trigger an alert.");
    }
}
