package io.github.flipchandler.quarkussocial.controller;

import io.github.flipchandler.quarkussocial.domain.Follower;
import io.github.flipchandler.quarkussocial.domain.User;
import io.github.flipchandler.quarkussocial.dto.FollowerRequest;
import io.github.flipchandler.quarkussocial.repository.FollowerRepository;
import io.github.flipchandler.quarkussocial.repository.UserRepository;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.transaction.Transactional;
import javax.ws.rs.core.Response;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;

@QuarkusTest
@TestHTTPEndpoint(FollowerController.class)
@RequiredArgsConstructor
class FollowerControllerTest {

    private final UserRepository userRepository;
    private final FollowerRepository followerRepository;

    Long userId;
    Long followerId;

    @BeforeEach
    @Transactional
    void setUp() {
        var user = User.of("User Test", 35);
        userRepository.persist(user);
        userId = user.getId();

        var follower = User.of("Follwer Test", 26);
        userRepository.persist(follower);
        followerId = follower.getId();

        var followerModel = Follower.of(user, follower);
        followerRepository.persist(followerModel);
    }

    @Test
    @DisplayName("should return 409 when followerId is equal to userId")
    void followUser_shouldReturnConflict_whenFollowerIdEqualsToUserId() {
        var body = new FollowerRequest(userId);

        given()
            .contentType(ContentType.JSON)
            .body(body)
            .pathParam("userId", userId)
        .when()
            .put()
        .then()
            .statusCode(Response.Status.CONFLICT.getStatusCode())
            .body(is("You can't follow yourself"));
    }

    @Test
    @DisplayName("should return 404 when userId doesn't exist")
    void followUser_shouldReturnNotFound_whenUserIdDoesNotExist() {
        var body = new FollowerRequest(userId);
        var inexistentUserId = 999;

        given()
            .contentType(ContentType.JSON)
            .body(body)
            .pathParam("userId", inexistentUserId)
        .when()
            .put()
        .then()
            .statusCode(Response.Status.NOT_FOUND.getStatusCode());
    }

    @Test
    @DisplayName("should return 204 when follower exists")
    void followUser_shouldReturnNoContent_whenFollowerExists() {
        var body = new FollowerRequest(followerId);

        given()
            .contentType(ContentType.JSON)
            .body(body)
            .pathParam("userId", userId)
        .when()
            .put()
        .then()
            .statusCode(Response.Status.NO_CONTENT.getStatusCode());
    }

    @Test
    @DisplayName("should return 404 when userId doesn't exist")
    void getAllByUser_shouldReturnNotFound_whenUserDoesNotExist() {
        var inexistentUserId = 999;

        given()
            .contentType(ContentType.JSON)
            .pathParam("userId", inexistentUserId)
        .when()
            .get()
        .then()
            .statusCode(Response.Status.NOT_FOUND.getStatusCode());
    }

    @Test
    @DisplayName("should return 200 when userId exists")
    void getAllByUser_shouldReturnOk_whenUserExists() {
        var response = given()
                .contentType(ContentType.JSON)
                .pathParam("userId", userId)
        .when()
                .get()
        .then()
                .extract()
                .response();

        assertEquals(Response.Status.OK.getStatusCode(), response.statusCode());

        var followersCount = response.jsonPath().get("followersCount");
        assertEquals(1, followersCount);

        var followersContent = response.jsonPath().getList("content");
        assertEquals(1, followersContent.size());
    }

    @Test
    @DisplayName("should return 404 when userId doesn't exist when unfollow user")
    void unfollowUser_shouldReturnNotFound_whenUserIdDoesNotExist() {
        var inexistentUserId = 999;

        given()
            .contentType(ContentType.JSON)
            .pathParam("userId", inexistentUserId)
            .queryParam("followerId", followerId)
        .when()
            .delete()
        .then()
            .statusCode(Response.Status.NOT_FOUND.getStatusCode());
    }

    @Test
    @DisplayName("should return 404 when userId doesn't exist when unfollow user")
    void unfollowUser_shouldReturnOk_whenUserIdExist() {
        given()
            .contentType(ContentType.JSON)
            .pathParam("userId", userId)
            .queryParam("followerId", followerId)
        .when()
            .delete()
        .then()
            .statusCode(Response.Status.NO_CONTENT.getStatusCode());
    }
}