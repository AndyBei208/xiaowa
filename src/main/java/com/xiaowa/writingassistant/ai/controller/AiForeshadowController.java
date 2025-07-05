package com.xiaowa.writingassistant.ai.controller;

import com.xiaowa.writingassistant.ai.service.AiForeshadowService;
import com.xiaowa.writingassistant.common.Result;
import com.xiaowa.writingassistant.entity.Foreshadow;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
}
