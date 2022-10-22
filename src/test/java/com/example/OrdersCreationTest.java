package com.example;

import com.client.OrderClient;
import com.dto.Order;
import io.qameta.allure.junit4.DisplayName;
import org.apache.http.HttpStatus;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.ArrayList;
import java.util.List;

import static com.dto.OrderGenerator.getRandomOrder;

@RunWith(Parameterized.class)
public class OrdersCreationTest {
    private OrderClient orderClient;
    private Order order;
    private Integer id;
    private final String color1;
    private final String color2;
    public OrdersCreationTest(String color1, String color2) {
        this.color1 = color1;
        this.color2 = color2;
    }

    @Parameterized.Parameters(name = "color1 = {0}, color2 = {1}")
    public static Object[][] colors() {
        return new Object[][]{
                {"BLACK", "GREY"},
                {"GREY", null},
                {"BLACK", null},
                {null, null}
        };
    }

    @Before
    public void setUp() {
        orderClient = new OrderClient();
    }

    @After
    public void tearDown() {
        if (id != null) {
            orderClient.decline(id)
                    .assertThat()
                    .statusCode(HttpStatus.SC_OK)
                    .and()
                    .body("ok", Matchers.equalTo(true));
        }
    }

    /*Проверь, что когда создаёшь заказ:
    можно указать один из цветов — BLACK или GREY;
    можно указать оба цвета;
    можно совсем не указывать цвет;
    тело ответа содержит track.
    Чтобы протестировать создание заказа, нужно использовать параметризацию.*/

    @Test
    @DisplayName("Check orders creation with different colors (parametrized)")
    public void colorParametrizedTest() {
        order = getRandomOrder();
        List<String> colorsList = new ArrayList<>();
        if (color1 != null) {
            colorsList.add(color1);
        }
        if (color2 != null) {
            colorsList.add(color2);
        }
        order.setColor(colorsList);

        id = orderClient.create(order)
                .assertThat()
                .statusCode(HttpStatus.SC_CREATED)
                .and()
                .body("track", Matchers.notNullValue())
                .extract()
                .path("track");
    }

}
