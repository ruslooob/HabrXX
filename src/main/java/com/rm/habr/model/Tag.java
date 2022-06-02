package com.rm.habr.model;

import lombok.Data;

@Data
public class Tag {

    private Long id;
    private String name;

    public Tag() {
    }

    public Tag(Long id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return name;
    }
}
