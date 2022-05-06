#version 330

in float pixelHeight;

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

float valueMapper(float value, float min, float max) {
    return ((value - min) / (max - min));
}

void main()
{
//    vec3 color = hsv2rgb(vec3(pixelHeight, 1.0, 1.0));
    vec3 color = mix(minColor, maxColor, clamp(valueMapper(clamp(pixelHeight, 0, colorDelimiter), 0, colorDelimiter), 0, 0.9));

    if (pixelHeight == -1) {
        color = vec3(0, 0, 0);
    }

    fragColor = vec4(color, 1.0);
}
