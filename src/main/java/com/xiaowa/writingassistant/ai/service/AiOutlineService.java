package com.xiaowa.writingassistant.ai.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xiaowa.writingassistant.entity.Document;
import com.xiaowa.writingassistant.service.DocumentService;
import com.xiaowa.writingassistant.service.VivoApiService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AiOutlineService {
    private final DocumentService documentService;
    private final VivoApiService vivoApiService;
    private final ObjectMapper objectMapper;

    public AiOutlineService(DocumentService documentService,
                            VivoApiService vivoApiService,
                            ObjectMapper objectMapper) {
        this.documentService = documentService;
        this.vivoApiService = vivoApiService;
        this.objectMapper = objectMapper;
    }

    /**
     * 单章大纲提取
     */
    public String extractOutlineText(Long docId) {
        Document doc = documentService.getDocument(docId);
        if (doc == null) throw new RuntimeException("文档不存在: " + docId);
        String content = doc.getContent();
        String title = doc.getTitle() != null ? doc.getTitle() : "";

        String prompt = String.format(
                "你是一位精通内容结构梳理的AI，请为以下小说正文生成一份章节分级结构大纲（只返回纯文本，无需额外解释）：\n【%s】\n%s",
                title, content
        );

        return callAiAndParseOutline(prompt);
    }

    /**
     * 多章合并生成大纲
     */
    public String extractOutlineTextBatch(List<Long> docIds) {
        List<Document> docs = documentService.getDocumentsByIds(docIds);
        if (docs == null || docs.isEmpty()) throw new RuntimeException("批量文档为空！");
        StringBuilder sb = new StringBuilder();
        for (Document doc : docs) {
            if (doc == null) continue;
            String title = doc.getTitle() != null ? doc.getTitle() : "";
            sb.append("【").append(title).append("】\n");
            sb.append(doc.getContent()).append("\n\n");
        }
        String allContent = sb.toString();

        String prompt = String.format(
                "你是一位精通内容结构梳理的AI，请为以下小说多章节内容生成一份章节分级结构大纲（只返回纯文本，无需额外解释）：\n\n%s",
                allContent
        );

        return callAiAndParseOutline(prompt);
    }

    // 公共方法，复用AI调用+结果解析
    private String callAiAndParseOutline(String prompt) {
        String aiResponse = vivoApiService.getCompletion(prompt).block();
        System.out.println("AI返回大纲: " + aiResponse);

        try {
            JsonNode root = objectMapper.readTree(aiResponse);
            JsonNode dataNode = root.get("data");
            if (dataNode == null) throw new RuntimeException("AI返回内容格式异常，'data'字段为空！");
            String outlineText = dataNode.get("content").asText();
            if (outlineText == null || outlineText.trim().isEmpty())
                throw new RuntimeException("AI返回内容格式异常，'content'字段为空！");
            return outlineText.trim();
        } catch (Exception e) {
            System.out.println("AI大纲解析异常：");
            System.out.println(aiResponse);
            throw new RuntimeException("解析 AI 返回结果失败", e);
        }
    }
}
