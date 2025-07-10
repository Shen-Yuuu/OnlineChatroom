// 缓存当前用户ID
let currentUserIdCache = null;

// 获取当前登录用户ID（异步）
async function getCurrentUserId() {
    try {
        const res = await fetch('/userInfo');
        if (!res.ok) throw new Error('无法获取用户信息');
        const user = await res.json();
        currentUserIdCache = user.userId;
        return user.userId || null;
    } catch (err) {
        alert("无法获取用户信息，请重新登录！");
        return null;
    }
}

// 显示当前用户名
function displayCurrentUser() {
    fetch('/userInfo')
        .then(res => res.json())
        .then(user => {
            document.getElementById('current-user').textContent = `当前用户：${user.username}`;
        })
        .catch(err => {
            console.error('获取用户信息失败:', err);
        });
}

// 加载所有动态
async function loadMoments() {
    const userId = await getCurrentUserId();
    if (!userId) {
        document.getElementById('moment-list').innerHTML = '<p>请先登录！</p>';
        return;
    }

    fetch('/api/moment/list')
        .then(res => res.json())
        .then(data => {
            let html = '';
            data.forEach(m => {
                const hasLiked = m.likedByCurrentUser || false;

                html += `
                <div class="moment-item">
                    <div class="moment-header">
                        <span class="username">${m.username || '未知用户'}</span>
                        ${m.userId === userId ? `<button class="delete-btn" onclick="deleteMoment(${m.id})">删除</button>` : ''}
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
                                        ${comment.userId === userId ?
                        `<button class="delete-btn" onclick="deleteComment(${comment.id}, ${comment.userId}, ${m.id})">删除</button>` : ''}
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
    const userId = await getCurrentUserId();

    if (!content || !userId) {
        alert("请输入内容并登录！");
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

    fetch(`/api/moment/delete/${id}?userId=${userId}`, { method: 'DELETE' })
        .then(response => {
            if (!response.ok) {
                throw new Error('网络错误');
            }
            // 如果后端返回的是空响应（如 204 No Content），不要调用 .json()
            return response.text(); // 或直接 return Promise.resolve()
        })
        .then(() => {
            // 成功删除后重新加载动态
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
        .then(count => {
            updateLikeUI(momentId, count, true);
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
        .then(count => {
            updateLikeUI(momentId, count, false);
            alert("取消点赞成功！");
        })
        .catch(err => {
            console.error(err);
            alert("取消点赞失败，请重试");
        });
}

// 更新点赞 UI
function updateLikeUI(momentId, count, isLiked) {
    const likeBtn = document.querySelector(`.like-btn[data-moment-id='${momentId}']`);
    const likeCountEl = document.getElementById(`like-count-${momentId}`);
    if (likeBtn && likeCountEl) {
        likeCountEl.textContent = count;
        if (isLiked) {
            likeBtn.className = 'like-btn liked';
            likeBtn.textContent = '取消点赞';
            likeBtn.onclick = () => unlikeMoment(momentId);
        } else {
            likeBtn.className = 'like-btn';
            likeBtn.textContent = '点赞';
            likeBtn.onclick = () => likeMoment(momentId);
        }
    }
}

// 提交评论
async function submitComment(momentId) {
    const content = document.getElementById(`comment-content-${momentId}`).value.trim();
    const userId = await getCurrentUserId();

    if (!content || !userId) {
        alert("请填写完整信息！");
        return;
    }

    try {
        // 发送评论请求
        const res = await fetch('/api/moment/comment', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ momentId: momentId, userId: userId, content: content })
        });

        if (!res.ok) throw new Error('网络错误');

        // 提交成功后重新加载该动态的所有评论
        loadComments(momentId);

        // 清空输入框
        document.getElementById(`comment-content-${momentId}`).value = '';
        alert("评论成功！");
    } catch (err) {
        console.error(err);
        alert("评论失败，请重试");
    }
}

// 加载指定动态下的所有评论
async function loadComments(momentId) {
    const userId = await getCurrentUserId();
    if (!userId) return;

    fetch(`/api/moment/comment/list?momentId=${momentId}`)
        .then(res => res.json())
        .then(comments => {
            const commentList = document.getElementById(`comment-list-${momentId}`);
            commentList.innerHTML = ''; // 清空旧评论

            if (comments && comments.length > 0) {
                comments.forEach(comment => {
                    const li = document.createElement('li');
                    li.innerHTML = `
                        <span class="comment-username">${comment.username}</span>: ${comment.content}
                        ${comment.userId === userId ?
                        `<button class="delete-btn" onclick="deleteComment(${comment.id}, ${comment.userId}, ${momentId})">删除</button>` : ''}
                    `;
                    commentList.appendChild(li);
                });
            } else {
                commentList.innerHTML = '<li>暂无评论</li>';
            }
        })
        .catch(err => {
            console.error(err);
            alert("加载评论失败，请刷新页面");
        });
}

// 删除评论
async function deleteComment(commentId, userId, momentId) {
    if (!confirm("确定要删除这条评论吗？")) return;

    const currentUserId = await getCurrentUserId();
    if (currentUserId !== userId) {
        alert("无权操作他人评论");
        return;
    }

    fetch(`/api/moment/comment/delete/${commentId}?userId=${userId}`, { method: 'DELETE' })
        .then(response => {
            if (!response.ok) throw new Error('网络错误');
            return response.json();
        })
        .then(updatedComments => {
            updateCommentList(momentId, updatedComments); // 局部更新评论列表
            alert("评论删除成功！");
        })
        .catch(err => {
            console.error(err);
            alert("删除失败，请重试");
        });
}

// 更新评论列表（通用方法）
function updateCommentList(momentId, comments) {
    const commentList = document.getElementById(`comment-list-${momentId}`);
    commentList.innerHTML = ''; // 清空旧评论

    if (comments && comments.length > 0) {
        comments.forEach(comment => {
            const li = document.createElement('li');
            li.innerHTML = `
                <span class="comment-username">${comment.username}</span>: ${comment.content}
                ${comment.userId === currentUserIdCache ?
                `<button class="delete-btn" onclick="deleteComment(${comment.id}, ${comment.userId}, ${momentId})">删除</button>` : ''}
            `;
            commentList.appendChild(li);
        });
    } else {
        commentList.innerHTML = '<li>暂无评论</li>';
    }
}

// 页面加载时获取动态列表
window.onload = () => {
    displayCurrentUser();
    loadMoments();
};
