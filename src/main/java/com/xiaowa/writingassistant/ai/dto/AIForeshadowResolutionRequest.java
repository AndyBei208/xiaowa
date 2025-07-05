package com.xiaowa.writingassistant.ai.dto;

import lombok.Data;
import java.util.List;

@Data
public class AIForeshadowResolutionRequest {
    private List<ForeshadowSimple> existingForeshadows;
    private String documentContent;

    @Data
    public static class ForeshadowSimple {
        private Long id;
        private String title;
        private String description;
    }
}
