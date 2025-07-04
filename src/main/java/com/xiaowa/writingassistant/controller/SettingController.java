package com.xiaowa.writingassistant.controller;

import com.xiaowa.writingassistant.common.Result;
import com.xiaowa.writingassistant.entity.SettingCollection;
import com.xiaowa.writingassistant.entity.SettingItem;
import com.xiaowa.writingassistant.mapper.UserAccountMapper;
import com.xiaowa.writingassistant.service.SettingService;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/settings")
public class SettingController {

    private final SettingService service;
    private final UserAccountMapper userMapper;

    public SettingController(SettingService service, UserAccountMapper userMapper) {
        this.service = service;
        this.userMapper = userMapper;
    }

    private Long getCurrentUserId(Principal p) {
        return userMapper.findByUsername(p.getName()).getId();
    }

    @PostMapping("/collections")
    public Result<SettingCollection> createCol(Principal p, @RequestBody SettingCollection c) {
        c.setUserId(getCurrentUserId(p));
        return Result.success(service.createCollection(c));
    }

    @GetMapping("/collections")
    public Result<List<SettingCollection>> listCols(Principal p) {
        return Result.success(service.listCollections(getCurrentUserId(p)));
    }

    @PutMapping("/collections/{id}")
    public Result<SettingCollection> updateCol(Principal p,
                                               @PathVariable Long id,
                                               @RequestBody SettingCollection c) {
        c.setId(id);
        c.setUserId(getCurrentUserId(p));
        return Result.success(service.updateCollection(c));
    }

    @DeleteMapping("/collections/{id}")
    public Result<String> deleteCol(@PathVariable Long id) {
        service.deleteCollection(id);
        return Result.success("Deleted");
    }

    @PostMapping("/collections/{colId}/items")
    public Result<SettingItem> createItem(@PathVariable Long colId,
                                          @RequestBody SettingItem i) {
        i.setCollectionId(colId);
        return Result.success(service.createItem(i));
    }

    @GetMapping("/collections/{colId}/items")
    public Result<List<SettingItem>> listItems(@PathVariable Long colId) {
        return Result.success(service.listItems(colId));
    }

    @PutMapping("/items/{id}")
    public Result<SettingItem> updateItem(@PathVariable Long id,
                                          @RequestBody SettingItem i) {
        i.setId(id);
        return Result.success(service.updateItem(i));
    }

    @DeleteMapping("/items/{id}")
    public Result<String> deleteItem(@PathVariable Long id) {
        service.deleteItem(id);
        return Result.success("Deleted");
    }

    @PostMapping("/collections/{colId}/extract")
    public Result<List<SettingItem>> extract(Principal p,
                                             @PathVariable Long colId,
                                             @RequestParam Long documentId) {
        Long userId = getCurrentUserId(p);
        // 这里可选：在 service 中校验 collection 所属
        return Result.success(service.extractFromDocument(documentId, colId));
    }
}