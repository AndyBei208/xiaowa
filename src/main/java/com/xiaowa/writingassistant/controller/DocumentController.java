package com.xiaowa.writingassistant.controller;

import com.xiaowa.writingassistant.common.Result;
import com.xiaowa.writingassistant.entity.Document;
import com.xiaowa.writingassistant.service.DocumentService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/docs")
public class DocumentController {

    private final DocumentService documentService;

    public DocumentController(DocumentService documentService) {
        this.documentService = documentService;
    }

    @PostMapping
    public Result<Document> create(@RequestBody Document doc) {
        documentService.createDocument(doc);
        return Result.success(documentService.getDocument(doc.getId()));
    }

    @GetMapping("/{id}")
    public Result<Document> get(@PathVariable Long id) {
        Document document = documentService.getDocument(id);
        return Result.success(document);
    }

    @GetMapping("/user/{userId}")
    public Result<List<Document>> list(@PathVariable Long userId) {
        List<Document> docs = documentService.listDocuments(userId);
        return Result.success(docs);
    }

    @PutMapping("/{id}")
    public Result<Document> update(@PathVariable Long id, @RequestBody Document doc) {
        doc.setId(id);
        documentService.updateDocument(doc);
        Document updated = documentService.getDocument(id);
        return Result.success(updated);
    }

    @DeleteMapping("/{id}")
    public Result<String> delete(@PathVariable Long id) {
        documentService.deleteDocument(id);
        return Result.success("Document deleted");
    }
}
