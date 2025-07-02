package com.xiaowa.writingassistant.entity;

import lombok.Data;

@Data
public class UserAccount {
    private Long id;
    private String username;
    private String email;
    private String passwordHash;
}
