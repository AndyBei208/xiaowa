package com.xiaowa.writingassistant.ai.controller;

import com.xiaowa.writingassistant.common.Result;
import com.xiaowa.writingassistant.entity.SettingItem;
import com.xiaowa.writingassistant.ai.service.AiSettingExtractService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ai/settings")
public class AiSettingExtractController {

    private final AiSettingExtractService aiSettingExtractService;

    public AiSettingExtractController(AiSettingExtractService aiSettingExtractService) {
        this.aiSettingExtractService = aiSettingExtractService;
    }

    // 传入文档ID和设定集ID，自动提取正文设定并存入设定集
    @PostMapping("/extract/{docId}/to/{collectionId}")
    public Result<List<SettingItem>> extractToCollection(@PathVariable Long docId,
                                                         @PathVariable Long collectionId) {
        List<SettingItem> created = aiSettingExtractService.extractAndSave(docId, collectionId);
        return Result.success(created);
    }
}
