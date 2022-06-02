package com.rm.habr.controller.admin;

import com.rm.habr.model.User;
import com.rm.habr.service.RightService;
import com.rm.habr.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;

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
    public String getAllUsers(Model model, HttpSession httpSession) {
        if (!rightService.isUserAdmin(httpSession)) {
            model.addAttribute("forbiddenMessage", "Вы не админ");
            return "forbidden";
        }

        List<User> users = userService.findAllUsers();
        model.addAttribute("users", users);
        return "admin/users";
    }

    @GetMapping("/users/create-form")
    public String showCreateUserForm(Model model, HttpSession httpSession) {
        if (!rightService.isUserAdmin(httpSession)) {
            model.addAttribute("forbiddenMessage", "Вы не админ");
            return "forbidden";
        }
        model.addAttribute("user", new User());
        return "admin/user-form";
    }

    @PostMapping("/users")
    public String createUser(@ModelAttribute User user,
                             @RequestParam(required = false) Boolean isAdmin,
                             HttpSession httpSession,
                             Model model) {
        if (!rightService.isUserAdmin(httpSession)) {
            model.addAttribute("forbiddenMessage", "Вы не админ");
            return "forbidden";
        }
        //todo сообщение об ошибках
        boolean isUserCanSignUp = userService.checkUserCanSignUp(user);
        if (isUserCanSignUp) {
            if (isAdmin != null) {
                userService.saveAdmin(user);
            } else {
                userService.save(user);
            }
        }
        List<User> users = userService.findAllUsers();
        model.addAttribute("users", users);
        return "admin/users";
    }

    @DeleteMapping("/users/{userId}")
    public String deleteUser(@PathVariable Long userId, HttpSession httpSession, Model model) {
        if (!rightService.isUserAdmin(httpSession)) {
            model.addAttribute("forbiddenMessage", "Вы не админ");
            return "forbidden";
        }
        userService.delete(userId);
        List<User> users = userService.findAllUsers();
        model.addAttribute("users", users);
        return "admin/users";
    }
}
