package com.xiaowa.writingassistant.mapper;

import com.xiaowa.writingassistant.entity.InspirationTag;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface InspirationTagMapper {
    @Insert("INSERT INTO inspiration_tag(inspiration_id, name) VALUES(#{inspirationId}, #{name})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(InspirationTag tag);

    @Select("SELECT * FROM inspiration_tag WHERE inspiration_id = #{inspirationId}")
    List<InspirationTag> findByInspirationId(Long inspirationId);

    @Delete("DELETE FROM inspiration_tag WHERE inspiration_id = #{inspirationId}")
    void deleteByInspirationId(Long inspirationId);
}
