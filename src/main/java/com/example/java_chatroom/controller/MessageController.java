package com.example.java_chatroom.controller;

import com.example.java_chatroom.model.Result;
import com.example.java_chatroom.model.Message;
import com.example.java_chatroom.model.MessageMapper;
import com.example.java_chatroom.model.User;
import com.example.java_chatroom.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpSession;
import com.example.java_chatroom.api.WebSocketAPI;
import java.io.IOException;

@RestController
@RequestMapping("/message")
public class MessageController {

    @Autowired
    private MessageService messageService;

    @Autowired
    private WebSocketAPI webSocketAPI;

    @Autowired
    private MessageMapper messageMapper;

    @PostMapping("/recall/{messageId}")
    public Result recallMessage(@PathVariable Long messageId, HttpSession session) {
        // 从 session 中获取 user 对象, 而不是直接获取 userId
        User user = (User) session.getAttribute("user");
        if (user == null) {
            // 如果 user 对象不存在, 说明用户未登录
            return Result.fail("请先登录");
        }
        Integer userId = user.getUserId();

        boolean success = messageService.recallMessage(messageId, userId);
        if (success) {
            try {
                Message message = messageMapper.selectByMessageId(messageId);
                if (message != null) {
                    webSocketAPI.sendRecallNotification(messageId, message.getSessionId());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return Result.success("消息撤回成功");
        } else {
            return Result.fail("撤回失败，只能撤回自己发送的消息");
        }
    }
}