package com.rm.habr.controller.admin;

import com.rm.habr.dto.RegisterUserDto;
import com.rm.habr.model.User;
import com.rm.habr.model.UsersPage;
import com.rm.habr.service.RightService;
import com.rm.habr.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Optional;

@Controller("AdminUserController")
@RequestMapping("/admin")
public class UserController {
    private final RightService rightService;
    private final UserService userService;

    @Autowired
    public UserController(RightService rightService, UserService userService) {
        this.rightService = rightService;
        this.userService = userService;
    }

    @GetMapping("/users")
    public String getUsersPage(Model model, HttpSession session, @RequestParam(defaultValue = "1") Integer page) {
        if (!rightService.isUserAdmin(session)) {
            model.addAttribute("forbiddenMessage", "Вы не админ");
            return "forbidden";
        }

        UsersPage usersPage = userService.getUsersPage(page);
        model.addAttribute("users", usersPage.getUsers());
        model.addAttribute("currentPage", page);
        model.addAttribute("pagesCount", usersPage.getRowsCount() / UsersPage.PAGE_SIZE + 1);
        return "admin/users";
    }

    @GetMapping("/users/create-form")
    public String showCreateUserForm(Model model, HttpSession session) {
        if (!rightService.isUserAdmin(session)) {
            model.addAttribute("forbiddenMessage", "Вы не админ");
            return "forbidden";
        }
        model.addAttribute("user", new User());
        return "admin/user-form";
    }

    @PostMapping("/users")
    public String createUser(@ModelAttribute RegisterUserDto user,
                             BindingResult bindingResult,
                             @RequestParam(required = false) Boolean isAdmin,
                             HttpSession session,
                             Model model,
                             @RequestParam(defaultValue = "1") Integer page) {
        if (!rightService.isUserAdmin(session)) {
            model.addAttribute("forbiddenMessage", "Вы не админ");
            return "forbidden";
        }
        //todo сообщение об ошибках
        Optional<String> validateSignUpMsg = userService.validateSignUp(user);
        if (validateSignUpMsg.isEmpty()) {
            if (isAdmin != null) {
                userService.saveAdmin(user);
            } else {
                userService.save(user);
            }
        } else {
            bindingResult.addError(new ObjectError("login", validateSignUpMsg.get()));
            long userId = userService.save(user);
            session.setAttribute("userId", userId);
            return "redirect:/users";
        }
        UsersPage usersPage = userService.getUsersPage(page);
        model.addAttribute("users", usersPage.getUsers());
        model.addAttribute("currentPage", page);
        model.addAttribute("pagesCount", usersPage.getRowsCount() / UsersPage.PAGE_SIZE + 1);
        return "admin/users";
    }

    @DeleteMapping("/users/{userId}")
    public String deleteUser(@PathVariable Long userId,
                             HttpSession session,
                             Model model,
                             @RequestParam(defaultValue = "1") Integer page) {
        if (!rightService.isUserAdmin(session)) {
            model.addAttribute("forbiddenMessage", "Вы не админ");
            return "forbidden";
        }
        userService.delete(userId);
        UsersPage usersPage = userService.getUsersPage(page);
        model.addAttribute("users", usersPage.getUsers());
        model.addAttribute("currentPage", page);
        model.addAttribute("pagesCount", usersPage.getRowsCount() / UsersPage.PAGE_SIZE + 1);
        return "admin/users";
    }
}
