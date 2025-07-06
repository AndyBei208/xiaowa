package com.xiaowa.writingassistant.controller;

import com.xiaowa.writingassistant.common.Result;
import com.xiaowa.writingassistant.entity.UserAccount;
import com.xiaowa.writingassistant.service.UserAccountService;
import com.xiaowa.writingassistant.security.CustomUserDetailsService; // 别忘了导入
import lombok.Data;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest; // 你的工程如果不是jakarta，请换成javax

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserAccountService service;
    private final CustomUserDetailsService userDetailsService; // 注入自定义UserDetailsService

    public AuthController(UserAccountService service, CustomUserDetailsService userDetailsService) {
        this.service = service;
        this.userDetailsService = userDetailsService;
    }

    @PostMapping("/register")
    public Result<UserAccount> register(@RequestBody RegisterDTO dto) {
        UserAccount user = service.register(dto.getUsername(), dto.getEmail(), dto.getPassword());
        return Result.success(user);
    }

    @PostMapping("/login")
    public Result<UserAccount> login(@RequestBody LoginDTO dto, HttpServletRequest request) {
        UserAccount user = service.login(dto.getUsernameOrEmail(), dto.getPassword());
        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUsername());
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);

        // 关键一步：写入Session
        request.getSession().setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, context);

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
