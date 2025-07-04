package com.xiaowa.writingassistant.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SettingCollection {
    private Long id;
    private Long userId;
    private String name;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
