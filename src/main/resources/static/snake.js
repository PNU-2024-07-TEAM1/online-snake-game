const canvas = document.getElementById('gameCanvas');
const ctx = canvas.getContext('2d');

// Fixed map size
const mapWidth = 5000; // Map width
const mapHeight = 5000; // Map height

// Game settings
const scale = 20; // Size of each segment
const initialSnakeLength = 5;
const speed = 100; // Speed of the game loop in ms

let snakes = [];
let foods = [];
let score = 0;
let directions = {}; // Object to hold directions for each snake
let viewX = 0; // Viewport X offset
let viewY = 0; // Viewport Y offset

function resizeCanvas() {
    const gameContainer = document.querySelector('.game-container');
    canvas.width = gameContainer.clientWidth;
    canvas.height = gameContainer.clientHeight;
    // Center view to initial snake position
    viewX = (mapWidth / 2) - (canvas.width / 2);
    viewY = (mapHeight / 2) - (canvas.height / 2);
}

function getRandomPosition() {
    return {
        x: Math.floor(Math.random() * (mapWidth / scale)) * scale,
        y: Math.floor(Math.random() * (mapHeight / scale)) * scale
    };
}

class Snake {
    constructor(id) {
        this.id = id;
        this.body = [];
        for (let i = initialSnakeLength - 1; i >= 0; i--) {
            this.body.push({ x: Math.floor(mapWidth / 2 + i * scale), y: Math.floor(mapHeight / 2) });
        }
        this.grow = false;
        this.direction = 'RIGHT';
        directions[id] = this.direction;
    }

    draw() {
        ctx.fillStyle = this.id === 1 ? 'green' : 'blue';
        for (let segment of this.body) {
            ctx.fillRect(segment.x - viewX, segment.y - viewY, scale, scale);
        }
    }

    update() {
        let head = { ...this.body[0] };
        switch (this.direction) {
            case 'LEFT':
                head.x -= scale;
                break;
            case 'RIGHT':
                head.x += scale;
                break;
            case 'UP':
                head.y -= scale;
                break;
            case 'DOWN':
                head.y += scale;
                break;
        }

        // Handle wall collision
        if (head.x < 0) head.x = mapWidth - scale;
        if (head.x >= mapWidth) head.x = 0;
        if (head.y < 0) head.y = mapHeight - scale;
        if (head.y >= mapHeight) head.y = 0;

        this.body.unshift(head);

        if (!this.grow) {
            this.body.pop();
        } else {
            this.grow = false;
        }

        // Update viewport position
        viewX = head.x - canvas.width / 2;
        viewY = head.y - canvas.height / 2;

        // Constrain viewport to map boundaries
        viewX = Math.max(0, Math.min(viewX, mapWidth - canvas.width));
        viewY = Math.max(0, Math.min(viewY, mapHeight - canvas.height));
    }

    setDirection(newDir) {
        if (newDir === 'LEFT' && this.direction !== 'RIGHT') {
            this.direction = newDir;
        } else if (newDir === 'RIGHT' && this.direction !== 'LEFT') {
            this.direction = newDir;
        } else if (newDir === 'UP' && this.direction !== 'DOWN') {
            this.direction = newDir;
        } else if (newDir === 'DOWN' && this.direction !== 'UP') {
            this.direction = newDir;
        }
    }

    collision(otherSnakes) {
        let head = this.body[0];
        // Check collision with own body (ignore self-collision)
        for (let i = 1; i < this.body.length; i++) {
            if (this.body[i].x === head.x && this.body[i].y === head.y) {
                return false; // Return false to indicate no death
            }
        }
        // Check collision with other snakes
        for (let snake of otherSnakes) {
            if (snake.id === this.id) continue;
            for (let segment of snake.body) {
                if (segment.x === head.x && segment.y === head.y) {
                    return true; // Return true to indicate death
                }
            }
        }
        return false;
    }

    eat() {
        this.grow = true;
    }
}

class Food {
    constructor() {
        this.position = getRandomPosition();
    }

    draw() {
        ctx.fillStyle = 'red';
        ctx.fillRect(this.position.x - viewX, this.position.y - viewY, scale, scale);
    }

    randomize() {
        this.position = getRandomPosition();
    }
}

function updateScore() {
    document.getElementById('score').textContent = `Score: ${score}`;
}

function gameLoop() {
    ctx.clearRect(0, 0, canvas.width, canvas.height);

    // Update and draw each snake
    for (let snake of snakes) {
        snake.update();
        snake.draw();
    }

    // Draw each food item
    for (let food of foods) {
        food.draw();
    }

    // Check for collisions and handle game logic
    for (let snake of snakes) {
        if (snake.collision(snakes)) {
            alert(`Snake ${snake.id} is dead!`);
            // Reset snake's position
            snake.body = [];
            for (let i = initialSnakeLength - 1; i >= 0; i--) {
                snake.body.push({ x: Math.floor(mapWidth / 2 + i * scale), y: Math.floor(mapHeight / 2) });
            }
            snake.grow = false;
        }

        for (let food of foods) {
            // Check for collision with food
            if (snake.body[0].x === food.position.x &&
                snake.body[0].y === food.position.y) {
                snake.eat();
                food.randomize();
                score++;
                updateScore();
            }
        }
    }

    // Draw boundaries for debugging
    ctx.strokeStyle = 'blue';
    ctx.lineWidth = 2;
    ctx.strokeRect(0, 0, canvas.width, canvas.height);
}

function setSnakeDirection(id, newDir) {
    let snake = snakes.find(snake => snake.id === id);
    if (snake) {
        snake.setDirection(newDir);
    }
}

document.addEventListener('keydown', (event) => {
    const key = event.key;
    if (key === 'ArrowLeft') {
        setSnakeDirection(1, 'LEFT');
    } else if (key === 'ArrowRight') {
        setSnakeDirection(1, 'RIGHT');
    } else if (key === 'ArrowUp') {
        setSnakeDirection(1, 'UP');
    } else if (key === 'ArrowDown') {
        setSnakeDirection(1, 'DOWN');
    }
});

window.addEventListener('resize', resizeCanvas);
resizeCanvas();

// Initialize snakes
snakes.push(new Snake(1));

// Initialize 100 food items
for (let i = 0; i < 100; i++) {
    foods.push(new Food());
}

updateScore();
setInterval(gameLoop, speed);
