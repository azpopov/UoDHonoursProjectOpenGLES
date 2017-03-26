#version 300 es

layout (location = 0) in vec3 a_Position;
out vec3 TexCoords;

uniform mat4 u_Projection;
uniform mat4 u_View;


void main()
{
    vec4 pos =   u_Projection * mat4(mat3(u_View)) * vec4(a_Position, 1.0);
    gl_Position = pos.xyww;
    TexCoords = a_Position;
}