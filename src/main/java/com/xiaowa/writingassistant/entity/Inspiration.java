package com.xiaowa.writingassistant.entity;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class Inspiration {
    private Long id;
    private Long userId;
    private String type;       // GLOBAL or ARTICLE
    private Long articleId;    // 当 type=ARTICLE 时不为 null
    private String title;
    private String content;
    private String address;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<InspirationTag> tags;
}

