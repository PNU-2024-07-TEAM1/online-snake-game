// 웹 소켓 관련 코드
var socket = new SockJS('/ws');
var stompClient = Stomp.over(socket);

let globalGameFrameDTO;
let snakes = [];
let experiences = [];

let ranking = [];

stompClient.connect({}, function (frame) {
    //console.log('Connected: ' + frame);
    stompClient.subscribe('/topic/messages', function (message) {
        var messageDTO = JSON.parse(message.body);
        // 색상을 적용한 username과 메시지 내용을 HTML로 생성
        var messageHtml = '<div><span style="color: ' + messageDTO.color + ';">' + messageDTO.username + '</span> : ' + messageDTO.content + '</div>';
        document.getElementById('messages').innerHTML += messageHtml;
        scrollToBottom();
    });

    var message =  "/in";
    stompClient.send("/app/sendMessage", {}, message);

    stompClient.subscribe('/topic/gameFrame', function (gameFrameDTO) {
        drawGameFrame(JSON.parse(gameFrameDTO.body));
        requestAnimationFrame(gameLoop);
    });
});

function scrollToBottom() {
    const container = document.getElementById('messages');
    container.scrollTop = container.scrollHeight;
}

function sendMessage() {
    var message = document.getElementById('message').value;
    stompClient.send("/app/sendMessage", {}, message);
    scrollToBottom();
}

function sendInput(direction) {
    stompClient.send("/app/gameInput", {}, direction);
}

// game 화면
const canvas = document.getElementById('gameCanvas');
const ctx = canvas.getContext('2d');

// Fixed map size
const mapWidth = 2000; // Map width
const mapHeight = 2000; // Map height
// Game settings
const scale = 20; // Size of each segment
const speed = 100; // Speed of the game loop in ms

let viewX = 0; // Viewport X offset
let viewY = 0; // Viewport Y offset

function resizeCanvas() {
    const gameContainer = document.querySelector('.game-container');
    canvas.width = gameContainer.clientWidth;
    canvas.height = gameContainer.clientHeight;
}

class Snake {
    constructor(id, snakeLength, snakeNodePlaces, isAlive, direction, grow, username, color) {
        this.id = id;
        this.snakeLength = snakeLength;
        this.snakeNodePlaces = snakeNodePlaces;
        this.isAlive = isAlive;
        this.direction = direction;
        this.grow = grow;
        this.username = username;
        this.color = color;

        this.delX = 0;
        this.delY = 0;
    }

    draw() {
        ctx.fillStyle = this.color ? this.color : 'blue';
        if (this.isAlive) {
            for (let segment of this.snakeNodePlaces) {
                this.drawSegment(segment.x * scale - viewX, segment.y * scale - viewY, scale / 2);
            }
            ctx.font = 'bold 14px Arial'; // 글씨체와 크기 조정
            ctx.textAlign = 'center';
            ctx.textBaseline = 'middle';

            // 그림자 효과 추가
            ctx.shadowColor = 'rgba(0, 0, 0, 0.7)';
            ctx.shadowBlur = 4;
            ctx.shadowOffsetX = 2;
            ctx.shadowOffsetY = 2;

            // 텍스트 색상 및 효과
            ctx.fillStyle = 'white'; // 텍스트 색상
            ctx.strokeStyle = 'rgba(0, 0, 0, 0.7)'; // 텍스트 외곽선 색상
            ctx.lineWidth = 1.5; // 외곽선 두께

            let head = this.snakeNodePlaces[0];
            ctx.strokeText(this.username, head.x * scale - viewX + scale / 2, head.y * scale - viewY - 5);
            ctx.fillText(this.username, head.x * scale - viewX + scale / 2, head.y * scale - viewY - 5);

            // 그림자 효과를 원래 상태로 복원
            ctx.shadowColor = 'transparent';
            ctx.shadowBlur = 0;
            ctx.shadowOffsetX = 0;
            ctx.shadowOffsetY = 0;
        }
    }

    drawSegment(x, y, radius) {
        const largerRadius = radius * 1.3; // 원의 크기를 1.3배로 확대
        const shadowOffset = largerRadius * 0.4; // 그림자의 위치를 조정

        // 그림자 그리기
        ctx.fillStyle = 'rgba(0, 0, 0, 0.3)'; // 그림자 색상 및 불투명도
        ctx.beginPath();
        ctx.arc(x + shadowOffset, y + shadowOffset, largerRadius * 0.8, 0, 2 * Math.PI); // 그림자 위치 및 크기
        ctx.fill();

        // 원의 그라디언트 그리기
        let gradient = ctx.createRadialGradient(x, y, largerRadius * 0.3, x, y, largerRadius);
        gradient.addColorStop(0, this.color ? this.color : 'deepskyblue'); // 중앙 색상: 강렬하게 설정
        gradient.addColorStop(0.6, 'rgba(0, 0, 0, 0.4)'); // 중앙 색상에서 가장자리로 갈수록 색상 어두워짐
        gradient.addColorStop(1, 'rgba(0, 0, 0, 0.6)'); // 가장자리 색상: 깊이감 있는 어두운 색상

        ctx.beginPath();
        ctx.arc(x, y, largerRadius, 0, 2 * Math.PI); // 원 크기 조정
        ctx.fillStyle = gradient;
        ctx.fill();

        // 별도의 광채 효과 (optional)
        const haloRadius = largerRadius * 1.1; // 광채 효과를 위한 반지름
        let haloGradient = ctx.createRadialGradient(x, y, largerRadius, x, y, haloRadius);
        haloGradient.addColorStop(0, 'rgba(255, 255, 255, 0.6)'); // 광채 중앙 색상
        haloGradient.addColorStop(1, 'rgba(255, 255, 255, 0)'); // 광채 가장자리 색상

        ctx.beginPath();
        ctx.arc(x, y, haloRadius, 0, 2 * Math.PI); // 광채 효과 적용
        ctx.fillStyle = haloGradient;
        ctx.fill();
    }

    update() {
        let newHead = { ...this.snakeNodePlaces[0] };

        switch (this.direction) {
            case 'left':
                newHead.x -= 0.1;
                break;
            case 'right':
                newHead.x += 0.1;
                break;
            case 'up':
                newHead.y -= 0.1;
                break;
            case 'down':
                newHead.y += 0.1;
                break;
        }

        this.snakeNodePlaces.unshift(newHead);

        if (this.snakeNodePlaces.length > this.snakeLength + 1) {
            this.snakeNodePlaces.splice(1, 1);
        }

        if (this.snakeNodePlaces.length > 1) {
            let lastSegment = this.snakeNodePlaces[this.snakeNodePlaces.length - 1];
            let secondLastSegment = this.snakeNodePlaces[this.snakeNodePlaces.length - 2];

            if (this.delX === 0 && this.delY === 0) {
                this.delX = (secondLastSegment.x - lastSegment.x) / 10;
                this.delY = (secondLastSegment.y - lastSegment.y) / 10;
            }

            this.snakeNodePlaces[this.snakeNodePlaces.length - 1] = { x: lastSegment.x + this.delX, y: lastSegment.y + this.delY };
        }
    }
}

// 프레임 단순 출력
async function drawGameFrame(gameFrameDTO) {
    ctx.clearRect(0, 0, canvas.width, canvas.height);
    globalGameFrameDTO = gameFrameDTO;
    var alive = false;
    snakes = [];
    ranking = [];
    for (let snakeDTO of gameFrameDTO.snakes) {
        let snake = new Snake(
            snakeDTO.memberId,
            snakeDTO.snakeLength,
            snakeDTO.snakeNodePlaces,
            snakeDTO.alive,
            snakeDTO.direction,
            snakeDTO.grow,
            snakeDTO.username,
            snakeDTO.color
        );
        snakes.push(snake);

        if (snakeDTO.memberId === memberId) {
            alive = true;

            let head = snake.snakeNodePlaces[0];
            viewX = head.x * scale - canvas.width / 2;
            viewY = head.y * scale - canvas.height / 2;

            viewX = Math.max(0, Math.min(viewX, mapWidth - canvas.width));
            viewY = Math.max(0, Math.min(viewY, mapHeight - canvas.height));



            updateScore(snake.snakeLength - 3);
        }
        ranking.push({
            name:snakeDTO.username,
            score:(snake.snakeLength-3)
        })
    }

    for (let snake of snakes) {
        snake.draw();
    }

    experiences = gameFrameDTO.experiences;
    for (let experience of experiences) {
        drawExperience(experience.position.x * scale - viewX, experience.position.y * scale - viewY, scale * 0.35);
    }

    if (!alive) {
        socket.close();

        // 1초 후에 새로운 페이지로 이동합니다.
        await sleep(1000);
        window.location.href = '/main';
    }
    updateRanking()
}

function drawExperience(x, y, radius) {
    const outerRadius = radius * 1.2; // 외부 원 반지름
    const innerRadius = radius * 0.8; // 내부 원 반지름

    // 그림자 그리기
    ctx.fillStyle = 'rgba(0, 0, 0, 0.4)'; // 그림자 색상 및 불투명도
    ctx.beginPath();
    ctx.arc(x + radius * 0.2, y + radius * 0.2, outerRadius * 0.7, 0, 2 * Math.PI); // 그림자 위치 및 크기
    ctx.fill();

    // 원의 그라디언트 그리기
    let gradient = ctx.createRadialGradient(x, y, innerRadius, x, y, outerRadius);
    gradient.addColorStop(0, 'limegreen'); // 중앙 색상: 강렬한 색상
    gradient.addColorStop(0.6, 'mediumseagreen'); // 중앙 색상에서 가장자리로 갈수록 색상 변화
    gradient.addColorStop(1, 'darkgreen'); // 가장자리 색상: 깊이감 있는 색상

    ctx.beginPath();
    ctx.arc(x, y, outerRadius, 0, 2 * Math.PI); // 원 크기 조정
    ctx.fillStyle = gradient;
    ctx.fill();

    // 별도의 광채 효과 추가
    const haloRadius = outerRadius * 1.3; // 광채 효과를 위한 반지름
    let haloGradient = ctx.createRadialGradient(x, y, outerRadius * 0.8, x, y, haloRadius);
    haloGradient.addColorStop(0, 'rgba(255, 255, 255, 0.6)'); // 광채 중앙 색상
    haloGradient.addColorStop(1, 'rgba(255, 255, 255, 0)'); // 광채 가장자리 색상

    ctx.beginPath();
    ctx.arc(x, y, haloRadius, 0, 2 * Math.PI); // 광채 효과 적용
    ctx.fillStyle = haloGradient;
    ctx.fill();

    // 추가적인 하이라이트 효과
    ctx.beginPath();
    ctx.arc(x - radius * 0.2, y - radius * 0.2, radius * 0.5, 0, 2 * Math.PI);
    ctx.fillStyle = 'rgba(255, 255, 255, 0.3)'; // 하이라이트 색상
    ctx.fill();
}

function updateRanking(){
    ranking.sort((a, b) => b.score - a.score);
    // HTML 테이블의 tbody 요소를 가져옴

    const tbody = document.getElementById('leaderboardBody');
    const rows = tbody.getElementsByTagName('tr');

    // 데이터 배열을 순회하면서 테이블의 각 행을 추가
    ranking.forEach((member, index) => {
        if (index >= 5)
            return;
        // name 셀의 span 요소 가져오기
        const nameCell = rows[index].querySelector('td.name');
        const nameSpan = nameCell.querySelector('span');
        // points 셀의 텍스트 가져오기
        const pointsCell = rows[index].querySelector('td.points');

        // 텍스트 업데이트
        if (nameSpan) {
            nameSpan.textContent = member.name;
        }
        pointsCell.textContent = member.score;
    });
}

// local update 출력
function drawGameFrame_local() {
    ctx.clearRect(0, 0, canvas.width, canvas.height);
    var alive = false;
    for (let snake of snakes) {
        snake.update();

        if (snake.id === memberId) {
            alive = true;

            let head = snake.snakeNodePlaces[0];
            viewX = head.x * scale - canvas.width / 2;
            viewY = head.y * scale - canvas.height / 2;

            viewX = Math.max(0, Math.min(viewX, mapWidth - canvas.width));
            viewY = Math.max(0, Math.min(viewY, mapHeight - canvas.height));
        }
    }

    for (let snake of snakes) {
        snake.draw();
    }

    for (let experience of experiences) {
        drawExperience(experience.position.x * scale - viewX, experience.position.y * scale - viewY, scale * 0.35);
    }

    ctx.strokeStyle = 'blue';
    ctx.lineWidth = 2;
    ctx.strokeRect(0, 0, canvas.width, canvas.height);
}

document.addEventListener('keydown', (event) => {
    const key = event.key;
    if (key === 'ArrowLeft') {
        sendInput('left');
    } else if (key === 'ArrowRight') {
        sendInput('right');
    } else if (key === 'ArrowUp') {
        sendInput('up');
    } else if (key === 'ArrowDown') {
        sendInput('down');
    }
});

function updateScore(score) {
    document.getElementById('score').textContent = `Score: ${score}`;
}

function sleep(ms) {
    return new Promise(resolve => setTimeout(resolve, ms));
}

window.addEventListener('resize', resizeCanvas);
resizeCanvas();

let toggle = 9;

let lastUpdateTime = 0;
const updateInterval = 25; // 애니메이션 업데이트 주기 (ms)
const serverUpdateInterval = 250; // 서버와의 데이터 동기화 주기 (ms)

function gameLoop(timestamp) {
    if (timestamp - lastUpdateTime >= updateInterval) {
        lastUpdateTime = timestamp;
        if (toggle < 10) {
            drawGameFrame_local(); // Local update
            toggle += 1;
        } else {
            toggle = 1;
            return;
        }
    }

    requestAnimationFrame(gameLoop); // 다음 프레임을 요청
}

setInterval(() => {
    stompClient.send("/app/gameOutput", {}); // 서버로 데이터 전송
}, serverUpdateInterval);

requestAnimationFrame(gameLoop); // 게임 루프 시작