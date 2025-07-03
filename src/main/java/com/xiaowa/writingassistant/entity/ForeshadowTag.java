package com.xiaowa.writingassistant.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ForeshadowTag {
    private Long id;
    private Long foreshadowId;
    private String tagType;        // BURY_LOCATION, RECOVER_LOCATION, etc.
    private String label;
    private String targetModule;   // e.g. "outline", "doc"
    private Long targetId;
    private String targetUrl;
    private LocalDateTime createdAt;
}