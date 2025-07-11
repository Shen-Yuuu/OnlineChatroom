package com.example.java_chatroom.service;

import com.example.java_chatroom.model.Message;
import com.example.java_chatroom.model.MessageMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class MessageService {

    @Autowired
    private MessageMapper messageMapper;

    // 撤回消息
    public boolean recallMessage(Long messageId, Integer userId) {
        Message message = messageMapper.selectByMessageId(messageId);

        if (message == null) {
            return false;
        }

        // 检查是否是消息发送者
        // 将 userId 拆箱为 int 类型进行比较, 避免潜在的 NullPointerException
        if (message.getFromId() != userId) {
            return false;
        }

        // 更新消息状态为已撤回
        return messageMapper.updateStatus(messageId, 1) > 0;
    }

    // 获取会话消息（过滤已撤回的消息）
    public List<Message> getSessionMessages(Integer sessionId) {
        return messageMapper.selectBySessionIdAndStatus(sessionId, 0);
    }
}