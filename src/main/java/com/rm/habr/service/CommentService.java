package com.rm.habr.service;

import com.rm.habr.model.AdminComment;
import com.rm.habr.model.Comment;
import com.rm.habr.repository.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.util.List;

@Service
public class CommentService {
    private final CommentRepository commentRepository;

    @Autowired
    public CommentService(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    public List<AdminComment> getAllAdminComments() {
        return commentRepository.findAllComments();
    }

    public void deleteById(Long commentId) {
        commentRepository.delete(commentId);
    }

    public void findCommentsByPublicationId(long publicationId, Model model) {
        List<Comment> comments = commentRepository.findCommentsByPublicationId(publicationId);
        model.addAttribute("comments", comments);
    }
}
