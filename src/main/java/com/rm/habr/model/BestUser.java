package com.rm.habr.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BestUser {
    private final String login;
    private final Integer publicationsCount;
    private final Integer karma;
}
