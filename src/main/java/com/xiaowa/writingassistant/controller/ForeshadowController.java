package com.xiaowa.writingassistant.controller;

import com.xiaowa.writingassistant.exception.BadRequestException;
import com.xiaowa.writingassistant.common.Result;
import com.xiaowa.writingassistant.entity.Foreshadow;
import com.xiaowa.writingassistant.entity.UserAccount;
import com.xiaowa.writingassistant.service.ForeshadowService;
import com.xiaowa.writingassistant.mapper.UserAccountMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/foreshadows")
public class ForeshadowController {
    private final ForeshadowService service;
    private final UserAccountMapper userAccountMapper;

    public ForeshadowController(ForeshadowService service, UserAccountMapper userAccountMapper) {
        this.service = service;
        this.userAccountMapper = userAccountMapper;
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
        Long userId = getCurrentUserId();
        List<Foreshadow> list = service.listByUser(userId);
        // 这里可加进一步的过滤，如果你需要支持 status/tagType/label 多条件
        // 例如：
        // if (status != null) { list = list.stream().filter(f -> status.equals(f.getStatus())).toList(); }
        // if (label != null) { ... }
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

    // 删除伏笔
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
        Long userId = getCurrentUserId();
        List<Foreshadow> list = service.listReminders(userId, threshold);
        return Result.success(list);
    }

    /** 获取当前登录用户id，通用写法 */
    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        UserAccount user = userAccountMapper.findByUsername(username);
        if (user == null) throw new RuntimeException("用户不存在");
        return user.getId();
    }
}
