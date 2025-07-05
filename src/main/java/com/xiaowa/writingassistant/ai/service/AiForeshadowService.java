package com.xiaowa.writingassistant.ai.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xiaowa.writingassistant.ai.dto.AIForeshadowDTO;
import com.xiaowa.writingassistant.entity.Document;
import com.xiaowa.writingassistant.entity.Foreshadow;
import com.xiaowa.writingassistant.service.DocumentService;
import com.xiaowa.writingassistant.service.ForeshadowService;
import com.xiaowa.writingassistant.service.VivoApiService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class AiForeshadowService {

    private final DocumentService documentService;
    private final ForeshadowService foreshadowService;
    private final VivoApiService vivoApiService;
    private final ObjectMapper objectMapper;

    public AiForeshadowService(
            DocumentService documentService,
            ForeshadowService foreshadowService,
            VivoApiService vivoApiService,
            ObjectMapper objectMapper) {
        this.documentService = documentService;
        this.foreshadowService = foreshadowService;
        this.vivoApiService = vivoApiService;
        this.objectMapper = objectMapper;
    }

    /**
     * 1. 读取 Document.content
     * 2. 构造 Prompt
     * 3. 调用AI模型Service获取JSON数组
     * 4. 解析为AIForeshadowDTO列表
     * 5. 转为Foreshadow实体并入库（状态PENDING）
     */
    public List<Foreshadow> identifyAndSave(Long docId) {
        Document doc = documentService.getDocument(docId);
        String content = doc.getContent();

        String prompt = String.format(
                "你是一位叙事结构敏感的读者。请从以下文本中识别所有可能构成伏笔的句子或情节，" +
                        "返回JSON数组，每个元素有{\"title\": \"...\", \"description\": \"...\"}：\n\n%s",
                content
        );

        String aiResponse = vivoApiService.getCompletion(prompt).block();  // 保持同步

        // 打印AI响应内容，方便调试
        System.out.println("AI返回内容: " + aiResponse);

        List<AIForeshadowDTO> aiList;
        try {
            // 1. 解析整个响应为JsonNode对象
            JsonNode root = objectMapper.readTree(aiResponse);

            // 2. 提取 "data" 字段
            JsonNode dataNode = root.get("data");
            if (dataNode == null) {
                throw new RuntimeException("AI返回内容格式异常，'data'字段为空！");
            }

            // 3. 提取 "content" 字段，通常是 markdown 包裹的字符串或纯数组字符串
            String contentJson = dataNode.get("content").asText();
            if (contentJson == null || contentJson.trim().isEmpty()) {
                throw new RuntimeException("AI返回内容格式异常，'content'字段为空！");
            }

            // 4. 尝试strip markdown代码块, 只保留json数组本体
            contentJson = extractJsonArray(contentJson);

            // 5. 解析为 List<AIForeshadowDTO>
            aiList = objectMapper.readValue(contentJson, new TypeReference<List<AIForeshadowDTO>>() {});
        } catch (Exception e) {
            // 打印原始内容方便调试
            System.out.println("AI解析异常，原始内容如下：");
            System.out.println(aiResponse);
            e.printStackTrace();
            throw new RuntimeException("解析 AI 返回结果失败", e);
        }

        return aiList.stream().map(dto -> {
            Foreshadow f = new Foreshadow();
            f.setUserId(doc.getUserId());
            f.setTitle(dto.getTitle());
            f.setDescription(dto.getDescription());
            f.setStatus("PENDING");  // 状态设为PENDING
            f.setRemainChapters(null);
            f.setCreatedAt(LocalDateTime.now());
            f.setUpdatedAt(LocalDateTime.now());
            return foreshadowService.create(f);
        }).toList();
    }

    // 辅助函数，健壮strip markdown/非markdown下的JSON数组本体
    private static String extractJsonArray(String raw) {
        if (raw == null) return "";
        // 正则匹配 markdown 中的json代码块
        Pattern pattern = Pattern.compile("```json\\s*(\\[.*?\\])\\s*```", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(raw);
        if (matcher.find()) {
            return matcher.group(1);
        }
        // 兼容```包裹但无json关键字
        pattern = Pattern.compile("```\\s*(\\[.*?\\])\\s*```", Pattern.DOTALL);
        matcher = pattern.matcher(raw);
        if (matcher.find()) {
            return matcher.group(1);
        }
        // 如果原始内容本身就是JSON数组
        raw = raw.trim();
        if (raw.startsWith("[")) {
            return raw;
        }
        // fallback
        throw new RuntimeException("AI内容不是合法的JSON数组！原始内容：" + raw);
    }
}
