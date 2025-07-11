package com.example.java_chatroom.model;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface MomentCommentMapper {
    int insertComment(MomentComment comment);
    List<MomentComment> selectCommentsByMomentId(@Param("momentId") Long momentId);
    int deleteComment(@Param("id") Long id, @Param("userId") Long userId);
    MomentComment selectById(@Param("id") Long id); // 新增方法
}
