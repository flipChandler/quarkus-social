package io.github.flipchandler.quarkussocial.controller;

import io.github.flipchandler.quarkussocial.dto.ResponseError;
import io.github.flipchandler.quarkussocial.dto.UserRequest;
import io.github.flipchandler.quarkussocial.repository.UserRepository;
import io.quarkus.test.common.http.TestHTTPResource;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.*;

import javax.transaction.Transactional;
import javax.ws.rs.core.Response;
import java.net.URL;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
@Transactional
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@RequiredArgsConstructor
class UserControllerTest {

    private static UserRepository userRepository;

    @TestHTTPResource("/api/users")
    URL apiUrl;

    @Test
    @DisplayName("should create an user successfully")
    @Order(1)
    void createUser_shouldReturnCreated_whenUserRequestOk() {
        UserRequest user = new UserRequest();
        user.setName("TEST USER");
        user.setAge(30);

        var response = given()
                                    .contentType(ContentType.JSON)
                                    .body(user)
                                .when()
                                    .post(apiUrl)
                                .then()
                                    .extract()
                                    .response();

        assertEquals(Response.Status.CREATED.getStatusCode(), response.statusCode());
        assertNotNull(response.jsonPath().getString("id"));
    }

    @Test
    @DisplayName("should not create an user when missing age")
    @Order(2)
    void createUser_shouldReturnUnprocessableEntity_whenMissingAge() {
        UserRequest user = new UserRequest();
        user.setName("TEST USER");

        var response = given()
                                    .contentType(ContentType.JSON)
                                    .body(user)
                                .when()
                                    .post(apiUrl)
                                .then()
                                    .extract()
                                    .response();

        assertEquals(ResponseError.UNPROCESSABLE_ENTITY_STATUS, response.statusCode());
        assertEquals("Validation Error", response.jsonPath().getString("message"));

        List<Map<String, String>> errors = response.jsonPath().getList("errors");
        assertEquals("é obrigatório", errors.get(0).get("message"));
    }

    @Test
    @DisplayName("should not create an user when missing name")
    @Order(3)
    void createUser_shouldReturnUnprocessableEntity_whenMissingName() {
        UserRequest user = new UserRequest();
        user.setAge(30);

        var response = given()
                                    .contentType(ContentType.JSON)
                                    .body(user)
                                .when()
                                    .post(apiUrl)
                                .then()
                                    .extract()
                                    .response();

        assertEquals(ResponseError.UNPROCESSABLE_ENTITY_STATUS, response.statusCode());
        assertNull(response.jsonPath().getString("id"));

        assertEquals("Validation Error", response.jsonPath().getString("message"));

        List<Map<String, String>> errors = response.jsonPath().getList("errors");
        assertEquals("é obrigatório", errors.get(0).get("message"));
    }

    @Test
    @DisplayName("should return ok when it's called")
    @Order(4)
    void createUser_shouldReturnOk_whenItsCalled() {
        given()
                .contentType(ContentType.JSON)
        .when()
                .get(apiUrl)
        .then()
                .statusCode(Response.Status.OK.getStatusCode())
                        .body("size()", is(1));
    }
}