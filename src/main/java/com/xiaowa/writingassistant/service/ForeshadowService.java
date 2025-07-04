package com.xiaowa.writingassistant.service;

import com.xiaowa.writingassistant.entity.Foreshadow;
import com.xiaowa.writingassistant.entity.ForeshadowTag;
import com.xiaowa.writingassistant.exception.ResourceNotFoundException;
import com.xiaowa.writingassistant.mapper.ForeshadowMapper;
import com.xiaowa.writingassistant.mapper.ForeshadowTagMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ForeshadowService {

    private final ForeshadowMapper foreshadowMapper;
    private final ForeshadowTagMapper tagMapper;

    public ForeshadowService(ForeshadowMapper foreshadowMapper, ForeshadowTagMapper tagMapper) {
        this.foreshadowMapper = foreshadowMapper;
        this.tagMapper = tagMapper;
    }

    @Transactional
    public Foreshadow create(Foreshadow f) {
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
        // 保持原有行为：只删除主表记录，不改动标签表
        foreshadowMapper.delete(id);
    }

    @Transactional
    public Foreshadow aiDetect(Long id, int predictedRemain) {
        // 先读取已有伏笔，避免字段丢失
        Foreshadow f = foreshadowMapper.findById(id);
        if (f == null) {
            throw new ResourceNotFoundException("Foreshadow not found: " + id);
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

    /**
     * 新增的方法：为指定 Foreshadow 添加一个标签
     */
    @Transactional
    public void addTag(Long foreshadowId, ForeshadowTag tag) {
        tag.setForeshadowId(foreshadowId);
        tagMapper.insert(tag);
    }
}
