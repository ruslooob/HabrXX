package com.rm.habr.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class UsersPage {
    public static final int PAGE_SIZE = 10;
    private List<User> users;
    private int rowsCount;
}
