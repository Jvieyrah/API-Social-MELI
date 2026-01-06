package com.meli.social.integration.controller;

import com.meli.social.post.inter.PostJpaRepository;
import com.meli.social.post.inter.ProductJpaRepository;
import com.meli.social.post.model.Product;
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
@DisplayName("Testes de Integração - PostController")
class PostControllerIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private UserJpaRepository userRepository;

    @Autowired
    private PostJpaRepository postRepository;

    @Autowired
    private ProductJpaRepository productRepository;

    @BeforeEach
    void setUp() {
        RestAssured.reset();
        RestAssured.port = port;
        RestAssured.basePath = "/products";
        RestAssured.baseURI = "http://localhost";

        postRepository.deleteAll();
        productRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("Deve publicar um post com sucesso e retornar 201")
    void shouldPublishPostSuccessfully() {
        User user = userRepository.saveAndFlush(new User("user_test"));

        Map<String, Object> product = new HashMap<>();
        product.put("productId", 1001);
        product.put("productName", "Mouse Gamer");
        product.put("type", "Periférico");
        product.put("brand", "Logitech");
        product.put("color", "Preto");
        product.put("notes", "Teste");

        Map<String, Object> request = new HashMap<>();
        request.put("userId", user.getUserId());
        request.put("date", null);
        request.put("product", product);
        request.put("category", 58);
        request.put("price", 299.90);

        given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/publish")
                .then()
                .statusCode(201)
                .body(is(emptyOrNullString()));

        assertThat(postRepository.count()).isEqualTo(1);
        assertThat(productRepository.existsById(1001)).isTrue();
    }

    @Test
    @DisplayName("Deve publicar um post com sucesso de um produto que já existe com data informada valida")
    void shouldPublishPostSuccessfully_whenProduct_alreadyExists(){
        Product productToSave = new Product(1001, "Mouse Gamer", "Periférico", "Logitech", "Preto", "Teste");
        productRepository.save(productToSave);

        User user = userRepository.saveAndFlush(new User("user_test"));

        Map<String, Object> request = new HashMap<>();
        request.put("userId", user.getUserId());
        request.put("date", "2025-02-01");
        request.put("product", productToSave);
        request.put("category", 58);
        request.put("price", 299.90);

        given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/publish")
                .then()
                .statusCode(201)
                .body(is(emptyOrNullString()));


        assertThat(productRepository.existsById(1001)).isTrue();
        assertThat(productRepository.count()).isEqualTo(1);
        assertThat(postRepository.count()).isEqualTo(1);
    }

    @Test
    @DisplayName("Deve retornar 400 quando user_id for nulo")
    void shouldReturn400WhenUserIdIsNull() {
        Map<String, Object> product = new HashMap<>();
        product.put("productId", 1001);
        product.put("productName", "Mouse Gamer");

        Map<String, Object> request = new HashMap<>();
        request.put("userId", null);
        request.put("date", null);
        request.put("product", product);
        request.put("category", 58);
        request.put("price", 299.90);

        given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/publish")
                .then()
                .statusCode(400)
                .contentType(ContentType.JSON)
                .body("status", equalTo(400))
                .body("error", equalTo("Bad Request"))
                .body("message", equalTo("UserId não pode ser nulo"))
                .body("timestamp", notNullValue());

        assertThat(postRepository.count()).isZero();
    }

    @Test
    @DisplayName("Deve retornar 400 quando usuário não existe")
    void shouldReturn400WhenUserDoesNotExist() {
        Map<String, Object> product = new HashMap<>();
        product.put("productId", 1001);
        product.put("productName", "Mouse Gamer");

        Map<String, Object> request = new HashMap<>();
        request.put("userId", 99999);
        request.put("date", null);
        request.put("product", product);
        request.put("category", 58);
        request.put("price", 299.90);

        given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/publish")
                .then()
                .statusCode(400)
                .contentType(ContentType.JSON)
                .body("status", equalTo(400))
                .body("error", equalTo("Bad Request"))
                .body("message", equalTo("Usuário não encontrado: 99999"))
                .body("timestamp", notNullValue());

        assertThat(postRepository.count()).isZero();
    }

    @Test
    @DisplayName("Deve retornar 400 quando data for inválida")
    void shouldReturn400WhenDateIsInvalid() {
        User user = userRepository.saveAndFlush(new User("user_test"));

        Map<String, Object> product = new HashMap<>();
        product.put("productId", 1001);
        product.put("productName", "Mouse Gamer");

        Map<String, Object> request = new HashMap<>();
        request.put("userId", user.getUserId());
        request.put("date", "not-a-date");
        request.put("product", product);
        request.put("category", 58);
        request.put("price", 299.90);

        given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/publish")
                .then()
                .statusCode(400)
                .contentType(ContentType.JSON)
                .body("status", equalTo(400))
                .body("error", equalTo("Bad Request"))
                .body("message", equalTo("Data inválida: not-a-date"))
                .body("timestamp", notNullValue());

        assertThat(postRepository.count()).isZero();
    }
}
