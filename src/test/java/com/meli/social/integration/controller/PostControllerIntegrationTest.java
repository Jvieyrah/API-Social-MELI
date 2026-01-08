package com.meli.social.integration.controller;

import com.meli.social.post.inter.PostJpaRepository;
import com.meli.social.post.inter.ProductJpaRepository;
import com.meli.social.post.model.Post;
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

import java.time.LocalDate;
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
                .statusCode(404)
                .contentType(ContentType.JSON)
                .body("status", equalTo(404))
                .body("error", equalTo("Not Found"))
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


    @Test
    @DisplayName("Deve trazer uma lista de posts com sucesso e retornar 200")
    void shouldReturnFeedSuccessfully_whenUserIdExists() {
        User user = userRepository.saveAndFlush(new User("test_user"));
        User user2 = userRepository.saveAndFlush(new User("test_user2"));
        User user3 = userRepository.saveAndFlush(new User("test_user3"));
        User user4 = userRepository.saveAndFlush(new User("test_user4"));

        user.follow(user2);
        user.follow(user3);
        // não segue user4

        userRepository.saveAndFlush(user);
        userRepository.saveAndFlush(user2);
        userRepository.saveAndFlush(user3);
        userRepository.saveAndFlush(user4);


        Post p1 = new Post();
        p1.setPostId(null);
        p1.setDate(LocalDate.now().minusWeeks(3)); //  fora do prazo
        Product product1 = productRepository.saveAndFlush(new Product(1, "Product 1", "Type 1", "Brand 1", "Color 1", "Notes 1"));
        p1.setProduct(product1);
        p1.setUser(user2);
        p1.setCategory(1);
        p1.setPrice(1200.50);
        p1.setHasPromo(false);
        p1.setDiscount(null);
        p1.setLikesCount(0);

        Post p2 = new Post();
        p2.setPostId(null);
        p2.setDate(LocalDate.now().minusDays(3));
        Product product2 = productRepository.saveAndFlush(new Product(2, "Product 2", "Type 2", "Brand 2", "Color 2", "Notes 2"));
        p2.setProduct(product2);
        p2.setUser(user3);
        p2.setCategory(2);
        p2.setPrice(599.99);
        p2.setHasPromo(false);
        p2.setDiscount(null);
        p2.setLikesCount(0);

        Post p3 = new Post();
        p3.setPostId(null);
        p3.setDate(LocalDate.now().minusWeeks(1));
        Product product3 = productRepository.saveAndFlush(new Product(3, "Product 3", "Type 3", "Brand 3", "Color 3", "Notes 3"));
        p3.setProduct(product3);
        p3.setUser(user2);
        p3.setCategory(3);
        p3.setPrice(399.99);
        p3.setHasPromo(false);
        p3.setDiscount(null);
        p3.setLikesCount(0);

        Post p4 = new Post(); // post dentro das duas semanas, porem de usuario nao seguido.
        p4.setPostId(null);
        p4.setDate(LocalDate.now().minusWeeks(1));
        Product product4 = productRepository.saveAndFlush(new Product(4, "Product 4", "Type 4", "Brand 4", "Color 4", "Notes 4"));
        p4.setProduct(product4);
        p4.setUser(user4);
        p4.setCategory(4);
        p4.setPrice(399.99);
        p4.setHasPromo(false);
        p4.setDiscount(null);
        p4.setLikesCount(0);

        postRepository.save(p1);
        postRepository.save(p2);
        postRepository.save(p3);
        postRepository.save(p4);

        given()
                .when()
                .get("/followed/{userId}/list", user.getUserId())
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("posts.size()", is(2))
                .body("posts[0].postId", is(p2.getPostId()))
                .body("posts[1].postId", is(p3.getPostId()));
    }

    @Test
    @DisplayName("Deve trazer uma lista descendente de posts com sucesso e retornar 200")
    void shouldReturnFeedSuccessfully_whenParamSortDesc() {
        User user = userRepository.saveAndFlush(new User("test_user"));
        User user2 = userRepository.saveAndFlush(new User("test_user2"));
        User user3 = userRepository.saveAndFlush(new User("test_user3"));
        User user4 = userRepository.saveAndFlush(new User("test_user4"));

        user.follow(user2);
        user.follow(user3);
        // não segue user4

        userRepository.saveAndFlush(user);
        userRepository.saveAndFlush(user2);
        userRepository.saveAndFlush(user3);
        userRepository.saveAndFlush(user4);


        Post p1 = new Post();
        p1.setPostId(null);
        p1.setDate(LocalDate.now().minusWeeks(3)); //  fora do prazo
        Product product1 = productRepository.saveAndFlush(new Product(1, "Product 1", "Type 1", "Brand 1", "Color 1", "Notes 1"));
        p1.setProduct(product1);
        p1.setUser(user2);
        p1.setCategory(1);
        p1.setPrice(1200.50);
        p1.setHasPromo(false);
        p1.setDiscount(null);
        p1.setLikesCount(0);

        Post p2 = new Post();
        p2.setPostId(null);
        p2.setDate(LocalDate.now().minusDays(3));
        Product product2 = productRepository.saveAndFlush(new Product(2, "Product 2", "Type 2", "Brand 2", "Color 2", "Notes 2"));
        p2.setProduct(product2);
        p2.setUser(user3);
        p2.setCategory(2);
        p2.setPrice(599.99);
        p2.setHasPromo(false);
        p2.setDiscount(null);
        p2.setLikesCount(0);

        Post p3 = new Post();
        p3.setPostId(null);
        p3.setDate(LocalDate.now().minusWeeks(1));
        Product product3 = productRepository.saveAndFlush(new Product(3, "Product 3", "Type 3", "Brand 3", "Color 3", "Notes 3"));
        p3.setProduct(product3);
        p3.setUser(user2);
        p3.setCategory(3);
        p3.setPrice(399.99);
        p3.setHasPromo(false);
        p3.setDiscount(null);
        p3.setLikesCount(0);

        Post p4 = new Post(); // post dentro das duas semanas, porem de usuario nao seguido.
        p4.setPostId(null);
        p4.setDate(LocalDate.now().minusWeeks(1));
        Product product4 = productRepository.saveAndFlush(new Product(4, "Product 4", "Type 4", "Brand 4", "Color 4", "Notes 4"));
        p4.setProduct(product4);
        p4.setUser(user4);
        p4.setCategory(4);
        p4.setPrice(399.99);
        p4.setHasPromo(false);
        p4.setDiscount(null);
        p4.setLikesCount(0);

        postRepository.save(p1);
        postRepository.save(p2);
        postRepository.save(p3);
        postRepository.save(p4);

        given()
                .when()
                .get("/followed/{userId}/list?order=date_desc", user.getUserId())
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("posts.size()", is(2))
                .body("posts[0].postId", is(p2.getPostId()))
                .body("posts[1].postId", is(p3.getPostId()));
    }

    @Test
    @DisplayName("Deve trazer uma lista ascendente de posts com sucesso e retornar 200")
    void shouldReturnFeedSuccessfully_whenParamSortAsc() {
        User user = userRepository.saveAndFlush(new User("test_user"));
        User user2 = userRepository.saveAndFlush(new User("test_user2"));
        User user3 = userRepository.saveAndFlush(new User("test_user3"));
        User user4 = userRepository.saveAndFlush(new User("test_user4"));

        user.follow(user2);
        user.follow(user3);
        // não segue user4

        userRepository.saveAndFlush(user);
        userRepository.saveAndFlush(user2);
        userRepository.saveAndFlush(user3);
        userRepository.saveAndFlush(user4);


        Post p1 = new Post();
        p1.setPostId(null);
        p1.setDate(LocalDate.now().minusWeeks(3)); //  fora do prazo
        Product product1 = productRepository.saveAndFlush(new Product(1, "Product 1", "Type 1", "Brand 1", "Color 1", "Notes 1"));
        p1.setProduct(product1);
        p1.setUser(user2);
        p1.setCategory(1);
        p1.setPrice(1200.50);
        p1.setHasPromo(false);
        p1.setDiscount(null);
        p1.setLikesCount(0);

        Post p2 = new Post();
        p2.setPostId(null);
        p2.setDate(LocalDate.now().minusDays(3));
        Product product2 = productRepository.saveAndFlush(new Product(2, "Product 2", "Type 2", "Brand 2", "Color 2", "Notes 2"));
        p2.setProduct(product2);
        p2.setUser(user3);
        p2.setCategory(2);
        p2.setPrice(599.99);
        p2.setHasPromo(false);
        p2.setDiscount(null);
        p2.setLikesCount(0);

        Post p3 = new Post();
        p3.setPostId(null);
        p3.setDate(LocalDate.now().minusWeeks(1));
        Product product3 = productRepository.saveAndFlush(new Product(3, "Product 3", "Type 3", "Brand 3", "Color 3", "Notes 3"));
        p3.setProduct(product3);
        p3.setUser(user2);
        p3.setCategory(3);
        p3.setPrice(399.99);
        p3.setHasPromo(false);
        p3.setDiscount(null);
        p3.setLikesCount(0);

        Post p4 = new Post(); // post dentro das duas semanas, porem de usuario nao seguido.
        p4.setPostId(null);
        p4.setDate(LocalDate.now().minusWeeks(1));
        Product product4 = productRepository.saveAndFlush(new Product(4, "Product 4", "Type 4", "Brand 4", "Color 4", "Notes 4"));
        p4.setProduct(product4);
        p4.setUser(user4);
        p4.setCategory(4);
        p4.setPrice(399.99);
        p4.setHasPromo(false);
        p4.setDiscount(null);
        p4.setLikesCount(0);

        postRepository.save(p1);
        postRepository.save(p2);
        postRepository.save(p3);
        postRepository.save(p4);

        given()
                .when()
                .get("/followed/{userId}/list?order=date_asc", user.getUserId())
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("posts.size()", is(2))
                .body("posts[0].postId", is(p3.getPostId()))
                .body("posts[1].postId", is(p2.getPostId()));
    }


    @Test
    @DisplayName("Deve retornar 404 quando usuário não existe")
    void shouldFeedReturn400WhenUserDoesNotExist() {
        given()
                .when()
                .get("/followed/{userId}/list", 99999)
                .then()
                .statusCode(404)
                .contentType(ContentType.JSON)
                .body("status", equalTo(404))
                .body("error", equalTo("Not Found"))
                .body("message", equalTo("Usuário não encontrado: 99999"))
                .body("timestamp", notNullValue());
    }

}