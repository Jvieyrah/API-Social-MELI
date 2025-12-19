package com.meli.social.integration.controller;

import com.meli.social.user.dto.UserSimpleDTO;
import com.meli.social.user.inter.UserJpaRepository;
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

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        RestAssured.basePath = "/users";
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("Deve criar um novo usuário com sucesso")
    void shouldCreateNewUserSuccessfully() {
        // Arrange
        Map<String, String> request = new HashMap<>();
        request.put("userName", "joao_silva");

        // Act & Assert
        UserSimpleDTO response = given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post()
                .then()
                .statusCode(201)
                .contentType(ContentType.JSON)
                .body("userId", notNullValue())
                .body("userName", equalTo("joao_silva"))
                .extract()
                .as(UserSimpleDTO.class);

        assertThat(response.getUserName()).isEqualTo("joao_silva");
        assertThat(userRepository.existsByUserName("joao_silva")).isTrue();
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
                .body("message", equalTo("Username é obrigatório"))
                .body("timestamp", notNullValue());

        assertThat(userRepository.count()).isZero();
    }

    @Test
    @DisplayName("Deve retornar erro 400 quando username já existe")
    void shouldReturn400WhenUsernameAlreadyExists() {
        Map<String, String> request = new HashMap<>();
        request.put("userName", "maria_santos");

        given().contentType(ContentType.JSON).body(request).post().then().statusCode(201);

        given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post()
                .then()
                .statusCode(400)
                .body("message", equalTo("Username já existe: maria_santos"));

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
}