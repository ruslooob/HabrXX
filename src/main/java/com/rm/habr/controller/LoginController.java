package com.rm.habr.controller;

import com.rm.habr.dto.LoginUserDto;
import com.rm.habr.dto.RegisterUserDto;
import com.rm.habr.model.User;
import com.rm.habr.service.RightService;
import com.rm.habr.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.Optional;

@Controller
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
    public String signIn(@Valid @ModelAttribute("user") LoginUserDto user,
                         BindingResult bindingResult,
                         HttpSession session,
                         Model model) {
        if (bindingResult.hasErrors()) {
            return "sign-in";
        }

        Optional<Long> optionalUserId = userService.checkUserCanSignInAndGetId(user);
        if (optionalUserId.isEmpty()) {
            model.addAttribute("errorMessage", "Пользователь с таким логином не зарегистрирован");
            return "redirect:/sign-in";
        }
        session.setAttribute("userId", optionalUserId.get());
        if (rightService.isUserAdmin(session)) {
            session.setAttribute("isAdmin", true);
        }
        return "redirect:/publications";
    }

    @GetMapping("/sign-up")
    public String signUpPage(Model model) {
        model.addAttribute("user", new User());
        return "sign-up";
    }

    @PostMapping("/sign-up")
    public String signUp(@Valid @ModelAttribute("user") RegisterUserDto user,
                         BindingResult bindingResult,
                         HttpSession session) {
        if (bindingResult.hasErrors()) {
            return "sign-up";
        }

        Optional<String> validationMsg = userService.validateSignUp(user);
        if (validationMsg.isPresent()) {
            bindingResult.addError(new ObjectError("login", validationMsg.get()));
            long userId = userService.save(user);
            session.setAttribute("userId", userId);
            return "redirect:/publications";
        }
        return "redirect:/";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.removeAttribute("userId");
        session.removeAttribute("isAdmin");
        return "redirect:/publications";
    }

}
