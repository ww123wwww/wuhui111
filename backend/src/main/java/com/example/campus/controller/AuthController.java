package com.example.campus.controller;

import com.example.campus.dto.LoginForm;
import com.example.campus.dto.User;
import com.example.campus.service.AuthService;
import jakarta.validation.Valid;
import java.util.Map;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService auth;
    public AuthController(AuthService auth) { this.auth = auth; }

    @PostMapping("/login")
    public Map<String, Object> login(@Valid @RequestBody LoginForm form) { return auth.login(form); }

    @PostMapping("/logout")
    public Map<String, String> logout() { auth.logout(); return Map.of("message", "已退出登录"); }

    @GetMapping("/me")
    public User me() { return auth.current(); }
}
