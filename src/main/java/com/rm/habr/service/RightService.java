package com.rm.habr.service;

import com.rm.habr.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;

@Service
@Slf4j
public class RightService {
    private final UserService userService;

    public RightService(UserService userService) {
        this.userService = userService;
    }

    public boolean isUserAdmin(HttpSession session) {
        if (!isUserAuthored(session)) {
            return false;
        }

        return userService.isUserAdmin((Long) session.getAttribute("userId"));
    }

    /* todo разделить этот метод на 2 части, один проверяет, что у пользователя есть аккаунт, а торой проверяет сессию */
    public boolean isUserAuthored(HttpSession session) {
        Long userId = (Long) (session.getAttribute("userId") == null ? null : session.getAttribute("userId"));
        if (userId == null) {
            return false;
        }
        User userById = userService.findUserById(userId);
        return userById != null;
    }
}
