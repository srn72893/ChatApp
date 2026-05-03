async function login() {
    const username = document.getElementById('username').value;

    await fetch('/login', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded'
        },
        body: `username=${encodeURIComponent(username)}`
    });

    location.href = '/';
}
