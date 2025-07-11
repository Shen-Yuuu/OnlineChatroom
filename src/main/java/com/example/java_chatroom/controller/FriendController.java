package com.example.java_chatroom.controller;

import com.example.java_chatroom.model.User;
import com.example.java_chatroom.service.FriendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/friend")
public class FriendController {

    @Autowired
    private FriendService friendService;

    /**
     * 删除好友接口
     */
    @PostMapping("/delete")
    public Map<String, Object> deleteFriend(@RequestParam int friendId, HttpSession session) {
        User user = (User) session.getAttribute("user");
        Map<String, Object> result = new HashMap<>();

        if (user == null) {
            result.put("success", false);
            result.put("message", "用户未登录");
            return result;
        }

        try {
            // 调用新的方法：删除好友并清理相关会话数据
            friendService.removeFriendAndClearSessions(user.getUserId(), friendId);
            result.put("success", true);
            result.put("message", "删除成功");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }

        return result;
    }

}
