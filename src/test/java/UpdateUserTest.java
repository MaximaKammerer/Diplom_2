import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import Model.UserDetails;
import HttpMrthods.User;

import static Model.Constants.*;
import static org.hamcrest.Matchers.equalTo;

public class UpdateUserTest {

    String[] userToken;

    UserDetails userDetails;
    User user;
    Response response;

    @Before
    @DisplayName("Создаем тестового пользователя")
    public void setup() {

        RestAssured.baseURI = BASE_URL;
        userDetails = new UserDetails(NAME, PASSWORD, EMAIL);
        user = new User();
        user.createUser(userDetails);
        Response responseLogin = user.authorizationUser(userDetails);
        String accessToken = responseLogin.body().jsonPath().getString("accessToken");
        userToken = accessToken.split(" ");

    }

    @After
    @DisplayName("Удаляем тестового пользователя")
    public void clearUser () {

        user.deleteUser(userToken[1]);

    }

    @Test
    @DisplayName("Позитивная проверка изменение пользователя с авторизацией")
    public void updateEmailWithAuthorization () {

        userDetails.setEmail("Ganzalis@mail.ru");
        response = user.updateUser(userDetails, userToken[1]);
        response.then()
                .statusCode(200)
                .and()
                .assertThat().body("success", equalTo(true));

    }

    @Test
    @DisplayName("Негативная проверка изменение пользователя без авторизации")
    public void updateEmailNotAuthorization () {

        userDetails.setEmail("Ganzalis@mail.ru");
        response = user.updateUser(userDetails, "");
        response.then()
                .statusCode(401)
                .and()
                .assertThat().body("message", equalTo("You should be authorised"));

    }
}
