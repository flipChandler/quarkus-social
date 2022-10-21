package io.github.flipchandler.quarkussocial.controller;

import io.github.flipchandler.quarkussocial.domain.Follower;
import io.github.flipchandler.quarkussocial.domain.User;
import io.github.flipchandler.quarkussocial.dto.FollowerRequest;
import io.github.flipchandler.quarkussocial.dto.FollowerResponse;
import io.github.flipchandler.quarkussocial.dto.FollowersPerUserResponse;
import io.github.flipchandler.quarkussocial.repository.FollowerRepository;
import io.github.flipchandler.quarkussocial.repository.UserRepository;
import lombok.RequiredArgsConstructor;

import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.stream.Collectors;

@Path("api/users/{userId}/followers")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RequiredArgsConstructor
public class FollowerController {

    private final FollowerRepository followerRepository;
    private final UserRepository userRepository;

    @GET
    public Response getAllByUser(@PathParam("userId") Long userId) {
        User user = userRepository.findById(userId);
        if (user == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        List<Follower> followers = followerRepository.getAllByUser(userId);
        List<FollowerResponse> responseList = followers.stream()
                .map(FollowerResponse::new)
                .collect(Collectors.toList());

        FollowersPerUserResponse followersPerUser = FollowersPerUserResponse.of(responseList);
        return Response.ok(followersPerUser).build();
    }

    @PUT
    @Transactional
    public Response followUser(@PathParam("userId") Long userId,
                                FollowerRequest followerRequest) {
        if (userId.equals(followerRequest.getFollowerId())) {
            return Response.status(Response.Status.CONFLICT)
                    .entity("You can't follow yourself")
                    .build();
        }

        User user = userRepository.findById(userId);
        if (user == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        User follower = userRepository.findById(followerRequest.getFollowerId());

        boolean isFollowing = followerRepository.isFollowing(follower, user);

        if (!isFollowing) {
            Follower newFollower = Follower.of(user, follower);
            followerRepository.persist(newFollower);
        }

        return Response.status(Response.Status.NO_CONTENT).build();
    }

    @DELETE
    @Transactional
    public Response unfollowUser(@PathParam("userId") Long userId,
                                 @QueryParam("followerId") Long followerId) {
        User user = userRepository.findById(userId);
        if (user == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        followerRepository.deleteByFollowerAndUser(followerId, userId);

        return Response.status(Response.Status.NO_CONTENT).build();
    }
}
