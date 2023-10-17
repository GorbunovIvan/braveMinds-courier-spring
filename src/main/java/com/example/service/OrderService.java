package com.example.service;

import com.example.data.Order;
import com.example.data.Point;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final JdbcTemplate jdbcTemplate;

    public List<Order> getAll() {
        return jdbcTemplate.query("SELECT * FROM orders", new OrderRowMapper());
    }

    private static class OrderRowMapper implements RowMapper<Order> {
        @Override
        public Order mapRow(ResultSet rs, int rowNum) throws SQLException {
            var order = new Order();
            order.setId(rs.getInt("id"));
            order.setPoint(new Point(rs.getDouble("latitude"), rs.getDouble("longitude")));
            order.setDeliveryFrom(rs.getTime("delivery_from").toLocalTime());
            order.setDeliveryTo(rs.getTime("delivery_to").toLocalTime());
            return order;
        }
    }

}
