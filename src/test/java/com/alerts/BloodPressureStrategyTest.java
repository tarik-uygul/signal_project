package com.alerts;

import com.data_management.Patient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class BloodPressureStrategyTest {
    private BloodPressureStrategy strategy;
    private Patient patient;

    @BeforeEach
    void setUp() {
        strategy = new BloodPressureStrategy();
        patient = new Patient(1);
    }

    @Test
    void testNormalBloodPressure() {
        patient.addRecord(120.0, "SystolicPressure", 1000L);
        patient.addRecord(80.0, "DiastolicPressure", 1000L);
        List<Alert> alerts = strategy.checkAlert(patient);
        assertTrue(alerts.isEmpty(), "No alerts should trigger for normal BP.");
    }

    @Test
    void testCriticalSystolicHighAndLow() {
        // High
        patient.addRecord(185.0, "SystolicPressure", 1000L);
        List<Alert> alertsHigh = strategy.checkAlert(patient);
        assertEquals(1, alertsHigh.size());
        assertEquals("SystolicPressure Critical Threshold", alertsHigh.get(0).getCondition());

        // Low
        patient = new Patient(2); // Reset patient
        patient.addRecord(85.0, "SystolicPressure", 1000L);
        List<Alert> alertsLow = strategy.checkAlert(patient);
        assertEquals(1, alertsLow.size());
    }

    @Test
    void testCriticalDiastolicHighAndLow() {
        // High
        patient.addRecord(125.0, "DiastolicPressure", 1000L);
        List<Alert> alertsHigh = strategy.checkAlert(patient);
        assertEquals(1, alertsHigh.size());
        assertEquals("DiastolicPressure Critical Threshold", alertsHigh.get(0).getCondition());

        // Low
        patient = new Patient(2);
        patient.addRecord(55.0, "DiastolicPressure", 1000L);
        List<Alert> alertsLow = strategy.checkAlert(patient);
        assertEquals(1, alertsLow.size());
    }

    @Test
    void testIncreasingTrendAlert() {
        patient.addRecord(110.0, "SystolicPressure", 1000L);
        patient.addRecord(121.0, "SystolicPressure", 2000L); // Increases by 11
        patient.addRecord(132.0, "SystolicPressure", 3000L); // Increases by 11

        List<Alert> alerts = strategy.checkAlert(patient);
        assertEquals(1, alerts.size());
        assertEquals("Systolic Trend Increasing", alerts.get(0).getCondition());
    }

    @Test
    void testDecreasingTrendAlert() {
        patient.addRecord(80.0, "DiastolicPressure", 1000L);
        patient.addRecord(69.0, "DiastolicPressure", 2000L); // Decreases by 11
        patient.addRecord(58.0, "DiastolicPressure", 3000L); // Decreases by 11
        
        List<Alert> alerts = strategy.checkAlert(patient);
        // Note: 58 also triggers a Critical Low Alert, so we expect 2 alerts!
        assertEquals(2, alerts.size()); 
        assertTrue(alerts.stream().anyMatch(a -> a.getCondition().equals("Diastolic Trend Decreasing")));
    }
}
