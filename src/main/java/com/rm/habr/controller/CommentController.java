package com.rm.habr.controller;

import com.rm.habr.dto.CreateCommentDto;
import com.rm.habr.model.Comment;
import com.rm.habr.model.User;
import com.rm.habr.repository.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Controller
@RequestMapping("/publications")
public class CommentController {

    private final CommentRepository commentRepository;

    @Autowired
    public CommentController(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    @PostMapping("/{publicationId}/comments")
    public String createComment(@PathVariable long publicationId, @ModelAttribute CreateCommentDto comment, HttpSession session) {
        comment.setPublicationId(publicationId);
        comment.setAuthor(new User((Long) session.getAttribute("userId")));
        commentRepository.insert(comment);

        return "redirect:/publications/" + publicationId;
    }

    @DeleteMapping("/{publicationId}/comments/{id}")
    public String deleteComment(@PathVariable long publicationId, @PathVariable long id) {
        commentRepository.delete(id);
        return "redirect:/publications/" + publicationId;
    }

}
