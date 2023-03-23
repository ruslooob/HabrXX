package com.rm.habr.controller.admin;

import com.rm.habr.model.AdminCommentsPage;
import com.rm.habr.service.CommentService;
import com.rm.habr.service.RightService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

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
    public String getCommentsPage(Model model, HttpSession session, @RequestParam(defaultValue = "1") Integer page) {
        if (!rightService.isUserAdmin(session)) {
            model.addAttribute("forbiddenMessage", "Вы не админ");
            return "forbidden";
        }
        AdminCommentsPage commentsPage = commentService.getAllAdminComments(page);
        model.addAttribute("comments", commentsPage.getComments());
        model.addAttribute("currentPage", page);
        model.addAttribute("pagesCount", commentsPage.getRowsCount() / AdminCommentsPage.PAGE_SIZE + 1);
        return "admin/comments";
    }

    @DeleteMapping("/comments/{commentId}")
    public String deleteComment(@PathVariable Long commentId, HttpSession session, Model model, @RequestParam(defaultValue = "1") Integer page) {
        if (!rightService.isUserAdmin(session)) {
            model.addAttribute("forbiddenMessage", "Вы не админ");
            return "forbidden";
        }
        commentService.deleteById(commentId);
        AdminCommentsPage commentsPage = commentService.getAllAdminComments(page);
        model.addAttribute("comments", commentsPage.getComments());
        model.addAttribute("currentPage", page);
        model.addAttribute("pagesCount", commentsPage.getRowsCount() / AdminCommentsPage.PAGE_SIZE + 1);
        return "admin/comments";
    }
}
