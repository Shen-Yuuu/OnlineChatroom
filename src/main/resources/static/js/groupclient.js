// 保存当前登录的用户信息
let currentUser = null;
// 保存当前选中的群组
let currentGroupId = null;

// 1. WebSocket 初始化
let websocket = new WebSocket("ws://" + location.host + "/groupWebSocket");

websocket.onopen = function () {
    console.log("群聊 WebSocket 连接成功!");
}

websocket.onmessage = function (e) {
    console.log("群聊 WebSocket 收到消息! " + e.data);
    let resp = JSON.parse(e.data);
    if (resp.type === 'groupMessage') {
        handleGroupMessage(resp);
    } else {
        console.log("收到了未知的消息类型: " + resp.type);
    }
}

websocket.onclose = function () {
    console.log("群聊 WebSocket 连接关闭!");
}

websocket.onerror = function () {
    console.log("群聊 WebSocket 连接异常!");
}

// 2. 获取并显示用户信息
function getUserInfo() {
    $.ajax({
        type: 'get',
        url: 'userInfo',
        success: function (body) {
            if (body.userId && body.userId > 0) {
                currentUser = body;
                let userDiv = document.querySelector('.main .left .user');
                userDiv.innerHTML = '<h5>' + body.username + '</h5>';
                userDiv.setAttribute("user-id", body.userId);
                // 获取到用户信息后再去获取群组列表
                getGroupList();
            } else {
                alert("当前用户未登录!");
                location.assign('login.html');
            }
        }
    });
}

// 3. 获取并显示群组列表
function getGroupList() {
    $.ajax({
        type: 'get',
        url: 'groupList',
        success: function (body) {
            let groupListUL = document.querySelector('#group-list');
            groupListUL.innerHTML = '';
            for (let group of body) {
                let li = document.createElement('li');
                li.innerHTML = '<h4>' + group.groupName + '</h4>' + '<p>...</p>';
                li.setAttribute('group-id', group.groupId);
                groupListUL.appendChild(li);

                li.onclick = function () {
                    clickGroup(li);
                }
            }
        }
    });
}

// 4. 点击群组的处理逻辑
function clickGroup(groupLi) {
    // 设置选中样式
    let allLis = document.querySelectorAll('#group-list li');
    allLis.forEach(li => li.classList.remove('selected'));
    groupLi.classList.add('selected');

    // 更新中间区域标题
    let groupName = groupLi.querySelector('h4').innerHTML;
    document.querySelector('.center .title span').innerHTML = groupName;
    document.getElementById('add-member-area').style.display = 'block';

    currentGroupId = groupLi.getAttribute('group-id');

    // 获取并显示历史消息
    getGroupHistoryMessages(currentGroupId);
    // 获取并显示群成员
    getGroupMembers(currentGroupId);
}

// 5. 获取群历史消息
function getGroupHistoryMessages(groupId) {
    const messageShowDiv = document.querySelector('.center .message-show');
    
    // [健壮性检查] 确保元素存在
    if (!messageShowDiv) {
        console.error("【关键错误】: 无法找到用于显示消息的元素 ('.center .message-show')！请检查 groupclient.html 的结构是否正确。");
        return; 
    }

    messageShowDiv.innerHTML = ''; // 清空历史消息
    $.ajax({
        type: 'get',
        url: 'groupMessage?groupId=' + groupId,
        success: function (messages) {
            for (let msg of messages) {
                let messageData = {
                    fromId: msg.userId, 
                    fromName: msg.fromName,
                    sessionId: msg.groupId,
                    content: msg.messageContent
                };
                addMessage(messageShowDiv, messageData, msg.userId === currentUser.userId);
            }
            scrollBottom(messageShowDiv);
        }
    });
}

// 6. 获取并显示群成员
function getGroupMembers(groupId) {
    $.ajax({
        type: 'get',
        url: '/group/members?groupId=' + groupId,
        success: function(members) {
            console.log('获取到群成员:', members);
            let memberListDiv = document.querySelector('#member-list');
            let memberListUL = memberListDiv.querySelector('ul');
            
            // [修复] 确保 memberListUL 存在
            if (!memberListUL) {
                console.error("【关键错误】: 无法找到群成员列表的 <ul> 元素! 请检查HTML结构。");
                return;
            }
            
            // 更新标题
            let title = memberListDiv.querySelector('h5');
            if (title) {
                title.innerText = '群成员 ('+ members.length +')';
            }

            // [修复] 只清空 ul 的内容，而不是整个 div
            memberListUL.innerHTML = '';

            // 添加新的列表项
            for(let member of members) {
                let li = document.createElement('li');
                li.innerText = member.username;
                li.setAttribute('user-id', member.userId);
                memberListUL.appendChild(li);
            }
        },
        error: function(xhr, status, error) {
            console.error("获取群成员失败!", error);
            alert("加载群成员列表失败，请查看控制台日志。");
        }
    });
}


// 7. 发送消息
function initSendButton() {
    let sendButton = document.querySelector('.center .ctrl button');
    let messageInput = document.querySelector('.center .message-input');
    
    sendButton.onclick = function () {
        sendMessage(messageInput);
    }
    
    messageInput.onkeydown = function(event) {
        if (event.key === 'Enter') {
            sendMessage(messageInput);
            // 阻止默认的回车换行行为
            event.preventDefault();
        }
    }
}

function sendMessage(messageInput) {
    if (!messageInput.value.trim()) {
        return;
    }
    if (currentGroupId === null) {
        alert('请先选择一个群组!');
        return;
    }
    let req = {
        type: 'groupMessage',
        groupId: currentGroupId,
        content: messageInput.value
    };
    websocket.send(JSON.stringify(req));
    messageInput.value = '';
}

// 8. 处理收到的群消息
function handleGroupMessage(resp) {
    // 如果收到的消息就是当前选中的群组的消息, 则直接在右侧展示
    if (resp.sessionId == currentGroupId) {
        let messageShowDiv = document.querySelector('.center .message-show');
        addMessage(messageShowDiv, resp, resp.fromId === currentUser.userId);
        scrollBottom(messageShowDiv);
    }
    // 更新左侧群组预览消息
    let groupLi = findGroupLi(resp.sessionId);
    if (groupLi) {
        let p = groupLi.querySelector('p');
        if (!p) {
            p = document.createElement('p');
            groupLi.appendChild(p);
        }
        p.innerHTML = resp.content;
        if (p.innerHTML.length > 20) {
            p.innerHTML = p.innerHTML.substring(0, 20) + '...';
        }
         // 把收到消息的会话, 给放到会话列表最上面. 
        let groupListUL = document.querySelector('#group-list');
        groupListUL.insertBefore(groupLi, groupListUL.children[0]);
    }
}

function findGroupLi(targetGroupId) {
    let groupLis = document.querySelectorAll('#group-list li');
    for (let li of groupLis) {
        if (li.getAttribute('group-id') == targetGroupId) {
            return li;
        }
    }
    return null;
}

// 9. 辅助函数：添加消息到聊天框
function addMessage(messageShowDiv, msg, isMe) {
    let messageDiv = document.createElement('div');
    messageDiv.className = isMe ? 'message-right' : 'message-left';
    // HTML 模板字符串
    messageDiv.innerHTML = `
        <div class="message-container">
            <div class="message-sender">${msg.fromName}</div>
            <div class="message-bubble">${msg.content}</div>
        </div>
    `;
    messageShowDiv.appendChild(messageDiv);
}

// 10. 辅助函数：滚动到底部
function scrollBottom(elem) {
    if(elem) {
       elem.scrollTop = elem.scrollHeight;
    }
}

// 11. 初始化创建群组功能
function initCreateGroup() {
    const showBtn = document.getElementById('create-group-show-btn');
    const form = document.getElementById('create-group-form');
    const submitBtn = document.getElementById('create-group-submit-btn');
    const nameInput = document.getElementById('group-name-input');
    const cancelBtn = document.getElementById('create-group-cancel-btn');

    if (!showBtn || !form || !submitBtn || !nameInput || !cancelBtn) {
        console.error("创建群组的按钮或表单未找到，请检查HTML！");
        return;
    }

    showBtn.onclick = function() {
        form.style.display = form.style.display === 'none' ? 'block' : 'none';
        nameInput.focus();
    }

    cancelBtn.onclick = function() {
        form.style.display = 'none';
    }

    submitBtn.onclick = function() {
        const groupName = nameInput.value.trim();
        if (!groupName) {
            alert('群组名称不能为空!');
            return;
        }

        $.ajax({
            type: 'post',
            url: 'createGroup?groupName=' + groupName,
            success: function(resp) {
                // [修复] 后端返回的是 {groupId: xxx} 而不是 {ok: true}
                if (resp && resp.groupId > 0) {
                    alert("创建成功!");
                    form.style.display = 'none';
                    nameInput.value = '';
                    // 刷新群组列表
                    getGroupList();
                } else {
                    // [修复] 提示更详细的错误
                    alert("创建失败! " + (resp.reason || '未能获取到群组ID'));
                }
            },
            error: function(req, status, err) {
                alert("创建群组请求失败: " + err);
            }
        });
    }
}

// 12. 初始化添加成员功能
function initAddMember() {
    const showBtn = document.getElementById('add-member-show-btn');
    const form = document.getElementById('add-member-form');
    const submitBtn = document.getElementById('add-member-submit-btn');
    const nameInput = document.getElementById('member-name-input');
    const cancelBtn = document.getElementById('add-member-cancel-btn');

    if (!showBtn || !form || !submitBtn || !nameInput || !cancelBtn) {
        console.error("添加成员的按钮或表单未找到，请检查HTML！");
        return;
    }

    showBtn.onclick = function() {
        if (currentGroupId === null) {
            alert("请先选择一个群组！");
            return;
        }
        form.style.display = form.style.display === 'none' ? 'block' : 'none';
        nameInput.focus();
    }
    
    cancelBtn.onclick = function() {
        form.style.display = 'none';
    }

    submitBtn.onclick = function() {
        const username = nameInput.value.trim();
        if (!username) {
            alert('要添加的用户名不能为空!');
            return;
        }
        if (currentGroupId === null) {
            alert('请先选择一个群组!');
            return;
        }

        // 步骤1: 根据用户名查找用户ID
        $.ajax({
            type: 'get',
            url: '/user/findByName?username=' + username,
            success: function(user) {
                if (user && user.userId) {
                    // 步骤2: 找到用户后，调用添加成员接口
                    addMemberToGroup(user.userId);
                } else {
                    alert("未找到用户: " + username);
                }
            },
            error: function() {
                alert("查找用户失败，请检查网络或服务器状态。");
            }
        });
    }

    function addMemberToGroup(userId) {
        $.ajax({
            type: 'post',
            url: '/group/addMember',
            // [修复] 后端使用 @RequestParam, 所以需要发送 form-data 格式, 而不是 json
            data: {
                groupId: currentGroupId,
                friendId: userId // [修复] 参数名从 userId 改为 friendId
            },
            success: function(resp) {
                // [修复] 后端返回的是 {ok: "true"}, 需要正确判断
                if (resp && resp.ok === "true") {
                    alert("添加成员成功!");
                    form.style.display = 'none';
                    nameInput.value = '';
                    // 刷新成员列表
                    getGroupMembers(currentGroupId);
                } else {
                    alert("添加成员失败: " + (resp.reason || '未知错误'));
                }
            },
            error: function(req, status, err) {
                // 后端可能直接返回文本错误信息
                let errorMsg = req.responseText || err;
                alert("添加成员请求失败: " + errorMsg);
            }
        });
    }
}


// 页面加载后立即执行的函数
window.onload = function() {
    getUserInfo();
    initSendButton();
    initCreateGroup(); 
    initAddMember();   
};