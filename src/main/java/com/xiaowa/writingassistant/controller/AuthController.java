package com.xiaowa.writingassistant.controller;

import com.xiaowa.writingassistant.common.Result;
import com.xiaowa.writingassistant.entity.UserAccount;
import com.xiaowa.writingassistant.service.UserAccountService;
import lombok.Data;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserAccountService service;
    public AuthController(UserAccountService service) {
        this.service = service;
    }

    @PostMapping("/register")
    public Result<UserAccount> register(@RequestBody RegisterDTO dto) {
        UserAccount user = service.register(dto.getUsername(), dto.getEmail(), dto.getPassword());
        return Result.success(user);
    }

    @PostMapping("/login")
    public Result<UserAccount> login(@RequestBody LoginDTO dto) {
        UserAccount user = service.login(dto.getUsernameOrEmail(), dto.getPassword());
        return Result.success(user);
    }

    @Data
    static class RegisterDTO {
        private String username;
        private String email;
        private String password;
    }

    @Data
    static class LoginDTO {
        private String usernameOrEmail;
        private String password;
    }
}
