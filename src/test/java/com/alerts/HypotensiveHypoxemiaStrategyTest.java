package com.alerts;

import com.data_management.Patient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class HypotensiveHypoxemiaStrategyTest {
    private HypotensiveHypoxemiaStrategy strategy;
    private Patient patient;

    @BeforeEach
    void setUp() {
        strategy = new HypotensiveHypoxemiaStrategy();
        patient = new Patient(1);
    }

    @Test
    void testCombinedAlertTriggers() {
        // Both drop below thresholds within 1 minute (60,000 ms) of each other
        long baseTime = 1600000000000L;
        patient.addRecord(85.0, "SystolicPressure", baseTime);
        patient.addRecord(90.0, "Saturation", baseTime + 30000L); // 30 seconds later

        List<Alert> alerts = strategy.checkAlert(patient);
        assertEquals(1, alerts.size(), "Combined alert should trigger.");
        assertEquals("Hypotensive Hypoxemia", alerts.get(0).getCondition());
    }

    @Test
    void testOutsideTimeWindowDoesNotTrigger() {
        // Time difference is 2 minutes (120,000 ms) - should NOT trigger
        long baseTime = 1600000000000L;
        patient.addRecord(85.0, "SystolicPressure", baseTime);
        patient.addRecord(90.0, "Saturation", baseTime + 120000L); 

        List<Alert> alerts = strategy.checkAlert(patient);
        assertTrue(alerts.isEmpty(), "Alert should not trigger if readings are too far apart.");
    }

    @Test
    void testOnlyOneConditionMetDoesNotTrigger() {
        long baseTime = 1600000000000L;
        patient.addRecord(120.0, "SystolicPressure", baseTime); // BP is fine
        patient.addRecord(90.0, "Saturation", baseTime + 10000L); // Sat is low

        List<Alert> alerts = strategy.checkAlert(patient);
        assertTrue(alerts.isEmpty(), "Alert should not trigger if BP is normal.");
    }
}
