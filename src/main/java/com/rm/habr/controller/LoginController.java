package com.rm.habr.controller;

import com.rm.habr.model.User;
import com.rm.habr.service.RightService;
import com.rm.habr.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.Optional;

@Controller
@Slf4j
public class LoginController {
    private final UserService userService;
    private final RightService rightService;

    @Autowired
    public LoginController(UserService userService, RightService rightService) {
        this.userService = userService;
        this.rightService = rightService;
    }

    @GetMapping("/")
    public String redirectFromRoot() {
        return "redirect:/publications";
    }

        @GetMapping("/sign-in")
    public String loginPage(Model model) {
        model.addAttribute("user", new User());
        return "sign-in";
    }

    @PostMapping("/sign-in")
    public String signIn(@ModelAttribute User user, HttpSession httpSession, Model model) {
        Optional<Long> optionalUserId = userService.checkUserCanSignInAndGetId(user);
        if (optionalUserId.isEmpty()) {
            model.addAttribute("errorMessage", "Пользователь с таким логином не зарегистрирован");
            return "redirect:/sign-in";
        }
        httpSession.setAttribute("userId", optionalUserId.get());
        if (rightService.isUserAdmin(httpSession)) {
            httpSession.setAttribute("isAdmin", true);
        }
        return "redirect:/publications";
    }

    @GetMapping("/sign-up")
    public String signUpPage(Model model) {
        model.addAttribute("user", new User());
        return "sign-up";
    }

    @PostMapping("/sign-up")
    public String signUp(@ModelAttribute User user, HttpSession httpSession) {
        boolean isUserCanSignUp = userService.checkUserCanSignUp(user);
        if (isUserCanSignUp) {
            long userId = userService.save(user);
            httpSession.setAttribute("userId", userId);
            return "redirect:/publications";
        }
        return "redirect:/";
    }

    @GetMapping("/logout")
    public String logout(HttpSession httpSession) {
        httpSession.removeAttribute("userId");
        httpSession.removeAttribute("isAdmin");
        return "redirect:/publications";
    }

}
