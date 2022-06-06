package com.rm.habr.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
public class RegisterUserDto {
    @NotNull
    @Size(min = 3, max = 20)
    private String login;

    @NotNull
    @Size(min = 3, max = 50)
    private String email;

    @NotNull
    @Size(min = 5, max = 100)
    private String password;
}
