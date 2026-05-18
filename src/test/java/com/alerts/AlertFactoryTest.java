package com.alerts;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class AlertFactoryTest {

    @Test
    void testBloodPressureFactory() {
        AlertFactory factory = new BloodPressureAlertFactory();
        Alert alert = factory.createAlert("1", "High BP", 1000L);
        
        assertEquals("1", alert.getPatientId());
        assertEquals("High BP", alert.getCondition());
        assertEquals(1000L, alert.getTimestamp());
    }

    @Test
    void testBloodOxygenFactory() {
        AlertFactory factory = new BloodOxygenAlertFactory();
        Alert alert = factory.createAlert("2", "Low Oxygen", 2000L);
        
        assertEquals("2", alert.getPatientId());
        assertEquals("Low Oxygen", alert.getCondition());
    }

    @Test
    void testECGFactory() {
        AlertFactory factory = new ECGAlertFactory();
        Alert alert = factory.createAlert("3", "Abnormal Peak", 3000L);
        
        assertEquals("3", alert.getPatientId());
        assertEquals("Abnormal Peak", alert.getCondition());
    }
}