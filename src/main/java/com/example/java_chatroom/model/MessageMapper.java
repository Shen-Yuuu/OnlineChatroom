package com.example.java_chatroom.model;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface MessageMapper {
    // 通过这个方法实现插入消息到数据库表中
    void insert(Message message);

    // 获取指定会话历史消息列表
    List<Message> selectBySessionId(int sessionId);

    // 获取指定会话历史消息列表，支持关键词搜索
    List<Message> selectBySessionIdWithQuery(@Param("sessionId") int sessionId, @Param("query") String query);

    String getLastMessageBySessionId(int sessionId);

    // 获取指定用户的所有历史消息, 支持模糊查询
    List<Message> selectHistoryForUser(@Param("userId") int userId, @Param("query") String query);
}
