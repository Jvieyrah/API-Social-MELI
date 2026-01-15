package com.meli.social.integration.controller;

import com.meli.social.post.inter.PostJpaRepository;
import com.meli.social.post.inter.PostLikeJpaRepository;
import com.meli.social.post.inter.ProductJpaRepository;
import com.meli.social.post.model.Post;
import com.meli.social.post.model.Product;
import com.meli.social.user.inter.UserFollowJpaRepository;
import com.meli.social.user.inter.UserJpaRepository;
import com.meli.social.user.model.UserFollow;
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
 
 import jakarta.persistence.EntityManager;

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
    private UserFollowJpaRepository userFollowRepository;

    @Autowired
    private PostJpaRepository postRepository;

    @Autowired
    private ProductJpaRepository productRepository;

    @Autowired
    private PostLikeJpaRepository postLikeRepository;
 
     @Autowired
     private EntityManager entityManager;

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
    @DisplayName("Deve publicar um post com sucesso e retornar 201")
    void shouldPublishPostSuccessfully() {
        User user = userRepository.saveAndFlush(new User("usertest"));

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
    @DisplayName("Deve publicar um post promocional com sucesso e persistir hasPromo e discount")
    void shouldPublishPromoPostSuccessfully() {
        User user = userRepository.saveAndFlush(new User("usertest"));

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
        request.put("has_promo", true);
        request.put("discount", 0.25);

        given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/promo-pub")
                .then()
                .statusCode(201)
                .body(is(emptyOrNullString()));

        assertThat(postRepository.count()).isEqualTo(1);
        Post savedPost = postRepository.findAll().get(0);
        assertThat(savedPost.getHasPromo()).isTrue();
        assertThat(savedPost.getDiscount()).isEqualTo(0.25);
    }

    @Test
    @DisplayName("Deve retornar a quantidade de produtos em promoção para um vendedor")
    void shouldReturnPromoProductsCountSuccessfully() {
        User user = userRepository.saveAndFlush(new User("vendedor1"));

        Product product1 = productRepository.saveAndFlush(new Product(5001, "Product 1", "Type 1", "Brand 1", "Color 1", "Notes 1"));
        Product product2 = productRepository.saveAndFlush(new Product(5002, "Product 2", "Type 2", "Brand 2", "Color 2", "Notes 2"));
        Product product3 = productRepository.saveAndFlush(new Product(5003, "Product 3", "Type 3", "Brand 3", "Color 3", "Notes 3"));

        Post promo1 = new Post();
        promo1.setUser(user);
        promo1.setDate(LocalDate.now());
        promo1.setProduct(product1);
        promo1.setCategory(1);
        promo1.setPrice(10.0);
        promo1.setHasPromo(true);
        promo1.setDiscount(0.10);
        promo1.setLikesCount(0);

        Post promo2 = new Post();
        promo2.setUser(user);
        promo2.setDate(LocalDate.now());
        promo2.setProduct(product2);
        promo2.setCategory(1);
        promo2.setPrice(20.0);
        promo2.setHasPromo(true);
        promo2.setDiscount(0.20);
        promo2.setLikesCount(0);

        Post normal = new Post();
        normal.setUser(user);
        normal.setDate(LocalDate.now());
        normal.setProduct(product3);
        normal.setCategory(1);
        normal.setPrice(30.0);
        normal.setHasPromo(false);
        normal.setDiscount(null);
        normal.setLikesCount(0);

        postRepository.save(promo1);
        postRepository.save(promo2);
        postRepository.save(normal);

        given()
                .when()
                .get("/promo-pub/count?userId={userId}", user.getUserId())
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("userId", is(user.getUserId()))
                .body("userName", is("vendedor1"))
                .body("promoProductsCount", is(2));
    }

    @Test
    @DisplayName("Deve retornar 404 ao buscar promo products count de usuário inexistente")
    void shouldReturn404WhenGetPromoProductsCountUserDoesNotExist() {
        Integer userId = 99999;

        given()
                .when()
                .get("/promo-pub/count?userId={userId}", userId)
                .then()
                .statusCode(404)
                .contentType(ContentType.JSON)
                .body("status", equalTo(404))
                .body("error", equalTo("Not Found"))
                .body("message", equalTo("Usuário não encontrado: " + userId))
                .body("timestamp", notNullValue());
    }

    @Test
    @DisplayName("Deve dar like em um post e incrementar likesCount")
    void shouldLikePostSuccessfully() {
        User user = userRepository.saveAndFlush(new User("userLike"));
        Product product = productRepository.saveAndFlush(new Product(7001, "Product", "Type", "Brand", "Color", "Notes"));

        Post post = new Post();
        post.setUser(user);
        post.setDate(LocalDate.of(2026, 1, 1));
        post.setProduct(product);
        post.setCategory(1);
        post.setPrice(10.0);
        post.setHasPromo(false);
        post.setDiscount(null);
        post.setLikesCount(0);

        Post saved = postRepository.save(post);

        given()
                .when()
                .post("/{postId}/like/{userId}", saved.getPostId(), user.getUserId())
                .then()
                .statusCode(200)
                .body(is(emptyOrNullString()));

        Post reloaded = postRepository.findById(saved.getPostId()).orElseThrow();
        assertThat(reloaded.getLikesCount()).isEqualTo(1);
        assertThat(postLikeRepository.count()).isEqualTo(1);
    }

    @Test
    @DisplayName("Deve remover like de um post e decrementar likesCount")
    void shouldUnlikePostSuccessfully() {
        User user = userRepository.saveAndFlush(new User("userUnlike"));
        Product product = productRepository.saveAndFlush(new Product(7002, "Product", "Type", "Brand", "Color", "Notes"));

        Post post = new Post();
        post.setUser(user);
        post.setDate(LocalDate.of(2026, 1, 1));
        post.setProduct(product);
        post.setCategory(1);
        post.setPrice(10.0);
        post.setHasPromo(false);
        post.setDiscount(null);
        post.setLikesCount(0);

        Post saved = postRepository.save(post);

        given()
                .when()
                .post("/{postId}/like/{userId}", saved.getPostId(), user.getUserId())
                .then()
                .statusCode(200);

        given()
                .when()
                .post("/{postId}/unlike/{userId}", saved.getPostId(), user.getUserId())
                .then()
                .statusCode(200)
                .body(is(emptyOrNullString()));

        Post reloaded = postRepository.findById(saved.getPostId()).orElseThrow();
        assertThat(reloaded.getLikesCount()).isEqualTo(0);
        assertThat(postLikeRepository.count()).isEqualTo(0);
    }

    @Test
    @DisplayName("Deve retornar 400 ao tentar dar like no mesmo post duas vezes")
    void shouldReturn400WhenUserLikesSamePostTwice() {
        User user = userRepository.saveAndFlush(new User("userDoubleLike"));
        Product product = productRepository.saveAndFlush(new Product(7003, "Product", "Type", "Brand", "Color", "Notes"));

        Post post = new Post();
        post.setUser(user);
        post.setDate(LocalDate.of(2026, 1, 1));
        post.setProduct(product);
        post.setCategory(1);
        post.setPrice(10.0);
        post.setHasPromo(false);
        post.setDiscount(null);
        post.setLikesCount(0);

        Post saved = postRepository.save(post);

        given()
                .when()
                .post("/{postId}/like/{userId}", saved.getPostId(), user.getUserId())
                .then()
                .statusCode(200);

        given()
                .when()
                .post("/{postId}/like/{userId}", saved.getPostId(), user.getUserId())
                .then()
                .statusCode(422)
                .contentType(ContentType.JSON)
                .body("status", equalTo(422))
                .body("error", equalTo("Unprocessable Entity"))
                .body("message", equalTo("Usuário %d já curtiu o post %d".formatted(user.getUserId(), saved.getPostId())))
                .body("timestamp", notNullValue());
    }

    @Test
    @DisplayName("Deve retornar a lista de produtos em promoção para um vendedor")
    void shouldReturnPromoProductsListSuccessfully() {
        User user = userRepository.saveAndFlush(new User("vendedor1"));

        Product product1 = productRepository.saveAndFlush(new Product(6001, "Product 1", "Type 1", "Brand 1", "Color 1", "Notes 1"));
        Product product2 = productRepository.saveAndFlush(new Product(6002, "Product 2", "Type 2", "Brand 2", "Color 2", "Notes 2"));
        Product product3 = productRepository.saveAndFlush(new Product(6003, "Product 3", "Type 3", "Brand 3", "Color 3", "Notes 3"));

        Post promo1 = new Post();
        promo1.setUser(user);
        promo1.setDate(LocalDate.of(2026, 1, 1));
        promo1.setProduct(product1);
        promo1.setCategory(1);
        promo1.setPrice(10.0);
        promo1.setHasPromo(true);
        promo1.setDiscount(0.10);
        promo1.setLikesCount(0);

        Post promo2 = new Post();
        promo2.setUser(user);
        promo2.setDate(LocalDate.of(2026, 1, 2));
        promo2.setProduct(product2);
        promo2.setCategory(1);
        promo2.setPrice(20.0);
        promo2.setHasPromo(true);
        promo2.setDiscount(0.20);
        promo2.setLikesCount(0);

        Post normal = new Post();
        normal.setUser(user);
        normal.setDate(LocalDate.of(2026, 1, 3));
        normal.setProduct(product3);
        normal.setCategory(1);
        normal.setPrice(30.0);
        normal.setHasPromo(false);
        normal.setDiscount(null);
        normal.setLikesCount(0);

        postRepository.save(promo1);
        postRepository.save(promo2);
        postRepository.save(normal);

        given()
                .when()
                .get("/promo-pub/list?userId={userId}", user.getUserId())
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("userId", is(user.getUserId()))
                .body("userName", is("vendedor1"))
                .body("posts", hasSize(2))
                .body("posts.userId", everyItem(is(user.getUserId())))
                .body("posts.hasPromo", everyItem(is(true)))
                .body("posts.discount", containsInAnyOrder(0.10F, 0.20F))
                .body("posts.date", containsInAnyOrder("2026-01-01", "2026-01-02"));
    }

    @Test
    @DisplayName("Deve retornar 404 ao buscar promo products list de usuário inexistente")
    void shouldReturn404WhenGetPromoProductsListUserDoesNotExist() {
        Integer userId = 99999;

        given()
                .when()
                .get("/promo-pub/list?userId={userId}", userId)
                .then()
                .statusCode(404)
                .contentType(ContentType.JSON)
                .body("status", equalTo(404))
                .body("error", equalTo("Not Found"))
                .body("message", equalTo("Usuário não encontrado: " + userId))
                .body("timestamp", notNullValue());
    }

    @Test
    @DisplayName("Deve publicar um post com sucesso de um produto que já existe com data informada valida")
    void shouldPublishPostSuccessfully_whenProduct_alreadyExists(){
        Product productToSave = new Product(1001, "Mouse Gamer", "Periférico", "Logitech", "Preto", "Teste");
        productRepository.save(productToSave);

        User user = userRepository.saveAndFlush(new User("usertest"));

        Map<String, Object> product = new HashMap<>();
        product.put("productId", 1001);
        product.put("productName", "Mouse Gamer");
        product.put("type", "Periférico");
        product.put("brand", "Logitech");
        product.put("color", "Preto");
        product.put("notes", "Teste");

        Map<String, Object> request = new HashMap<>();
        request.put("userId", user.getUserId());
        request.put("date", "2025-02-01");
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
        product.put("type", "Periférico");
        product.put("brand", "Logitech");
        product.put("color", "Preto");
        product.put("notes", "Teste");

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
        product.put("type", "Periférico");
        product.put("brand", "Logitech");
        product.put("color", "Preto");
        product.put("notes", "Teste");

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
        User user = userRepository.saveAndFlush(new User("usertest"));

        Map<String, Object> product = new HashMap<>();
        product.put("productId", 1001);
        product.put("productName", "Mouse Gamer");
        product.put("type", "Periférico");
        product.put("brand", "Logitech");
        product.put("color", "Preto");
        product.put("notes", "Teste");

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
        User user = userRepository.saveAndFlush(new User("testuser"));
        User user2 = userRepository.saveAndFlush(new User("testuser2"));
        User user3 = userRepository.saveAndFlush(new User("testuser3"));
        User user4 = userRepository.saveAndFlush(new User("testuser4"));

        userFollowRepository.saveAndFlush(new UserFollow(user, user2));
        userFollowRepository.saveAndFlush(new UserFollow(user, user3));
        // não segue user4

        user2.incrementFollowersCount();
        user3.incrementFollowersCount();
        userRepository.saveAndFlush(user2);
        userRepository.saveAndFlush(user3);


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
        User user = userRepository.saveAndFlush(new User("testuser"));
        User user2 = userRepository.saveAndFlush(new User("testuser2"));
        User user3 = userRepository.saveAndFlush(new User("testuser3"));
        User user4 = userRepository.saveAndFlush(new User("testuser4"));

        userFollowRepository.saveAndFlush(new UserFollow(user, user2));
        userFollowRepository.saveAndFlush(new UserFollow(user, user3));
        // não segue user4

        user2.incrementFollowersCount();
        user3.incrementFollowersCount();
        userRepository.saveAndFlush(user2);
        userRepository.saveAndFlush(user3);


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
        User user = userRepository.saveAndFlush(new User("testuser"));
        User user2 = userRepository.saveAndFlush(new User("testuser2"));
        User user3 = userRepository.saveAndFlush(new User("testuser3"));
        User user4 = userRepository.saveAndFlush(new User("testuser4"));

        userFollowRepository.save(new UserFollow(user, user2));
        userFollowRepository.save(new UserFollow(user, user3));
        // não segue user4

        user2.incrementFollowersCount();
        user3.incrementFollowersCount();
        userRepository.saveAndFlush(user2);
        userRepository.saveAndFlush(user3);
        userFollowRepository.flush();


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