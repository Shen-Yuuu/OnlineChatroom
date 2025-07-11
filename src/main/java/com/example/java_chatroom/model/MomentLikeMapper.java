package com.example.java_chatroom.model;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface MomentLikeMapper {
    int insertLike(MomentLike like);
    int deleteLike(@Param("momentId") Long momentId, @Param("userId") Long userId);
    int countLikes(@Param("momentId") Long momentId);
    List<MomentLike> selectByMomentId(@Param("momentId") Long momentId);

    boolean existsLike(Long momentId, Long userId);
}

