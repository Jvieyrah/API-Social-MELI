package com.meli.social.integration.controller;

import com.meli.social.user.inter.UserJpaRepository;
import com.meli.social.user.inter.UserFollowJpaRepository;
import com.meli.social.user.model.User;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class FollowIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private UserJpaRepository userRepository;

    @Autowired
    private UserFollowJpaRepository userFollowRepository;

    @BeforeEach
    void setUp() {
        RestAssured.reset();
        RestAssured.port = port;
        RestAssured.basePath = "";
        RestAssured.baseURI = "http://localhost";
        userFollowRepository.deleteAll();
        userRepository.deleteAll();
    }


    @Test
    @Order(1)
    @DisplayName("Deve seguir um usuário com sucesso")
    void testFollowUser_Success() {
        User userA = createAndSaveUser("usera");
        User userB = createAndSaveUser("userb");

        given()
                .contentType(ContentType.JSON)
                .when()
                .post("/users/{userId}/follow/{userIdToFollow}", userA.getUserId(), userB.getUserId())
                .then()
                .statusCode(200);
    }

    @Test
    @Order(2)
    @DisplayName("Deve retornar 400 quando usuário tenta seguir a si mesmo")
    void testFollowUser_WhenUserTriesToFollowHimself() {
        User user = createAndSaveUser("testuser");

        given()
                .contentType(ContentType.JSON)
                .when()
                .post("/users/{userId}/follow/{userIdToFollow}", user.getUserId(), user.getUserId())
                .then()
                .statusCode(400);
    }

    @Test
    @Order(3)
    @DisplayName("Deve retornar 422 quando usuário já segue o outro")
    void testFollowUser_WhenAlreadyFollowing() {
        User userA = createAndSaveUser("usera");
        User userB = createAndSaveUser("userb");

        // Seguir pela primeira vez
        given()
                .when()
                .post("/users/{userId}/follow/{userIdToFollow}", userA.getUserId(), userB.getUserId())
                .then()
                .statusCode(200);

        // Tentar seguir novamente
        given()
                .when()
                .post("/users/{userId}/follow/{userIdToFollow}", userA.getUserId(), userB.getUserId())
                .then()
                .statusCode(422);
    }

    @Test
    @Order(4)
    @DisplayName("Deve retornar 404 quando follower não existe")
    void testFollowUser_WhenFollowerDoesNotExist() {
        User userB = createAndSaveUser("userb");

        given()
                .when()
                .post("/users/{userId}/follow/{userIdToFollow}", 99999, userB.getUserId())
                .then()
                .statusCode(404);
    }

    @Test
    @Order(5)
    @DisplayName("Deve retornar 404 quando followed não existe")
    void testFollowUser_WhenFollowedDoesNotExist() {
        User userA = createAndSaveUser("usera");

        given()
                .when()
                .post("/users/{userId}/follow/{userIdToFollow}", userA.getUserId(), 99999)
                .then()
                .statusCode(404);
    }

    // ==================== UNFOLLOW TESTS ====================

    @Test
    @Order(6)
    @DisplayName("Deve deixar de seguir um usuário com sucesso")
    void testUnfollowUser_Success() {
        User userA = createAndSaveUser("usera");
        User userB = createAndSaveUser("userb");

        // Follow
        given()
                .when()
                .post("/users/{userId}/follow/{userIdToFollow}", userA.getUserId(), userB.getUserId())
                .then()
                .statusCode(200);

        // Unfollow
        given()
                .when()
                .post("/users/{userId}/unfollow/{userIdToUnfollow}", userA.getUserId(), userB.getUserId())
                .then()
                .statusCode(200);
    }

    @Test
    @Order(7)
    @DisplayName("Deve retornar 400 quando usuário tenta deixar de seguir a si mesmo")
    void testUnfollowUser_WhenUserTriesToUnfollowHimself() {
        User user = createAndSaveUser("testuser");

        given()
                .when()
                .post("/users/{userId}/unfollow/{userIdToUnfollow}", user.getUserId(), user.getUserId())
                .then()
                .statusCode(400);
    }

    @Test
    @Order(8)
    @DisplayName("Deve retornar 422 quando usuário não segue o outro")
    void testUnfollowUser_WhenNotFollowing() {
        User userA = createAndSaveUser("usera");
        User userB = createAndSaveUser("userb");

        given()
                .when()
                .post("/users/{userId}/unfollow/{userIdToUnfollow}", userA.getUserId(), userB.getUserId())
                .then()
                .statusCode(422);
    }


    @Test
    @Order(11)
    @DisplayName("Deve permitir seguir novamente após deixar de seguir")
    void testFollowAgainAfterUnfollow() {
        User userA = createAndSaveUser("usera");
        User userB = createAndSaveUser("userb");

        // Seguir
        given().post("/users/{userId}/follow/{userIdToFollow}", userA.getUserId(), userB.getUserId()).then().statusCode(200);

        // Deixar de seguir
        given().post("/users/{userId}/unfollow/{userIdToUnfollow}", userA.getUserId(), userB.getUserId()).then().statusCode(200);

        // Seguir novamente
        given().post("/users/{userId}/follow/{userIdToFollow}", userA.getUserId(), userB.getUserId()).then().statusCode(200);
    }

    @Test
    @Order(12)
    @DisplayName("Deve permitir múltiplos usuários seguirem o mesmo usuário")
    void testMultipleUsersFollowSameUser() {
        User userA = createAndSaveUser("usera");
        User userB = createAndSaveUser("userb");
        User userC = createAndSaveUser("userc");

        given().post("/users/{userId}/follow/{userIdToFollow}", userA.getUserId(), userC.getUserId()).then().statusCode(200);
        given().post("/users/{userId}/follow/{userIdToFollow}", userB.getUserId(), userC.getUserId()).then().statusCode(200);
    }

    @Test
    @Order(13)
    @DisplayName("Deve permitir um usuário seguir múltiplos usuários")
    void testUserFollowsMultipleUsers() {
        User userA = createAndSaveUser("usera");
        User userB = createAndSaveUser("userb");
        User userC = createAndSaveUser("userc");

        given().post("/users/{userId}/follow/{userIdToFollow}", userA.getUserId(), userB.getUserId()).then().statusCode(200);
        given().post("/users/{userId}/follow/{userIdToFollow}", userA.getUserId(), userC.getUserId()).then().statusCode(200);
    }

    @Test
    @Order(14)
    @DisplayName("Deve retornar um usuário com nenhum seguidor")
    void testGetFollowersCount_WhenUserHasNoFollowers() {
        User user = createAndSaveUser("userx");

        given()
                .when()
                .get("/users/{userId}/followers/count", user.getUserId())
                .then()
                .statusCode(200)
                .body("userId", is(user.getUserId()))
                .body("userName", is(user.getUserName()))
                .body("followersCount", is(0));
    }

    @Test
    @Order(15)
    @DisplayName("Deve retornar a contagem correta de seguidores")
    void testGetFollowersCount_WhenUserHasFollowers() {
        User target = createAndSaveUser("target");
        User followerA = createAndSaveUser("followera");
        User followerB = createAndSaveUser("followerb");

        given().post("/users/{userId}/follow/{userIdToFollow}", followerA.getUserId(), target.getUserId()).then().statusCode(200);
        given().post("/users/{userId}/follow/{userIdToFollow}", followerB.getUserId(), target.getUserId()).then().statusCode(200);

        given()
                .when()
                .get("/users/{userId}/followers/count", target.getUserId())
                .then()
                .statusCode(200)
                .body("userId", is(target.getUserId()))
                .body("userName", is(target.getUserName()))
                .body("followersCount", is(2));
    }

    @Test
    @Order(16)
    @DisplayName("Deve retornar 404 quando usuário não existe ao buscar contagem de seguidores")
    void testGetFollowersCount_WhenUserDoesNotExist() {
        given()
                .when()
                .get("/users/{userId}/followers/count", 99999)
                .then()
                .statusCode(404)
                .body("status", is(404));
    }

    private User createAndSaveUser(String userName) {
        User user = new User(userName);
        User saved = userRepository.saveAndFlush(user);
        Assertions.assertNotNull(saved.getUserId(), "userId não foi gerado após saveAndFlush");
        return saved;
    }
}