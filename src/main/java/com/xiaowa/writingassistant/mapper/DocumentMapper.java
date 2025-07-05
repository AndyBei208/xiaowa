package com.xiaowa.writingassistant.mapper;

import com.xiaowa.writingassistant.entity.Document;
import org.apache.ibatis.annotations.*;
import java.util.List;

public interface DocumentMapper {

    @Insert("INSERT INTO document (user_id, type, title, content, status, created_at, updated_at) " +
            "VALUES (#{userId}, #{type}, #{title}, #{content}, #{status}, NOW(), NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(Document document);

    @Select("SELECT * FROM document WHERE id = #{id}")
    Document findById(Long id);

    @Select("SELECT * FROM document WHERE user_id = #{userId}")
    List<Document> findByUserId(Long userId);

    @Select("SELECT * FROM document")
    List<Document> selectAll();

    @Update("UPDATE document SET title = #{title}, content = #{content}, status = #{status}, type = #{type}, " +
            "updated_at = NOW() WHERE id = #{id}")
    int update(Document document);

    @Delete("DELETE FROM document WHERE id = #{id}")
    int delete(Long id);

    @Select({
            "<script>",
            "SELECT * FROM document WHERE id IN",
            "<foreach collection='ids' item='id' open='(' separator=',' close=')'>",
            "#{id}",
            "</foreach>",
            "</script>"
    })
    List<Document> findByIds(@Param("ids") List<Long> ids);
}
