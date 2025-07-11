// src/main/java/com/example/java_chatroom/api/GroupWebSocketAPI.java

package com.example.java_chatroom.api;

import com.example.java_chatroom.component.MultiSessionOnlineUserManager;
import com.example.java_chatroom.model.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Date;
import java.util.List;

@Component
public class GroupWebSocketAPI extends TextWebSocketHandler {

    @Autowired
    private MultiSessionOnlineUserManager onlineUserManager;

    @Autowired
    private GroupMapper groupMapper;

    @Autowired
    private GroupUsersMapper groupUsersMapper;

    @Autowired
    private GroupMessageRecordMapper groupMessageRecordMapper;

    private ObjectMapper objectMapper = new ObjectMapper();

    // 连接建立后执行
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        System.out.println("[GroupWebSocketAPI] 连接成功!");
        User user = (User) session.getAttributes().get("user");
        if (user == null) {
            return;
        }
        onlineUserManager.online(user.getUserId(), session);
    }

    // 收到消息时执行
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        System.out.println("[GroupWebSocketAPI] 收到消息: " + message.getPayload());

        User user = (User) session.getAttributes().get("user");
        if (user == null) {
            System.out.println("[GroupWebSocketAPI] 用户未登录，无法处理群消息");
            return;
        }

        MessageRequest req = objectMapper.readValue(message.getPayload(), MessageRequest.class);

        if ("groupMessage".equals(req.getType())) {
            transferGroupMessage(user, req);
        } else {
            System.out.println("[GroupWebSocketAPI] 不支持的消息类型: " + req.getType());
        }
    }

    // 群组消息转发逻辑
    private void transferGroupMessage(User fromUser, MessageRequest req) throws IOException {
        // [安全检查] 验证用户是否在群聊中
        GroupUsers memberCheck = groupUsersMapper.findByGroupIdAndUserId(req.getGroupId(), fromUser.getUserId());
        if (memberCheck == null) {
            System.out.println("[GroupWebSocketAPI] 安全警告: 用户 " + fromUser.getUserId() + " 尝试向未加入的群聊 " + req.getGroupId() + " 发送消息");
            return;
        }

        // 构造响应对象
        MessageResponse resp = new MessageResponse();
        resp.setType("groupMessage");
        resp.setFromId(fromUser.getUserId());
        resp.setFromName(fromUser.getUsername());
        resp.setSessionId(req.getGroupId()); // sessionId 可作为 groupId 使用
        resp.setContent(req.getContent());

        String respJson = objectMapper.writeValueAsString(resp);

        // 获取群成员列表
        List<GroupUsers> members = groupUsersMapper.selectByGroupId(req.getGroupId());

        // 广播给所有在线成员
        for (GroupUsers member : members) {
            List<WebSocketSession> targetSessions = onlineUserManager.getSessions(member.getUserId());
            if (targetSessions == null) {
                // 用户不在线
                continue;
            }
            for (WebSocketSession targetSession : targetSessions) {
                if (targetSession.isOpen()) {
                    targetSession.sendMessage(new TextMessage(respJson));
                }
            }
        }

        // 存入数据库
        GroupMessageRecord record = new GroupMessageRecord();
        record.setGroupId(req.getGroupId());
        record.setUserId(fromUser.getUserId());
        record.setMessageContent(req.getContent());
        record.setCreateTime(new Date());
        groupMessageRecordMapper.insert(record);
    }

    // 连接异常时执行
    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        System.out.println("[GroupWebSocketAPI] 连接异常: " + exception.getMessage());

        User user = (User) session.getAttributes().get("user");
        if (user != null) {
            onlineUserManager.offline(user.getUserId(), session);
        }
    }

    // 连接关闭时执行
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        System.out.println("[GroupWebSocketAPI] 连接关闭: " + status.getReason());

        User user = (User) session.getAttributes().get("user");
        if (user != null) {
            onlineUserManager.offline(user.getUserId(), session);
        }
    }
}
