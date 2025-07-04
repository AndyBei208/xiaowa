package com.xiaowa.writingassistant.mapper;

import com.xiaowa.writingassistant.entity.SettingItem;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface SettingItemMapper {
    @Insert("INSERT INTO setting_item(collection_id,entity_name,content,source_document_id) VALUES(#{collectionId},#{entityName},#{content},#{sourceDocumentId})")
    @Options(useGeneratedKeys=true, keyProperty="id")
    void insert(SettingItem i);

    @Select("SELECT * FROM setting_item WHERE id=#{id}")
    SettingItem findById(Long id);

    @Select("SELECT * FROM setting_item WHERE collection_id=#{cid} ORDER BY updated_at DESC")
    List<SettingItem> findByCollectionId(@Param("cid") Long collectionId);

    @Update("UPDATE setting_item SET entity_name=#{entityName},content=#{content},updated_at=NOW() WHERE id=#{id}")
    void update(SettingItem i);

    @Delete("DELETE FROM setting_item WHERE id=#{id}")
    void delete(Long id);
}