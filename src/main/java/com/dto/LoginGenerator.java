package com.dto;

public class LoginGenerator {
    public static CourierLogin from(Courier courier) {
        CourierLogin courierLogin = new CourierLogin();
        courierLogin.setLogin(courier.getLogin());
        courierLogin.setPassword(courier.getPassword());
        return courierLogin;
    }
}
