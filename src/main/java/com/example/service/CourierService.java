package com.example.service;

import com.example.data.CourierData;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CourierService {

    private final ObjectMapper objectMapper;

    @SneakyThrows
    public CourierData readSettings() {
        var resource = new ClassPathResource("courier.json");
        return objectMapper.readValue(resource.getInputStream(), CourierData.class);
    }
}
