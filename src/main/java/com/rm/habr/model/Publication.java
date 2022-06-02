package com.rm.habr.model;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class Publication {
    private Long id;
    private User author;
    private String header;
    private String content;
    private LocalDateTime publishDateTime;
    private int viewsCount;
    private int karma;
    private String previewImagePath;
    private List<Genre> genres = new ArrayList<>();
    private List<Tag> tags = new ArrayList<>();
    private List<Comment> comments = new ArrayList<>();
}
