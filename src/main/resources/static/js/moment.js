// 获取当前登录用户ID（异步）
async function getCurrentUserId() {
    try {
        const res = await fetch('/userInfo');
        if (!res.ok) throw new Error('无法获取用户信息');
        const user = await res.json();
        return user.userId || null;
    } catch (err) {
        alert("无法获取用户信息，请重新登录！");
        return null;
    }
}

// 显示当前用户
function displayCurrentUser() {
    fetch('/userInfo')
        .then(res => res.json())
        .then(user => {
            document.getElementById('current-user').textContent = user.username;
        })
        .catch(err => {
            console.error('获取用户信息失败:', err);
        });
}

// 加载所有动态
function loadMoments() {
    fetch('/api/moment/list')
        .then(res => res.json())
        .then(data => {
            let html = '';
            data.forEach(m => {
                // 检查当前用户是否已点赞
                const hasLiked = m.likedByCurrentUser || false;

                html += `
                <div class="moment-item">
                    <div class="moment-header">
                        <span class="username">${m.username || '未知用户'}</span>
                        ${m.userId === getCurrentUserIdSync() ?
                    `<button class="delete-btn" onclick="deleteMoment(${m.id})">删除</button>` : ''}
                    </div>
                    <div class="moment-content">${m.content}</div>
                    <div class="moment-actions">
                        <button class="like-btn ${hasLiked ? 'liked' : ''}" 
                                data-moment-id="${m.id}"
                                onclick="${hasLiked ? 'unlikeMoment(' + m.id + ')' : 'likeMoment(' + m.id + ')'}">
                            ${hasLiked ? '取消点赞' : '点赞'}
                        </button>
                        <span>点赞数：<span id="like-count-${m.id}">${m.likeCount || 0}</span></span>
                    </div>
                    <div class="comment-section">
                        <ul class="comment-list" id="comment-list-${m.id}">
                            ${(m.comments && m.comments.length > 0) ?
                    m.comments.map(comment => `
                                    <li>
                                        <span class="comment-username">${comment.username}</span>: ${comment.content}
                                        ${comment.userId === getCurrentUserIdSync() ?
                        `<button class="delete-btn" onclick="deleteComment(${comment.id}, ${comment.userId})">删除</button>` : ''}
                                    </li>
                                `).join('') : '暂无评论'}
                        </ul>
                        <div class="comment-box">
                            <input type="text" id="comment-content-${m.id}" placeholder="写下你的评论...">
                            <button onclick="submitComment(${m.id})">提交评论</button>
                        </div>
                    </div>
                </div>`;
            });

            document.getElementById('moment-list').innerHTML = html;
        })
        .catch(err => {
            console.error(err);
            document.getElementById('moment-list').innerHTML = '<p>加载失败，请刷新页面</p>';
        });
}

// 发送新动态
async function sendMoment() {
    const content = document.getElementById('content').value.trim();
    if (!content) {
        alert("请输入内容！");
        return;
    }

    const userId = await getCurrentUserId();
    if (!userId) {
        alert("请先登录！");
        return;
    }

    fetch('/api/moment/send', {
        method: 'POST',
        headers: {'Content-Type': 'application/json'},
        body: JSON.stringify({userId: userId, content: content})
    })
        .then(() => {
            loadMoments();
            document.getElementById('content').value = '';
            alert("发布成功！");
        })
        .catch(err => {
            console.error(err);
            alert("发送失败，请重试");
        });
}

// 删除动态
async function deleteMoment(id) {
    if (!confirm("确定要删除这条动态吗？")) return;

    const userId = await getCurrentUserId();
    if (!userId) return;

    fetch(`/api/moment/delete/${id}?userId=${userId}`, {method: 'DELETE'})
        .then(response => {
            if (!response.ok) throw new Error('网络错误');
            return response.json();
        })
        .then(() => {
            loadMoments();
            alert("删除成功！");
        })
        .catch(err => {
            console.error(err);
            alert("删除失败，请重试");
        });
}

// 点赞动态
async function likeMoment(momentId) {
    const userId = await getCurrentUserId();
    if (!userId) {
        alert("请先登录！");
        return;
    }

    fetch('/api/moment/like', {
        method: 'POST',
        headers: {'Content-Type': 'application/json'},
        body: JSON.stringify({momentId: momentId, userId: userId})
    })
        .then(response => {
            if (!response.ok) throw new Error('网络错误');
            return response.json();
        })
        .then(() => {
            loadMoments();
            alert("点赞成功！");
        })
        .catch(err => {
            console.error(err);
            alert("点赞失败，请重试");
        });
}

// 取消点赞
async function unlikeMoment(momentId) {
    const userId = await getCurrentUserId();
    if (!userId) {
        alert("请先登录！");
        return;
    }

    fetch('/api/moment/unlike', {
        method: 'POST',
        headers: {'Content-Type': 'application/json'},
        body: JSON.stringify({momentId: momentId, userId: userId})
    })
        .then(response => {
            if (!response.ok) throw new Error('网络错误');
            return response.json();
        })
        .then(() => {
            loadMoments();
            alert("取消点赞成功！");
        })
        .catch(err => {
            console.error(err);
            alert("取消点赞失败，请重试");
        });
}

// 提交评论
async function submitComment(momentId) {
    const content = document.getElementById(`comment-content-${momentId}`).value.trim();
    const userId = await getCurrentUserId();

    if (!content || !userId) {
        alert("请填写完整信息！");
        return;
    }

    fetch('/api/moment/comment', {
        method: 'POST',
        headers: {'Content-Type': 'application/json'},
        body: JSON.stringify({momentId: momentId, userId: userId, content: content})
    })
        .then(response => {
            if (!response.ok) throw new Error('网络错误');
            return response.json();
        })
        .then(() => {
            loadMoments(); // 刷新朋友圈内容
            document.getElementById(`comment-content-${momentId}`).value = '';
            alert("评论成功！");
        })
        .catch(err => {
            console.error(err);
            alert("评论失败，请重试");
        });
}

// 删除评论
async function deleteComment(commentId, userId) {
    if (!confirm("确定要删除这条评论吗？")) return;

    const currentUserId = await getCurrentUserId();
    if (currentUserId !== userId) {
        alert("无权操作他人评论");
        return;
    }

    fetch(`/api/moment/comment/delete/${commentId}?userId=${userId}`, {method: 'DELETE'})
        .then(response => {
            if (!response.ok) throw new Error('网络错误');
            return response.json();
        })
        .then(() => {
            loadMoments();
            alert("评论删除成功！");
        })
        .catch(err => {
            console.error(err);
            alert("删除失败，请重试");
        });
}

// 同步获取用户ID（用于模板渲染）
let currentUserIdCache = null;
function getCurrentUserIdSync() {
    if (currentUserIdCache !== null) return currentUserIdCache;
    fetch('/userInfo', { method: 'GET' }).then(async res => {
        if (res.ok) {
            const user = await res.json();
            currentUserIdCache = user.userId;
        }
    });
    return null;
}
// 切换点赞状态
async function toggleLike(momentId, button) {
    const userId = await getCurrentUserId();
    if (!userId) {
        alert("请先登录！");
        return;
    }

    const isLiked = button.textContent.trim() === "取消点赞";

    const url = isLiked ? "/api/moment/unlike" : "/api/moment/like";
    const method = "POST";

    fetch(url, {
        method: method,
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ momentId: momentId, userId: userId })
    })
        .then(res => res.ok ? loadMoments() : Promise.reject("操作失败"))
        .catch(err => console.error(err));
}



// 页面加载时获取动态列表
window.onload = () => {
    displayCurrentUser();
    loadMoments();
};
