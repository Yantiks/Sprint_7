package com.dto;

import org.apache.commons.lang3.RandomStringUtils;

public class CourierGenerator {
    public static Courier getRandomCourier() {
        Courier courier = new Courier();
        courier.setFirstName(RandomStringUtils.randomAlphabetic(10));
        courier.setLogin(RandomStringUtils.randomAlphabetic(15));
        courier.setPassword(RandomStringUtils.randomAlphabetic(10));
        return courier;
    }
}
