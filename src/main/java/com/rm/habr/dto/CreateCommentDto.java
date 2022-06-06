package com.rm.habr.dto;


import com.rm.habr.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CreateCommentDto {
    private Long publicationId;
    private String content;
    private User author;
}