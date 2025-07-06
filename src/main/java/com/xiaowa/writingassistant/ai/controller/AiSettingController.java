package com.xiaowa.writingassistant.ai.controller;

import com.xiaowa.writingassistant.common.Result;
import com.xiaowa.writingassistant.dto.SettingsDTO;
import com.xiaowa.writingassistant.service.VivoApiService;
import com.xiaowa.writingassistant.ai.service.AiSettingCheckService;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/api/ai/settings")
public class AiSettingController {

    private final VivoApiService vivoApiService;
    private final AiSettingCheckService aiSettingCheckService;

    // 构造函数注入 VivoApiService 和 AiSettingCheckService
    public AiSettingController(VivoApiService vivoApiService,
                               AiSettingCheckService aiSettingCheckService) {
        this.vivoApiService = vivoApiService;
        this.aiSettingCheckService = aiSettingCheckService;
    }

    /**
     * 检测文本和设定集之间的逻辑冲突
     * @param dto 包含待分析的文本和已有设定
     * @return 返回 AI 分析出的逻辑冲突报告（JSON格式字符串）
     */
    @PostMapping("/check-conflict")
    public Mono<String> checkSettingsConflict(@RequestBody SettingsDTO dto) {
        // 构建用于与AI大模型交互的 prompt
        String prompt = String.format(
                "你是一个世界观架构师。请分析新加入的文本内容是否与已存在的设定集产生逻辑冲突。如果存在冲突，请指出冲突的具体设定和原因。\n\n已有设定集：%s\n\n新文本内容：\n%s",
                dto.getExistingSettings().toString(),
                dto.getDocumentContent()
        );
        // 调用 VivoApiService 处理逻辑冲突分析
        return vivoApiService.getCompletion(prompt);
    }

    /**
     * 新增：检测指定正文id的内容与其sourceDocumentId对应设定集的引用/冲突/遗漏等关系
     * @param docIds 正文id列表
     * @return 返回 AI 分析结果（JSON格式字符串）
     */
    @PostMapping("/check-by-doc")
    public Mono<String> checkSettingsByDocuments(@RequestBody List<Long> docIds) {
        return aiSettingCheckService.checkDocumentsWithSettings(docIds);
    }
}
