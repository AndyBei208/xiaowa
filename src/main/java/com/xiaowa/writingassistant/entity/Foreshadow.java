package com.xiaowa.writingassistant.entity;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class Foreshadow {
    private Long id;
    private Long userId;
    private String title;
    private String description;
    private String status;         // PENDING, ACTIVE, RECOVERED
    private Integer remainChapters;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // 关联标签列表
    private List<ForeshadowTag> tags;
}

