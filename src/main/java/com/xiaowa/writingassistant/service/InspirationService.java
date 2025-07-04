package com.xiaowa.writingassistant.service;

import com.xiaowa.writingassistant.entity.Inspiration;
import com.xiaowa.writingassistant.entity.InspirationTag;
import com.xiaowa.writingassistant.entity.Foreshadow;
import com.xiaowa.writingassistant.entity.ForeshadowTag;
import com.xiaowa.writingassistant.mapper.InspirationMapper;
import com.xiaowa.writingassistant.mapper.InspirationTagMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class InspirationService {

    private final InspirationMapper inspirationMapper;
    private final InspirationTagMapper inspirationTagMapper;
    private final ForeshadowService foreshadowService;

    public InspirationService(InspirationMapper inspirationMapper,
                              InspirationTagMapper inspirationTagMapper,
                              ForeshadowService foreshadowService) {
        this.inspirationMapper = inspirationMapper;
        this.inspirationTagMapper = inspirationTagMapper;
        this.foreshadowService = foreshadowService;
    }

    @Transactional
    public Inspiration create(Inspiration insp) {
        inspirationMapper.insert(insp);
        if (insp.getTags() != null) {
            insp.getTags().forEach(tag -> {
                tag.setInspirationId(insp.getId());
                inspirationTagMapper.insert(tag);
            });
        }
        return getById(insp.getId());
    }

    public Inspiration getById(Long id) {
        Inspiration insp = inspirationMapper.findById(id);
        insp.setTags(inspirationTagMapper.findByInspirationId(id));
        return insp;
    }

    /**
     * 分页列表
     * @param userId 当前用户 ID
     * @param sort   "created" 或 "updated"
     * @param page   从 0 开始的页码
     * @param size   每页大小
     */
    public List<Inspiration> list(Long userId, String sort, int page, int size) {
        int offset = page * size;  // 计算跳过记录数
        List<Inspiration> list = inspirationMapper.findByUserWithPaging(userId, sort, size, offset);
        return list.stream()
                .peek(i -> i.setTags(inspirationTagMapper.findByInspirationId(i.getId())))
                .collect(Collectors.toList());
    }

    @Transactional
    public Inspiration update(Inspiration insp) {
        inspirationMapper.update(insp);
        inspirationTagMapper.deleteByInspirationId(insp.getId());
        if (insp.getTags() != null) {
            insp.getTags().forEach(tag -> {
                tag.setInspirationId(insp.getId());
                inspirationTagMapper.insert(tag);
            });
        }
        return getById(insp.getId());
    }

    @Transactional
    public void delete(Long id) {
        inspirationTagMapper.deleteByInspirationId(id);
        inspirationMapper.delete(id);
    }

    public List<Inspiration> search(Long userId, String keyword) {
        List<Inspiration> list = inspirationMapper.search(userId, keyword);
        return list.stream()
                .peek(i -> i.setTags(inspirationTagMapper.findByInspirationId(i.getId())))
                .collect(Collectors.toList());
    }

    @Transactional
    public Foreshadow createForeshadowFromInspiration(Long inspirationId, Long userId) {
        Inspiration insp = getById(inspirationId);
        if (!insp.getUserId().equals(userId)) {
            throw new IllegalArgumentException("无权操作此灵感");
        }
        Foreshadow f = new Foreshadow();
        f.setUserId(userId);
        f.setTitle("");
        f.setDescription("");
        f.setStatus("PENDING");
        f.setRemainChapters(null);
        Foreshadow created = foreshadowService.create(f);
        ForeshadowTag tag = new ForeshadowTag();
        tag.setTagType("CUSTOM");
        tag.setLabel(insp.getAddress());
        tag.setTargetModule("inspiration");
        tag.setTargetId(inspirationId);
        tag.setTargetUrl("/api/inspirations/" + inspirationId);
        foreshadowService.addTag(created.getId(), tag);
        return foreshadowService.getById(created.getId());
    }
}
