package com.xiaowa.writingassistant.service;

import com.xiaowa.writingassistant.entity.SettingCollection;
import com.xiaowa.writingassistant.entity.SettingItem;
import com.xiaowa.writingassistant.entity.Document;
import com.xiaowa.writingassistant.exception.ResourceNotFoundException;
import com.xiaowa.writingassistant.mapper.DocumentMapper;
import com.xiaowa.writingassistant.mapper.SettingCollectionMapper;
import com.xiaowa.writingassistant.mapper.SettingItemMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SettingService {

    private final SettingCollectionMapper colMapper;
    private final SettingItemMapper itemMapper;
    private final DocumentMapper docMapper;
    private final AIClient aiClient;

    public SettingService(SettingCollectionMapper colMapper,
                          SettingItemMapper itemMapper,
                          DocumentMapper docMapper,
                          AIClient aiClient) {
        this.colMapper = colMapper;
        this.itemMapper = itemMapper;
        this.docMapper = docMapper;
        this.aiClient = aiClient;
    }

    public SettingCollection createCollection(SettingCollection c) {
        colMapper.insert(c);
        return colMapper.findById(c.getId());
    }

    public List<SettingCollection> listCollections(Long userId) {
        return colMapper.findByUser(userId);
    }

    public SettingCollection updateCollection(SettingCollection c) {
        colMapper.update(c);
        return colMapper.findById(c.getId());
    }

    public void deleteCollection(Long id) {
        colMapper.delete(id);
    }

    public SettingItem createItem(SettingItem i) {
        itemMapper.insert(i);
        return itemMapper.findById(i.getId());
    }

    public List<SettingItem> listItems(Long collectionId) {
        return itemMapper.findByCollectionId(collectionId);
    }

    public SettingItem updateItem(SettingItem i) {
        itemMapper.update(i);
        return itemMapper.findById(i.getId());
    }

    public void deleteItem(Long id) {
        itemMapper.delete(id);
    }

    @Transactional
    public List<SettingItem> extractFromDocument(Long documentId, Long collectionId) {
        Document doc = docMapper.findById(documentId);
        if (doc == null) throw new ResourceNotFoundException("Document not found: " + documentId);
        // 调用 AI 提取角色设定
        List<AIClient.CharacterSetting> extracted = aiClient.extractCharacterSettings(doc.getContent());
        for (AIClient.CharacterSetting cs : extracted) {
            SettingItem i = new SettingItem();
            i.setCollectionId(collectionId);
            i.setEntityName(cs.getName());
            i.setContent(cs.getDescription());
            i.setSourceDocumentId(documentId);
            itemMapper.insert(i);
        }
        return itemMapper.findByCollectionId(collectionId);
    }
}