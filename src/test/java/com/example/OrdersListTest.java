package com.example;

import com.client.OrderClient;
import com.dto.Order;
import io.qameta.allure.junit4.DisplayName;
import org.apache.http.HttpStatus;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static com.dto.OrderGenerator.getRandomOrder;

public class OrdersListTest {
    private OrderClient orderClient;
    private Order order;
    private Integer id;

    @Before
    public void setUp() {
        orderClient = new OrderClient();
        order = getRandomOrder();
        id = orderClient.create(order)
                .assertThat()
                .statusCode(HttpStatus.SC_CREATED)
                .and()
                .body("track", Matchers.notNullValue())
                .extract()
                .path("track");
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


    //Проверь, что в тело ответа возвращается список заказов.
    @Test
    @DisplayName("Check getting orders list")
    public void getOrdersListTest() {
        orderClient.getOrderList()
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .and()
                .body("orders", Matchers.notNullValue());
    }
}
