#version 330

uniform mat4 viewMatrix;
uniform mat4 projectionMatrix;

void main()
{
    vec4 mvPos = viewMatrix * vec4(position.x, position.y, position.z, 1.0);
    gl_Position = projectionMatrix * mvPos;
}
