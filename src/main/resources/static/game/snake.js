// 웹 소켓 관련 코드
var socket = new SockJS('/ws');
var stompClient = Stomp.over(socket);
stompClient.connect({}, function (frame) {
    console.log('Connected: ' + frame);

    stompClient.subscribe('/topic/messages', function (message) {
        var messageDTO = JSON.parse(message.body);
        document.getElementById('chatBox').innerHTML += '<div>' + messageDTO.username + " : " + messageDTO.content + '</div>';
        scrollToBottom();
    });
});

function scrollToBottom() {
    const container = document.getElementById('chatBox');
    container.scrollTop = container.scrollHeight;
}

function sendMessage() {
    var message = document.getElementById('chatInput').value;
    stompClient.send("/app/sendMessage", {}, message);
    scrollToBottom();
}

function sendInput(direction) {
    stompClient.send("/app/gameInput", {}, direction);
    scrollToBottom();
}

// game 화면
const canvas = document.getElementById('gameCanvas');
const ctx = canvas.getContext('2d');

// Fixed map size
const mapWidth = 5000; // Map width
const mapHeight = 5000; // Map height

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

function getRandomPosition() {
    return {
        x: Math.floor(Math.random() * (mapWidth / scale)) * scale,
        y: Math.floor(Math.random() * (mapHeight / scale)) * scale
    };
}

class Snake {
    constructor(id, snakeLength, snakeNodePlaces, isAlive, direction, grow) {
        this.id = id;
        this.snakeLength = snakeLength;
        this.snakeNodePlaces = snakeNodePlaces;
        this.isAlive = isAlive;
        this.direction = direction;
        this.grow = grow;
    }

    draw() {
        ctx.fillStyle = this.id === 1 ? 'green' : 'blue';
        if(this.isAlive) {
            for (let segment of this.snakeNodePlaces) {
                ctx.fillRect(segment.x - viewX, segment.y - viewY, scale, scale);
            }

            // 텍스트 색상 및 폰트 설정
            ctx.fillStyle = 'white';
            ctx.font = '12px Arial';
            ctx.textAlign = 'center';
            ctx.textBaseline = 'middle';

            // 머리 부분의 ID 표시
            let head = this.snakeNodePlaces[0];
            ctx.fillText(this.id, head.x - viewX + scale / 2, head.y - viewY - 5);
        }
    }
}

class Food {
    constructor(position) {
        this.position = position;
    }

    draw() {
        ctx.fillStyle = 'red';
        ctx.fillRect(this.position.x - viewX, this.position.y - viewY, scale, scale);
    }
}

function updateScore(score) {
    document.getElementById('score').textContent = `Score: ${score}`;
}

// 프레임 단순 출력
function drawGameFrame(gameFrameDTO) {
    ctx.clearRect(0, 0, canvas.width, canvas.height);
    let snakes = [];
    let foods = [];
    let score = gameFrameDTO.score;

    for (let snakeDTO of gameFrameDTO.snakes) {
        let snake = new Snake(
            snakeDTO.memberid,
            snakeDTO.snakeLength,
            snakeDTO.snakeNodePlaces,
            snakeDTO.isAlive,
            snakeDTO.direction,
            snakeDTO.grow
        );
        snakes.push(snake);

        // viewX, viewY 값 player지렁이 위치로
        if (snakeDTO.memberid === gameFrameDTO.playerId) {
            let head = snake.snakeNodePlaces[0];
            viewX = head.x - canvas.width / 2;
            viewY = head.y - canvas.height / 2;

            // Constrain viewport to map boundaries
            viewX = Math.max(0, Math.min(viewX, mapWidth - canvas.width));
            viewY = Math.max(0, Math.min(viewY, mapHeight - canvas.height));
        }
    }

    // Draw each snake
    for (let snake of snakes) {
        snake.draw();
    }

    // Update the score
    updateScore(score);

    // Draw boundaries for debugging
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

window.addEventListener('resize', resizeCanvas);
resizeCanvas();

// test
function fetchGameFrame() {
    return {
        playerId: 1,
        isAlive: true,
        score: 10,
        snakes: [
            {
                memberid: 1,
                snakeLength: 5,
                snakeNodePlaces: [
                    { x: 100, y: 100 },
                    { x: 120, y: 100 },
                    { x: 140, y: 100 },
                    { x: 160, y: 100 },
                    { x: 180, y: 100 }
                ],
                isAlive: true,
                direction: 'RIGHT',
                grow: false
            }
        ]
    };

    /*
        return fetch('/')
            .then(response =>response.json())
            .then(data => {
                drawGameFrame(data);
            })
            .catch(error => console.error('Error fetching game frame:' error));

     */
}

const gameFrameDTO = fetchGameFrame();
drawGameFrame(gameFrameDTO);

setInterval(() => {
    const gameFrameDTO = fetchGameFrame();
    drawGameFrame(gameFrameDTO);
}, 100);

/*
setInterval(() => {
    fetchGameFrame();
}, 100)

 */
