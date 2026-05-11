package com.alerts;

import com.data_management.Patient;
import java.util.List;


public interface AlertStrategy {

    /**
     *  Checks a patients data against the alert strategy and generates an alert if the strategy's conditions are met.
     * @param patient the patient whose data is being checked against the alert strategy
     * @return a string representing the alert message if the strategy's conditions are met
     */
    List<Alert> checkAlert(Patient patient);

} 
    

