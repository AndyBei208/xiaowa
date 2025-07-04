package com.xiaowa.writingassistant.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class InspirationTag {
    private Long id;
    private Long inspirationId;
    private String name;
    private LocalDateTime createdAt;
}
