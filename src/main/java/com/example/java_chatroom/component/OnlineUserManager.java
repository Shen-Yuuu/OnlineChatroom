package com.example.java_chatroom.component;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

// 通过这个类来记录当前用户在线的状态. (维护了 userId 和 WebSocketSession 之间的映射)
@Component
public class OnlineUserManager {
    // 此处这个 哈希表 要考虑到线程安全问题.
    private ConcurrentHashMap<Integer, WebSocketSession> sessions = new ConcurrentHashMap<>();

    // 1) 用户上线, 给这个哈希表中插入键值对
    public void online(int userId, WebSocketSession session) {
        // 不论用户是否在线, 都直接覆盖. 以最新的 session 为准
        sessions.put(userId, session);
        System.out.println("[" + userId + "] 上线!");
    }

    // 2) 用户下线, 针对这个哈希表进行删除元素
    public void offline(int userId, WebSocketSession session) {
        WebSocketSession existSession = sessions.get(userId);
        if (existSession == session) {
            // 如果这俩 session 是同一个, 才真正进行下线操作. 否则就啥都不干
            sessions.remove((userId));
            System.out.println("[" + userId + "] 下线!");
        }
    }

    // 3) 根据 userId 获取到 WebSocketSession
    public WebSocketSession getSession(int userId) {
        return sessions.get(userId);
    }
}
