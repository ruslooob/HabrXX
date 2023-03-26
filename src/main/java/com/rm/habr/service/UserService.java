package com.rm.habr.service;

import com.rm.habr.dto.LoginUserDto;
import com.rm.habr.dto.RegisterUserDto;
import com.rm.habr.model.User;
import com.rm.habr.model.UsersPage;
import com.rm.habr.queue.RabbitSender;
import com.rm.habr.queue.RegisteredUserMessage;
import com.rm.habr.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import javax.validation.constraints.NotNull;
import java.util.Optional;

@Service
@AllArgsConstructor
@Slf4j
/*todo перенести методы отсюда в rightSErvice*/
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RabbitSender rabbitSender;

    public Optional<String> validateSignUp(RegisterUserDto user) {
        // check that user login, email does not repeat
        Optional<User> optionalUser = userRepository.findByLogin(user.getLogin());
        if (optionalUser.isEmpty()) {
            return Optional.of("Пользователь с таким логином уже существует!");
        }
        return Optional.empty();
    }

    public Optional<Long> checkUserCanSignInAndGetId(LoginUserDto user) {
        Optional<User> optionalUser = userRepository.findByLogin(user.getLogin());
        if (optionalUser.isEmpty()) {
            return Optional.empty();
        }
        User dbUser = optionalUser.get();
        if (passwordEncoder.matches(user.getPassword(), dbUser.getPassword())) {
            return Optional.of(dbUser.getId());
        } else {
            return Optional.empty();
        }
    }

    public String hashPassword(String password) {
        return passwordEncoder.encode(password);
    }


    public User findUserById(@NotNull Long id) {
        return userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
    }

    public boolean isUserAdmin(@NotNull Long id) {
        return userRepository.isUserAdmin(id);
    }

    public Long save(RegisterUserDto user) {
        String hashedPassword = hashPassword(user.getPassword());
        user.setPassword(hashedPassword);
        long id = userRepository.insert(user);
        rabbitSender.send(new RegisteredUserMessage(user.getEmail(), user.getLogin()));
        return id;
    }

    public void saveAdmin(RegisterUserDto user) {
        Long saveId = save(user);
        userRepository.saveAdmin(saveId);
    }

    public UsersPage getUsersPage(Integer page) {
        return new UsersPage(userRepository.findPage(page), userRepository.getUsersCount());
    }

    public void fillGetUsersPageModel(Integer page, Model model) {
        UsersPage usersPage = getUsersPage(page);
        model.addAttribute("users", usersPage.getUsers());
        model.addAttribute("currentPage", page);
        model.addAttribute("pagesCount", usersPage.getRowsCount() / (UsersPage.PAGE_SIZE + 1) + 1);
    }

    public void delete(long userId) {
        userRepository.delete(userId);
    }
}
