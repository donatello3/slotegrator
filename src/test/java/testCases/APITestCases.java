package testCases;

import dto.GetPlayerInfoDTO;
import helpers.DataGenerator;
import helpers.Http;
import io.github.artsok.ParameterizedRepeatedIfExceptionsTest;
import io.github.artsok.RepeatedIfExceptionsTest;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.provider.MethodSource;


import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.*;
import static settings.Constants.*;

import java.util.*;
import java.util.stream.Stream;

public class APITestCases extends TestCase {

    static Stream<Map<String, String>> optionalFields() {
        //Необязательные поля при создании игрока
        return Stream.of(
                Map.of(
                        CURRENCY_CODE_JSON, "RUB",
                        NAME_JSON, "John",
                        SURNAME_JSON, "Doe"
                ),
                Map.of(
                        CURRENCY_CODE_JSON, "RUB",
                        NAME_JSON, "Jack",
                        SURNAME_JSON, "Doe"
                ),
                Map.of(
                        CURRENCY_CODE_JSON, "RUB",
                        NAME_JSON, "Rust",
                        SURNAME_JSON, "Doe"
                ),
                Map.of(
                        CURRENCY_CODE_JSON, "RUB",
                        NAME_JSON, "Joe",
                        SURNAME_JSON, "Doe"
                ),
                Map.of(
                        CURRENCY_CODE_JSON, "RUB",
                        NAME_JSON, "Ross",
                        SURNAME_JSON, "Doe"
                ),
                Map.of(
                        CURRENCY_CODE_JSON, "RUB",
                        NAME_JSON, "Glen",
                        SURNAME_JSON, "Doe"
                ),
                Map.of(
                        CURRENCY_CODE_JSON, "RUB",
                        NAME_JSON, "Peter",
                        SURNAME_JSON, "Doe"
                ),
                Map.of(
                        CURRENCY_CODE_JSON, "RUB",
                        NAME_JSON, "Rebecka",
                        SURNAME_JSON, "Doe"
                ),
                Map.of(
                        CURRENCY_CODE_JSON, "RUB",
                        NAME_JSON, "Suizy",
                        SURNAME_JSON, "Doe"
                ),
                Map.of(
                        CURRENCY_CODE_JSON, "RUB",
                        NAME_JSON, "Daniel",
                        SURNAME_JSON, "Doe"
                ),
                Map.of(
                        CURRENCY_CODE_JSON, "RUB",
                        NAME_JSON, "Alex",
                        SURNAME_JSON, "Doe"
                ),
                Map.of(
                        CURRENCY_CODE_JSON, "RUB",
                        NAME_JSON, "Marty",
                        SURNAME_JSON, "Doe"
                )
        );
    }

    @RepeatedIfExceptionsTest(name = "Получить токен пользователя", suspend = suspend, repeats = repeats)
    @DisplayName("Получить токен пользователя")
    public void clientCredentialsGrantTest() {
        ValidatableResponse response = Http.requestTokenPOST(Map.of(
                EMAIL_JSON, TOKEN_AUTH_USERNAME,
                PASSWORD_JSON, TOKEN_AUTH_PASSWORD
        ));
        response.statusCode(201);
        response.body(ACCESS_TOKEN_JSON, not(empty()));
    }

    @ParameterizedRepeatedIfExceptionsTest(name = "Зарегистрировать игрока", suspend = suspend)
    @MethodSource("testCases.APITestCases#optionalFields")
    @DisplayName("Зарегистрировать игрока")
    public void registerNewPlayerTest(Map<String, String> optionalFields) {
        String username = DataGenerator.generateRandomUsername("tester");
        Map<String, String> newPlayerInfo = new java.util.HashMap<>(Map.of(
                USERNAME_JSON, username,
                PASSWORD_CHANGE_JSON, "erhokp",
                PASSWORD_REPEAT_JSON, "erhokp",
                EMAIL_JSON, username + "@gmail.com"
        ));
        newPlayerInfo.putAll(optionalFields);
        ValidatableResponse response = Http.requestPlayersPOST(newPlayerInfo);
        response.statusCode(201);
        response.body(matchesJsonSchemaInClasspath("schema/createPlayerResponse.json"));
    }

    @RepeatedIfExceptionsTest(name = "Запросить данные профиля игрока", suspend = suspend)
    @DisplayName("Запросить данные профиля игрока")
    public void getCurrentPlayerProfileInfo() {
        Map<String, String> playerData = Map.of(
                CURRENCY_CODE_JSON, "RUB",
                NAME_JSON, "John",
                SURNAME_JSON, "Doe"
        );
        Response createdPlayer = Http.registerNewPlayer(playerData);
        ValidatableResponse response = Http.getOnePlayerInfo(createdPlayer.path(EMAIL_JSON));
        response.statusCode(201);
        response.body(matchesJsonSchemaInClasspath("schema/getOnePlayerResponse.json"));
    }

    @Test
    @DisplayName("Запросить данные всех пользователей и отсортировать их по имени")
    public void getAllPlayersInfo() {
        ValidatableResponse response = Http.allPlayersInfoGET();
        response.statusCode(200);
        List<GetPlayerInfoDTO> resultList = Arrays.asList(response.extract().as(GetPlayerInfoDTO[].class));
        resultList.sort(new Comparator<GetPlayerInfoDTO>() {
            @Override
            public int compare(GetPlayerInfoDTO o1, GetPlayerInfoDTO o2) {
                if (o1.getName() == null && o2.getName() == null) {
                    return 0;
                } else if (o1.getName() == null) {
                    return -1;
                } else if (o2.getName() == null) {
                    return 1;
                } else {
                    return o1.getName().compareTo(o2.getName());
                }
            }
        });

        for (GetPlayerInfoDTO playerInfo : resultList) {
            System.out.println("Player id=" + playerInfo.getId() + ", name=" + playerInfo.getName());
        }
    }

    /**
    * Не будет работать из-за ошибки в методе delete
     * /api/automationTask/deleteOne/{id} на вход ожидается числовое значение, но id пользователей приходят в виде строки
     * из-за чего при передаче значения в запрос возвращается 404 ошибка "Not Found"
    * */
    @Test
    @DisplayName("Удалить всех ранее созданных пользователей и" +
            "запросить список всех пользователей, убедиться что он пустой")
    public void checkPlayersListIsEmpty() {
        ValidatableResponse response = Http.allPlayersInfoGET();
        List<GetPlayerInfoDTO> allPlayers = Arrays.asList(response.extract().as(GetPlayerInfoDTO[].class));
        for (GetPlayerInfoDTO player : allPlayers) {
            Http.requestPlayerDELETE(player.getId());
        }

        response = Http.allPlayersInfoGET();
        response.statusCode(200);
        response.assertThat().body(equalTo("[]"));
    }

}
