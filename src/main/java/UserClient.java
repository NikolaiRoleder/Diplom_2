import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;
import pojo.User;
import pojo.UserCredentials;

import static io.restassured.RestAssured.given;


public class UserClient {
    private static final String CREATE_USER = "api/auth/register/";
    private static final String LOGIN_USER = "api/auth/login/";
    private static final String USER_ENDPOINT = "api/auth/user/";

    @Step("Регистрация пользователя")
    public ValidatableResponse createUser(User user) {
        return given()
                .spec(RestClient.getBaseSpec())
                .body(user)
                .when().log().all()
                .post(CREATE_USER)
                .then().log().all();
    }

    @Step("Логин пользователя")
    public ValidatableResponse loginUser(UserCredentials credentials) {
        return given()
                .spec(RestClient.getBaseSpec())
                .body(credentials)
                .when().log().all()
                .post(LOGIN_USER)
                .then().log().all();
    }
    @Step("Получение информации о пользователе")
    public ValidatableResponse getUserInformation(String accessToken) {
        return given()
                .spec(RestClient.getBaseSpec())
                .header("Authorization", accessToken)
                .when()
                .get(USER_ENDPOINT)
                .then().log().all();
    }
    @Step("Изменение информации о зарегестрированом пользователе")
    public ValidatableResponse changeUserInformation(User credentials, String accessToken) {
        return given()
                .spec(RestClient.getBaseSpec())
                .header("Authorization", accessToken)
                .body(credentials)
                .when().log().all()
                .patch(USER_ENDPOINT)
                .then().log().all();
    }
    @Step("Получение информации о не зарегестрированом пользователе")
    public ValidatableResponse changeUserInformation(User credentials) {
        return given()
                .spec(RestClient.getBaseSpec())
                .body(credentials)
                .when().log().all()
                .patch(USER_ENDPOINT)
                .then().log().all();
    }
    @Step("Удаление пользователя")
    public static void deleteUser(String accessToken) {
        given()
                .spec(RestClient.getBaseSpec())
                .header("Authorization", accessToken)
                .when().log().all()
                .delete(USER_ENDPOINT)
                .then().log().all();
    }
}
