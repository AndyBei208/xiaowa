package com.xiaowa.writingassistant.controller;

import com.xiaowa.writingassistant.dto.AIChatRequest;
import com.xiaowa.writingassistant.common.Result;
import com.xiaowa.writingassistant.service.VivoApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/ai")
public class AiChatController {

    @Autowired
    private VivoApiService vivoApiService;

    @PostMapping("/chat")
    public Mono<Result<String>> chat(@RequestBody AIChatRequest request) {
        return vivoApiService.getCompletion(request.getPrompt())
                .map(Result::success)
                .onErrorResume(e -> Mono.just(Result.fail("AI对话失败: " + e.getMessage())));
    }
}
