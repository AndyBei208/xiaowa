package com.xiaowa.writingassistant.service;

import com.xiaowa.writingassistant.entity.Foreshadow;
import com.xiaowa.writingassistant.entity.ForeshadowTag;
import com.xiaowa.writingassistant.entity.UserAccount;
import com.xiaowa.writingassistant.exception.ResourceNotFoundException;
import com.xiaowa.writingassistant.mapper.ForeshadowMapper;
import com.xiaowa.writingassistant.mapper.ForeshadowTagMapper;
import com.xiaowa.writingassistant.mapper.UserAccountMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ForeshadowService {

    private final ForeshadowMapper foreshadowMapper;
    private final ForeshadowTagMapper tagMapper;
    private final UserAccountMapper userAccountMapper;

    public ForeshadowService(ForeshadowMapper foreshadowMapper,
                             ForeshadowTagMapper tagMapper,
                             UserAccountMapper userAccountMapper) {
        this.foreshadowMapper = foreshadowMapper;
        this.tagMapper = tagMapper;
        this.userAccountMapper = userAccountMapper;
    }

    public List<Foreshadow> listByDocumentId(Long docId) {
        return foreshadowMapper.findByDocumentId(docId);
    }

    @Transactional
    public Foreshadow create(Foreshadow f) {
        // 自动写入当前用户的 user_id
        Long userId = getCurrentUserId();
        f.setUserId(userId);
        foreshadowMapper.insert(f);
        if (f.getTags() != null) {
            for (ForeshadowTag tag : f.getTags()) {
                tag.setForeshadowId(f.getId());
                tagMapper.insert(tag);
            }
        }
        return getById(f.getId());
    }

    public Foreshadow getById(Long id) {
        Foreshadow f = foreshadowMapper.findById(id);
        if (f == null) {
            throw new ResourceNotFoundException("Foreshadow not found: " + id);
        }
        f.setTags(tagMapper.findByForeshadowId(id));
        return f;
    }

    public List<Foreshadow> listByUser(Long userId) {
        List<Foreshadow> list = foreshadowMapper.findByUserId(userId);
        return list.stream()
                .map(f -> {
                    f.setTags(tagMapper.findByForeshadowId(f.getId()));
                    return f;
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public Foreshadow update(Foreshadow f) {
        Foreshadow existing = foreshadowMapper.findById(f.getId());
        if (existing == null) {
            throw new ResourceNotFoundException("Foreshadow not found: " + f.getId());
        }
        // 只允许本人修改
        Long userId = getCurrentUserId();
        if (!userId.equals(existing.getUserId())) {
            throw new RuntimeException("无权限操作该伏笔");
        }
        foreshadowMapper.update(f);
        tagMapper.deleteByForeshadowId(f.getId());
        if (f.getTags() != null) {
            for (ForeshadowTag tag : f.getTags()) {
                tag.setForeshadowId(f.getId());
                tagMapper.insert(tag);
            }
        }
        return getById(f.getId());
    }

    @Transactional
    public void delete(Long id) {
        Foreshadow f = foreshadowMapper.findById(id);
        if (f == null) {
            throw new ResourceNotFoundException("Foreshadow not found: " + id);
        }
        Long userId = getCurrentUserId();
        if (!userId.equals(f.getUserId())) {
            throw new RuntimeException("无权限操作该伏笔");
        }
        foreshadowMapper.deleteByIdAndUserId(id, userId);
    }

    @Transactional
    public Foreshadow aiDetect(Long id, int predictedRemain) {
        Foreshadow f = foreshadowMapper.findById(id);
        if (f == null) {
            throw new ResourceNotFoundException("Foreshadow not found: " + id);
        }
        Long userId = getCurrentUserId();
        if (!userId.equals(f.getUserId())) {
            throw new RuntimeException("无权限操作该伏笔");
        }
        f.setRemainChapters(predictedRemain);
        foreshadowMapper.update(f);
        return getById(id);
    }

    public List<Foreshadow> listReminders(Long userId, int threshold) {
        return foreshadowMapper.findByUserId(userId).stream()
                .filter(f -> "ACTIVE".equals(f.getStatus())
                        && f.getRemainChapters() != null
                        && f.getRemainChapters() <= threshold)
                .map(f -> getById(f.getId()))
                .collect(Collectors.toList());
    }

    @Transactional
    public void addTag(Long foreshadowId, ForeshadowTag tag) {
        tag.setForeshadowId(foreshadowId);
        tagMapper.insert(tag);
    }

    /** 工具方法：取当前用户id */
    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        UserAccount user = userAccountMapper.findByUsername(username);
        if (user == null) throw new RuntimeException("用户不存在");
        return user.getId();
    }
}
