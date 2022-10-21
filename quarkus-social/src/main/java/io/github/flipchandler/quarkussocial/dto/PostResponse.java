package io.github.flipchandler.quarkussocial.dto;

import io.github.flipchandler.quarkussocial.domain.Post;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Builder
@Getter
@Setter
public class PostResponse {

    private String text;
    private LocalDateTime dateTime;

    public static PostResponse of (Post post) {
        return PostResponse.builder()
                .text(post.getText())
                .dateTime(post.getDateTime())
                .build();
    }
}
