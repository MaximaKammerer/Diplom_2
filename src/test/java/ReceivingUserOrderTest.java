import com.github.javafaker.Faker;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import Model.UserDetails;
import HttpMrthods.User;
import HttpMrthods.Order;

import static Model.Constants.*;
import static org.hamcrest.Matchers.equalTo;

public class ReceivingUserOrderTest {

    String[] userToken;

    UserDetails userDetails;
    User user;
    Response response;

    @Before
    @DisplayName("Создаем тестового пользователя")
    public void setup() {

        RestAssured.baseURI = BASE_URL;
        Faker faker = new Faker();
        String name = faker.name().firstName();
        String password = name + "12345";
        String email = name + "@yandex.ru";
        userDetails = new UserDetails(name, password, email);
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
    @DisplayName("Позитивная проверка получение заказов авторизованного пользователя")
    public void receivingOrdersAuthorizedUser () {

        Order order = new Order();
        response = order.getOrderUser(userToken[1]);

        response.then()
                .statusCode(200)
                .and()
                .assertThat().body("success", equalTo(true));

    }

    @Test
    @DisplayName("Негативная проверка получение заказов не авторизованного пользователя")
    public void receivingOrdersNotAuthorizedUser () {

        Order order = new Order();
        response = order.getOrderUser("");

        response.then()
                .statusCode(401)
                .and()
                .assertThat().body("success", equalTo(false));

    }
}
