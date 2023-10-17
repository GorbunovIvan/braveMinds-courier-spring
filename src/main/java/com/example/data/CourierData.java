package com.example.data;

import lombok.Data;

import java.time.LocalTime;

@Data
public class CourierData {
    private LocalTime beginningOfWorkDay;
    private LocalTime endOfWorkDay;
    private LocalTime timeSpentOnDeliveryPoint;
    private double speedKmPerHour;
    private double startingCoordinatesLatitude;
    private double startingCoordinatesLongitude;
}
