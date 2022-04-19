#version 330

layout (location=0) in vec3 position;

uniform mat4 modelViewMatrix;
uniform mat4 projectionMatrix;
uniform float iGlobalTime;

out float height;

float valueMapper(float value, float min, float max) {
    return ((value - min) / (max - min));
}

void main()
{
    float amplitude = 0.01;
    float cyclicFrequency = 4;
    float wavelength = 0.1;
    float initialPhase = 0;

    float k = 6.28 / wavelength;
    float r = distance(vec2(position.x, position.z), vec2(0, 0));
    float phi = cyclicFrequency * iGlobalTime - k * r + initialPhase;
    float ampl = amplitude / r;
    float value = ampl * sin(phi);
    height = value * 0.5;

    vec3 timePos = vec3(position.x, value, position.z);
	vec4 mvPos = modelViewMatrix * vec4(timePos, 1.0);
    gl_Position = projectionMatrix * mvPos;
}
