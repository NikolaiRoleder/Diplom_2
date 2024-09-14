import io.qameta.allure.junit4.DisplayName;
import pojo.User;
import io.restassured.response.ValidatableResponse;
import org.apache.http.HttpStatus;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import pojo.UserCredentials;

import static org.junit.Assert.*;

public class UserRegistrationTest {
    private UserClient userClient;
    private User user;
    private String accessToken;

    @Before
    public void setUp() {
        userClient = new UserClient();
        user = UserData.getRandomUser();
}
    @After
    public void cleanUp(){UserClient.deleteUser(accessToken);}

    @DisplayName("Регистрация пользователя")
    @Test
    public void userCanBeCreated(){
        ValidatableResponse createResponse = userClient.createUser(user);
        int createStatusCode = createResponse.extract().statusCode();
        assertEquals(HttpStatus.SC_OK, createStatusCode);
        boolean created = createResponse.extract().path("success");
        assertTrue(created);
        // проверяем что в теле ответа есть accessToken и refreshToken
        accessToken = createResponse.extract().path("accessToken");
        assertNotNull(accessToken);
        String refreshToken = createResponse.extract().path("refreshToken");
        assertNotNull(refreshToken);
    }
    @DisplayName("Повторная регистрация пользователя с теми же кредами")
    @Test
    public void impossibleCreateSameUser(){
        //Создаем пользователя
        userClient.createUser(user);

        //Проверяем, что можем залогиниться под пользователем(значит он создан)
        // и вычисляем access token для того, чтобы потом удалить пользователя
        ValidatableResponse loginResponse = userClient.loginUser(UserCredentials.from(user));
        accessToken = loginResponse.extract().path("accessToken");

        // Повторно пытаемся создать пользователя с такими же данными и проверяем ответ
        ValidatableResponse createErrorResponse = userClient.createUser(user);
        int createStatusCode = createErrorResponse.extract().statusCode();
        assertEquals(HttpStatus.SC_FORBIDDEN, createStatusCode);
        boolean created = createErrorResponse.extract().path("success");
        assertFalse(created);
        String errorMessage = createErrorResponse.extract().path("message");
        assertEquals("User already exists", errorMessage);
    }
}

