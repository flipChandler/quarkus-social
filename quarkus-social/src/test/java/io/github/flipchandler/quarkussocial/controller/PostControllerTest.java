package io.github.flipchandler.quarkussocial.controller;

import io.github.flipchandler.quarkussocial.domain.Follower;
import io.github.flipchandler.quarkussocial.domain.Post;
import io.github.flipchandler.quarkussocial.domain.User;
import io.github.flipchandler.quarkussocial.dto.PostRequest;
import io.github.flipchandler.quarkussocial.repository.FollowerRepository;
import io.github.flipchandler.quarkussocial.repository.PostRepository;
import io.github.flipchandler.quarkussocial.repository.UserRepository;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import javax.transaction.Transactional;
import javax.ws.rs.core.Response;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

@QuarkusTest
@TestHTTPEndpoint(PostController.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@RequiredArgsConstructor
public class PostControllerTest {

    private final UserRepository userRepository;
    private final FollowerRepository followerRepository;
    private final PostRepository postRepository;
    Long userId;
    Long userNotFollowerId;
    Long userFollowerId;

    @BeforeEach
    @Transactional
    void setUp() {
        User user = User.of("TEST USER", 35);
        userRepository.persist(user);
        userId = user.getId();

        User userNotFollower = User.of("TEST NOT USER FOLLOWER", 22);
        userRepository.persist(userNotFollower);
        userNotFollowerId = userNotFollower.getId();

        User userFollower = User.of("TEST USER FOLLOWER", 25);
        userRepository.persist(userFollower);
        userFollowerId = userFollower.getId();

        Follower follower = Follower.of(user, userFollower);
        followerRepository.persist(follower);

        Post post = Post.of("Ola, tudo bem?", user);
        postRepository.persist(post);
    }

    @Test
    @DisplayName("should return ok when user exists")
    void createPost_shouldReturnOk_whenUserExists() {
        PostRequest postRequest = new PostRequest();
        postRequest.setText("TEST TEXT");

        given()
                .contentType(ContentType.JSON)
                .pathParam("userId", userId)
                .body(postRequest)
        .when()
                .post()
        .then()
                .statusCode(Response.Status.CREATED.getStatusCode());
    }

    @Test
    @DisplayName("should return not found when user does not exist")
    void createPost_shouldReturnNotFound_whenUserDoesNotExist() {
        PostRequest postRequest = new PostRequest();
        postRequest.setText("TEST TEXT");

        int InexistentUserId = 999;

        given()
                .contentType(ContentType.JSON)
                .pathParam("userId", InexistentUserId)
                .body(postRequest)
        .when()
                .post()
        .then()
                .statusCode(Response.Status.NOT_FOUND.getStatusCode());
    }

    @Test
    @DisplayName("should return 404 when user does not exist")
    void listsPosts_shouldReturnNotFound_whenUserDoesNotExist() {
        int InexistentUserId = 999;

        given()
                .pathParam("userId", InexistentUserId)
        .when()
                .get()
        .then()
                .statusCode(Response.Status.NOT_FOUND.getStatusCode());
    }

    @Test
    @DisplayName("should return 400 when header not sent")
    void listsPosts_shouldReturnBadRequest_whenHeaderNotSent() {
        given()
                .pathParam("userId", userId)
        .when()
                .get()
        .then()
                .statusCode(Response.Status.BAD_REQUEST.getStatusCode())
                .body(is("You forgot the followerId header"));
    }

    @Test
    @DisplayName("should return 400 when follower doesn't exist")
    void listsPosts_shouldReturnBadRequest_whenFollowerDoesNotExist() {
        var inexistentFollowerId = 999;

        given()
                .pathParam("userId", userId)
                .header("followerId", inexistentFollowerId)
        .when()
                .get()
        .then()
                .statusCode(Response.Status.BAD_REQUEST.getStatusCode())
                .body(is("This followerId does not exist"));
    }

    @Test
    @DisplayName("should return 403 when user is not a follower")
    void listsPosts_shouldReturnForbidden_whenUserIsNotAFollower() {
        given()
                .pathParam("userId", userId)
                .header("followerId", userNotFollowerId)
        .when()
                .get()
        .then()
                .statusCode(Response.Status.FORBIDDEN.getStatusCode())
                .body(is("You can't see these posts"));
    }

    @Test
    @DisplayName("should return 200 when user is not a follower")
    void listsPosts_shouldReturnOk_whenUserIsNotAFollower() {
        var response = given()
                .pathParam("userId", userId)
                .header("followerId", userFollowerId)
        .when()
                .get()
        .then()
                .statusCode(Response.Status.OK.getStatusCode())
                .body("size()", is(1));     // one post
    }
}