#version 300 es

precision mediump float;

in vec3 TexCoords;
out vec4 color;

uniform samplerCube skyboxSampler;

void main()
{
    color = texture(skyboxSampler, TexCoords);
}