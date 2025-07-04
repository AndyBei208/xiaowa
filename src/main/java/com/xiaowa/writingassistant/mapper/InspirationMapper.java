package com.xiaowa.writingassistant.mapper;

import com.xiaowa.writingassistant.entity.Inspiration;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface InspirationMapper {

    @Insert("""
      INSERT INTO inspiration(user_id, type, article_id, title, content, address)
      VALUES(#{userId}, #{type}, #{articleId}, #{title}, #{content}, #{address})
      """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(Inspiration insp);

    @Select("SELECT * FROM inspiration WHERE id = #{id}")
    Inspiration findById(Long id);

    /**
     * 分页查询：size 为每页大小，offset 为跳过的记录数
     */
    @Select("""
      SELECT * FROM inspiration 
      WHERE user_id = #{userId}
      ORDER BY 
        CASE WHEN #{sort} = 'created' THEN created_at END DESC,
        CASE WHEN #{sort} = 'updated' THEN updated_at END DESC
      LIMIT #{size} OFFSET #{offset}
      """)
    List<Inspiration> findByUserWithPaging(
            @Param("userId") Long userId,
            @Param("sort") String sort,
            @Param("size") int size,
            @Param("offset") int offset
    );

    @Update("""
      UPDATE inspiration
      SET title=#{title}, content=#{content}, type=#{type}, article_id=#{articleId},
          address=#{address}, updated_at=NOW()
      WHERE id=#{id}
      """)
    void update(Inspiration insp);

    @Delete("DELETE FROM inspiration WHERE id = #{id}")
    void delete(Long id);

    // 简单的全文搜索（strict match）
    @Select("""
      SELECT * FROM inspiration
      WHERE user_id = #{userId}
        AND (title LIKE CONCAT('%', #{kw}, '%') OR content LIKE CONCAT('%', #{kw}, '%'))
      """)
    List<Inspiration> search(@Param("userId") Long userId, @Param("kw") String kw);
}
