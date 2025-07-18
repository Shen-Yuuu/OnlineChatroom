package com.example.java_chatroom.api;

import com.example.java_chatroom.model.User;
import com.example.java_chatroom.model.UserMapper;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

@RestController
public class UserAPI {
    @Resource
    UserMapper userMapper;


    @PostMapping("/login")
    @ResponseBody
    public Object login(String username, String password, HttpServletRequest req) {
        // 1. 先去数据库中查查, 看 username 能否找到对应的 user 对象
        //    如果能找到则看一下密码是否匹配
        User user = userMapper.selectByName(username);
        if (user == null || !user.getPassword().equals(password)) {
            // 这俩条件具备一个, 就是登录失败!! 同时返回一个空的对象即可.
            System.out.println("登录失败! 用户名或者密码错误! " + user);
            return new User();
        }

        // 2. 如果都匹配, 登录成功! 创建会话!!
        HttpSession session = req.getSession(true);
        session.setAttribute("user", user);
        // 在返回之前, 把 password 给干掉. 避免返回不必要的信息.
        user.setPassword("");
        return user;
    }

    @PostMapping("/register")
    @ResponseBody
    public Object register(String username, String password) {
        User user = null;
        try {
            user = new User();
            user.setUsername(username);
            user.setPassword(password);
            int ret = userMapper.insert(user);
            System.out.println("注册 ret: " + ret);
            user.setPassword("");
        } catch (DuplicateKeyException e) {
            // 如果 insert 方法抛出上述异常, 说明名字重复了. 注册失败.
            user = new User();
            System.out.println("注册失败! username = " + username);
        }
        return user;
    }

    @GetMapping("/userInfo")
    @ResponseBody
    public Object getUserInfo(HttpServletRequest req) {
        // 1. 先从请求中获取到会话
        HttpSession session = req.getSession(false);
        if (session == null) {
            // 会话不存在, 用户尚未登录. 此时返回一个空的对象即可.
            System.out.println("[getUserInfo] 当前获取不到 session 对象!");
            return new User();
        }
        // 2. 从会话中获取到之前保存的用户对象.
        User user = (User) session.getAttribute("user");
        if (user == null) {
            System.out.println("[getUserInfo] 当前获取不到 user 对象!");
            return new User();
        }
        user.setPassword("");
        return user;
    }

    @GetMapping("/user/find")
    public ResponseEntity<Object> findUser(@RequestParam int userId) {
        User user = userMapper.findUserById(userId);
        if (user == null) {
            return new ResponseEntity<>("用户不存在", HttpStatus.NOT_FOUND);
        }
        // 只返回不敏感的信息
        Map<String, Object> result = new HashMap<>();
        result.put("userId", user.getUserId());
        result.put("username", user.getUsername());
        return ResponseEntity.ok(result);
    }

    @GetMapping("/user/findByName")
    public ResponseEntity<Object> findUserByName(@RequestParam String username) {
        User user = userMapper.selectByName(username);
        if (user == null) {
            return new ResponseEntity<>("用户不存在", HttpStatus.NOT_FOUND);
        }
        // 只返回不敏感的信息
        Map<String, Object> result = new HashMap<>();
        result.put("userId", user.getUserId());
        result.put("username", user.getUsername());
        return ResponseEntity.ok(result);
    }

    @PostMapping("/updateUser")
    @ResponseBody
    public Object updateUser(@RequestBody User submitData) {
        int i = userMapper.updateById(submitData);
        return i;
    }

    @GetMapping("/userInfo1")
    @ResponseBody
    public Object getUserInfo1(HttpServletRequest req) {
        // 1. 先从请求中获取到会话
        HttpSession session = req.getSession(false);
        if (session == null) {
            // 会话不存在, 用户尚未登录. 此时返回一个空的对象即可.
            System.out.println("[getUserInfo] 当前获取不到 session 对象!");
            return new User();
        }
        // 2. 从会话中获取到之前保存的用户对象.
        User user = (User) session.getAttribute("user");
        if (user == null) {
            System.out.println("[getUserInfo] 当前获取不到 user 对象!");
            return new User();
        }
        return user;
//        User test = new User();
//        test.setUserId(5);
//        test.setDescription("uienviuev");
//        test.setGender(1);
//        test.setPassword("787877");
//        test.setUsername("test");
//        return test;
    }
}




