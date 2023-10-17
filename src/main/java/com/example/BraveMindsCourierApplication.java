package com.example;

import com.example.service.CourierService;
import com.example.service.OrderService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BraveMindsCourierApplication {

    public static void main(String[] args) {

        var context = SpringApplication.run(BraveMindsCourierApplication.class, args);

        var orderService = context.getBean(OrderService.class);
        var courierService = context.getBean(CourierService.class);

        orderService.getAll().forEach(System.out::println);
        System.out.println(courierService.readSettings());
    }
}
