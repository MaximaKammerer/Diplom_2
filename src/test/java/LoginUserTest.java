import com.github.javafaker.Faker;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.*;
import Model.UserDetails;
import static Model.Constants.*;
import HttpMrthods.User;

import static org.hamcrest.Matchers.equalTo;

public class LoginUserTest {

    UserDetails userDetails;
    User user;
    Response response;

    @Before
    @DisplayName("Создаем тестового пользователя")
    public void setup() {
        Faker faker = new Faker();
        String name = faker.name().firstName();
        String password = name + "12345";
        String email = name + "@yandex.ru";
        userDetails = new UserDetails(name, password, email);
        RestAssured.baseURI = BASE_URL;
        user = new User();
        user.createUser(userDetails);

    }

    @After
    @DisplayName("Удаляем тестового пользователя")
    public void clearUser () {

        userDetails.setEmail(userDetails.getEmail());
        userDetails.setPassword(userDetails.getPassword());
        Response responseLogin = user.authorizationUser(userDetails);
        String accessToken = responseLogin.body().jsonPath().getString("accessToken");
        String[] userToken = accessToken.split(" ");
        user.deleteUser(userToken[1]);
    }

    @Test
    @DisplayName("Позитивная проверка авторизации пользователя")
    public void authorizationUser () {

        response = user.authorizationUser(userDetails);
        response.then()
                .statusCode(200)
                .and()
                .assertThat().body("success", equalTo(true));

    }

    @Test
    @DisplayName("Негативная проверка авторизации пользователя с не корректным паролем")
    public void authorizationUserBadPassword () {
        UserDetails userDetailsWithBadPass;
        userDetailsWithBadPass = userDetails;
        userDetailsWithBadPass.setPassword("werw");
        response = user.authorizationUser(userDetailsWithBadPass);
        response.then()
                .statusCode(401)
                .and()
                .assertThat().body("message", equalTo("email or password are incorrect"));

    }

    @Test
    @DisplayName("Негативная проверка авторизации пользователя с не корректным email")
    public void authorizationUserBadEmail () {

        userDetails.setEmail("Ganzalis132333332@mail.ru");
        response = user.authorizationUser(userDetails);
        response.then()
                .statusCode(401)
                .and()
                .assertThat().body("message", equalTo("email or password are incorrect"));

    }
}
