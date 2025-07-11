package com.example.java_chatroom.model;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface MessageMapper extends BaseMapper<Message> {
    // 通过这个方法实现插入消息到数据库表中
    void insert1(Message message);

    Message selectByMessageId(Long messageId);

    // 获取指定会话历史消息列表
    List<Message> selectBySessionId(int sessionId);

    // 获取指定会话历史消息列表，支持关键词搜索
    List<Message> selectBySessionIdWithQuery(@Param("sessionId") int sessionId, @Param("query") String query);

    String getLastMessageBySessionId(int sessionId);

    // 获取指定用户的所有历史消息, 支持模糊查询
    List<Message> selectHistoryForUser(@Param("userId") int userId, @Param("query") String query);

    @Update("UPDATE message SET status = #{status} WHERE messageId = #{messageId}")
    int updateStatus(@Param("messageId") Long messageId, @Param("status") Integer status);

    @Select("SELECT * FROM message WHERE sessionId = #{sessionId} AND status = #{status} ORDER BY createTime")
    List<Message> selectBySessionIdAndStatus(@Param("sessionId") Integer sessionId, @Param("status") Integer status);


}
