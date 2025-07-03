package com.xiaowa.writingassistant.security;

import com.xiaowa.writingassistant.entity.UserAccount;
import com.xiaowa.writingassistant.mapper.UserAccountMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private static final Logger logger = LoggerFactory.getLogger(CustomUserDetailsService.class);

    private final UserAccountMapper mapper;

    public CustomUserDetailsService(UserAccountMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 日志输出，确认是否被调用
        logger.info("CustomUserDetailsService: loading user by username = {}", username);

        UserAccount user = mapper.findByUsername(username);
        if (user == null) {
            logger.warn("CustomUserDetailsService: user not found = {}", username);
            throw new UsernameNotFoundException("用户不存在: " + username);
        }

        // 返回 Spring Security 需要的 UserDetails 对象
        return User.builder()
                .username(user.getUsername())
                .password(user.getPasswordHash())  // 已使用 BCrypt 加密
                .roles("USER")                     // 根据需要自定义角色
                .build();
    }
}
