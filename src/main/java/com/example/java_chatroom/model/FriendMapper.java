package com.example.java_chatroom.model;

import org.apache.ibatis.annotations.*;

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

    /**
     * 删除单向好友关系
     */
    @Delete("DELETE FROM friend WHERE userId = #{userId} AND friendId = #{friendId}")
    void deleteFriendOneWay(@Param("userId") int userId, @Param("friendId") int friendId);

    /**
     * 删除反向好友关系
     */
    @Delete("DELETE FROM friend WHERE userId = #{friendId} AND friendId = #{userId}")
    void deleteFriendReverseWay(@Param("userId") int userId, @Param("friendId") int friendId);

    /**
     * 获取两个用户之间的共有会话 ID 列表
     */
    @Select("SELECT sessionId FROM message_session_user WHERE userId = #{userId1} INTERSECT SELECT sessionId FROM message_session_user WHERE userId = #{userId2}")
    List<Integer> getCommonSessionIds(@Param("userId1") int userId1, @Param("userId2") int userId2);

    /**
     * 删除指定会话中涉及的两个用户的记录（message_session_user 表）
     */
    @Delete("DELETE FROM message_session_user WHERE sessionId = #{sessionId} AND userId IN (#{userId1}, #{userId2})")
    void deleteSessionUserRecords(@Param("userId1") int userId1, @Param("userId2") int userId2, @Param("sessionId") int sessionId);

    /**
     * 查询指定会话中的用户数量
     */
    @Select("SELECT COUNT(*) FROM message_session_user WHERE sessionId = #{sessionId}")
    int getUserCountInSession(int sessionId);

    /**
     * 根据 sessionId 删除 message_session 表中的记录
     */
    @Delete("DELETE FROM message_session WHERE sessionId = #{sessionId}")
    void deleteSessionById(int sessionId);
}
