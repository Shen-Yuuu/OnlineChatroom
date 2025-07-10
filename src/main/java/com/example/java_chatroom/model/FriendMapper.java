package com.example.java_chatroom.model;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface FriendMapper {
    List<Friend> selectFriendList(int userId);
    /**
     * 检查是否已经是好友关系
     */
    @Select("SELECT COUNT(*) FROM friend WHERE userId = #{userId} AND friendId = #{friendId}")
    int checkFriendship(@Param("userId") int userId, @Param("friendId") int friendId);

    /**
     * 添加好友关系 (双向)
     */
    @Insert("INSERT INTO friend (userId, friendId) VALUES (#{userId}, #{friendId}), (#{friendId}, #{userId})")
    void addFriend(@Param("userId") int userId, @Param("friendId") int friendId);

}
