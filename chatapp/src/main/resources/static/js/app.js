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
            ${isSelf ? `<span class="read-status">${m.read ? "既読" : ""}</span>` : ""}
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

//過去ログ取得
async function loadMessages() {
    const res = await fetch('/messages');
    const data = await res.json();

    const div = document.getElementById('messages');
    div.innerHTML = '';

    data.forEach(addMessage);
}

//既読状態取得
function updateReadStatus() {
    document.querySelectorAll('.message.self .read-status')
        .forEach(el => el.textContent = "既読");
}

//既読化
async function markAsRead() {
    //スマホバックグラウンド判定厳しいのでここでチェック
    const visible =
        document.visibilityState === "visible" &&
        document.hasFocus();

    if (!visible) return;

    await fetch('/messages/read', {
        method: 'POST'
    });
}

//WebSocket
let stompClient = null;

//接続
function connectWebSocket() {
    const socket = new SockJS('/chat');
    stompClient = Stomp.over(socket);

    stompClient.connect({}, async function() {
        stompClient.subscribe('/topic/messages', async function(msg) {
            const data = JSON.parse(msg.body);
            addMessage(data);

            if (data.username !== currentUser) {
                await markAsRead();
            }
        });
        //既読通知
        stompClient.subscribe('/topic/read', function() {
            updateReadStatus();
        });

    });
}

//送信
function sendMessage() {
    const input = document.getElementById('content');
    const content = input.value.trim();

    if (!content) return;

    stompClient.send("/app/chat", {}, JSON.stringify({
        username: currentUser,
        content: content
    }));

    input.value = "";
}

//初期化
async function init() {
    await loadCurrentUser();
    await loadMessages();
    connectWebSocket();
}

//イベント登録
document.getElementById('sendBtn').addEventListener('click', sendMessage);

//バックグラウンドで既読ついてしまう対策
document.addEventListener("visibilitychange", async () => {
    if (document.visibilityState === "visible") {
        await loadMessages();
        await markAsRead();
    }
});

//初期化
init();
