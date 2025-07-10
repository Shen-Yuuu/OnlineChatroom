package com.example.java_chatroom.model;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface GroupMessageRecordMapper {
    List<GroupMessageRecord> getMessagesByGroupId(int groupId);
    void addMessage(GroupMessageRecord record);

    List<GroupMessageRecord> selectByGroupId(int groupId);

    void insert(GroupMessageRecord record);
}
