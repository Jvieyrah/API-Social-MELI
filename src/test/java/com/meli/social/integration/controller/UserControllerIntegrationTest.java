package com.meli.social.integration.controller;

import com.meli.social.user.dto.UserSimpleDTO;
import com.meli.social.user.inter.UserFollowJpaRepository;
import com.meli.social.user.inter.UserJpaRepository;
import com.meli.social.user.model.User;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@DisplayName("Testes de Integração - UserController com HSQLDB")
class UserControllerIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private UserJpaRepository userRepository;

    @Autowired
    private UserFollowJpaRepository userFollowRepository;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        RestAssured.basePath = "/users";
        userFollowRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("Deve criar um novo usuário com sucesso")
    void shouldCreateNewUserSuccessfully() {
        Map<String, String> request = new HashMap<>();
        request.put("userName", "joaosilva");

        UserSimpleDTO response = given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post()
                .then()
                .statusCode(201)
                .contentType(ContentType.JSON)
                .body("userId", notNullValue())
                .body("userName", equalTo("joaosilva"))
                .extract()
                .as(UserSimpleDTO.class);

        assertThat(response.getUserName()).isEqualTo("joaosilva");
        assertThat(userRepository.existsByUserName("joaosilva")).isTrue();
    }

    @Test
    @DisplayName("Deve retornar erro 400 quando username for nulo")
    void shouldReturn400WhenUsernameIsNull() {
        Map<String, String> request = new HashMap<>();
        request.put("userName", null);

        given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post()
                .then()
                .statusCode(400)
                .body("status", equalTo(400))
                .body("error", equalTo("Bad Request"))
                .body("message", equalTo("Nome do usuário não pode ser vazio"))
                .body("timestamp", notNullValue());

        assertThat(userRepository.count()).isZero();
    }

    @Test
    @DisplayName("Deve retornar erro 400 quando username já existe")
    void shouldReturn400WhenUsernameAlreadyExists() {
        Map<String, String> request = new HashMap<>();
        request.put("userName", "mariasantos");

        given().contentType(ContentType.JSON).body(request).post().then().statusCode(201);

        given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post()
                .then()
                .statusCode(400)
                .body("message", equalTo(
                        "Usuário já existe: mariasantos"));

        assertThat(userRepository.count()).isEqualTo(1);
    }

    @Test
    @DisplayName("Deve criar múltiplos usuários")
    void shouldCreateMultipleUsers() {
        String[] usernames = {"user1", "user2", "user3"};

        for (String username : usernames) {
            Map<String, String> request = new HashMap<>();
            request.put("userName", username);

            given()
                    .contentType(ContentType.JSON)
                    .body(request)
                    .post()
                    .then()
                    .statusCode(201);
        }

        assertThat(userRepository.count()).isEqualTo(3);
    }

    @Test
    @DisplayName("Deve retornar a lista de following do usuário")
    void shouldReturnUserFollowingList() {
        User userA = createAndSaveUser("usera");
        User userB = createAndSaveUser("userb");
        User userC = createAndSaveUser("userc");

        given().when().post("/{userId}/follow/{userIdToFollow}", userA.getUserId(), userB.getUserId()).then().statusCode(200);
        given().when().post("/{userId}/follow/{userIdToFollow}", userA.getUserId(), userC.getUserId()).then().statusCode(200);

        given()
                .when()
                .get("/{userId}/followed/list", userA.getUserId())
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("userId", is(userA.getUserId()))
                .body("userName", is(userA.getUserName()))
                .body("followed", hasSize(2))
                .body("followed.userId", containsInAnyOrder(userB.getUserId(), userC.getUserId()))
                .body("followed.userName", containsInAnyOrder(userB.getUserName(), userC.getUserName()));
    }

    @Test
    @DisplayName("Deve paginar a lista de following do usuário")
    void shouldPaginateUserFollowingList() {
        User userA = createAndSaveUser("usera");
        User userB = createAndSaveUser("userb");
        User userC = createAndSaveUser("userc");

        given().when().post("/{userId}/follow/{userIdToFollow}", userA.getUserId(), userB.getUserId()).then().statusCode(200);
        given().when().post("/{userId}/follow/{userIdToFollow}", userA.getUserId(), userC.getUserId()).then().statusCode(200);

        given()
                .when()
                .get("/{userId}/followed/list?page=0&size=1", userA.getUserId())
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("userId", is(userA.getUserId()))
                .body("userName", is(userA.getUserName()))
                .body("followed", hasSize(1));

        given()
                .when()
                .get("/{userId}/followed/list?page=1&size=1", userA.getUserId())
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("userId", is(userA.getUserId()))
                .body("userName", is(userA.getUserName()))
                .body("followed", hasSize(1));
    }

    @Test
    @DisplayName("Deve retornar a lista de following do usuário em ordem alfabética cescente")
    void shouldReturnUserFollowingList_ascending() {
        User userA = createAndSaveUser("usera");
        User userB = createAndSaveUser("ana");
        User userC = createAndSaveUser("bruno");

        given().when().post("/{userId}/follow/{userIdToFollow}", userA.getUserId(), userB.getUserId()).then().statusCode(200);
        given().when().post("/{userId}/follow/{userIdToFollow}", userA.getUserId(), userC.getUserId()).then().statusCode(200);

        given()
                .when()
                .get("/{userId}/followed/list?order=name_asc", userA.getUserId())
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("userId", is(userA.getUserId()))
                .body("userName", is(userA.getUserName()))
                .body("followed", hasSize(2))
                .body("followed[0].userName", is("ana"))
                .body("followed[1].userName", is("bruno"));
    }

    @Test
    @DisplayName("Deve retornar a lista de following do usuário em ordem alfabética decrescente")
    void shouldReturnUserFollowingList_descending() {
        User userA = createAndSaveUser("usera");
        User userB = createAndSaveUser("ana");
        User userC = createAndSaveUser("bruno");

        given().when().post("/{userId}/follow/{userIdToFollow}", userA.getUserId(), userB.getUserId()).then().statusCode(200);
        given().when().post("/{userId}/follow/{userIdToFollow}", userA.getUserId(), userC.getUserId()).then().statusCode(200);

        given()
                .when()
                .get("/{userId}/followed/list?order=name_desc", userA.getUserId())
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("userId", is(userA.getUserId()))
                .body("userName", is(userA.getUserName()))
                .body("followed", hasSize(2))
                .body("followed[0].userName", is("bruno"))
                .body("followed[1].userName", is("ana"));
    }

    @Test
    @DisplayName("Deve retornar 400 ao buscar following com order inválido")
    void shouldReturn400WhenGetFollowingWithInvalidOrder() {
        User userA = createAndSaveUser("usera");
        User userB = createAndSaveUser("ana");
        User userC = createAndSaveUser("bruno");

        given().when().post("/{userId}/follow/{userIdToFollow}", userA.getUserId(), userB.getUserId()).then().statusCode(200);
        given().when().post("/{userId}/follow/{userIdToFollow}", userA.getUserId(), userC.getUserId()).then().statusCode(200);

        String invalidOrder = "invalid_order";

        given()
                .when()
                .get("/{userId}/followed/list?order=" + invalidOrder, userA.getUserId())
                .then()
                .statusCode(400)
                .contentType(ContentType.JSON)
                .body("status", equalTo(400))
                .body("error", equalTo("Bad Request"))
                .body("message", equalTo("Order inválido: " + invalidOrder))
                .body("timestamp", notNullValue());
    }

    @Test
    @DisplayName("Deve retornar 400 ao buscar following de usuário inexistente")
    void shouldReturn400WhenGetFollowingUserDoesNotExist() {
        given()
                .when()
                .get("/{userId}/followed/list", 99999)
                .then()
                .statusCode(400)
                .contentType(ContentType.JSON)
                .body("status", equalTo(400))
                .body("error", equalTo("Bad Request"))
                .body("message", equalTo("Usuário não encontrado: 99999"))
                .body("timestamp", notNullValue());
    }

    @Test
    @DisplayName("Deve retornar a lista de followers do usuário")
    void shouldReturnUserFollowersList() {
        User userA = createAndSaveUser("usera");
        User userB = createAndSaveUser("userb");
        User userC = createAndSaveUser("userc");

        given().when().post("/{userId}/follow/{userIdToFollow}", userB.getUserId(), userA.getUserId()).then().statusCode(200);
        given().when().post("/{userId}/follow/{userIdToFollow}", userC.getUserId(), userA.getUserId()).then().statusCode(200);

        given()
                .when()
                .get("/{userId}/followers/list", userA.getUserId())
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("userId", is(userA.getUserId()))
                .body("userName", is(userA.getUserName()))
                .body("followers", hasSize(2))
                .body("followers.userId", containsInAnyOrder(userB.getUserId(), userC.getUserId()))
                .body("followers.userName", containsInAnyOrder(userB.getUserName(), userC.getUserName()));
    }

    @Test
    @DisplayName("Deve paginar a lista de followers do usuário")
    void shouldPaginateUserFollowersList() {
        User userA = createAndSaveUser("usera");
        User userB = createAndSaveUser("userb");
        User userC = createAndSaveUser("userc");

        given().when().post("/{userId}/follow/{userIdToFollow}", userB.getUserId(), userA.getUserId()).then().statusCode(200);
        given().when().post("/{userId}/follow/{userIdToFollow}", userC.getUserId(), userA.getUserId()).then().statusCode(200);

        given()
                .when()
                .get("/{userId}/followers/list?page=0&size=1", userA.getUserId())
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("userId", is(userA.getUserId()))
                .body("userName", is(userA.getUserName()))
                .body("followers", hasSize(1));

        given()
                .when()
                .get("/{userId}/followers/list?page=1&size=1", userA.getUserId())
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("userId", is(userA.getUserId()))
                .body("userName", is(userA.getUserName()))
                .body("followers", hasSize(1));
    }

    @Test
    @DisplayName("Deve retornar a lista de followers do usuário em ordem alfabética cescente")
    void shouldReturnUserFollowersListInAlphabeticalOrderAscending() {
        User userA = createAndSaveUser("usera");
        User userB = createAndSaveUser("ana");
        User userC = createAndSaveUser("bruno");

        given().when().post("/{userId}/follow/{userIdToFollow}", userB.getUserId(), userA.getUserId()).then().statusCode(200);
        given().when().post("/{userId}/follow/{userIdToFollow}", userC.getUserId(), userA.getUserId()).then().statusCode(200);

        given()
                .when()
                .get("/{userId}/followers/list?order=name_asc", userA.getUserId())
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("userId", is(userA.getUserId()))
                .body("userName", is(userA.getUserName()))
                .body("followers", hasSize(2))
                .body("followers[0].userName", is("ana"))
                .body("followers[1].userName", is("bruno"));
    }

    @Test
    @DisplayName("Deve retornar a lista de followers do usuário em ordem alfabética decrescente")
    void shouldReturnUserFollowersListInAlphabeticalOrderDescending() {
        User userA = createAndSaveUser("usera");
        User userB = createAndSaveUser("ana");
        User userC = createAndSaveUser("bruno");

        given().when().post("/{userId}/follow/{userIdToFollow}", userB.getUserId(), userA.getUserId()).then().statusCode(200);
        given().when().post("/{userId}/follow/{userIdToFollow}", userC.getUserId(), userA.getUserId()).then().statusCode(200);

        given()
                .when()
                .get("/{userId}/followers/list?order=name_desc", userA.getUserId())
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("userId", is(userA.getUserId()))
                .body("userName", is(userA.getUserName()))
                .body("followers", hasSize(2))
                .body("followers[0].userName", is("bruno"))
                .body("followers[1].userName", is("ana"));
    }

    @Test
    @DisplayName("Deve retornar 400 ao buscar followers com order inválido")
    void shouldReturn400WhenGetFollowersWithInvalidOrder() {
        User userA = createAndSaveUser("usera");
        User userB = createAndSaveUser("ana");
        User userC = createAndSaveUser("bruno");

        given().when().post("/{userId}/follow/{userIdToFollow}", userB.getUserId(), userA.getUserId()).then().statusCode(200);
        given().when().post("/{userId}/follow/{userIdToFollow}", userC.getUserId(), userA.getUserId()).then().statusCode(200);

        String invalidOrder = "invalid_order";

        given()
                .when()
                .get("/{userId}/followers/list?order=" + invalidOrder, userA.getUserId())
                .then()
                .statusCode(400)
                .contentType(ContentType.JSON)
                .body("status", equalTo(400))
                .body("error", equalTo("Bad Request"))
                .body("message", equalTo("Order inválido: " + invalidOrder))
                .body("timestamp", notNullValue());
    }

    @Test
    @DisplayName("Deve retornar 400 ao buscar followers de usuário inexistente")
    void shouldReturn400WhenGetFollowersUserDoesNotExist() {
        given()
                .when()
                .get("/{userId}/followers/list", 99999)
                .then()
                .statusCode(400)
                .contentType(ContentType.JSON)
                .body("status", equalTo(400))
                .body("error", equalTo("Bad Request"))
                .body("message", equalTo("Usuário não encontrado: 99999"))
                .body("timestamp", notNullValue());
    }

    private User createAndSaveUser(String userName) {
        User user = new User(userName);
        User saved = userRepository.saveAndFlush(user);
        assertThat(saved.getUserId()).isNotNull();
        return saved;
    }
}