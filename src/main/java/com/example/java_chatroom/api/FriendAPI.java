package com.example.java_chatroom.api;

import com.example.java_chatroom.model.Friend;
import com.example.java_chatroom.model.FriendMapper;
import com.example.java_chatroom.model.User;
import com.example.java_chatroom.service.FriendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class FriendAPI {
    @Resource
    private FriendMapper friendMapper;

    @Autowired
    private FriendService friendService;


    @GetMapping("/friendList")
    @ResponseBody
    public Object getFriendList(HttpServletRequest req) {
        // 1. 先从会话中, 获取到 userId 是啥.
        HttpSession session = req.getSession(false);
        if (session == null) {
            // 当前用户会话不存在(未登录)
            // 直接返回一个空的列表即可.
            System.out.println("[getFriendList] session 不存在");
            return new ArrayList<Friend>();
        }
        User user = (User) session.getAttribute("user");
        if (user == null) {
            // 当前用户对象没在会话中存在
            System.out.println("[getFriendList] user 不存在");
            return new ArrayList<Friend>();
        }
        // 2. 根据 userId 从数据库查数据即可
        return friendMapper.selectFriendList(user.getUserId());
    }

    @PostMapping("/friend/add")
    public Map<String, Object> addFriend(@RequestParam int friendId, HttpServletRequest req) {
        Map<String, Object> result = new HashMap<>();
        // 假设当前登录用户的 User 对象存储在 Session 中
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            result.put("success", false);
            result.put("message", "用户未登录");
            return result;
        }

        // 从 Session 中获取当前用户的 ID
        com.example.java_chatroom.model.User currentUser = (com.example.java_chatroom.model.User) session.getAttribute("user");
        int currentUserId = currentUser.getUserId();

        try {
            friendService.addFriend(currentUserId, friendId);
            result.put("success", true);
            result.put("message", "好友添加成功");
        } catch (RuntimeException e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return result;
    }
}
