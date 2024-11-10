package com.parkit.parkingsystem.service;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.model.Ticket;

public class FareCalculatorService {

    public void calculateFare(Ticket ticket) {
        if ((ticket.getOutTime() == null) || (ticket.getOutTime().before(ticket.getInTime()))) {
            throw new IllegalArgumentException("Out time provided is incorrect:" + ticket.getOutTime().toString());
        }
        // Calculer la durée en millisecondes
        long inTimeMillis =  ticket.getInTime().getTime();
        long outTimeMillis = ticket.getOutTime().getTime();
        // Convertir la durée en heures
        //TODO: Some tests are failing here. Need to check if this logic is correct
        double durationMinute = (outTimeMillis - inTimeMillis) / (1000.0* 60);// Diviser pour obtenir la durée en minute
        // Si la durée est inférieure à 30 minutes, le prix est 0
        if (durationMinute < 30) {
            ticket.setPrice(0);
            return;
        }
        double durationHours = durationMinute / 60.0;// Diviser pour obtenir la durée en heure
        // Appliquer les tarifs en fonction du type de véhicule
        switch (ticket.getParkingSpot().getParkingType()) {
            case CAR: {
                ticket.setPrice(durationHours * Fare.CAR_RATE_PER_HOUR);
                break;
            }
            case BIKE: {
                ticket.setPrice(durationHours * Fare.BIKE_RATE_PER_HOUR);
                break;
            }
            default:
                throw new IllegalArgumentException("Unkown Parking Type");
        }

    }
}