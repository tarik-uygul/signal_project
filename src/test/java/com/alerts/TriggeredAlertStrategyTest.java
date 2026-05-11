package com.alerts;

import com.data_management.Patient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class TriggeredAlertStrategyTest {
    private TriggeredAlertStrategy strategy;
    private Patient patient;

    @BeforeEach
    void setUp() {
        strategy = new TriggeredAlertStrategy();
        patient = new Patient(1);
    }

    @Test
    void testManualTrigger() {
        // 1.0 represents "triggered" mapped by the DataReader
        patient.addRecord(1.0, "Alert", 1000L);
        
        List<Alert> alerts = strategy.checkAlert(patient);
        assertEquals(1, alerts.size());
        assertEquals("Manual Alert Triggered", alerts.get(0).getCondition());
    }

    @Test
    void testManualResolved() {
        // 0.0 represents "resolved" mapped by the DataReader
        patient.addRecord(0.0, "Alert", 1000L);
        
        List<Alert> alerts = strategy.checkAlert(patient);
        assertTrue(alerts.isEmpty(), "Resolved alerts should not trigger the system.");
    }
}
