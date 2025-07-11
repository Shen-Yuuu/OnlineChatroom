package com.example.java_chatroom.model;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface MomentMapper {
    int insertMoment(Moment moment);
    int deleteMoment(@Param("id") Long id, @Param("userId") Long userId);
    List<Moment> selectAllMoments();
    Moment selectMomentById(@Param("id") Long id);
    List<Moment> selectMomentsByUserId(@Param("userId") Long userId);

    List<Moment> selectAllMomentsByFriend(int userId);
}
