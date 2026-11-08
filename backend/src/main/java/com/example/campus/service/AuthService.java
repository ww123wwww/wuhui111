package com.example.campus.service;

import cn.dev33.satoken.stp.StpUtil;
import com.example.campus.dto.LoginForm;
import com.example.campus.dto.User;
import com.example.campus.exception.AppException;
import com.example.campus.repository.UserRepository;
import java.util.Map;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final UserRepository users;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    public AuthService(UserRepository users) { this.users = users; }

    public Map<String, Object> login(LoginForm form) {
        var account = users.findByUsername(form.username().strip());
        if (account.isEmpty() || !encoder.matches(form.password(), account.get().passwordHash())) {
            throw new AppException(401, "账号或密码错误");
        }
        StpUtil.login(account.get().id());
        return Map.of("token", StpUtil.getTokenValue(), "user", account.get().toUser());
    }

    public void logout() { StpUtil.logout(); }

    public User current() {
        var account = users.findById(StpUtil.getLoginIdAsLong());
        if (account.isEmpty()) {
            StpUtil.logout();
            throw new AppException(401, "账号不存在");
        }
        return account.get().toUser();
    }

    public User admin() {
        User user = current();
        if (!"ADMIN".equals(user.role())) throw new AppException(403, "仅管理员可执行此操作");
        return user;
    }

    public User student() {
        User user = current();
        if (!"STUDENT".equals(user.role())) throw new AppException(403, "请使用学生账号报名");
        return user;
    }
}
