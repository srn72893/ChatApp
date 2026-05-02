let lastId = 0;
//メッセージ表示
function addMessage(m) {
    const div = document.getElementById('messages');

    const currentUser = document.getElementById('username').value;
    const isSelf = m.username === currentUser;

    div.innerHTML += `
        <div class="message ${isSelf ? 'self' : 'other'}">
            <div class="bubble">
                <strong>${m.username}</strong><br>
                ${m.content}
            </div>
        </div>
    `;

    div.scrollTop = div.scrollHeight;
}

//初期ロード 全件取得
async function loadMessages() {
    const res = await fetch('/messages');
    const data = await res.json();

    const div = document.getElementById('messages');
    div.innerHTML = '';

    data.forEach(m => {
        addMessage(m);
        lastId = m.id;  //最後の ID 更新
    });
}

//送信
async function sendMessage() {
    const username = document.getElementById('username').value.trim();
    const content = document.getElementById('content').value.trim();

    if (!username || !content) return;

    await fetch('/messages', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded'
        },
        body: `username=${encodeURIComponent(username)}&content=${encodeURIComponent(content)}`
    });

    document.getElementById('content').value = '';

    await loadMessages();
}

//差分取得
async function loadNewMessages() {
    const res = await fetch(`/messages/new?lastId=${lastId}`);
    const data = await res.json();

    data.forEach(m => {
        addMessage(m);
        lastId = m.id;
    });
}

//イベント登録
document.getElementById('sendBtn').addEventListener('click', sendMessage);
document.getElementById('refreshBtn').addEventListener('click', loadMessages);

//初期実行
loadMessages();

//定期更新（3 秒ごと）  重いので本番のみ採用
// setInterval(loadNewMessages, 3000);
