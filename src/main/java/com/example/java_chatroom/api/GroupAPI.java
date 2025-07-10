package com.example.java_chatroom.api;

import com.example.java_chatroom.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

@RestController
public class GroupAPI {

    @Autowired
    private GroupMapper groupMapper;

    @Autowired
    private GroupUsersMapper groupUsersMapper;

    @Autowired
    private GroupMessageRecordMapper groupMessageRecordMapper;

    // 创建新群组
    @PostMapping("/createGroup")
    public Object createGroup(@RequestParam String groupName, @SessionAttribute("user") User user) {
        Group group = new Group();
        group.setGroupName(groupName);
        group.setOwnerId(user.getUserId());
        groupMapper.insert(group);

        // 添加创建者到群成员表
        GroupUsers gu = new GroupUsers();
        gu.setGroupId(group.getGroupId());
        gu.setUserId(user.getUserId());
        groupUsersMapper.insert(gu);

        HashMap<String, Integer> resp = new HashMap<>();
        resp.put("groupId", group.getGroupId());
        return resp;
    }

    // 获取当前用户所在的所有群
    @GetMapping("/groupList")
    public Object getGroupList(@SessionAttribute("user") User user) {
        return groupMapper.selectByUserId(user.getUserId());
    }

    // 获取群历史消息
    @GetMapping("/groupMessage")
    public Object getGroupMessages(int groupId) {
        List<GroupMessageRecord> messages = groupMessageRecordMapper.selectByGroupId(groupId);
        Collections.reverse(messages); // 按时间升序排列
        return messages;
    }
}
