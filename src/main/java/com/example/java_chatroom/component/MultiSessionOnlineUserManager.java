package com.example.java_chatroom.component;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

// 支持多会话的在线用户管理器
@Component
public class MultiSessionOnlineUserManager {
    // 使用 CopyOnWriteArrayList 来处理并发读写，适用于读多写少的场景
    private final ConcurrentHashMap<Integer, List<WebSocketSession>> sessions = new ConcurrentHashMap<>();

    // 1) 用户上线, 记录会话
    public void online(int userId, WebSocketSession session) {
        // 使用 computeIfAbsent 确保线程安全地获取或创建列表
        List<WebSocketSession> userSessions = sessions.computeIfAbsent(userId, k -> new CopyOnWriteArrayList<>());
        userSessions.add(session);
        System.out.println("[" + userId + "] 的一个新会话上线! 当前总会话数: " + userSessions.size());
    }

    // 2) 用户下线, 移除会话
    public void offline(int userId, WebSocketSession session) {
        List<WebSocketSession> userSessions = sessions.get(userId);
        if (userSessions != null) {
            userSessions.remove(session);
            System.out.println("[" + userId + "] 的一个会话下线!");
            // 如果该用户的所有会话都已下线，则从 map 中移除该用户
            if (userSessions.isEmpty()) {
                sessions.remove(userId);
                System.out.println("[" + userId + "] 所有会话均已下线!");
            }
        }
    }

    // 3) 根据 userId 获取到该用户的所有 WebSocketSession
    public List<WebSocketSession> getSessions(int userId) {
        return sessions.get(userId);
    }
} 