package com.meli.social.integration.controller;

import com.meli.social.post.inter.PostJpaRepository;
import com.meli.social.post.inter.PostLikeJpaRepository;
import com.meli.social.post.inter.ProductJpaRepository;
import com.meli.social.user.dto.UserSimpleDTO;
import com.meli.social.user.inter.UserFollowJpaRepository;
import com.meli.social.user.inter.UserJpaRepository;
import com.meli.social.post.model.Product;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@DisplayName("Testes de Integração - ProductController")
class ProductControllerIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private UserJpaRepository userRepository;

    @Autowired
    private UserFollowJpaRepository userFollowRepository;

    @Autowired
    private PostJpaRepository postRepository;

    @Autowired
    private ProductJpaRepository productRepository;

    @Autowired
    private PostLikeJpaRepository postLikeRepository;

    @BeforeEach
    void setUp() {
        RestAssured.reset();
        RestAssured.port = port;
        RestAssured.basePath = "/products";
        RestAssured.baseURI = "http://localhost";

        postLikeRepository.deleteAll();
        postRepository.deleteAll();
        productRepository.deleteAll();
        userFollowRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("Deve retornar 200 e o produto quando existir")
    void shouldReturnProductWhenExists() {
        productRepository.saveAndFlush(new Product(1001, "Mouse Gamer", "Periférico", "Logitech", "Preto", "Teste"));

        given()
                .when()
                .get("/{productId}", 1001)
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("productId", is(1001))
                .body("productName", is("Mouse Gamer"))
                .body("type", is("Periférico"))
                .body("brand", is("Logitech"))
                .body("color", is("Preto"))
                .body("notes", is("Teste"));
    }

    @Test
    @DisplayName("Deve retornar 404 quando produto não existir")
    void shouldReturn404WhenProductDoesNotExist() {
        given()
                .when()
                .get("/{productId}", 99999)
                .then()
                .statusCode(404)
                .contentType(ContentType.JSON)
                .body("status", equalTo(404))
                .body("error", equalTo("Not Found"))
                .body("message", equalTo("Produto não encontrado: 99999"))
                .body("timestamp", notNullValue());
    }
}
