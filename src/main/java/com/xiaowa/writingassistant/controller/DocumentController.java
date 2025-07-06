package com.xiaowa.writingassistant.controller;

import com.xiaowa.writingassistant.common.Result;
import com.xiaowa.writingassistant.entity.Document;
import com.xiaowa.writingassistant.mapper.UserAccountMapper; // 新增
import com.xiaowa.writingassistant.service.DocumentService;
import org.springframework.web.bind.annotation.*;

import java.security.Principal; // 新增
import java.util.List;

@RestController
@RequestMapping("/api/docs")
public class DocumentController {

    private final DocumentService documentService;
    private final UserAccountMapper userMapper; // 新增

    public DocumentController(DocumentService documentService, UserAccountMapper userMapper) {
        this.documentService = documentService;
        this.userMapper = userMapper; // 新增
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

    @GetMapping("/batch")
    public Result<List<Document>> batchGet(@RequestParam List<Long> ids) {
        List<Document> docs = documentService.getDocumentsByIds(ids);
        return Result.success(docs);
    }

    // ===================== 改为“取当前登录用户的所有文档” ==========================
    @GetMapping
    public Result<List<Document>> listAllDocs(Principal principal) {
        // 用登录用户名查 userId（比如 testuser 就查 testuser 的 userId）
        Long userId = userMapper.findByUsername(principal.getName()).getId();
        List<Document> docs = documentService.listDocuments(userId);
        return Result.success(docs);
    }
    // ================================================================================
}
