package HttpMrthods;

import io.restassured.response.Response;
import Model.UserDetails;
import static Model.Constants.BASE_URL;

import static io.restassured.RestAssured.given;

public class User {

    public Response createUser(UserDetails userDetails) {

        return given()
                .header("Content-type", "application/json")
                .and()
                .body(userDetails)
                .when()
                .post("/api/auth/register");

    }

    public Response authorizationUser(UserDetails userDetails) {

        return given()
                .header("Content-type", "application/json")
                .and()
                .body(userDetails)
                .when()
                .post("/api/auth/login");

    }

    public Response updateUser(UserDetails userDetails, String userToken) {

        return given()
                .header("Content-type", "application/json")
                .auth().oauth2(userToken)
                .and()
                .body(userDetails)
                .when()
                .patch("/api/auth/user");

    }

    public Response deleteUser(String userToken) {

        return given()
                .header("Content-type", "application/json")
                .auth().oauth2(userToken)
                .when()
                .delete("api/auth/user")
                .then()
                .statusCode(202)
                .and()
                .extract()
                .path("ok");

    }
}

