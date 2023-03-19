package HttpMrthods;

import io.restassured.response.Response;
import Model.Ingredients;

import static io.restassured.RestAssured.given;

public class Order {

    public Response createOrder(Ingredients ingredients, String userToken) {

        return given()
                .header("Content-type", "application/json")
                .auth().oauth2(userToken)
                .and()
                .body(ingredients)
                .when()
                .post("https://stellarburgers.nomoreparties.site/api/orders");

    }

    public Response createOrderNotAuthorization(Ingredients ingredients) {

        return given()
                .header("Content-type", "application/json")
                .and()
                .body(ingredients)
                .when()
                .post("https://stellarburgers.nomoreparties.site/api/orders");

    }

    public Response getOrderUser(String userToken) {

        return given()
                .auth().oauth2(userToken)
                .get("https://stellarburgers.nomoreparties.site/api/orders");

    }
}

