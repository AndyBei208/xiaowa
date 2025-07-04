package com.xiaowa.writingassistant.mapper;

import com.xiaowa.writingassistant.entity.SettingCollection;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface SettingCollectionMapper {
    @Insert("INSERT INTO setting_collection(user_id,name,description) VALUES(#{userId},#{name},#{description})")
    @Options(useGeneratedKeys=true, keyProperty="id")
    void insert(SettingCollection c);

    @Select("SELECT * FROM setting_collection WHERE id=#{id}")
    SettingCollection findById(Long id);

    @Select("SELECT * FROM setting_collection WHERE user_id=#{userId} ORDER BY updated_at DESC")
    List<SettingCollection> findByUser(Long userId);

    @Update("UPDATE setting_collection SET name=#{name},description=#{description},updated_at=NOW() WHERE id=#{id}")
    void update(SettingCollection c);

    @Delete("DELETE FROM setting_collection WHERE id=#{id}")
    void delete(Long id);
}