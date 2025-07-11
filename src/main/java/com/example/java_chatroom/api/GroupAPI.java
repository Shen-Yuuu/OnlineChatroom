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

    @Autowired
    private UserMapper userMapper;

    // 创建新群组
    @Transactional
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
    public Object getGroupMessages(@RequestParam int groupId) {
        List<GroupMessageRecord> messages = groupMessageRecordMapper.selectByGroupId(groupId);
        //Collections.reverse(messages); // 按时间升序排列
        return messages;
    }

    // 添加群成员
    @PostMapping("/group/addMember")
    public Object addMember(@RequestParam int groupId, @RequestParam int friendId, @SessionAttribute("user") User user) {
        // 1. 先校验操作者是否为群主
        Group group = groupMapper.selectById(groupId);
        if (group == null) {
            return new HashMap<String, String>() {{
                put("ok", "false");
                put("reason", "群组不存在");
            }};
        }
        if (group.getOwnerId() != user.getUserId()) {
            return new HashMap<String, String>() {{
                put("ok", "false");
                put("reason", "您不是群主, 没有权限操作");
            }};
        }

        // 2. 校验用户是否已经在群里
        if (groupUsersMapper.findByGroupIdAndUserId(groupId, friendId) != null) {
            return new HashMap<String, String>() {{
                put("ok", "false");
                put("reason", "该用户已在群聊中");
            }};
        }

        // 3. 添加成员
        GroupUsers newMember = new GroupUsers();
        newMember.setGroupId(groupId);
        newMember.setUserId(friendId);
        groupUsersMapper.insert(newMember);

        return new HashMap<String, String>() {{
            put("ok", "true");
        }};
    }

    // 获取群成员列表
    @GetMapping("/group/members")
    public Object getMembers(@RequestParam int groupId) {
        return userMapper.selectUsersByGroupId(groupId);
    }
}
