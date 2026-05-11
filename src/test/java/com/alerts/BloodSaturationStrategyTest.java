package com.alerts;

import com.data_management.Patient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class BloodSaturationStrategyTest {

    private BloodSaturationStrategy strategy;
    private Patient patient;

    @BeforeEach
    void setUp() {
        strategy = new BloodSaturationStrategy();
        patient = new Patient(1); // Create a fresh patient for each test
    }

    @Test
    void testNormalSaturation() {
        patient.addRecord(98.0, "Saturation", 1000L);
        patient.addRecord(97.0, "Saturation", 2000L);

        List<Alert> alerts = strategy.checkAlert(patient);
        assertTrue(alerts.isEmpty(), "No alerts should be generated for normal saturation.");
    }

    @Test
    void testLowSaturationAlert() {
        // Add a record below 92%
        patient.addRecord(91.0, "Saturation", 1000L);

        List<Alert> alerts = strategy.checkAlert(patient);
        assertEquals(1, alerts.size(), "One alert should be generated.");
        assertEquals("Low Saturation", alerts.get(0).getCondition());
        assertEquals("1", alerts.get(0).getPatientId());
    }

    @Test
    void testRapidDropWithinTenMinutes() {
        long startTime = 1600000000000L;
        patient.addRecord(98.0, "Saturation", startTime);
        
        // 5 minutes later (5 * 60 * 1000 = 300,000 ms), drops by exactly 5%
        patient.addRecord(93.0, "Saturation", startTime + 300000L); 

        List<Alert> alerts = strategy.checkAlert(patient);
        assertEquals(1, alerts.size(), "One rapid drop alert should be generated.");
        assertEquals("Rapid Saturation Drop", alerts.get(0).getCondition());
    }

    @Test
    void testGradualDropOverTenMinutes() {
        long startTime = 1600000000000L;
        patient.addRecord(98.0, "Saturation", startTime);
        
        // 11 minutes later (11 * 60 * 1000 = 660,000 ms), drops by 5%
        // Because it took longer than 10 mins, it should NOT trigger the rapid drop alert
        patient.addRecord(93.0, "Saturation", startTime + 660000L);

        List<Alert> alerts = strategy.checkAlert(patient);
        assertTrue(alerts.isEmpty(), "No alert should be generated because the drop took longer than 10 minutes.");
    }
}