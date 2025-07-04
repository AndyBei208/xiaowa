package com.xiaowa.writingassistant.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SettingItem {
    private Long id;
    private Long collectionId;
    private String entityName;
    private String content;
    private Long sourceDocumentId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
