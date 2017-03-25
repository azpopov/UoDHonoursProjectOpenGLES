#version 300 es

precision highp float;

layout(location = 0) in vec3 a_Position;
layout(location = 1) in vec2 a_UV;
layout(location = 2) in vec3 a_Normal;

// Values that stay constant for the whole mesh.
uniform mat4 u_MVPMatrix;
uniform mat4 u_ViewMatrix;
uniform mat4 u_ModelMatrix;
uniform vec3 u_LightPositionWorldSpace;
uniform mat4 u_NormalMatrix;

out vec3 vertPos;
out vec3 normalInterp;
out vec2 UV;

void main(){
    gl_Position = u_MVPMatrix * vec4(a_Position, 1.0);
    vec4 vertPos4 = u_ViewMatrix * u_ModelMatrix * vec4(a_Position, 1.0);
    vertPos = vec3(vertPos4) / vertPos4.w;
    normalInterp = vec3(u_NormalMatrix * vec4(a_Normal, 0.0));

    UV = a_UV;
}
