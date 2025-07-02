package com.xiaowa.writingassistant.service;

import com.xiaowa.writingassistant.entity.UserAccount;
import com.xiaowa.writingassistant.mapper.UserAccountMapper;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Service
public class UserAccountService {
    private final UserAccountMapper mapper;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public UserAccountService(UserAccountMapper mapper) {
        this.mapper = mapper;
    }

    // 注册
    public UserAccount register(String username, String email, String rawPassword) {
        if (mapper.findByUsername(username) != null) {
            throw new IllegalArgumentException("用户名已存在");
        }
        if (mapper.findByEmail(email) != null) {
            throw new IllegalArgumentException("邮箱已被注册");
        }
        UserAccount user = new UserAccount();
        user.setUsername(username);
        user.setEmail(email);
        user.setPasswordHash(passwordEncoder.encode(rawPassword));
        mapper.insert(user);
        return user;
    }

    // 登录
    public UserAccount login(String usernameOrEmail, String rawPassword) {
        UserAccount user = mapper.findByUsername(usernameOrEmail);
        if (user == null) {
            user = mapper.findByEmail(usernameOrEmail);
        }
        if (user == null || !passwordEncoder.matches(rawPassword, user.getPasswordHash())) {
            throw new IllegalArgumentException("用户名或密码错误");
        }
        return user;
    }
}
