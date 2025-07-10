package com.example.java_chatroom.api;

import com.example.java_chatroom.model.*;
import com.example.java_chatroom.model.MomentCommentMapper;
import com.example.java_chatroom.model.MomentLikeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/moment")
public class MomentAPI {

    @Autowired
    private MomentMapper momentMapper;
    @Autowired
    private MomentLikeMapper likeMapper;
    @Autowired
    private MomentCommentMapper commentMapper;

    // 发送说说
    @PostMapping("/send")
    public String sendMoment(@RequestBody Moment moment) {
        momentMapper.insertMoment(moment);
        return "ok";
    }

    // 删除说说
    @DeleteMapping("/delete/{id}")
    public String deleteMoment(@PathVariable Long id, @RequestParam Long userId) {
        // 先删除相关的点赞
        List<MomentLike> likes = likeMapper.selectByMomentId(id);
        for (MomentLike like : likes) {
            likeMapper.deleteLike(like.getMomentId(), like.getUserId());
        }

        // 再删除相关的评论
        List<MomentComment> comments = commentMapper.selectCommentsByMomentId(id);
        for (MomentComment comment : comments) {
            commentMapper.deleteComment(comment.getId(), comment.getUserId());
        }

        // 最后删除说说本身
        momentMapper.deleteMoment(id, userId);
        return "ok";
    }

    // 展示所有说说
    @GetMapping("/list")
    public List<Moment> listMoments() {
        List<Moment> moments = momentMapper.selectAllMoments();
        for (Moment moment : moments) {
            moment.setComments(commentMapper.selectCommentsByMomentId(moment.getId()));
        }
        return moments;
    }

    // 点赞
    @PostMapping("/like")
    public String likeMoment(@RequestBody MomentLike like) {
        if (!likeMapper.existsLike(like.getMomentId(), like.getUserId())) {
            likeMapper.insertLike(like);
        }
        return "ok";
    }

    // 取消点赞
    @PostMapping("/unlike")
    public String unlikeMoment(@RequestBody MomentLike like) {
        likeMapper.deleteLike(like.getMomentId(), like.getUserId());
        return "ok";
    }

    // 查询点赞数
    @GetMapping("/like/count")
    public int countLikes(@RequestParam Long momentId) {
        return likeMapper.countLikes(momentId);
    }

    // 评论
    @PostMapping("/comment")
    public String commentMoment(@RequestBody MomentComment comment) {
        commentMapper.insertComment(comment);
        return "ok";
    }

    // 查询评论列表
    @GetMapping("/comment/list")
    public List<MomentComment> listComments(@RequestParam Long momentId) {
        return commentMapper.selectCommentsByMomentId(momentId);
    }

    // 删除评论（可选）
    @DeleteMapping("/comment/delete/{id}")
    public String deleteComment(@PathVariable Long id, @RequestParam Long userId) {
        commentMapper.deleteComment(id, userId);
        return "ok";
    }
}
