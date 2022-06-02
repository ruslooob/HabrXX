package com.rm.habr.model;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class Comment {

    private Long id;
    private User user;
    private Long publicationId;
    private String content;
    private LocalDateTime dateTime;
    private int karma;

}
