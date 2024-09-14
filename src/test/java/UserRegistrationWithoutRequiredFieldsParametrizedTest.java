import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.apache.http.HttpStatus;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import pojo.User;

import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)

public class UserRegistrationWithoutRequiredFieldsParametrizedTest {
    private UserClient userClient;
    private User user;
    private final String email;
    private final String password;
    private final String name;

    @Before
    public void setUp() {
        userClient = new UserClient();
        user = new User(email,password,name);
    }

    public UserRegistrationWithoutRequiredFieldsParametrizedTest(String email, String password, String name) {
        this.email = email;
        this.password = password;
        this.name = name;
    }

    @Parameterized.Parameters
    public static Collection<Object[]> getUserData() {
        return Arrays.asList(new Object[][]{
                {null, "draft", "craft"},
                {"puska@yandex.ru", "nraft", null},
                {"duska@yandex.ru", null, "sraft"},
                {"", "praft", "jraft"},
                {"muska@yandex.ru", "", "praft"},
                {"quska@yandex.ru", "graft", ""}
        });
    }

    @DisplayName("Регистрация пользователя с различными незаполнеными обязательными полями")
    @Test
    public void errorValidationFieldCreateUserTest() {
        ValidatableResponse createErrorResponse = userClient.createUser(user);
        int createStatusCode = createErrorResponse.extract().statusCode();
        assertEquals(HttpStatus.SC_FORBIDDEN, createStatusCode);
        String errorMessage = createErrorResponse.extract().path("message");
        assertEquals("Email, password and name are required fields", errorMessage);

    }
}

