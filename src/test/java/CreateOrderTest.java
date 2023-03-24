import com.github.javafaker.Faker;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import Model.Ingredients;
import Model.UserDetails;
import HttpMrthods.User;
import HttpMrthods.Order;
import static Model.Constants.*;
import static org.hamcrest.Matchers.equalTo;

public class CreateOrderTest {


    String[] userToken;

    UserDetails userDetails;
    Ingredients ingredients;
    Ingredients ingredientsMany;
    Ingredients ingredientsIsEmpty;
    Ingredients ingredientsWithNoValidHash;

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
        ingredients = new Ingredients (new String[]{"61c0c5a71d1f82001bdaaa6d","61c0c5a71d1f82001bdaaa70"});
        ingredientsMany =  new Ingredients(
                new String[]{"61c0c5a71d1f82001bdaaa6d","61c0c5a71d1f82001bdaaa70", "61c0c5a71d1f82001bdaaa6f",
                        "61c0c5a71d1f82001bdaaa72", "61c0c5a71d1f82001bdaaa73", "61c0c5a71d1f82001bdaaa76"});
        ingredientsIsEmpty =  new Ingredients(new String[]{});
        ingredientsWithNoValidHash = new Ingredients (new String[]{"61c0c5a71d1f82001bdaaa6dфф","61c0c5a71d1f82001bdaaa70фф"});
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
    @DisplayName("Позитивная проверка создания бургера с авторизацией")
    public void createOrderTest () {

        Order order = new Order();
        response = order.createOrder(ingredients,
                userToken[1]);

        response.then()
                .statusCode(200)
                .and()
                .assertThat().body("success", equalTo(true));

    }

    @Test
    @DisplayName("Позитивная проверка создания бургера без авторизацией")
    public void createOrderTestNotAuthorization () {

        Order order = new Order();
        response = order.createOrderNotAuthorization(ingredients);

        response.then()
                .statusCode(200)
                .and()
                .assertThat().body("success", equalTo(true));

    }

    @Test
    @DisplayName("Позитивная проверка создания бургера с разными ингридиентами")
    public void createOrderTestWithOtherIngredients () {

        Order order = new Order();
        response = order.createOrderNotAuthorization(ingredientsMany);

        response.then()
                .statusCode(200)
                .and()
                .assertThat().body("success", equalTo(true));
    }

    @Test
    @DisplayName("Негативная проверка создания бургера без ингридиентов")
    public void createOrderTestNotIngredients () {

        Order order = new Order();
        response = order.createOrderNotAuthorization(ingredientsIsEmpty);

        response.then()
                .statusCode(400)
                .and()
                .assertThat().body("message", equalTo("Ingredient ids must be provided"));

    }

    @Test
    @DisplayName("Негативная проверка создания бургера c невалидным хешем")
    public void createOrderTestInvalidHash () {

        Order order = new Order();
        response = order.createOrderNotAuthorization(ingredientsWithNoValidHash);

        response.then()
                .statusCode(500);

    }
}
