import HttpMrthods.User;
import static Model.Constants.*;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import Model.UserDetails;

import java.util.Objects;

import static org.hamcrest.Matchers.equalTo;

public class CreateUserTest {

    UserDetails userDetails;
    User user;
    Response response;

    @Before
    public void setup() {

        RestAssured.baseURI = BASE_URL;
        userDetails = new UserDetails(NAME, PASSWORD, EMAIL);
        user = new User();

    }

    @After
    @DisplayName("Удаляем тестового пользователя")
    public void clearUser () {

        if (!Objects.equals(userDetails.getEmail(), "") && !Objects.equals(userDetails.getName(), "") && !Objects.equals(userDetails.getPassword(), "")) {

            Response responseLogin = user.authorizationUser(userDetails);
            String accessToken = responseLogin.body().jsonPath().getString("accessToken");
            String[] userToken = accessToken.split(" ");
            user.deleteUser(userToken[1]);

        }
    }

    @Test
    @DisplayName("Позитивная проверка на создание пользователя")
    public void createNewUser () {

        response = user.createUser(userDetails);
        response.then()
                .statusCode(200)
                .and()
                .assertThat().body("success", equalTo(true));

    }

    @Test
    @DisplayName("Негативная проверка на создание пользователя, который уже зарегистрирован")
    public void createDoubleUser () {

        user.createUser(userDetails);
        response = user.createUser(userDetails);
        response.then()
                .statusCode(403)
                .and()
                .assertThat().body("message", equalTo("User already exists"));

    }

    @Test
    @DisplayName("Негативная проверка на создание пользователя с пустым полем Name")
    public void createNewUserWithoutName () {

        userDetails.setName("");
        response = user.createUser(userDetails);
        response.then()
                .statusCode(403)
                .and()
                .assertThat().body("message", equalTo("Email, password and name are required fields"));

    }

    @Test
    @DisplayName("Негативная проверка на создание пользователя с пустым полем Password")
    public void createNewUserWithoutPassword () {

        userDetails.setPassword("");
        response = user.createUser(userDetails);
        response.then()
                .statusCode(403)
                .and()
                .assertThat().body("message", equalTo("Email, password and name are required fields"));

    }

    @Test
    @DisplayName("Негативная проверка на создание пользователя с пустым полем Email")
    public void createNewUserWithoutEmail () {

        userDetails.setEmail("");
        response = user.createUser(userDetails);
        response.then()
                .statusCode(403)
                .and()
                .assertThat().body("message", equalTo("Email, password and name are required fields"));

    }
}
