package com.example.WOTER.Controllers;

import com.example.WOTER.DTO.RegisterRequest;
import com.example.WOTER.Entities.User;
import com.example.WOTER.Repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthController(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // GET / → форма входа/регистрации
    @GetMapping("/")
    public String loginRegisterPage(Model model) {
        model.addAttribute("user", new User());
        return "index"; // index.html
    }

    // POST /register → регистрация
    @PostMapping("/register")
    public String register(@ModelAttribute User user, Model model) {
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            model.addAttribute("error", "Пользователь уже существует!");
            return "index";
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // ⚡ назначаем роль по умолчанию
        user.setRole("USER");

        userRepository.save(user);

        model.addAttribute("success", "Регистрация успешна! Теперь войдите.");
        return "index";
    }
}
