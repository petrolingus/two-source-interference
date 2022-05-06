#version 330

layout (location=0) in vec3 position;

uniform mat4 viewMatrix;
uniform mat4 projectionMatrix;

out float pixelHeight;

void main()
{
    float yValue = clamp(position.y, 0, 0.4);
	vec4 mvPos = viewMatrix * vec4(position.x, yValue, position.z, 1.0);
    gl_Position = projectionMatrix * mvPos;
    pixelHeight = position.y;
}
