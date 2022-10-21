package io.github.flipchandler.quarkussocial.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class FieldError {

    private String field;
    private String message;
}
