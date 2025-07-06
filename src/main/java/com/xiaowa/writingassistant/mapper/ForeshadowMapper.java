package com.xiaowa.writingassistant.mapper;

import com.xiaowa.writingassistant.entity.Foreshadow;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface ForeshadowMapper {
    @Select("SELECT * FROM foreshadow WHERE document_id = #{docId}")
    List<Foreshadow> findByDocumentId(Long docId);

    @Insert("INSERT INTO foreshadow (user_id, title, description, status, remain_chapters) VALUES (#{userId}, #{title}, #{description}, #{status}, #{remainChapters})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(Foreshadow f);

    @Select("SELECT * FROM foreshadow WHERE id = #{id}")
    Foreshadow findById(Long id);

    @Select("SELECT * FROM foreshadow WHERE user_id = #{userId}")
    List<Foreshadow> findByUserId(Long userId);

    @Select("SELECT * FROM foreshadow")
    List<Foreshadow> selectAll();

    @Update("UPDATE foreshadow SET title=#{title}, description=#{description}, status=#{status}, remain_chapters=#{remainChapters} WHERE id=#{id}")
    void update(Foreshadow f);

    @Delete("DELETE FROM foreshadow WHERE id = #{id}")
    void delete(Long id);

    // 新增：只允许本人删除自己的伏笔
    @Delete("DELETE FROM foreshadow WHERE id = #{id} AND user_id = #{userId}")
    void deleteByIdAndUserId(@Param("id") Long id, @Param("userId") Long userId);
}
