package com.example.java_chatroom.api;

import com.example.java_chatroom.model.Message;
import com.example.java_chatroom.model.MessageMapper;
import com.example.java_chatroom.model.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Collections;
import java.util.List;

@RestController
public class MessageAPI {
    @Resource
    private MessageMapper messageMapper;

    @GetMapping("/message")
    public List<Message> getMessages(@RequestParam int sessionId, @RequestParam(required = false) String query) {
        if (query != null && !query.trim().isEmpty()) {
            // 如果有搜索关键词，使用带搜索功能的查询
            return messageMapper.selectBySessionIdWithQuery(sessionId, query);
        } else {
            // 如果没有搜索关键词，使用原来的查询
            return messageMapper.selectBySessionId(sessionId);
        }
    }

    @GetMapping("/message/history")
    public List<Message> getHistory(@RequestParam(required = false) String query, HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        if (session == null) {
            // 用户未登录
            return Collections.emptyList();
        }
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return Collections.emptyList();
        }
        return messageMapper.selectHistoryForUser(user.getUserId(), query);
    }
}
