package com.rm.habr.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RegisterUserDto {
    private String fullName;
    private String login;
    private String email;
    private String password;
}
