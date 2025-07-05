package com.xiaowa.writingassistant.ai;

import java.util.List;

/**
 * AI 客户端接口：负责调用外部大模型进行内容提取
 */
public interface AIClient {
    /**
     * 提取文档中的角色设定
     * @param content 文档正文
     * @return 角色设定列表，每个 CharacterSetting 包含 name 与 description
     */
    List<CharacterSetting> extractCharacterSettings(String content);

    /**
     * 角色设定 DTO
     */
    class CharacterSetting {
        private String name;
        private String description;

        public CharacterSetting() {}
        public CharacterSetting(String name, String description) {
            this.name = name;
            this.description = description;
        }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
    }
}