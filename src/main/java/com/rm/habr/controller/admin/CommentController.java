package com.rm.habr.controller.admin;

import com.rm.habr.model.AdminComment;
import com.rm.habr.repository.CommentRepository;
import com.rm.habr.service.CommentService;
import com.rm.habr.service.RightService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpSession;
import java.util.List;

@Controller("AdminCommentController")
@RequestMapping("/admin")
public class CommentController {
    private final RightService rightService;
    private final CommentService commentService;

    @Autowired
    public CommentController(RightService rightService, CommentService commentService) {
        this.rightService = rightService;
        this.commentService = commentService;
    }

    @GetMapping("/comments")
    public String getAllComments(Model model, HttpSession session) {
        if (!rightService.isUserAdmin(session)) {
            model.addAttribute("forbiddenMessage", "Вы не админ");
            return "forbidden";
        }

        List<AdminComment> comments = commentService.getAllAdminComments();
        model.addAttribute("comments", comments);
        return "admin/comments";
    }

    @DeleteMapping("/comments/{commentId}")
    public String deleteComment(@PathVariable Long commentId, HttpSession session, Model model) {
        if (!rightService.isUserAdmin(session)) {
            model.addAttribute("forbiddenMessage", "Вы не админ");
            return "forbidden";
        }
        commentService.deleteById(commentId);
        List<AdminComment> comments = commentService.getAllAdminComments();
        model.addAttribute("comments", comments);
        return "admin/comments";
    }
}
