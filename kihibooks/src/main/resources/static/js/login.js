document.getElementById('login-form').addEventListener('submit', async (event) => {
    event.preventDefault();

    const username = document.getElementById('email').value;
    const password = document.getElementById('ur_pw').value;
    const rememberMe = document.querySelector('input[name="remember-me"]').checked ? 'on' : 'off'; // remember-me 값 가져오기

    try {
        const response = await fetch('/login', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded'
            },
            body: new URLSearchParams({
                username: username,
                password: password,
                'remember-me': rememberMe // remember-me 값을 body에 추가
            }).toString()
        });

        if (!response.ok) {
            const data = await response.json();
            Swal.fire({
                title: '로그인 실패',
                text: data.message,
                icon: 'error',
                confirmButtonText: '확인',
                confirmButtonColor: '#ff7f50'
            });
        } else {
            window.location.href = "/";
        }
    } catch (error) {
        console.error('로그인 중 오류 발생:', error);
        Swal.fire({
            title: '오류 발생',
            text: '로그인 처리 중 오류가 발생했습니다.',
            icon: 'error',
            confirmButtonText: '확인',
            confirmButtonColor: '#ff7f50'
        });
    }
});
