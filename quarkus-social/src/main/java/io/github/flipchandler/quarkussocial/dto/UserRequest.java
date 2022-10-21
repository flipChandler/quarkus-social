package io.github.flipchandler.quarkussocial.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
public class UserRequest {

    @NotBlank
    private String name;

    @NotNull
    private Integer age;
}
