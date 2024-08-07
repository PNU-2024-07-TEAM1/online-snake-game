// 웹 소켓 관련 코드
var socket = new SockJS('/ws');
var stompClient = Stomp.over(socket);

let globalGameFrameDTO;
stompClient.connect({}, function (frame) {
    console.log('Connected: ' + frame);
    stompClient.subscribe('/topic/messages', function (message) {
        var messageDTO = JSON.parse(message.body);
        document.getElementById('messages').innerHTML += '<div>' + messageDTO.username + " : " + messageDTO.content + '</div>';
        scrollToBottom();
    });
    stompClient.subscribe('/topic/gameFrame', function (gameFrameDTO) {
        console.log(gameFrameDTO.body);
        drawGameFrame(JSON.parse(gameFrameDTO.body));
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
                ctx.fillRect(segment.x * scale - viewX, segment.y * scale - viewY, scale, scale);
            }
            // 텍스트 색상 및 폰트 설정
            ctx.fillStyle = 'white';
            ctx.font = '12px Arial';
            ctx.textAlign = 'center';
            ctx.textBaseline = 'middle';
            // 머리 부분의 ID 표시
            let head = this.snakeNodePlaces[0];
            ctx.fillText(this.id, head.x * scale - viewX + scale / 2, head.y * scale - viewY - 5);
        }
    }

    update() {
        // Update the position based on the current direction
        /*for(let node of this.snakeNodePlaces)
        {
            switch (this.direction) {
                case 'left':
                    node.x -= 0.5;
                    break;
                case 'right':
                    node.x += 0.5;
                    break;
                case 'up':
                    node.y -= 0.5;
                    break;
                case 'down':
                    node.y += 0.5;
                    break;
            }
        }
        */
        // Update the position based on the current direction
        // First, create a copy of the current head to be updated
        let head = { ...this.snakeNodePlaces[0]};

        switch (this.direction) {
            case 'left':
                head.x -= 0.5;
                break;
            case 'right':
                head.x += 0.5;
                break;
            case 'up':
                head.y -= 0.5;
                break;
            case 'down':
                head.y += 0.5;
                break;
        }

        if (this.snakeNodePlaces.length > 1) {
            let lastSegment = this.snakeNodePlaces[this.snakeNodePlaces.length - 1];
            let secondLastSegment = this.snakeNodePlaces[this.snakeNodePlaces.length - 2];

            // Calculate the midpoint
            let midX = (lastSegment.x + secondLastSegment.x) / 2;
            let midY = (lastSegment.y + secondLastSegment.y) / 2;

            // Update the last segment to the midpoint
            this.snakeNodePlaces[this.snakeNodePlaces.length - 1] = { x: midX, y: midY };
        }

        this.snakeNodePlaces.unshift(head);
    }
}
// 프레임 단순 출력
async function drawGameFrame(gameFrameDTO) {
    ctx.clearRect(0, 0, canvas.width, canvas.height);
    globalGameFrameDTO = gameFrameDTO;
    var alive = false;
    let snakes = [];
    for (let snakeDTO of gameFrameDTO.snakes) {
        let snake = new Snake(
            snakeDTO.memberId,
            snakeDTO.snakeLength,
            snakeDTO.snakeNodePlaces,
            snakeDTO.alive,
            snakeDTO.direction,
            snakeDTO.grow
        );
        snakes.push(snake);

        // viewX, viewY 값 player지렁이 위치로
        if (snakeDTO.memberId === memberId) {
            alive = true;

            let head = snake.snakeNodePlaces[0];
            viewX = head.x * scale - canvas.width / 2;
            viewY = head.y * scale - canvas.height / 2;

            // Constrain viewport to map boundaries
            viewX = Math.max(0, Math.min(viewX, mapWidth - canvas.width));
            viewY = Math.max(0, Math.min(viewY, mapHeight - canvas.height));

            // Update the score
            updateScore(snake.snakeLength - 3);
        }
    }

    // Draw each snake
    for (let snake of snakes) {
        snake.draw();
    }

    for (let experience of gameFrameDTO.experiences) {
        experience.x
        ctx.fillRect(experience.position.x * scale - viewX, experience.position.y * scale - viewY, scale * 0.7, scale * 0.7);
    }

    if (!alive) {
        await sleep(1000);
        window.location.href = '/main';
    }



    // Draw boundaries for debugging
    ctx.strokeStyle = 'blue';
    ctx.lineWidth = 2;
    ctx.strokeRect(0, 0, canvas.width, canvas.height);
}

// local update 출력
function drawGameFrame2() {
    ctx.clearRect(0, 0, canvas.width, canvas.height);
    var alive = false;
    let snakes = [];
    for (let snakeDTO of globalGameFrameDTO.snakes) {
        let snake = new Snake(
            snakeDTO.memberId,
            snakeDTO.snakeLength,
            snakeDTO.snakeNodePlaces,
            snakeDTO.alive,
            snakeDTO.direction,
            snakeDTO.grow
        );
        snake.update();
        snakes.push(snake);

        // viewX, viewY 값 player지렁이 위치로
        if (snakeDTO.memberId === memberId) {
            alive = true;

            let head = snake.snakeNodePlaces[0];
            viewX = head.x * scale - canvas.width / 2;
            viewY = head.y * scale - canvas.height / 2;

            // Constrain viewport to map boundaries
            viewX = Math.max(0, Math.min(viewX, mapWidth - canvas.width));
            viewY = Math.max(0, Math.min(viewY, mapHeight - canvas.height));
        }
    }

    // Draw each snake
    for (let snake of snakes) {
        snake.draw();
    }

    for (let experience of globalGameFrameDTO.experiences) {
        experience.x
        ctx.fillRect(experience.position.x * scale - viewX, experience.position.y * scale - viewY, scale * 0.7, scale * 0.7);
    }

    // Update the score
    // updateScore(score);

    // Draw boundaries for debugging
    ctx.strokeStyle = 'blue';
    ctx.lineWidth = 2;
    ctx.strokeRect(0, 0, canvas.width, canvas.height);
}

// local update 출력
function drawGameFrame2() {
    ctx.clearRect(0, 0, canvas.width, canvas.height);
    var alive = false;
    let snakes = [];
    for (let snakeDTO of globalGameFrameDTO.snakes) {
        let snake = new Snake(
            snakeDTO.memberId,
            snakeDTO.snakeLength,
            snakeDTO.snakeNodePlaces,
            snakeDTO.alive,
            snakeDTO.direction,
            snakeDTO.grow
        );
        snake.update();
        snakes.push(snake);

        // viewX, viewY 값 player지렁이 위치로
        if (snakeDTO.memberId === memberId) {
            alive = true;

            let head = snake.snakeNodePlaces[0];
            viewX = head.x * scale - canvas.width / 2;
            viewY = head.y * scale - canvas.height / 2;

            // Constrain viewport to map boundaries
            viewX = Math.max(0, Math.min(viewX, mapWidth - canvas.width));
            viewY = Math.max(0, Math.min(viewY, mapHeight - canvas.height));
        }
    }

    // Draw each snake
    for (let snake of snakes) {
        snake.draw();
    }

    for (let experience of globalGameFrameDTO.experiences) {
        experience.x
        ctx.fillRect(experience.position.x * scale - viewX, experience.position.y * scale - viewY, scale * 0.7, scale * 0.7);
    }

    // Update the score
    // updateScore(score);

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


function updateScore(score) {
    document.getElementById('score').textContent = `Score: ${score}`;
}

function sleep(ms) {
    return new Promise(resolve => setTimeout(resolve, ms));
}

window.addEventListener('resize', resizeCanvas);
resizeCanvas();
// test
function fetchGameFrame() {
    return {
        score:0,
        snakes:[
            {
                memberId:1,
                snakeLength:3,
                snakeNodePlaces:[
                    {x:4693,y:1320},
                    {x:4694,y:1320},
                    {x:4695,y:1320}
                ],
                direction:"left",
                grow:false,
                alive:true
            }
        ]
    }
}
// const gameFrameDTO = fetchGameFrame();
// drawGameFrame(gameFrameDTO);

let toggle = 1;

setInterval(() => {
    if (toggle)
    {
        stompClient.send("/app/gameOutput", {}, );
        toggle = 0;
    }
    else{
        drawGameFrame2();
        toggle = 1;
    }
}, 125);