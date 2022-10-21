package io.github.flipchandler.quarkussocial.controller;

import io.github.flipchandler.quarkussocial.domain.User;
import io.github.flipchandler.quarkussocial.dto.ResponseError;
import io.github.flipchandler.quarkussocial.dto.UserRequest;
import io.github.flipchandler.quarkussocial.repository.UserRepository;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import lombok.RequiredArgsConstructor;

import javax.transaction.Transactional;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Set;

import static io.github.flipchandler.quarkussocial.dto.ResponseError.UNPROCESSABLE_ENTITY_STATUS;

@Path("api/users")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;
    private final Validator validator;

    @GET
    public Response getAllUsers() {
        PanacheQuery<User> query = userRepository.findAll();
        return Response.ok(query.list()).build();
    }

    @GET
    @Path("{id}")
    public Response getUserById(@PathParam("id") Long id) {
        User user = userRepository.findById(id);

        if (user != null) {
            return Response.ok(user).build();
        }

        return Response.status(Response.Status.NOT_FOUND).build();
    }

    @POST
    @Transactional
    public Response createUser(UserRequest userRequest) {
        Set<ConstraintViolation<UserRequest>> violations = validator.validate(userRequest);
        if (!violations.isEmpty()) {
            return ResponseError.createFromValidation(violations)
                    .withSatusCode(UNPROCESSABLE_ENTITY_STATUS);
        }

        User user = User.of(userRequest.getName(), userRequest.getAge());

        userRepository.persist(user);

        return Response.status(Response.Status.CREATED.getStatusCode())
                .entity(user)
                .build();
    }

    @PUT
    @Path("{id}")
    @Transactional
    public Response updateUser(@PathParam("id") Long id, UserRequest userRequest) {
        User user = userRepository.findById(id);

        if (user != null) {
            user.setName(userRequest.getName());
            user.setAge(userRequest.getAge());

            return Response.noContent().build();
        }

        return Response.status(Response.Status.NOT_FOUND).build();
    }

    @DELETE
    @Path("{id}")
    @Transactional
    public Response deleteUser(@PathParam("id") Long id) {
        User user = userRepository.findById(id);

        if (user != null) {
            userRepository.deleteById(id);
            return Response.noContent().build();
        }

        return Response.status(Response.Status.NOT_FOUND).build();
    }
}
