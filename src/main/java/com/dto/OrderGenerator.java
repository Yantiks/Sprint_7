package com.dto;

import org.apache.commons.lang3.RandomStringUtils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class OrderGenerator {
    public static Order getRandomOrder() {
        Order order = new Order();
        Random random = new Random();
        List<String> color = new ArrayList<>();
        order.setFirstName(RandomStringUtils.randomAlphabetic(10));
        order.setLastName(RandomStringUtils.randomAlphabetic(10));
        order.setAddress(RandomStringUtils.randomAlphabetic(10));
        order.setMetroStation(random.nextInt(100));
        order.setPhone(RandomStringUtils.randomAlphabetic(10));
        order.setRentTime(random.nextInt(100));
        order.setDeliveryDate(LocalDate.now().toString());
        order.setComment(RandomStringUtils.randomAlphabetic(10));
        order.setColor(color);
        return order;
    }
}
