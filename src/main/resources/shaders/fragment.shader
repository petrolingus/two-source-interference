#version 330

in vec2 pixelPos;
in float pixelHeight;

uniform float iGlobalTime;
uniform float timeMul;
uniform float amplitude;
uniform float wavelength;
uniform float cyclicFrequency;
uniform float initialPhase;
uniform float between;

out vec4 fragColor;

vec3 hsv2rgb(vec3 c)
{
    vec4 K = vec4(1.0, 2.0 / 3.0, 1.0 / 3.0, 3.0);
    vec3 p = abs(fract(c.xxx + K.xyz) * 6.0 - K.www);
    return c.z * mix(K.xxx, clamp(p - K.xxx, 0.0, 1.0), c.y);
}

float valueMapper(float value, float min, float max)
{
    return ((value - min) / (max - min));
}

void main()
{
    //    float amplitude = 10;
    //    float cyclicFrequency = 1;
    //    float wavelength = 100;
    //    float initialPhase = 0;
    //    float between = 200;

    float k = (2 * 3.141592653589793) / wavelength;
    float pos = sqrt(between * between / 2);
    float t = iGlobalTime * timeMul;

    float r1 = distance(pixelPos, vec2(pos, -pos));
    float ampl1 = amplitude / r1;
    float value1 = ampl1 * sin(cyclicFrequency * t - k * r1 + initialPhase);

    float r2 = distance(pixelPos, vec2(-pos, pos));
    float ampl2 = amplitude / r2;
    float value2 = ampl2 * sin(cyclicFrequency * t - k * r2 + initialPhase);

    float amplSum = ampl1 + ampl2;
    float value = valueMapper(value1 + value2, -amplSum, amplSum);

    float hue = (0.66666666666 - 0.66666666666 * value) * 1 + clamp(pixelHeight, 0, 1) * 0;
    vec3 color = hsv2rgb(vec3(hue, 1.0, 1.0));
//    vec3 color = mix(vec3(0, 0.9, 0.9), vec3(1.0, 0, 0), hue);
    fragColor = vec4(color, 1.0);
}
