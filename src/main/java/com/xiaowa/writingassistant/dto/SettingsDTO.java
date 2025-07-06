package com.xiaowa.writingassistant.dto;

import java.util.List;

public class SettingsDTO {

    private List<SettingItemSimpleDTO> existingSettings; // 已有设定集（对象数组）
    private String documentContent;

    // getters and setters
    public List<SettingItemSimpleDTO> getExistingSettings() {
        return existingSettings;
    }

    public void setExistingSettings(List<SettingItemSimpleDTO> existingSettings) {
        this.existingSettings = existingSettings;
    }

    public String getDocumentContent() {
        return documentContent;
    }

    public void setDocumentContent(String documentContent) {
        this.documentContent = documentContent;
    }
}
