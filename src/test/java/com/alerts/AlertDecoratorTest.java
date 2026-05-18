package com.alerts;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class AlertDecoratorTest {

    @Test
    void testPriorityDecorator() {
        Alert basicAlert = new BasicAlert("1", "Low Heart Rate", 1000L);
        Alert priorityAlert = new PriorityAlertDecorator(basicAlert);

        assertEquals("1", priorityAlert.getPatientId());
        assertEquals(1000L, priorityAlert.getTimestamp());
        assertEquals("[URGENT PRIORITY] Low Heart Rate", priorityAlert.getCondition());
    }

    @Test
    void testRepeatedDecorator() {
        Alert basicAlert = new BasicAlert("2", "High Blood Pressure", 2000L);
        Alert repeatedAlert = new RepeatedAlertDecorator(basicAlert);

        assertEquals("2", repeatedAlert.getPatientId());
        assertEquals(2000L, repeatedAlert.getTimestamp());
        assertEquals("High Blood Pressure (Repeated Alert)", repeatedAlert.getCondition());
    }

    @Test
    void testCombinedDecorators() {
        // You can wrap decorators inside other decorators!
        Alert basicAlert = new BasicAlert("3", "Critical ECG", 3000L);
        Alert repeatedAlert = new RepeatedAlertDecorator(basicAlert);
        Alert urgentRepeatedAlert = new PriorityAlertDecorator(repeatedAlert);

        assertEquals("[URGENT PRIORITY] Critical ECG (Repeated Alert)", urgentRepeatedAlert.getCondition());
    }
}