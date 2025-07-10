package com.example.java_chatroom.model;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface GroupMapper {
    int createGroup(Group group);
    List<Group> getGroupsByUserId(int userId);

    void insert(Group group);

    Object selectByUserId(int userId);
}
