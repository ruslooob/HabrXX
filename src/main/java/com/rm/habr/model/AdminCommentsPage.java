package com.rm.habr.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class AdminCommentsPage {
    public static final int PAGE_SIZE = 10;

    List<AdminComment> comments;
    int rowsCount;
}
