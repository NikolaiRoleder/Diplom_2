import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.apache.http.HttpStatus;
import org.junit.Before;
import org.junit.Test;
import pojo.User;
import pojo.UserCredentials;

import static org.junit.Assert.*;

public class UserChangeCredentialsTest {
    private UserClient userClient;
    private User user;
    private User changeUser;

    @Before
    public void setUp() {
        userClient = new UserClient();
        user = UserData.getRandomUser();
        changeUser = UserData.getChangeUserData();
    }

    @DisplayName("Обновление информации о пользователе с токеном авторизации")
    @Test
    public void loginUserChangeCredentialsTest() {
        String accessToken;
        //Создаем пользователя
        // и вычисляем access token для того, чтобы потом удалить пользователя, и передать ручку для изменения кредов
        ValidatableResponse createResponse = userClient.createUser(user);
        accessToken = createResponse.extract().path("accessToken");
        // Дергаем ручку изменения информации о пользователе
        ValidatableResponse changeInformationResponse = userClient.changeUserInformation(changeUser, accessToken);
        int changeStatusCode = changeInformationResponse.extract().statusCode();
        assertEquals(HttpStatus.SC_OK, changeStatusCode);
        assertTrue(changeInformationResponse.extract().path("success"));
        // Сравниваем параметры из тела ответа
        String actualChangeUserName = changeInformationResponse.extract().path("user.name");
        String actualChangeUserEmail = changeInformationResponse.extract().path("user.email");
        assertEquals(changeUser.getName(), actualChangeUserName);
        assertEquals(changeUser.getEmail(), actualChangeUserEmail);
        // Пробуем залогинится с новым паролем и email, потому что в теле ответа новый пароль не возвращается.
        ValidatableResponse loginResponse = userClient.loginUser(UserCredentials.from(changeUser));
        int loginStatusCode = loginResponse.extract().statusCode();
        assertEquals(HttpStatus.SC_OK, loginStatusCode);
        //удаляем данные о пользователе.
        UserClient.deleteUser(accessToken);
    }

    @DisplayName("Обновление информации о пользователе без токена авторизации")
    @Test
    public void notLoginUserChangeCredentialsTest() {
        ValidatableResponse changeInformationErrorResponse = userClient.changeUserInformation(changeUser);
        int changeStatusCode = changeInformationErrorResponse.extract().statusCode();
        assertEquals(HttpStatus.SC_UNAUTHORIZED, changeStatusCode);
        assertFalse(changeInformationErrorResponse.extract().path("success"));
        String errorMessage = changeInformationErrorResponse.extract().path("message");
        assertEquals("You should be authorised", errorMessage);
    }
}

