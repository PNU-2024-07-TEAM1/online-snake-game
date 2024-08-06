const numPoints = 120;
const radius = 150;
const sphereElement = document.querySelector('.scene');

function createPoint(x, y, z, index) {
    const point = document.createElement('i');
    point.className = 'point';
    point.style.transform = `translate3d(${x}px, ${y}px, ${z}px)`;
    point.style.setProperty('--i', index);
    point.style.setProperty('--x', x + 'px');
    point.style.setProperty('--y', y + 'px');
    point.style.setProperty('--z', z + 'px');
    // Set animation color based on the index
    if (index % 2 === 0) {
        point.style.setProperty('--animation-color', 'color 4s linear infinite alternate');
    } else {
        point.style.setProperty('--animation-color', 'color2 4s linear infinite alternate');
    }
    return point;
}

function sphere(samples, radius) {
    const points = [];
    const phi = Math.PI * (3 - Math.sqrt(5));

    for (let i = 0; i < samples; i++) {
        const y = 1 - (i / (samples - 1)) * 2;
        const radiusAtY = Math.sqrt(1 - y * y);

        const theta = phi * i;

        const x = Math.cos(theta) * radiusAtY;
        const z = Math.sin(theta) * radiusAtY;

        points.push({ x: x * radius, y: y * radius, z: z * radius, index: i });
    }
    return points;
}

const points = sphere(numPoints, radius);
points.forEach(point => {
    const elem = createPoint(point.x, point.y, point.z, point.index);
    sphereElement.appendChild(elem);
});
