package io.github.flipchandler.quarkussocial.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Builder
@Getter
@Setter
public class FollowersPerUserResponse {

    private Integer followersCount;
    private List<FollowerResponse> content;

    public static FollowersPerUserResponse of (List<FollowerResponse> followers) {
        return FollowersPerUserResponse.builder()
                .followersCount(followers.size())
                .content(followers)
                .build();
    }
}
