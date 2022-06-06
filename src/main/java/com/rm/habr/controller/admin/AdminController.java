package com.rm.habr.controller.admin;

import com.rm.habr.model.AdminComment;
import com.rm.habr.model.User;
import com.rm.habr.service.*;
import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.JRException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

@Slf4j
@Controller
@RequestMapping("/admin")
public class AdminController {
    private final RightService rightService;

    @Autowired
    public AdminController(RightService rightService) {
        this.rightService = rightService;
    }

    @GetMapping
    public String getAdminPage(Model model, HttpSession session) {
        if (!rightService.isUserAdmin(session)) {
            model.addAttribute("forbiddenMessage", "Вы не админ");
            return "forbidden";
        }
        return "admin/home";
    }
}
