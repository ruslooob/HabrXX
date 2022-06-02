package com.rm.habr.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.management.ConstructorParameters;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@Builder
@AllArgsConstructor
public class User {
    private Long id;
    private String fullName;
    private String email;
    @Size(min = 3, max = 20)
    private String login;
    @Size(min = 6, max = 40)
    private String password;
    private int karma;

    public User() {
    }

    public User(Long id) {
        this.id = id;
    }

    /*todo убрать отсюда этот конструктор*/
    public User(String login) {
        this.login = login;
    }
}
