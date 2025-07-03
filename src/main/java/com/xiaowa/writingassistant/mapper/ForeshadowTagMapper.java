package com.xiaowa.writingassistant.mapper;

import com.xiaowa.writingassistant.entity.ForeshadowTag;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface ForeshadowTagMapper {
    @Insert("INSERT INTO foreshadow_tag (foreshadow_id, tag_type, label, target_module, target_id, target_url) VALUES (#{foreshadowId}, #{tagType}, #{label}, #{targetModule}, #{targetId}, #{targetUrl})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(ForeshadowTag tag);

    @Select("SELECT * FROM foreshadow_tag WHERE foreshadow_id = #{foreshadowId}")
    List<ForeshadowTag> findByForeshadowId(Long foreshadowId);

    @Delete("DELETE FROM foreshadow_tag WHERE foreshadow_id = #{foreshadowId}")
    void deleteByForeshadowId(Long foreshadowId);
}