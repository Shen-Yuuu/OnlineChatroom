package com.example.java_chatroom.service;

import com.example.java_chatroom.model.FriendMapper;
import com.example.java_chatroom.model.UserMapper;
import com.example.java_chatroom.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class FriendService {

    @Autowired
    private FriendMapper friendMapper;

    @Autowired
    private UserMapper userMapper;
    /**
     * 添加好友（双向）
     */
    @Transactional
    public void addFriend(int userId, int friendId) {
        // 1. 不能添加自己为好友
        if (userId == friendId) {
            throw new RuntimeException("不能添加自己为好友");
        }

        // 2. 检查要添加的好友是否存在
        User friend = userMapper.findUserById(friendId);
        if (friend == null) {
            throw new RuntimeException("该用户不存在");
        }

        // 3. 检查是否已经是好友
        if (friendMapper.checkFriendship(userId, friendId) > 0) {
            throw new RuntimeException("你们已经是好友了");
        }

        // 4. 添加好友关系 (双向)
        friendMapper.addFriend(userId, friendId);
    }

    /**
     * 删除好友（双向）
     */
    @Transactional
    public void removeFriend(int userId, int friendId) {
        // 1. 不能删除自己
        if (userId == friendId) {
            throw new RuntimeException("不能删除自己");
        }

        // 2. 检查是否是好友关系
        if (friendMapper.checkFriendship(userId, friendId) <= 0) {
            throw new RuntimeException("你们不是好友关系");
        }

        // 3. 删除双向好友关系
        friendMapper.deleteFriendOneWay(userId, friendId);
        friendMapper.deleteFriendReverseWay(userId, friendId);
    }

    @Transactional
    public void removeFriendAndClearSessions(int userId, int friendId) {
        if (userId == friendId) {
            throw new RuntimeException("不能删除自己");
        }

        // 检查是否为好友
        if (friendMapper.checkFriendship(userId, friendId) <= 0) {
            throw new RuntimeException("你们不是好友关系");
        }

        // 1. 删除双向好友关系
        friendMapper.deleteFriendOneWay(userId, friendId);
        friendMapper.deleteFriendReverseWay(userId, friendId);

        // 2. 获取两人之间的共有会话 ID
        List<Integer> commonSessionIds = friendMapper.getCommonSessionIds(userId, friendId);

        // 3. 清理会话数据
        for (Integer sessionId : commonSessionIds) {
            // a. 删除 message_session_user 表中这两个人的记录
            friendMapper.deleteSessionUserRecords(userId, friendId, sessionId);

            // b. 判断当前会话是否还有其他用户存在
            int userCount = friendMapper.getUserCountInSession(sessionId);

            // c. 如果只剩这两个人，就彻底删除这个会话
            if (userCount <= 2) {
                friendMapper.deleteSessionById(sessionId); // 删除 message_session 表
            }
        }
    }


}