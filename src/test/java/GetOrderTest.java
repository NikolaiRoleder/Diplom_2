import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.apache.http.HttpStatus;
import org.junit.Before;
import org.junit.Test;
import pojo.Order;
import pojo.User;

import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.*;

public class GetOrderTest {
    private UserClient userClient;
    private User user;
    private Order order;
    private OrderClient orderClient;
    private String accessToken;

    @Before
    public void setUp() {
        userClient = new UserClient();
        user = UserData.getCorrectUserData();
        orderClient = new OrderClient();
    }

    @DisplayName("Получение истории заказов у залогиненого пользователя")
    @Test
    public void giveOrderLoginUserTest() {
        order = OrderData.getCorrectIngredientHash();
        //Создаем пользователя
        // и вычисляем access token для того, чтобы потом удалить пользователя, и передать в заказе
        ValidatableResponse createResponse = userClient.createUser(user);
        accessToken = createResponse.extract().path("accessToken");
        //Создаем заказ что бы проверить тело ответа
        orderClient.createOrder(order, accessToken);
        // Получаем список заказов конкретного пользователя
        ValidatableResponse giveOrderResponse = orderClient.giveOrder(accessToken);
        List<Map<String, String>> orderList = giveOrderResponse.extract().path("orders");
        int createStatusCode = giveOrderResponse.extract().statusCode();
        assertEquals(HttpStatus.SC_OK, createStatusCode);
        boolean created = giveOrderResponse.extract().path("success");
        assertTrue(created);
        assertThat(orderList, hasSize(1));
        // Удаляем пользователя
        UserClient.deleteUser(accessToken);
    }

    @DisplayName("Получение истории заказов у незалогиненого пользователя")
    @Test
    public void giveOrderNotLoginUserTest() {
        // Создаем некорректный accessToken, для прохождения проверки незарегестрированого пользователя
        ValidatableResponse giveErrorOrderResponse = orderClient.giveOrder();
        int createStatusCode = giveErrorOrderResponse.extract().statusCode();
        assertEquals(HttpStatus.SC_UNAUTHORIZED, createStatusCode);
        boolean created = giveErrorOrderResponse.extract().path("success");
        assertFalse(created);
        String errorMessage = giveErrorOrderResponse.extract().path("message");
        assertEquals("You should be authorised", errorMessage);
    }
}
