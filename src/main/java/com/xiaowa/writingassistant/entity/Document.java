package com.xiaowa.writingassistant.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Document {
    private Long id;
    private Long userId;           // 创建者用户 ID
    private String type;           // 正文 / 大纲 / 细纲 / 灵感 等
    private String title;          // 标题
    private String content;        // 富文本/Markdown 内容
    private String status;         // 状态：DRAFT / PUBLISHED
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
