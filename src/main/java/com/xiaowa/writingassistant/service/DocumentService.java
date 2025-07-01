package com.xiaowa.writingassistant.service;

import com.xiaowa.writingassistant.entity.Document;
import com.xiaowa.writingassistant.mapper.DocumentMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DocumentService {

    private final DocumentMapper documentMapper;

    public DocumentService(DocumentMapper documentMapper) {
        this.documentMapper = documentMapper;
    }

    public void createDocument(Document doc) {
        documentMapper.insert(doc);
    }

    public Document getDocument(Long id) {
        return documentMapper.findById(id);
    }

    public List<Document> listDocuments(Long userId) {
        return documentMapper.findByUserId(userId);
    }

    public void updateDocument(Document doc) {
        documentMapper.update(doc);
    }

    public void deleteDocument(Long id) {
        documentMapper.delete(id);
    }
}
