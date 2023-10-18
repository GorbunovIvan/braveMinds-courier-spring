package com.example;

import com.example.data.CourierData;
import com.example.data.Order;
import com.example.data.OrderExtended;
import com.example.data.Point;
import com.example.service.CourierService;
import com.example.service.OrderService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.Function;
import java.util.stream.Collectors;

@SpringBootApplication
public class BraveMindsCourierApplication {

    public static void main(String[] args) {

        var context = SpringApplication.run(BraveMindsCourierApplication.class, args);

        var orderService = context.getBean(OrderService.class);
        var courierService = context.getBean(CourierService.class);

        var orders = orderService.getAll();
        var courierData = courierService.readSettings();

        if (courierData.getSpeedKmPerHour() <= 0) {
            throw new RuntimeException("Courier speed is invalid");
        }

        OrderExtended.SPEED_KM_PER_HOUR = courierData.getSpeedKmPerHour();

        showTheWay("Minimum distance", orders, courierData, BraveMindsCourierApplication::nextOrderWithTheMinimumDistanse);
        showTheWay("Work completion", orders, courierData, BraveMindsCourierApplication::nextOrderWithTheEarliestCompletion);
        showTheWay("Wait time", orders, courierData, BraveMindsCourierApplication::nextOrderWithTheMinimumWaitTime);
    }


    private static void showTheWay(String title,
                                    List<Order> ordersInit, CourierData courierData,
                                    Function<List<OrderExtended>, OrderExtended> functionForDefiningNextOrder) {

        var orders = extendListOfOrdersWithMoreFields(ordersInit);
        var resultOrders = new ArrayList<OrderExtended>();

        var currentPoint = new Point(courierData.getStartingCoordinatesLatitude(), courierData.getStartingCoordinatesLongitude());
        var currentTime = courierData.getBeginningOfWorkDay();

        while (!orders.isEmpty()) {

            calculateAdditionalInformationForAllOrders(orders, currentPoint, currentTime);

            orders.removeIf(order -> !order.canBeAchievedInTime());
            orders.removeIf(order -> order.getTimeAfterArrivingFromCurrentPoint().isAfter(courierData.getEndOfWorkDay()));

            var nearestOrder = functionForDefiningNextOrder.apply(orders);
            if (nearestOrder == null) {
                break;
            }

            resultOrders.add(nearestOrder);
            orders.remove(nearestOrder);

            currentPoint = nearestOrder.getPoint();
            currentTime = LocalTime.ofSecondOfDay(nearestOrder.getTimeAfterArrivingFromCurrentPoint().toSecondOfDay() + courierData.getTimeSpentOnDeliveryPoint().toSecondOfDay());
        }

        outputResult(title, resultOrders);

        if (!orders.isEmpty()) {
            var unexecutedOrders = orders.stream()
                    .sorted(Comparator.comparing(Order::getId))
                    .map(order -> "[" + order.getId() + "]")
                    .collect(Collectors.joining(", "));
            System.out.println("Not done: " + unexecutedOrders);
        }
    }


    private static OrderExtended nextOrderWithTheMinimumDistanse(List<OrderExtended> orders) {
        return orders.stream()
                .filter(order -> order.getDeliveryFrom().isBefore(getMinDeliveryTime(orders)))
                .min(Comparator.comparing(OrderExtended::getDistanceFromCurrentPoint))
                .orElse(null);
    }

    private static OrderExtended nextOrderWithTheEarliestCompletion(List<OrderExtended> orders) {
        return orders.stream()
                .filter(order -> order.getDeliveryFrom().isBefore(getMinDeliveryTime(orders)))
                .min(Comparator.comparing(OrderExtended::getTimeAfterArrivingFromCurrentPoint))
                .orElse(null);
    }

    private static OrderExtended nextOrderWithTheMinimumWaitTime(List<OrderExtended> orders) {
        return orders.stream()
                .filter(order -> order.getDeliveryFrom().isBefore(getMinDeliveryTime(orders)))
                .min(Comparator.comparing(OrderExtended::getWaitTime))
                .orElse(null);
    }


    private static void calculateAdditionalInformationForAllOrders(List<OrderExtended> ordersExtended, Point currentPoint, LocalTime currentTime) {
        ordersExtended.forEach(order -> order.calculateFromCurrentPointAndTime(currentPoint, currentTime));
    }

    private static List<OrderExtended> extendListOfOrdersWithMoreFields(List<Order> orders) {
        return orders.stream()
                .map(OrderExtended::new)
                .sorted(Comparator.comparing(Order::getDeliveryFrom)
                        .thenComparing(Order::getDeliveryTo)
                        .thenComparing(Order::getId))
                .collect(Collectors.toList());
    }

    private static LocalTime getMinDeliveryTime(List<OrderExtended> orders) {
        return orders.stream()
                .min(Comparator.comparing(OrderExtended::getDeliveryTo))
                .map(OrderExtended::getDeliveryTo)
                .orElseThrow(NoSuchElementException::new);
    }

    private static void outputResult(String title, List<OrderExtended> orders) {

        // output results
        var wayPoints = orders.stream()
                .map(order -> "[" + order.getId() + "]")
                .collect(Collectors.joining(" => "));

        int distance = (int) Math.round(orders.stream().mapToDouble(OrderExtended::getDistanceFromCurrentPoint).sum());
        LocalTime travelTime = orders.stream().map(OrderExtended::getTimeInTravel).reduce(LocalTime.MIN, (t1, t2) -> LocalTime.ofSecondOfDay(t1.toSecondOfDay() + t2.toSecondOfDay()));
        LocalTime waitTime = orders.stream().map(OrderExtended::getWaitTime).reduce(LocalTime.MIN, (t1, t2) -> LocalTime.ofSecondOfDay(t1.toSecondOfDay() + t2.toSecondOfDay()));
        LocalTime completionTime = orders.get(orders.size()-1).getTimeAfterArrivingFromCurrentPoint();

        System.out.printf("%s. %s | Distance: %d km. Travel time: %s. Wait time: %s. Work completed in %s\n",
                title, wayPoints, distance, travelTime, waitTime, completionTime);
    }
}
