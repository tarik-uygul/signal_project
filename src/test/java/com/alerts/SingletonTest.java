package com.alerts;

import com.data_management.DataStorage;
import com.cardio_generator.HealthDataSimulator;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class SingletonTest {

    @Test
    void testDataStorageSingleton() {
        DataStorage instance1 = DataStorage.getInstance();
        DataStorage instance2 = DataStorage.getInstance();
        
        assertNotNull(instance1);
        assertSame(instance1, instance2, "DataStorage should return the exact same instance in memory.");
    }

    @Test
    void testHealthDataSimulatorSingleton() {
        HealthDataSimulator instance1 = HealthDataSimulator.getInstance();
        HealthDataSimulator instance2 = HealthDataSimulator.getInstance();
        
        assertNotNull(instance1);
        assertSame(instance1, instance2, "HealthDataSimulator should return the exact same instance in memory.");
    }
}