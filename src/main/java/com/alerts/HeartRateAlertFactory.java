package com.alerts;

public class HeartRateAlertFactory extends AlertFactory {
    @Override
    public Alert createAlert(String patientId, String condition, long timestamp) {
        return new HeartRateAlert(patientId, condition, timestamp);
    }
}