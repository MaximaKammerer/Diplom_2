package HttpMrthods;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import Model.Ingredients;

import static io.restassured.RestAssured.given;

public class Order {

    @Step("Создание заказа")
    public Response createOrder(Ingredients ingredients, String userToken) {

        return given()
                .header("Content-type", "application/json")
                .auth().oauth2(userToken)
                .and()
                .body(ingredients)
                .when()
                .post("/api/orders");

    }
    @Step("Создание заказа без авторизации")
    public Response createOrderNotAuthorization(Ingredients ingredients) {

        return given()
                .header("Content-type", "application/json")
                .and()
                .body(ingredients)
                .when()
                .post("/api/orders");

    }
    @Step("Получение заказов пользователя")
    public Response getOrderUser(String userToken) {

        return given()
                .auth().oauth2(userToken)
                .get("" +
                        "/api/orders");

    }
}

