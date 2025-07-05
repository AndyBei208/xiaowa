package com.xiaowa.writingassistant.ai.controller;

import com.xiaowa.writingassistant.ai.service.AiOutlineService;
import com.xiaowa.writingassistant.common.Result;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ai/outline")
public class AiOutlineController {
    private final AiOutlineService aiOutlineService;

    public AiOutlineController(AiOutlineService aiOutlineService) {
        this.aiOutlineService = aiOutlineService;
    }

    /**
     * 单章提取大纲接口
     * POST /api/ai/outline/{docId}/extract
     */
    @PostMapping("/{docId}/extract")
    public Result<String> extractOutline(@PathVariable Long docId) {
        String outline = aiOutlineService.extractOutlineText(docId);
        return Result.success(outline);
    }

    /**
     * 多章合并提取大纲接口
     * POST /api/ai/outline/extract-batch
     * 请求体： [1,2,3,4,5]
     */
    @PostMapping("/extract-batch")
    public Result<String> extractOutlineBatch(@RequestBody List<Long> docIds) {
        String outline = aiOutlineService.extractOutlineTextBatch(docIds);
        return Result.success(outline);
    }
}
