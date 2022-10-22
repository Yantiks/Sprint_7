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


public class CourierCreationTest {
    private CourierClient courierClient;
    private Courier randomCourier;
    private Integer id;

    @Before
    public void setUp() {
        courierClient = new CourierClient();
        randomCourier = getRandomCourier();
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

    //Курьера можно создать
    //запрос возвращает правильный код ответа; (проверка для http status 201)
    //успешный запрос возвращает ok: true;
    @Test
    @DisplayName("Check successful courier creation")
    public void createCourierTest() {
        //create
        courierClient.create(randomCourier)
                .assertThat()
                .statusCode(HttpStatus.SC_CREATED)
                .and()
                .body("ok", Matchers.equalTo(true));

        //login
        CourierLogin courierLogin = LoginGenerator.from(randomCourier);

        id = courierClient.login(courierLogin)
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .and()
                .body("id", Matchers.notNullValue())
                .extract()
                .path("id");
    }

    //Нельзя создать двух одинаковых курьеров
    //запрос возвращает правильный код ответа; (проверка для http status 409)
    //!!! тест падает, т.к. возвращается неверное сообщение в ответе: "Этот логин уже используется. Попробуйте другой."
    @Test
    @DisplayName("Check identical courier creation")
    public void identicalCouriersTest() {

        courierClient.create(randomCourier)
                .assertThat()
                .statusCode(HttpStatus.SC_CREATED)
                .and()
                .body("ok", Matchers.equalTo(true));

        courierClient.create(randomCourier)
                .assertThat()
                .statusCode(HttpStatus.SC_CONFLICT)
                .and()
                .body("message", Matchers.equalTo("Этот логин уже используется"));

        CourierLogin courierLogin = LoginGenerator.from(randomCourier);
        id = courierClient.login(courierLogin)
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .and()
                .body("id", Matchers.notNullValue())
                .extract()
                .path("id");
    }

    //чтобы создать курьера, нужно передать в ручку все обязательные поля (проверяем, что достаточно указать только обязательные поля)
    @Test
    @DisplayName("Check courier creation with mandatory parameters")
    public void mandatoryFieldsTest() {
        randomCourier.setFirstName(null);

        courierClient.create(randomCourier)
                .assertThat()
                .statusCode(HttpStatus.SC_CREATED)
                .and()
                .body("ok", Matchers.equalTo(true));

        CourierLogin courierLogin = LoginGenerator.from(randomCourier);
        id = courierClient.login(courierLogin)
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .and()
                .body("id", Matchers.notNullValue())
                .extract()
                .path("id");
    }

    //если одного из полей нет, запрос возвращает ошибку; (не передаем login)
    //запрос возвращает правильный код ответа; (проверка для http status 400)
    @Test
    @DisplayName("Check courier creation without login")
    public void emptyLoginTest() {
        randomCourier.setLogin(null);

        courierClient.create(randomCourier)
                .assertThat()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .and()
                .body("message", Matchers.equalTo("Недостаточно данных для создания учетной записи"));
    }

    //если одного из полей нет, запрос возвращает ошибку; (не передаем password)
    //запрос возвращает правильный код ответа; (проверка для http status 400)
    @Test
    @DisplayName("Check courier creation without password")
    public void emptyPasswordTest() {
        randomCourier.setPassword(null);

        courierClient.create(randomCourier)
                .assertThat()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .and()
                .body("message", Matchers.equalTo("Недостаточно данных для создания учетной записи"));
    }

    //если создать пользователя с логином, который уже есть, возвращается ошибка.
    @Test
    @DisplayName("Check courier creation with existing login")
    public void identicalLoginTest() {

        //создаем первого пользователя
        courierClient.create(randomCourier)
                .assertThat()
                .statusCode(HttpStatus.SC_CREATED)
                .and()
                .body("ok", Matchers.equalTo(true));

        CourierLogin courierLogin = LoginGenerator.from(randomCourier);
        id = courierClient.login(courierLogin).extract().path("id");

        //создаем другого пользователя с тем же логином
        randomCourier.setPassword(RandomStringUtils.randomAlphabetic(9));
        randomCourier.setFirstName(RandomStringUtils.randomAlphabetic(9));
        courierClient.create(randomCourier)
                .assertThat()
                .statusCode(HttpStatus.SC_CONFLICT)
                .and()
                .body("message", Matchers.equalTo("Этот логин уже используется"));
    }
}
