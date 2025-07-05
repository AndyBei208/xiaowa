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

import com.xiaowa.writingassistant.ai.dto.AIForeshadowResolutionRequest;
import java.util.Map;
import java.util.HashMap;

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

    /**
     * 检测伏笔回收状态，返回 JSON：{ "id": true/false }
     */
    public Map<Long, Boolean> checkForeshadowingResolution(AIForeshadowResolutionRequest req) {
        // 构造伏笔列表字符串，便于 AI 理解
        StringBuilder foreshadowStr = new StringBuilder();
        for (AIForeshadowResolutionRequest.ForeshadowSimple f : req.getExistingForeshadows()) {
            foreshadowStr.append(String.format("- ID: %d, 伏笔: %s, 说明: %s\n", f.getId(), f.getTitle(), f.getDescription()));
        }

        String prompt = String.format(
                "你是一位精通故事结构分析的AI。请判断在给出的新文本中，下面这些伏笔是否得到了明确的解释或解决（即“回收”）。" +
                        "请返回一个JSON对象，key为伏笔的ID，value为布尔值（true表示已回收，false表示未回收）。\n\n" +
                        "---\n待检测的伏笔列表：\n%s\n---\n待分析的新文本内容：\n%s",
                foreshadowStr, req.getDocumentContent()
        );

        String aiResponse = vivoApiService.getCompletion(prompt).block();
        System.out.println("AI回收检测返回内容: " + aiResponse);

        try {
            // =======【修正开始】========
            // 1. 解析顶层响应
            JsonNode root = objectMapper.readTree(aiResponse);
            JsonNode dataNode = root.get("data");
            if (dataNode == null) throw new RuntimeException("AI返回无data字段！");
            String contentRaw = dataNode.get("content").asText();
            if (contentRaw == null) throw new RuntimeException("AI返回无content字段！");

            // 2. 去除 markdown，只留 { ... }
            String contentJson = extractJsonArrayOrObject(contentRaw);

            // 3. 反序列化
            Map<String, Boolean> map = objectMapper.readValue(contentJson, new TypeReference<Map<String, Boolean>>() {});
            Map<Long, Boolean> result = new HashMap<>();
            for (Map.Entry<String, Boolean> entry : map.entrySet()) {
                try {
                    result.put(Long.valueOf(entry.getKey()), entry.getValue());
                } catch (Exception e) {
                    // key不是数字，跳过
                }
            }
            return result;
            // =======【修正结束】========
        } catch (Exception e) {
            System.out.println("AI回收检测解析异常：");
            System.out.println(aiResponse);
            e.printStackTrace();
            throw new RuntimeException("解析 AI 回收检测结果失败", e);
        }
    }

    // 兼容对象的markdown strip方法
    private static String extractJsonArrayOrObject(String raw) {
        if (raw == null) return "";
        // markdown 代码块带json
        Pattern pattern = Pattern.compile("```json\\s*([\\[{].*?[\\]}])\\s*```", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(raw);
        if (matcher.find()) return matcher.group(1);
        // markdown代码块无json关键字
        pattern = Pattern.compile("```\\s*([\\[{].*?[\\]}])\\s*```", Pattern.DOTALL);
        matcher = pattern.matcher(raw);
        if (matcher.find()) return matcher.group(1);
        // 本身就是json
        raw = raw.trim();
        if (raw.startsWith("{") || raw.startsWith("[")) return raw;
        throw new RuntimeException("AI内容不是合法JSON！原始内容：" + raw);
    }
}
