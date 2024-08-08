document.addEventListener('DOMContentLoaded', function() {
    const chatContainer = document.getElementById('chatContainer');
    const chatInput = document.getElementById('message');
    const chatBox = document.getElementById('messages');

    // 채팅창 클릭 시 열기/닫기
    chatContainer.addEventListener('click', function() {
        chatContainer.classList.add('expanded');
        chatInput.focus();
        chatBox.classList.add('visible'); // 채팅 로그가 보이도록 설정
    });

    // 채팅 입력란에서 Enter 키 누르면 메시지 추가
    chatInput.addEventListener('keypress', function(event) {
        if (event.key === 'Enter' && !event.shiftKey) {
            event.preventDefault();
            sendMessage(); // 메시지를 보내는 함수 호출
            chatInput.value = '';
            chatBox.scrollTop = chatBox.scrollHeight; // 최신 메시지로 스크롤
        }
    });

    // 입력란 포커스 아웃 시 채팅창 닫기
    chatInput.addEventListener('blur', function() {
        setTimeout(() => {
            if (!chatInput.value.trim()) {
                chatContainer.classList.remove('expanded');
                chatBox.classList.remove('visible');
                // 채팅 로그를 바로 이전 메시지 1개만 보이도록 설정
                const messages = chatBox.querySelectorAll('div');
                if (messages.length > 1) {
                    for (let i = 0; i < messages.length - 1; i++) {
                        messages[i].style.display = 'none';
                    }
                }
                if (messages.length > 0) {
                    messages[messages.length - 1].style.display = 'block';
                }
            }
        }, 100);
    });

    // 채팅창 클릭 시 로그가 다시 보이도록 하기
    chatContainer.addEventListener('click', function() {
        chatBox.classList.add('visible');
        const messages = chatBox.querySelectorAll('div');
        messages.forEach(message => message.style.display = 'block'); // 모든 메시지 보이기
    });
});