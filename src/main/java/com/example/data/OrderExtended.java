package com.example.data;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalTime;

@Getter @Setter
@ToString(callSuper = true)
public class OrderExtended extends Order {

    private double distanceFromCurrentPoint;
    private LocalTime timeInTravel;
    private LocalTime timeAfterArrivingFromCurrentPoint;
    private LocalTime waitTime = LocalTime.MIN;

    public static double SPEED_KM_PER_HOUR;

    public OrderExtended(Order order) {
        setId(order.getId());
        setPoint(order.getPoint());
        setDeliveryFrom(order.getDeliveryFrom());
        setDeliveryTo(order.getDeliveryTo());
    }

    public void calculateFromCurrentPointAndTime(Point currentPoint, LocalTime currentTime) {

        // Resetting
        this.distanceFromCurrentPoint = 0;
        this.timeInTravel = null;
        this.timeAfterArrivingFromCurrentPoint = null;
        this.waitTime = LocalTime.MIN;

        // Distance
        this.distanceFromCurrentPoint = calculateDistanceBetweenPoints(this.getPoint(), currentPoint);

        // Time
        double timeInHours = this.distanceFromCurrentPoint / SPEED_KM_PER_HOUR;

        int hour = (int) timeInHours;
        int minute = (int) Math.round(timeInHours * 60);

        this.timeInTravel = LocalTime.of(hour, minute);
        this.timeAfterArrivingFromCurrentPoint = LocalTime.ofSecondOfDay(currentTime.toSecondOfDay() + timeInTravel.toSecondOfDay());

        if (this.timeAfterArrivingFromCurrentPoint.isBefore(getDeliveryFrom())) {
            this.waitTime = LocalTime.ofSecondOfDay(getDeliveryFrom().toSecondOfDay() - timeAfterArrivingFromCurrentPoint.toSecondOfDay());
            this.timeAfterArrivingFromCurrentPoint = getDeliveryFrom();
        }
    }

    public boolean canBeAchievedInTime() {
        return !this.timeAfterArrivingFromCurrentPoint.isAfter(getDeliveryTo());
    }

    public static double calculateDistanceBetweenPoints(Point point1, Point point2) {

        // Earth radius in km
        double radius = 6371.0;

        // Converting coordinates to radians
        double lat1 = Math.toRadians(point1.getLatitude());
        double lon1 = Math.toRadians(point1.getLongitude());
        double lat2 = Math.toRadians(point2.getLatitude());
        double lon2 = Math.toRadians(point2.getLongitude());

        // Differences in coordinates
        double dlat = lat2 - lat1;
        double dlon = lon2 - lon1;

        // Haversine formula
        double a = Math.pow(Math.sin(dlat / 2), 2) + Math.cos(lat1) * Math.cos(lat2) * Math.pow(Math.sin(dlon / 2), 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        // Distance calculation
        return radius * c;
    }
}
