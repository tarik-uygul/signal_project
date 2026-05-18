package com.alerts;

import com.data_management.DataStorage;
import com.data_management.Patient;
import java.util.ArrayList;
import java.util.List;

public class AlertGenerator {
    private DataStorage dataStorage;
    private List<AlertStrategy> alertStrategies;

    public AlertGenerator(DataStorage dataStorage) {
        this.dataStorage = dataStorage;
        this.alertStrategies = new ArrayList<>();
        
        
        // Register all your alert rules here
         alertStrategies.add(new BloodPressureStrategy());
         alertStrategies.add(new BloodSaturationStrategy());
         // Inside AlertGenerator constructor:
       
        alertStrategies.add(new HypotensiveHypoxemiaStrategy());
        alertStrategies.add(new ECGStrategy());
        alertStrategies.add(new TriggeredAlertStrategy());
        // Add others as you create them...
    }

    public void evaluateData(Patient patient) {
        for (AlertStrategy strategy : alertStrategies) {
            List<Alert> triggeredAlerts = strategy.checkAlert(patient);
            for (Alert alert : triggeredAlerts) {
                triggerAlert(alert);
            }
        }
    }

   private void triggerAlert(Alert alert) {
        // Example: If it's a critical threshold, let's decorate it as Priority!
        if (alert.getCondition().contains("Critical")) {
            alert = new PriorityAlertDecorator(alert);
        }
        
        System.out.println("ALERT TRIGGERED! Patient: " + alert.getPatientId() + 
                           " | Condition: " + alert.getCondition() + 
                           " | Time: " + alert.getTimestamp());
    }
}