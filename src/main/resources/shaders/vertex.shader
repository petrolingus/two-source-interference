#version 330

layout (location=0) in vec3 position;

uniform mat4 viewMatrix;
uniform mat4 projectionMatrix;

uniform float iGlobalTime;
uniform float timeMul;
uniform float amplitude;
uniform float wavelength;
uniform float cyclicFrequency;
uniform float initialPhase;
uniform float between;

out vec2 pixelPos;
out float pixelHeight;

float valueMapper(float value, float min, float max)
{
    return ((value - min) / (max - min));
}

void main()
{
    float k = (2 * 3.141592653589793) / wavelength;
    float pos = sqrt(between * between / 2);
    float t = iGlobalTime * timeMul;
    vec2 vertexPos = 800 * position.xz;

    float r1 = distance(vertexPos, vec2(pos, -pos));
    float ampl1 = amplitude / r1;
    float value1 = ampl1 * sin(cyclicFrequency * t - k * r1 + initialPhase);

    float r2 = distance(vertexPos, vec2(-pos, pos));
    float ampl2 = amplitude / r2;
    float value2 = ampl2 * sin(cyclicFrequency * t - k * r2 + initialPhase);

    float amplSum = ampl1 + ampl2;
//    float value = valueMapper(value1 + value2, -amplSum, amplSum);
    float value = clamp(value1 + value2, -0.5, 1) * 0;

    int indexX = int(800 * (position.x + 1.0) / 2.0);
//    int indexY = int((position.z + 1.0) / 2.0);
//    int index = 800 * indexY + indexX;
//    float deltaH = heightArray[indexX];

//    float deltaH = temp.x;
//    float yValue = clamp(position.y, -0.5, 1) + value;
    float yValue = clamp(position.y + value, -1, 1);

	vec4 mvPos = viewMatrix * vec4(position.x, yValue, position.z, 1.0);
    gl_Position = projectionMatrix * mvPos;
    pixelPos = 800 * position.xz;
    pixelHeight = position.y;
}
