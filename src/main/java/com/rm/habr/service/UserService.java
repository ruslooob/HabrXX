package com.rm.habr.service;

import com.rm.habr.dto.LoginUserDto;
import com.rm.habr.dto.RegisterUserDto;
import com.rm.habr.model.User;
import com.rm.habr.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;

@Service
/*todo перенести методы отсюда в rightSErvice*/
public class UserService {
    Logger log = LoggerFactory.getLogger(UserService.class);
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }


    public Optional<String> validateSignUp(RegisterUserDto user) {
        // check that user login, email does not repeat
        Optional<User> optionalUser = userRepository.findByLogin(user.getLogin());
        if (optionalUser.isEmpty())  {
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
        return userRepository.insert(user);
    }

    public void saveAdmin(RegisterUserDto user) {
        Long saveId = save(user);
        userRepository.saveAdmin(saveId);
    }

    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    public void delete(long userId) {
        userRepository.delete(userId);
    }
}
