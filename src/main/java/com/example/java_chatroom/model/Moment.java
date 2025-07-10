package com.example.java_chatroom.model;

import java.util.Date;
import java.util.List;

public class Moment {
    private Long id;
    private Long userId;
    private String content;
    private Date createTime;
    private int likeCount;
    private boolean likedByCurrentUser;
    private String username;  // 新增的用户名字段
    private List<MomentComment> comments;  // 评论列表

    // getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public Date getCreateTime() { return createTime; }
    public void setCreateTime(Date createTime) { this.createTime = createTime; }

    public int getLikeCount() { return likeCount; }
    public void setLikeCount(int likeCount) { this.likeCount = likeCount; }

    public boolean isLikedByCurrentUser() { return likedByCurrentUser; }
    public void setLikedByCurrentUser(boolean likedByCurrentUser) { this.likedByCurrentUser = likedByCurrentUser; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public List<MomentComment> getComments() { return comments; }
    public void setComments(List<MomentComment> comments) { this.comments = comments; }
}
