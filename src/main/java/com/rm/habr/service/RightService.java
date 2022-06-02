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

    public boolean isUserAdmin(HttpSession httpSession) {
        if (!isUserAuthored(httpSession)) {
            return false;
        }

        return userService.isUserAdmin((Long) httpSession.getAttribute("userId"));
    }

    // todo что-то тут не чисто (сессию вроде бы не нужно проверять)
    public boolean isUserAuthored(HttpSession httpSession) {
        Long userId = (Long) (httpSession.getAttribute("userId") == null ? null : httpSession.getAttribute("userId"));
        if (userId == null) {
            return false;
        }
        User userById = userService.findUserById(userId);
        return userById != null;
    }
}
