package com.client;

import com.dto.Courier;
import com.dto.CourierLogin;
import io.restassured.response.ValidatableResponse;

import static io.restassured.RestAssured.given;

public class CourierClient extends RestClient {

    //create
    public ValidatableResponse create(Courier courier) {
        return given()
                .spec(getDefaultRequestSpec())
                .body(courier)
                .post("/api/v1/courier")
                .then();
    }

    //login
    public ValidatableResponse login(CourierLogin courierLogin) {
        return given()
                .spec(getDefaultRequestSpec())
                .body(courierLogin)
                .post("/api/v1/courier/login")
                .then();

    }

    //delete
    public ValidatableResponse delete(int id) {
        return given()
                .spec(getDefaultRequestSpec())
                .delete("/api/v1/courier/{id}", id)
                .then();
    }
}
