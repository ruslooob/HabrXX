package com.rm.habr.model;

import lombok.Data;

@Data
public class Genre {

    private Long id;
    private String name;

    public Genre() {
    }

    public Genre(Long id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return name;
    }
}