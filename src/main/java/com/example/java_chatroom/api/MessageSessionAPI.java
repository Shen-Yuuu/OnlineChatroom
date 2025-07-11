package com.example.java_chatroom.api;

import com.example.java_chatroom.model.*;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@RestController
public class MessageSessionAPI {
    @Resource
    private MessageSessionMapper messageSessionMapper;

    @Resource
    private MessageMapper messageMapper;

    @GetMapping("/sessionList")
    @ResponseBody
    public Object getMessageSessionList(HttpServletRequest req) {
        List<MessageSession> messageSessionList = new ArrayList<>();
        // 1. 获取到当前用户的 userId (从 spring 的 session 中获取)
        HttpSession session = req.getSession(false);
        if (session == null) {
            System.out.println("[getMessageSessionList] session == null");
            return messageSessionList;
        }
        User user = (User) session.getAttribute("user");
        if (user == null) {
            System.out.println("[getMessageSessionList] user == null");
            return messageSessionList;
        }
        // 2. 根据 userId 查询数据库, 查出来有哪些会话id
        List<Integer> sessionIdList = messageSessionMapper.getSessionIdsByUserId(user.getUserId());
        for (int sessionId : sessionIdList) {
            MessageSession messageSession = new MessageSession();
            messageSession.setSessionId(sessionId);
            // 3. 遍历会话id, 查询出每个会话里涉及到的好友都有谁
            List<Friend> friends = messageSessionMapper.getFriendsBySessionId(sessionId, user.getUserId());
            messageSession.setFriends(friends);
            // 4. 遍历会话id, 查询出每个会话的最后一条消息
            //    有可能出现按照会话 id 查不到消息的情况 (新创建的会话可能还没来得及发消息呢
            String lastMessage = messageMapper.getLastMessageBySessionId(sessionId);
            if (lastMessage == null) {
                messageSession.setLastMessage("");
            } else {
                messageSession.setLastMessage(lastMessage);
            }
            messageSessionList.add(messageSession);
        }
        // 最终目标就是构造出一个 MessageSession 对象数组
        return messageSessionList;
    }

    @PostMapping("/session")
    @ResponseBody
    @Transactional
    public Object addMessageSession(@RequestParam int toUserId, @SessionAttribute("user") User user) {
        HashMap<String, Integer> resp = new HashMap<>();

        // [修复] 改造为 Get Or Create
        // 1. 先查询是否存在
        Integer sessionId = messageSessionMapper.findSessionByUserIds(user.getUserId(), toUserId);

        if (sessionId != null) {
            // 2. 如果存在，直接返回
            System.out.println("[addMessageSession] 会话已存在! sessionId=" + sessionId);
            resp.put("sessionId", sessionId);
            return resp;
        }

        // 3. 如果不存在，创建新的会话
        MessageSession messageSession = new MessageSession();
        messageSessionMapper.addMessageSession(messageSession);

        MessageSessionUserItem item1 = new MessageSessionUserItem();
        item1.setSessionId(messageSession.getSessionId());
        item1.setUserId(user.getUserId());
        messageSessionMapper.addMessageSessionUser(item1);

        MessageSessionUserItem item2 = new MessageSessionUserItem();
        item2.setSessionId(messageSession.getSessionId());
        item2.setUserId(toUserId);
        messageSessionMapper.addMessageSessionUser(item2);

        System.out.println("[addMessageSession] 新增会话成功! sessionId=" + messageSession.getSessionId()
                + " userId1=" + user.getUserId() + " userId2=" + toUserId);

        resp.put("sessionId", messageSession.getSessionId());
        return resp;
    }
}
