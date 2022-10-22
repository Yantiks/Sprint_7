package com.client;

import com.dto.Order;
import io.restassured.response.ValidatableResponse;

import static io.restassured.RestAssured.given;

public class OrderClient extends RestClient {

    //create
    public ValidatableResponse create(Order order) {
        return given()
                .log()
                .all()
                .spec(getDefaultRequestSpec())
                .body(order)
                .post("/api/v1/orders")
                .then();
    }

    //decline
    public ValidatableResponse decline(int id) {
        return given()
                .spec(getDefaultRequestSpec())
                .param("track", id)
                .put("/api/v1/orders/cancel")
                .then();
    }

    public ValidatableResponse getOrderList() {
        return given()
                .spec(getDefaultRequestSpec())
                .get("/api/v1/orders")
                .then();
    }
}