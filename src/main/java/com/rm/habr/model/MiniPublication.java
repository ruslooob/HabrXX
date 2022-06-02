package com.rm.habr.model;

import lombok.Data;

@Data
public class MiniPublication {
    private Long id;
    private String header;
    private Integer viewsCount;
    private Integer commentsCount;
}
