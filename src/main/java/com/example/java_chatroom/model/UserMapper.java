package com.example.java_chatroom.model;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface UserMapper {
    // 把用户插入到数据库中 -> 注册
    int insert(User user);

    // 根据用户名查询用户信息 -> 登录
    User selectByName(String username);

    @Select("SELECT * FROM user WHERE userId = #{userId}")
    User findUserById(int userId);

    int updateById(User user);

    List<User> selectUsersByGroupId(int groupId);
}
