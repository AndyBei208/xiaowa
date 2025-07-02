package com.xiaowa.writingassistant.mapper;

import com.xiaowa.writingassistant.entity.UserAccount;
import org.apache.ibatis.annotations.*;

public interface UserAccountMapper {

    @Insert("INSERT INTO user_account (username, email, password_hash) " +
            "VALUES (#{username}, #{email}, #{passwordHash})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(UserAccount user);

    @Select("SELECT * FROM user_account WHERE username = #{username}")
    UserAccount findByUsername(String username);

    @Select("SELECT * FROM user_account WHERE email = #{email}")
    UserAccount findByEmail(String email);

    @Select("SELECT * FROM user_account WHERE id = #{id}")
    UserAccount findById(Long id);
}
