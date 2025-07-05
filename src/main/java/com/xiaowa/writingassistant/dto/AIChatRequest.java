package com.xiaowa.writingassistant.dto;

public class AIChatRequest {
    private String prompt;

    // 可以加更多字段，如历史对话 history，但基础功能只需要 prompt
    public String getPrompt() {
        return prompt;
    }
    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }
}
