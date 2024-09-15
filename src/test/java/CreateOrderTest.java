import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.apache.http.HttpStatus;
import org.junit.Before;
import org.junit.Test;
import pojo.Order;
import pojo.User;

import static org.junit.Assert.*;

public class CreateOrderTest {
    private UserClient userClient;
    private User user;
    private Order order;
    private OrderClient orderClient;
    private String accessToken;

    @Before
    public void setUp() {
        userClient = new UserClient();
        user = UserData.getRandomUser();
        orderClient = new OrderClient();
    }

    @DisplayName("Создание заказа с токеном и ингредиентами")
    @Test
    public void createOrderWithTokenAndIngredients() {
        order = OrderData.getCorrectIngredientHash();
        //Создаем пользователя
        // и вычисляем access token для того, чтобы потом удалить пользователя, и передать в заказе
        ValidatableResponse createResponse = userClient.createUser(user);
        accessToken = createResponse.extract().path("accessToken");
        //Создаем заказ
        ValidatableResponse createOrderResponse = orderClient.createOrder(order, accessToken);
        int createStatusCode = createOrderResponse.extract().statusCode();
        assertEquals(HttpStatus.SC_OK, createStatusCode);
        boolean created = createOrderResponse.extract().path("success");
        assertTrue(created);
        String actualOrderName = createOrderResponse.extract().path("name");
        int actualOrderNumber = createOrderResponse.extract().path("order.number");
        assertNotNull(actualOrderName);
        assertNotEquals(0, actualOrderNumber);
        // Удаляем пользователя
        UserClient.deleteUser(accessToken);
    }

    @DisplayName("Создание заказа без токена с ингридиентами")
    @Test
    public void createOrderWithoutTokenAndWithIngredient() {
        order = OrderData.getCorrectIngredientHash();
        //Создаем заказ
        ValidatableResponse createOrderResponse = orderClient.createOrder(order);
        int createStatusCode = createOrderResponse.extract().statusCode();
        assertEquals(HttpStatus.SC_OK, createStatusCode);
        boolean created = createOrderResponse.extract().path("success");
        assertTrue(created);
        String actualOrderName = createOrderResponse.extract().path("name");
        int actualOrderNumber = createOrderResponse.extract().path("order.number");
        assertNotNull(actualOrderName);
        assertNotEquals(0, actualOrderNumber);
    }

    @DisplayName("Создание заказа с некорректными хешами ингредиентов у залогиненого пользователя")
    @Test
    public void createOrderWithIncorrectHashLoginUser() {
        order = OrderData.getIncorrectOrder();
        //Создаем пользователя
        // и вычисляем access token для того, чтобы потом удалить пользователя, и передать в заказе
        ValidatableResponse createResponse = userClient.createUser(user);
        accessToken = createResponse.extract().path("accessToken");
        //Создаем заказ
        ValidatableResponse createOrderResponse = orderClient.createOrder(order, accessToken);
        int createStatusCode = createOrderResponse.extract().statusCode();
        assertEquals(HttpStatus.SC_INTERNAL_SERVER_ERROR, createStatusCode);
        // Удаляем пользователя
        UserClient.deleteUser(accessToken);
    }

    @DisplayName("Создание заказа с некорректными хешами ингредиентов у залогиненого пользователя")
    @Test
    public void createOrderWithIncorrectHashNotLoginUser() {
        order = OrderData.getIncorrectOrder();
        //Создаем заказ
        ValidatableResponse createOrderResponse = orderClient.createOrder(order);
        int createStatusCode = createOrderResponse.extract().statusCode();
        assertEquals(HttpStatus.SC_INTERNAL_SERVER_ERROR, createStatusCode);
    }

    @DisplayName("Создание заказа без ингредиентов у залогиненого пользователя")
    @Test
    public void createOrderWithoutIngredientsLoginUser() {
        order = OrderData.getOrderWithoutIngredients();
        //Создаем пользователя
        // и вычисляем access token для того, чтобы потом удалить пользователя, и передать в заказе
        ValidatableResponse createResponse = userClient.createUser(user);
        accessToken = createResponse.extract().path("accessToken");
        //Создаем заказ
        ValidatableResponse createErrorResponse = orderClient.createOrder(order, accessToken);
        int createStatusCode = createErrorResponse.extract().statusCode();
        assertEquals(HttpStatus.SC_BAD_REQUEST, createStatusCode);
        boolean created = createErrorResponse.extract().path("success");
        assertFalse(created);
        String errorMessage = createErrorResponse.extract().path("message");
        assertEquals("Ingredient ids must be provided", errorMessage);
        // Удаляем пользователя
        UserClient.deleteUser(accessToken);
    }

    @DisplayName("Создание заказа без ингредиентов у незалогиненого пользователя")
    @Test
    public void createOrderWithoutIngredientsNotLoginUser() {
        order = OrderData.getOrderWithoutIngredients();
        ValidatableResponse createErrorResponse = orderClient.createOrder(order);
        int createStatusCode = createErrorResponse.extract().statusCode();
        assertEquals(HttpStatus.SC_BAD_REQUEST, createStatusCode);
        boolean created = createErrorResponse.extract().path("success");
        assertFalse(created);
        String errorMessage = createErrorResponse.extract().path("message");
        assertEquals("Ingredient ids must be provided", errorMessage);
    }
}