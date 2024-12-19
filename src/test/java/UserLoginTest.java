import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.apache.http.HttpStatus;
import org.junit.Before;
import org.junit.Test;
import pojo.User;
import pojo.UserCredentials;

import static org.junit.Assert.*;

public class UserLoginTest {
    private UserClient userClient;
    private User user;


    @Before
    public void setUp() {
        userClient = new UserClient();
        user = UserData.getCorrectUserData();
    }

    @DisplayName("Логин пользователя с коректными кредами")
    @Test
    public void userCanBeLogin() {
        // сначала регистрируем пользователя
        userClient.createUser(user);
        //Проверяем, что можем залогинится под кредами ранее созданного пользователя
        ValidatableResponse createResponse = userClient.loginUser(UserCredentials.from(user));
        int createStatusCode = createResponse.extract().statusCode();
        assertEquals(HttpStatus.SC_OK, createStatusCode);
        boolean created = createResponse.extract().path("success");
        assertTrue(created);
        // проверяем что в теле ответа есть accessToken и refreshToken
        String accessToken = createResponse.extract().path("accessToken");
        assertNotNull(accessToken);
        String refreshToken = createResponse.extract().path("refreshToken");
        assertNotNull(refreshToken);
        // Сравниваем параметры из тела ответа
        String actualUserName = createResponse.extract().path("user.name");
        String actualUserEmail = createResponse.extract().path("user.email");
        assertEquals(user.getName(), actualUserName);
        assertEquals(user.getEmail(), actualUserEmail);
        //удаляем данные о пользователе.
        UserClient.deleteUser(accessToken);
    }

    @DisplayName("Логин пользователя с некоректными кредами")
    @Test
    public void loginWithIncorrectCredential() {
        ValidatableResponse createErrorResponse = userClient.loginUser(UserCredentials.from(user));
        int createStatusCode = createErrorResponse.extract().statusCode();
        assertEquals(HttpStatus.SC_UNAUTHORIZED, createStatusCode);
        boolean created = createErrorResponse.extract().path("success");
        assertFalse(created);
        String errorMessage = createErrorResponse.extract().path("message");
        assertEquals("email or password are incorrect", errorMessage);
    }
}
