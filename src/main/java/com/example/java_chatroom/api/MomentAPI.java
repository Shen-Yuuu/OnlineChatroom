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
    public List<Moment> listMoments(@SessionAttribute("user") User user) {
        List<Moment> moments = momentMapper.selectAllMomentsByFriend(user.getUserId());
        for (Moment moment : moments) {
            // 判断当前用户是否已点赞
            boolean liked = likeMapper.existsLike(moment.getId(), (long) user.getUserId());
            moment.setLikedByCurrentUser(liked);
            moment.setComments(commentMapper.selectCommentsByMomentId(moment.getId()));
        }
        return moments;
    }



    // 点赞
    @PostMapping("/like")
    @ResponseBody
    public int likeMoment(@RequestBody MomentLike like) {
        if (!likeMapper.existsLike(like.getMomentId(), like.getUserId())) {
            likeMapper.insertLike(like);
        }
        return likeMapper.countLikes(like.getMomentId());
    }

    @PostMapping("/unlike")
    @ResponseBody
    public int unlikeMoment(@RequestBody MomentLike like) {
        likeMapper.deleteLike(like.getMomentId(), like.getUserId());
        return likeMapper.countLikes(like.getMomentId());
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
    @ResponseBody
    public List<MomentComment> deleteComment(@PathVariable Long id, @RequestParam Long userId) {
        // 获取当前评论所属的 momentId
        MomentComment comment = commentMapper.selectById(id);
        if (comment == null || !comment.getUserId().equals(userId)) {
            throw new RuntimeException("无权操作此评论");
        }

        // 删除评论
        commentMapper.deleteComment(id, userId);

        // 返回该动态的最新评论列表
        return commentMapper.selectCommentsByMomentId(comment.getMomentId());
    }

}
