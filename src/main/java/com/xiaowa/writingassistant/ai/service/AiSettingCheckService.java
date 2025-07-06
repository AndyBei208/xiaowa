package com.xiaowa.writingassistant.ai.service;

import com.xiaowa.writingassistant.entity.Document;
import com.xiaowa.writingassistant.entity.SettingItem;
import com.xiaowa.writingassistant.mapper.SettingItemMapper;
import com.xiaowa.writingassistant.service.DocumentService;
import com.xiaowa.writingassistant.service.VivoApiService;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class AiSettingCheckService {

    private final DocumentService documentService;
    private final SettingItemMapper settingItemMapper;
    private final VivoApiService vivoApiService;

    public AiSettingCheckService(DocumentService documentService,
                                 SettingItemMapper settingItemMapper,
                                 VivoApiService vivoApiService) {
        this.documentService = documentService;
        this.settingItemMapper = settingItemMapper;
        this.vivoApiService = vivoApiService;
    }

    /**
     * 检查正文与 sourceDocumentId 关联设定项
     */
    public Mono<String> checkDocumentsWithSettings(List<Long> docIds) {
        List<Document> docs = documentService.getDocumentsByIds(docIds);
        List<SettingItem> settings = settingItemMapper.findBySourceDocumentIds(docIds);

        StringBuilder prompt = new StringBuilder();
        prompt.append("请你作为小说世界观设定审核AI，判断下列正文内容与其关联设定是否有引用、冲突或遗漏。\n")
                .append("正文如下：\n");
        for (Document doc : docs) {
            prompt.append("【").append(doc.getTitle()).append("】\n")
                    .append(doc.getContent()).append("\n\n");
        }
        prompt.append("关联设定集如下：\n");
        for (SettingItem setting : settings) {
            prompt.append("- ").append(setting.getEntityName()).append("：").append(setting.getContent()).append("\n");
        }
        prompt.append("\n请分析哪些设定被文本正确引用、哪些设定有误用/冲突/遗漏，返回JSON数组，结构如下：")
                .append("[{\"setting\": \"设定名\", \"matchedTexts\": [\"片段...\"], \"conflict\": true/false, \"comment\": \"分析意见\"}]\n");

        return vivoApiService.getCompletion(prompt.toString());
    }
}
