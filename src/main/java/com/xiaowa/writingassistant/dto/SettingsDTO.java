package com.xiaowa.writingassistant.dto;

import java.util.List;

public class SettingsDTO {

    private List<String> existingSettings; // 已有设定集（JSON 字符串列表）
    private String documentContent; // 新文本内容

    // getters and setters
    public List<String> getExistingSettings() {
        return existingSettings;
    }

    public void setExistingSettings(List<String> existingSettings) {
        this.existingSettings = existingSettings;
    }

    public String getDocumentContent() {
        return documentContent;
    }

    public void setDocumentContent(String documentContent) {
        this.documentContent = documentContent;
    }
}
