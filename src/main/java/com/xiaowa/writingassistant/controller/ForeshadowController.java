package com.xiaowa.writingassistant.controller;

import com.xiaowa.writingassistant.exception.BadRequestException;
import com.xiaowa.writingassistant.common.Result;
import com.xiaowa.writingassistant.entity.Foreshadow;
import com.xiaowa.writingassistant.service.ForeshadowService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/foreshadows")
public class ForeshadowController {
    private final ForeshadowService service;

    public ForeshadowController(ForeshadowService service) {
        this.service = service;
    }

    // 创建伏笔
    @PostMapping
    public Result<Foreshadow> create(@RequestBody Foreshadow f) {
        Foreshadow created = service.create(f);
        return Result.success(created);
    }

    // 获取列表，支持过滤参数（status, tagType, label）
    @GetMapping
    public Result<List<Foreshadow>> list(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String tagType,
            @RequestParam(required = false) String label
    ) {
        // TODO: service 方法增加条件过滤
        List<Foreshadow> list = service.listByUser(/* 从安全上下文获取 userId */ 1L);
        return Result.success(list);
    }

    // 获取详细信息
    @GetMapping("/{id}")
    public Result<Foreshadow> get(@PathVariable Long id) {
        Foreshadow f = service.getById(id);
        return Result.success(f);
    }

    // 更新伏笔及其标签
    @PutMapping("/{id}")
    public Result<Foreshadow> update(@PathVariable Long id, @RequestBody Foreshadow f) {
        f.setId(id);
        Foreshadow updated = service.update(f);
        return Result.success(updated);
    }

    // 删除伏笔（忽略）
    @DeleteMapping("/{id}")
    public Result<String> delete(@PathVariable Long id) {
        service.delete(id);
        return Result.success("Foreshadow deleted");
    }

    // 人工确认 PENDING -> ACTIVE
    @PostMapping("/{id}/confirm")
    public Result<Foreshadow> confirm(@PathVariable Long id) {
        Foreshadow f = service.getById(id);
        if (!"PENDING".equals(f.getStatus())) {
            throw new BadRequestException("Only PENDING items can be confirmed");
        }
        f.setStatus("ACTIVE");
        Foreshadow updated = service.update(f);
        return Result.success(updated);
    }

    // AI 检测并刷新 remainChapters
    @PostMapping("/{id}/ai-detect")
    public Result<Foreshadow> aiDetect(
            @PathVariable Long id,
            @RequestParam int predicted
    ) {
        Foreshadow f = service.aiDetect(id, predicted);
        return Result.success(f);
    }

    // 获取回收提醒列表
    @GetMapping("/reminders")
    public Result<List<Foreshadow>> reminders(@RequestParam(defaultValue = "2") int threshold) {
        List<Foreshadow> list = service.listReminders(/* userId */ 1L, threshold);
        return Result.success(list);
    }
}