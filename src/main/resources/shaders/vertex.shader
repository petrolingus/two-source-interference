#version 330

layout (location=0) in vec3 position;

uniform mat4 modelViewMatrix;
uniform mat4 projectionMatrix;
uniform float iGlobalTime;
uniform float between;

out float height;

float valueMapper(float value, float min, float max)
{
    return ((value - min) / (max - min));
}

void main()
{
    float amplitude = 0.01 * sqrt(2);
    float cyclicFrequency = 1;
    float wavelength = 0.1;
    float initialPhase = 0;
    float k = (2 * 3.141592653589793) / wavelength;

    float r1 = distance(vec2(position.x, position.z), vec2(-between/2, 0));
    float phi1 = cyclicFrequency * iGlobalTime - k * r1 + initialPhase;
    float ampl1 = amplitude / r1;
    float value1 = ampl1 * sin(phi1);

    float r2 = distance(vec2(position.x, position.z), vec2(between/2, 0));
    float phi2 = cyclicFrequency * iGlobalTime - k * r2 + initialPhase;
    float ampl2 = amplitude / r2;
    float value2 = ampl2 * sin(phi2);

    float value = clamp(value1 + value2, 0, 0.2);

    height = clamp(value, 0, 10 / 8);

    vec3 timePos = vec3(position.x, position.y + value, position.z);
	vec4 mvPos = modelViewMatrix * vec4(timePos, 1.0);
    gl_Position = projectionMatrix * mvPos;
}
