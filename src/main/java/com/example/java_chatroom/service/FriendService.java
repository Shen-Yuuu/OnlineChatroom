package com.example.java_chatroom.service;

import com.example.java_chatroom.model.FriendMapper;
import com.example.java_chatroom.model.UserMapper;
import com.example.java_chatroom.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class FriendService {

    @Autowired
    private FriendMapper friendMapper;

    @Autowired
    private UserMapper userMapper;

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
}