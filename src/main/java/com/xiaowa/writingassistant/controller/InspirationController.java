// src/main/java/com/xiaowa/writingassistant/controller/InspirationController.java
package com.xiaowa.writingassistant.controller;

import com.xiaowa.writingassistant.common.Result;
import com.xiaowa.writingassistant.entity.Inspiration;
import com.xiaowa.writingassistant.entity.Foreshadow;
import com.xiaowa.writingassistant.entity.UserAccount;
import com.xiaowa.writingassistant.mapper.UserAccountMapper;
import com.xiaowa.writingassistant.service.InspirationService;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/inspirations")
public class InspirationController {

    private final InspirationService inspirationService;
    private final UserAccountMapper userAccountMapper;

    public InspirationController(InspirationService inspirationService,
                                 UserAccountMapper userAccountMapper) {
        this.inspirationService = inspirationService;
        this.userAccountMapper = userAccountMapper;
    }

    private Long currentUserId(Principal principal) {
        // principal.getName() 返回的是 username
        UserAccount user = userAccountMapper.findByUsername(principal.getName());
        return user.getId();
    }

    @PostMapping
    public Result<Inspiration> create(Principal principal,
                                      @RequestBody Inspiration insp) {
        insp.setUserId(currentUserId(principal));
        return Result.success(inspirationService.create(insp));
    }

    @GetMapping("/{id}")
    public Result<Inspiration> get(@PathVariable Long id) {
        return Result.success(inspirationService.getById(id));
    }

    @GetMapping
    public Result<List<Inspiration>> list(Principal principal,
                                          @RequestParam String sort,
                                          @RequestParam(defaultValue = "0") int page,
                                          @RequestParam(defaultValue = "10") int size) {
        Long uid = currentUserId(principal);
        return Result.success(inspirationService.list(uid, sort, page, size));
    }

    @PutMapping("/{id}")
    public Result<Inspiration> update(Principal principal,
                                      @PathVariable Long id,
                                      @RequestBody Inspiration insp) {
        insp.setId(id);
        insp.setUserId(currentUserId(principal));
        return Result.success(inspirationService.update(insp));
    }

    @DeleteMapping("/{id}")
    public Result<String> delete(Principal principal,
                                 @PathVariable Long id) {
        inspirationService.delete(id);
        return Result.success("Deleted");
    }

    /** 严格关键字搜索 **/
    @GetMapping("/search")
    public Result<List<Inspiration>> search(Principal principal,
                                            @RequestParam String kw) {
        Long uid = currentUserId(principal);
        return Result.success(inspirationService.search(uid, kw));
    }

    /** 拖拽灵感到伏笔库 **/
    @PostMapping("/{id}/to-foreshadow")
    public Result<Foreshadow> toForeshadow(Principal principal,
                                           @PathVariable Long id) {
        Long uid = currentUserId(principal);
        return Result.success(inspirationService.createForeshadowFromInspiration(id, uid));
    }

    /** AI 辅助搜索（待实现） **/
    @GetMapping("/search/ai")
    public Result<List<Inspiration>> aiSearch(@RequestParam String query) {
        throw new UnsupportedOperationException("待实现");
    }
}
