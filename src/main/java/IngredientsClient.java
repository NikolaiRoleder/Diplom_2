import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;
import pojo.Order;

import static io.restassured.RestAssured.given;

public class IngredientsClient {
    private static final String INGREDIENTS_PATH = "api/ingredients/";

    @Step("Получение списка ингредиентов")
    public ValidatableResponse getIngredients() {
        return given()
                .spec(RestClient.getBaseSpec())
                .when()
                .get(INGREDIENTS_PATH)
                .then().log().all();
    }
}
