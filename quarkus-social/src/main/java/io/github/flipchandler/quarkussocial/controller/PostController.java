package io.github.flipchandler.quarkussocial.controller;

import io.github.flipchandler.quarkussocial.domain.Post;
import io.github.flipchandler.quarkussocial.domain.User;
import io.github.flipchandler.quarkussocial.dto.PostRequest;
import io.github.flipchandler.quarkussocial.dto.PostResponse;
import io.github.flipchandler.quarkussocial.repository.FollowerRepository;
import io.github.flipchandler.quarkussocial.repository.PostRepository;
import io.github.flipchandler.quarkussocial.repository.UserRepository;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Sort;
import lombok.RequiredArgsConstructor;

import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.stream.Collectors;

@Path("api/users/{userId}/posts")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RequiredArgsConstructor
public class PostController {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final FollowerRepository followerRepository;

    @GET
    public Response listsPosts(@PathParam("userId") Long userId,
                               @HeaderParam("followerId") Long followerId) {
        User user = userRepository.findById(userId);
        if (user == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        if (followerId == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("You forgot the followerId header")
                    .build();
        }

        User follower = userRepository.findById(followerId);
        if (follower == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("This followerId does not exist")
                    .build();
        }

        boolean isFollowing = followerRepository.isFollowing(follower, user);
        if (!isFollowing) {
            return Response.status(Response.Status.FORBIDDEN)
                    .entity("You can't see these posts")
                    .build();
        }

        PanacheQuery<Post> query = postRepository.find("user",
                Sort.by("dateTime", Sort.Direction.Descending),
                user);
        List<PostResponse> posts = query.list()
                .stream()
                .map(PostResponse::of)
                .collect(Collectors.toList());

        return Response.ok(posts).build();
    }

    @POST
    @Transactional
    public Response savePost(@PathParam("userId") Long userId, PostRequest postRequest) {
        User user = userRepository.findById(userId);
        if (user == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        Post post = new Post();
        post.setText(postRequest.getText());
        post.setUser(user);

        postRepository.persist(post);

        return Response.status(Response.Status.CREATED).build();
    }
}
