package com.xiaowa.writingassistant.ai.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xiaowa.writingassistant.ai.dto.AISettingExtractDTO;
import com.xiaowa.writingassistant.entity.Document;
import com.xiaowa.writingassistant.entity.SettingItem;
import com.xiaowa.writingassistant.service.DocumentService;
import com.xiaowa.writingassistant.mapper.SettingItemMapper;
import com.xiaowa.writingassistant.service.VivoApiService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class AiSettingExtractService {

    private final DocumentService documentService;
    private final SettingItemMapper itemMapper;
    private final VivoApiService vivoApiService;
    private final ObjectMapper objectMapper;

    public AiSettingExtractService(DocumentService documentService,
                                   SettingItemMapper itemMapper,
                                   VivoApiService vivoApiService,
                                   ObjectMapper objectMapper) {
        this.documentService = documentService;
        this.itemMapper = itemMapper;
        this.vivoApiService = vivoApiService;
        this.objectMapper = objectMapper;
    }

    /**
     * AI提取正文设定并批量写入指定设定集
     * @param docId 文档ID
     * @param collectionId 目标设定集ID
     * @return 已保存的设定条目
     */
    public List<SettingItem> extractAndSave(Long docId, Long collectionId) {
        Document doc = documentService.getDocument(docId);
        String content = doc.getContent();

        String prompt = String.format(
                "你是一位专业的网文世界观架构师。请从以下文本中，提取所有明确的实体设定，包括但不限于角色、地点、物品、组织、能力、概念等。请以JSON数组的格式返回，每个对象应包含'title'（设定名）和'content'（详细描述）两个字段。\n\n---\n文本内容：\n%s",
                content
        );

        String aiResponse = vivoApiService.getCompletion(prompt).block(); // 同步获取AI回复

        System.out.println("AI设定抽取返回: " + aiResponse);

        List<AISettingExtractDTO> aiList;
        try {
            JsonNode root = objectMapper.readTree(aiResponse);
            JsonNode dataNode = root.get("data");
            if (dataNode == null) throw new RuntimeException("AI无data字段");
            String contentJson = dataNode.get("content").asText();
            contentJson = extractJsonArray(contentJson);
            aiList = objectMapper.readValue(contentJson, new TypeReference<List<AISettingExtractDTO>>() {});
        } catch (Exception e) {
            System.out.println("AI设定抽取解析异常：\n" + aiResponse);
            e.printStackTrace();
            throw new RuntimeException("解析 AI 设定抽取失败", e);
        }

        // 写入数据库
        return aiList.stream().map(dto -> {
            SettingItem i = new SettingItem();
            i.setCollectionId(collectionId);
            i.setEntityName(dto.getTitle());
            i.setContent(dto.getContent());
            i.setSourceDocumentId(docId);
            itemMapper.insert(i);
            return itemMapper.findById(i.getId());
        }).collect(Collectors.toList());
    }

    // markdown/json 数组strip辅助
    private static String extractJsonArray(String raw) {
        if (raw == null) return "";
        Pattern pattern = Pattern.compile("```json\\s*(\\[.*?\\])\\s*```", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(raw);
        if (matcher.find()) return matcher.group(1);
        pattern = Pattern.compile("```\\s*(\\[.*?\\])\\s*```", Pattern.DOTALL);
        matcher = pattern.matcher(raw);
        if (matcher.find()) return matcher.group(1);
        raw = raw.trim();
        if (raw.startsWith("[")) return raw;
        throw new RuntimeException("AI内容不是合法JSON数组！原始内容：" + raw);
    }
}
