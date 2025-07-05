package com.xiaowa.writingassistant.ai.controller;

import com.xiaowa.writingassistant.ai.service.AiForeshadowService;
import com.xiaowa.writingassistant.common.Result;
import com.xiaowa.writingassistant.entity.Foreshadow;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import com.xiaowa.writingassistant.ai.dto.AIForeshadowResolutionRequest;

import java.util.Map;
@RestController
@RequestMapping("/api/ai/foreshadows")
public class AiForeshadowController {

    private final AiForeshadowService aiService;

    public AiForeshadowController(AiForeshadowService aiService) {
        this.aiService = aiService;
    }

    @PostMapping("/{docId}/identify")
    public Result<List<Foreshadow>> identifyFromDocument(@PathVariable Long docId) {
        List<Foreshadow> created = aiService.identifyAndSave(docId);
        return Result.success(created);
    }

    @PostMapping("/check-resolution")
    public Result<Map<Long, Boolean>> checkForeshadowResolution(@RequestBody AIForeshadowResolutionRequest req) {
        Map<Long, Boolean> result = aiService.checkForeshadowingResolution(req);
        return Result.success(result);
    }
}
