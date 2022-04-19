#version 330

layout (location=0) in vec3 position;

uniform mat4 modelViewMatrix;
uniform mat4 projectionMatrix;
uniform float iGlobalTime;

out float height;

float valueMapper(float value, float min, float max)
{
    return ((value - min) / (max - min));
}

void main()
{
    float amplitude = 0.02 * sqrt(2);
    float cyclicFrequency = 1;
    float wavelength = 0.05;
    float initialPhase = 0;
    float k = (2 * 3.141592653589793) / wavelength;
    float between = 0.1;

    float r1 = distance(vec2(position.x, position.z), vec2(-between/2, 0));
    float phi1 = cyclicFrequency * iGlobalTime - k * r1 + initialPhase;
    float ampl1 = amplitude / r1;
    float value1 = clamp(ampl1 * sin(phi1), -0.5, 0.5);

    float r2 = distance(vec2(position.x, position.z), vec2(between/2, 0));
    float phi2 = cyclicFrequency * iGlobalTime - k * r2 + initialPhase;
    float ampl2 = amplitude / r2;
    float value2 = clamp(ampl2 * sin(phi2), -0.5, 0.5);

    height = value1 + value2 + 0.5;

    vec3 timePos = vec3(position.x, height, position.z);
	vec4 mvPos = modelViewMatrix * vec4(timePos, 1.0);
    gl_Position = projectionMatrix * mvPos;
}
