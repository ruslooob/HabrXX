package com.rm.habr.model;


import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AdminComment {
    private Long id;
    private User user;
    private Long publicationId;
    private String content;
    private LocalDateTime dateTime;
    private int karma;
    private String publicationHeader;
}
