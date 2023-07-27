package helpers;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import settings.Constants;


import java.util.Map;

import static io.restassured.RestAssured.given;
import static settings.Constants.*;

public class Http {

    private static RequestSpecification requestSpec = RestAssured.given().baseUri(API_URL);

    public static ValidatableResponse requestTokenPOST(Map<String, String> body) {
        System.out.println("Making POST request to " + Constants.TOKEN_REQUEST_PATH + " with " + body);
        ValidatableResponse response = requestSpec
                .auth()
                .preemptive()
                .basic(Constants.HTTP_AUTH_USERNAME, HTTP_AUTH_PASSWORD)
                .contentType(ContentType.JSON)
                .body(body)
                .when()
                .post(Constants.TOKEN_REQUEST_PATH)
                .then();
        System.out.println("requestTokenPOST response - " + response.extract().response().asPrettyString());
        return response;
    }

    public static ValidatableResponse requestPlayersPOST(String authToken, Map<String, String> body) {
        System.out.println("Making POST request to " + Constants.PLAYERS_REQUEST_PATH + " with " + body + " \nand token " + authToken);
        ValidatableResponse response = requestSpec
                .auth()
                .preemptive()
                .oauth2(authToken)
                .contentType(ContentType.JSON)
                .body(body)
                .when()
                .post(Constants.PLAYERS_REQUEST_PATH)
                .then();
        System.out.println("requestPlayersPOST - " + response.extract().response().asPrettyString());
        return response;
    }

    public static ValidatableResponse allPlayersInfoGET(String authToken) {
        System.out.println("Making POST request to " + GET_ALL_PATH + " with " + " \nand token " + authToken);
        ValidatableResponse response = requestSpec
                .auth()
                .preemptive()
                .oauth2(authToken)
                .when()
                .get(Constants.GET_ALL_PATH)
                .then();
        System.out.println("allPlayersInfoGET - " + response.extract().response().asPrettyString());
        return response;
    }

    public static ValidatableResponse allPlayersInfoGET() {
        return allPlayersInfoGET(getGuestToken());
    }

    public static Response requestPlayersPOSTResponse(String authToken, Map<String, String> body) {
        System.out.println("Making POST request to " + Constants.PLAYERS_REQUEST_PATH + " with " + body + " \nand token " + authToken);
        Response response = requestSpec
                .auth()
                .preemptive()
                .oauth2(authToken)
                .contentType(ContentType.JSON)
                .body(body)
                .when()
                .post(Constants.PLAYERS_REQUEST_PATH);
        System.out.println("requestPlayersPOST - " + response.asPrettyString());
        return response;
    }

    public static ValidatableResponse requestPlayerDELETE(String authToken, String id) {
        System.out.println("Making DELETE request to " + PLAYER_DELETE_PATH + " with " + id + " \nand token " + authToken);
        ValidatableResponse response = requestSpec
                .auth()
                .preemptive()
                .oauth2(authToken)
                .when()
                .delete(PLAYER_DELETE_PATH + id)
                .then();
        System.out.println("requestPlayersPOST - " + response.extract().asPrettyString());
        return response;
    }

    public static ValidatableResponse requestPlayerDELETE(String id) {
        return requestPlayerDELETE(getGuestToken(), id);
    }

    public static ValidatableResponse requestPlayersPOST(Map<String, String> body) {
        return requestPlayersPOST(getGuestToken(), body);
    }

    public static ValidatableResponse getOnePlayerInfo(String authToken, String email) {
        System.out.println("Making POST request to " + GET_ONE_PATH + " for player  " + email);
        ValidatableResponse response = requestSpec
                .auth()
                .preemptive()
                .oauth2(authToken)
                .contentType(ContentType.JSON)
                .body(Map.of(EMAIL_JSON, email))
                .when()
                .post(Constants.GET_ONE_PATH)
                .then();
        System.out.println(response.extract().response().asPrettyString());
        return response;
    }

    public static ValidatableResponse getOnePlayerInfo(String email) {
        return getOnePlayerInfo(getGuestToken(), email);
    }

    public static Response registerNewPlayer(Map<String, String> optionalFields) {
        //Создает нового пользователя и возвращает его username
        System.out.println("Creating new player");
        String username = DataGenerator.generateRandomUsername("tester");
        Map<String, String> newPlayerInfo = new java.util.HashMap<>(Map.of(
                USERNAME_JSON, username,
                PASSWORD_CHANGE_JSON, PASSWORD_BASE64,
                PASSWORD_REPEAT_JSON, PASSWORD_BASE64,
                EMAIL_JSON, username + "@gmail.com"
        ));
        newPlayerInfo.putAll(optionalFields);
        return requestPlayersPOST(newPlayerInfo).extract().response();
    }

    public static String getGuestToken() {
        Map<String, String> requestBody = Map.of(
                EMAIL_JSON, TOKEN_AUTH_USERNAME,
                PASSWORD_JSON, TOKEN_AUTH_PASSWORD
        );
        return getToken(requestBody);
    }

    private static String getToken(Map<String, String> requestBody) {
        System.out.println("Requesting token with " + requestBody);

        ValidatableResponse response = requestTokenPOST(requestBody);
        String token = response.extract().response().path(ACCESS_TOKEN_JSON);
        System.out.println(token);
        return token;
    }

}
