package com.xiaowa.writingassistant.ai.controller;

import com.xiaowa.writingassistant.common.Result;
import com.xiaowa.writingassistant.dto.SettingsDTO;
import com.xiaowa.writingassistant.service.VivoApiService;
import com.xiaowa.writingassistant.ai.service.AiSettingCheckService;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/ai/settings")
public class AiSettingController {

private final VivoApiService vivoApiService;
private final AiSettingCheckService aiSettingCheckService;

// 构造函数注入
public AiSettingController(VivoApiService vivoApiService,
                           AiSettingCheckService aiSettingCheckService) {
    this.vivoApiService = vivoApiService;
    this.aiSettingCheckService = aiSettingCheckService;
}

/**
 * 检测文本和设定集之间的逻辑冲突
 */
@PostMapping("/check-conflict")
public Mono<String> checkSettingsConflict(@RequestBody SettingsDTO dto) {
    String prompt = String.format(
            "你是一个世界观架构师。请分析新加入的文本内容是否与已存在的设定集产生逻辑冲突。如果存在冲突，请指出冲突的具体设定和原因。\n\n已有设定集：%s\n\n新文本内容：\n%s",
            dto.getExistingSettings().toString(),
            dto.getDocumentContent()
    );
    return vivoApiService.getCompletion(prompt);
}

/**
 * 检测指定正文id的内容与其sourceDocumentId对应设定集的引用/冲突/遗漏等关系
 */
@PostMapping("/check-by-doc")
public Mono<String> checkSettingsByDocuments(@RequestBody List<Long> docIds) {
    return aiSettingCheckService.checkDocumentsWithSettings(docIds);
}

/**
 * AI助手对话悬浮窗接口
 */
@PostMapping("/dialog")
public Mono<Result<Object>> chatDialog(@RequestBody Map<String, String> req) {
    String prompt = req.getOrDefault("prompt", "");
    return vivoApiService.getCompletion(prompt)
            .map(reply -> {
                String content = "AI返回异常";
                try {
                    com.fasterxml.jackson.databind.JsonNode node =
                            new com.fasterxml.jackson.databind.ObjectMapper().readTree(reply);
                    if (node.has("data") && node.get("data").has("content")) {
                        content = node.get("data").get("content").asText();
                    }
                } catch (Exception e) {
                    content = reply;
                }
                return Result.<Object>success(Map.of("content", content));
            })
            .onErrorResume(e ->
                    Mono.just(Result.<Object>of(500, "AI助手接口异常: " + e.getMessage(), null))
            );
}

}
