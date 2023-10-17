package com.example.data;

import lombok.Data;

import java.time.LocalTime;

@Data
public class Order {
    private int id;
    private Point point;
    private LocalTime deliveryFrom;
    private LocalTime deliveryTo;
}
