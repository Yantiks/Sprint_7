package com.example;

import com.client.CourierClient;
import com.dto.Courier;
import com.dto.CourierLogin;
import com.dto.LoginGenerator;
import io.qameta.allure.junit4.DisplayName;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.http.HttpStatus;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static com.dto.CourierGenerator.getRandomCourier;

public class CourierLoginTest {

    private CourierClient courierClient;
    private Courier randomCourier;
    private CourierLogin courierLogin;
    private Integer id;

    @Before
    public void setUp() {
        courierClient = new CourierClient();
        randomCourier = getRandomCourier();
        //create
        courierClient.create(randomCourier)
                .assertThat()
                .statusCode(HttpStatus.SC_CREATED)
                .and()
                .body("ok", Matchers.equalTo(true));

        courierLogin = LoginGenerator.from(randomCourier);
    }


    @After
    public void tearDown() {
        if (id != null) {
            courierClient.delete(id)
                    .assertThat()
                    .statusCode(HttpStatus.SC_OK)
                    .and()
                    .body("ok", Matchers.equalTo(true));
        }
    }


    //курьер может авторизоваться;
    //для авторизации нужно передать все обязательные поля;
    //успешный запрос возвращает id;
    @Test
    @DisplayName("Check success login with mandatory parameters")
    public void successLoginTest() {
        id = courierClient.login(courierLogin)
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .and()
                .body("id", Matchers.notNullValue())
                .extract()
                .path("id");
    }

    //система вернёт ошибку, если неправильно указать логин;
    @Test
    @DisplayName("Check failed authentication with incorrect login")
    public void incorrectLoginTest() {
        courierLogin.setLogin(RandomStringUtils.randomAlphabetic(14));
        id = courierClient.login(courierLogin)
                .assertThat()
                .statusCode(HttpStatus.SC_NOT_FOUND)
                .and()
                .body("message", Matchers.equalTo("Учетная запись не найдена"))
                .extract()
                .path("id");
    }

    //система вернёт ошибку, если неправильно указать пароль;
    @Test
    @DisplayName("Check failed authentication with incorrect password")
    public void incorrectPasswordTest() {
        courierLogin.setPassword(RandomStringUtils.randomAlphabetic(9));
        id = courierClient.login(courierLogin)
                .assertThat()
                .statusCode(HttpStatus.SC_NOT_FOUND)
                .and()
                .body("message", Matchers.equalTo("Учетная запись не найдена"))
                .extract()
                .path("id");
    }


    //если логина нет, запрос возвращает ошибку;
    @Test
    @DisplayName("Check failed authentication without login")
    public void emptyLoginTest() {
        courierLogin.setLogin(null);
        id = courierClient.login(courierLogin)
                .assertThat()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .and()
                .body("message", Matchers.equalTo("Недостаточно данных для входа"))
                .extract()
                .path("id");
    }

    //если пароля нет, запрос возвращает ошибку;
    //Тест падает, т.к. ответ сервера не соответствует требованиям - Expected status code <400> but was <504>.
    @Test
    @DisplayName("Check failed authentication without password")
    public void emptyPasswordTest() {
        courierLogin.setPassword(null);
        id = courierClient.login(courierLogin)
                .assertThat()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .and()
                .body("message", Matchers.equalTo("Недостаточно данных для входа"))
                .extract()
                .path("id");
    }

    //если авторизоваться под несуществующим пользователем, запрос возвращает ошибку;
    @Test
    @DisplayName("Check failed authentication with nonexistent user")
    public void nonexistentCourierLoginTest() {
        id = courierClient.login(courierLogin)
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .and()
                .body("id", Matchers.notNullValue())
                .extract()
                .path("id");

        courierClient.delete(id)
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .and()
                .body("ok", Matchers.equalTo(true));

        id = courierClient.login(courierLogin)
                .assertThat()
                .statusCode(HttpStatus.SC_NOT_FOUND)
                .and()
                .body("message", Matchers.equalTo("Учетная запись не найдена"))
                .extract()
                .path("id");
    }

}
