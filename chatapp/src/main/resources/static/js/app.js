let lastId = 0;
let currentUser = "";
//自分を取得
async function loadCurrentUser() {
    const res = await fetch('/me');
    currentUser = await res.text();
}

//アイコン
function getInitial(name) {
    return name ? name.charAt(0) : "?";
}

//メッセージ表示
function addMessage(m) {
    const div = document.getElementById('messages');
    const isSelf = m.username === currentUser;

    div.insertAdjacentHTML("beforeend", `
        <div class="message ${isSelf ? 'self' : 'other'}">
            ${isSelf
                ? `
                    <div class="bubble">${m.content}</div>
                    <div class="avatar">${getInitial(m.username)}</div>
                `
                : `
                    <div class="avatar">${getInitial(m.username)}</div>
                    <div class="bubble">${m.content}</div>
                `
            }
        </div>
        `);
    requestAnimationFrame(() => {
        div.scrollTop = div.scrollHeight;
    });
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
    const content = document.getElementById('content').value.trim();

    if (!content) return;

    await fetch('/messages', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded'
        },
        body: `content=${encodeURIComponent(content)}`
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

//初期化
async function init() {
    await loadCurrentUser();
    await loadMessages();
}

//イベント登録
document.getElementById('sendBtn').addEventListener('click', sendMessage);
document.getElementById('refreshBtn').addEventListener('click', loadMessages);
//初期化
init();

//定期更新（3 秒ごと）  重いので本番のみ採用
// setInterval(loadNewMessages, 3000);
