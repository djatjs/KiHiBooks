document.getElementById('login-form').addEventListener('submit', async (event) => {
    event.preventDefault();

    const username = document.getElementById('email').value;
    const password = document.getElementById('ur_pw').value;

    try {
        const response = await fetch('/login', { // 서버의 로그인 API 엔드포인트
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded'
            },
            body: new URLSearchParams({ username: username, password: password }).toString()
        });

        if (!response.ok) { // 응답 상태가 2xx가 아닌지 확인
            const data = await response.json(); // CustomLoginFailureHandler에서 온 JSON 응답을 파싱
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