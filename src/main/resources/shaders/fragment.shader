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

uniform vec3 minColor;
uniform vec3 maxColor;
uniform float colorDelimiter;

out vec4 fragColor;

vec3 hsv2rgb(vec3 c)
{
    vec4 K = vec4(1.0, 2.0 / 3.0, 1.0 / 3.0, 3.0);
    vec3 p = abs(fract(c.xxx + K.xyz) * 6.0 - K.www);
    return c.z * mix(K.xxx, clamp(p - K.xxx, 0.0, 1.0), c.y);
}

vec3 rgb2hsv(vec3 c)
{
    vec4 K = vec4(0.0, -1.0 / 3.0, 2.0 / 3.0, -1.0);
    vec4 p = mix(vec4(c.bg, K.wz), vec4(c.gb, K.xy), step(c.b, c.g));
    vec4 q = mix(vec4(p.xyw, c.r), vec4(c.r, p.yzx), step(p.x, c.r));

    float d = q.x - min(q.w, q.y);
    float e = 1.0e-10;
    return vec3(abs(q.z + (q.w - q.y) / (6.0 * d + e)), d / (q.x + e), q.x);
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
    float valueSum = value1 + value2;

    //    float value = valueMapper(value1 + value2, -amplSum, amplSum);
    //    float value = valueMapper(clamp(valueSum, -0.2, 0.2), -0.2, 0.2);
    float value = valueMapper(clamp(valueSum, -colorDelimiter, colorDelimiter), -colorDelimiter, colorDelimiter);

    float hue = (0.67 - 0.67 * value) * 1 + clamp(pixelHeight, 0, 1) * 0;

    // COLOR SCHEME
    //    vec3 color = hsv2rgb(vec3(hue, 1 - hue, 1 - hue));
    //    vec3 color = mix(vec3(0, 1, 1), vec3(1.0, 0, 0), 1 - hue);

    vec3 maxC = rgb2hsv(maxColor);
    vec3 minC = rgb2hsv(minColor);

    vec3 color = hsv2rgb(vec3(minC.x, 1.0, 1 - hue)) + hsv2rgb(vec3(maxC.x, 1.0, 1 - hue));
    //    vec3 color = mix(hsv2rgb(vec3(minC.x, 1.0, 1 - hue)), hsv2rgb(vec3(maxC.x, 1.0, 1 - hue)), 1 - hue);
    //    vec3 color = mix(hsv2rgb(vec3(0.67, 1, 1)), hsv2rgb(vec3(0, 1, 1)), 1 - hue);
    //    vec3 color = mix(minColor, maxColor, 1 - hue);

    //    vec3 color = hsv2rgb(vec3(0, 1, 1 - hue)) + hsv2rgb(vec3(0.67, 1, 0.33 + hue));

    fragColor = vec4(color, 1.0);
}
