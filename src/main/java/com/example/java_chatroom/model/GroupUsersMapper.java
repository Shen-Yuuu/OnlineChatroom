package com.example.java_chatroom.model;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface GroupUsersMapper {
    void addMember(GroupUsers groupUser);
    List<Integer> getMembers(int groupId);

    void insert(GroupUsers gu);

    List<GroupUsers> selectByGroupId(int groupId);
}
