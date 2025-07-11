$(document).ready(function() {
    // 页面加载时获取所有聊天记录
    loadHistory();

    // 搜索按钮点击事件
    $('#search-button').on('click', function() {
        const query = $('#search-input').val();
        loadHistory(query);
    });

    // 支持回车键搜索
    $('#search-input').on('keypress', function(e) {
        if (e.which === 13) {
            const query = $('#search-input').val();
            loadHistory(query);
        }
    });
});

// 获取URL参数的函数
function getUrlParameter(name) {
    name = name.replace(/[\[]/, '\\[').replace(/[\]]/, '\\]');
    var regex = new RegExp('[\\?&]' + name + '=([^&#]*)');
    var results = regex.exec(location.search);
    return results === null ? '' : decodeURIComponent(results[1].replace(/\+/g, ' '));
}

function loadHistory(query = '') {
    const sessionId = getUrlParameter('sessionId');
    let url = '/message';

    if (sessionId) {
        // 如果有sessionId，查询特定会话的历史记录
        url += '?sessionId=' + sessionId;
    } else {
        // 如果没有sessionId，查询用户的所有历史记录
        url = '/message/history';
        if (query) {
            url += '?query=' + encodeURIComponent(query);
        }
    }

    // 如果既有sessionId又有query，需要同时传递
    if (sessionId && query) {
        url += '&query=' + encodeURIComponent(query);
    }

    $.ajax({
        url: url,
        type: 'GET',
        success: function(messages) {
            renderHistory(messages, query);
        },
        error: function(xhr, status, error) {
            console.error("获取聊天记录失败:", error);
            $('#history-list').html('<li>获取聊天记录失败，请稍后再试。</li>');
        }
    });
}

function renderHistory(messages, query = '') {
    const historyList = $('#history-list');
    historyList.empty();

    if (messages.length === 0) {
        if (query) {
            historyList.html('<li>没有找到包含"' + query + '"的聊天记录。</li>');
        } else {
            historyList.html('<li>没有找到相关聊天记录。</li>');
        }
        return;
    }

    // 如果有搜索关键词，高亮显示
    messages.forEach(function(message) {
        const messageDate = new Date(message.createTime).toLocaleString();
        let content = message.content;

        // 如果有搜索关键词，高亮显示
        if (query && query.trim() !== '') {
            const regex = new RegExp('(' + query.replace(/[.*+?^${}()|[\]\\]/g, '\\$&') + ')', 'gi');
            content = content.replace(regex, '<mark>$1</mark>');
        }

        const listItem = `
            <li>
                <div class="message-meta">
                    <span class="sender">${message.fromName}</span> 在会话
                    <span class="session">(ID: ${message.sessionId})</span> 中说:
                    <span class="timestamp float-right">${messageDate}</span>
                </div>
                <div class="message-content">
                    ${content}
                </div>
            </li>
        `;
        historyList.append(listItem);
    });
}
