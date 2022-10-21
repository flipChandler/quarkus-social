package io.github.flipchandler.quarkussocial.dto;

import io.github.flipchandler.quarkussocial.domain.Follower;
import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
public class FollowerResponse {

    private Long id;
    private String name;

    public FollowerResponse (Follower follower) {
        this(follower.getId(), follower.getFollower().getName()); // calling AllArgsConstructor
    }
}
