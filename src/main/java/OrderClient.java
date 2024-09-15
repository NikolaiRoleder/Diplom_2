import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;
import pojo.Order;

import static io.restassured.RestAssured.given;

public class OrderClient {
    private static final String ORDER_PATH = "api/orders/";

    @Step("Создание заказа у незарегестрированных пользователей")
    public ValidatableResponse createOrder(Order order) {
        return given()
                .spec(RestClient.getBaseSpec())
                .body(order).log().all()
                .when()
                .post(ORDER_PATH)
                .then().log().all();
    }

    @Step("Создание заказа у зарегестрированных пользователей")
    public ValidatableResponse createOrder(Order order, String accessToken) {
        return given()
                .spec(RestClient.getBaseSpec())
                .header("Authorization", accessToken)
                .body(order).log().all()
                .when()
                .post(ORDER_PATH)
                .then().log().all();
    }

    @Step("Получение информации о заказах у незарегестрированных пользователей")
    public ValidatableResponse giveOrder(String accessToken) {
        return given()
                .spec(RestClient.getBaseSpec())
                .header("Authorization", accessToken)
                .when()
                .get(ORDER_PATH)
                .then().log().all();
    }

    @Step("Получение информации о заказах у зарегестрированных пользователей")
    public ValidatableResponse giveOrder() {
        return given()
                .spec(RestClient.getBaseSpec())
                .when()
                .get(ORDER_PATH)
                .then().log().all();
    }
}
