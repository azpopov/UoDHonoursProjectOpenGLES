#version 300 es

precision mediump float;

layout(location = 0) in vec3 a_Position;
layout(location = 1) in vec2 a_UV;
layout(location = 2) in vec3 a_Normal;
layout(location = 3) in vec3 a_Tangent;
layout(location = 4) in vec3 a_BiTangent;


out vec3 vertPos;
out vec3 normalInterp;
out vec2 UV;
out vec3 TangentLightPos;
out vec3 TangentViewPos;
out vec3 TangentFragPos;



// Values that stay constant for the whole mesh.
uniform mat4 u_MVPMatrix;
uniform mat4 u_ViewMatrix;
uniform mat4 u_ModelMatrix;
uniform vec3 u_LightPositionWorldSpace;
uniform vec3 u_ViewPositionWorldSpace;
uniform mat4 u_NormalMatrix;

void main(){
    gl_Position = u_MVPMatrix * vec4(a_Position, 1.0);
    vec4 vertPos4 = u_ViewMatrix * u_ModelMatrix * vec4(a_Position, 1.0);
    vertPos = vec3(vertPos4) / vertPos4.w;
    normalInterp = vec3(u_NormalMatrix * vec4(a_Normal, 0.0));

    UV = a_UV;

	vec3 vertexPosition_cameraspace = vec3(vertPos);

    vec3 PositionWorldspace = (u_ModelMatrix * vec4(a_Position, 1.0)).xyz;

   vec3 T = normalize(vec3(u_NormalMatrix * vec4(a_Tangent,   0.0)));
   vec3 B = normalize(vec3(u_NormalMatrix * vec4(a_BiTangent, 0.0)));
   vec3 N = normalize(vec3(u_NormalMatrix * vec4(a_Normal,    0.0)));
   mat3 TBN = transpose(mat3(T, B, N));
   TangentLightPos = TBN * u_LightPositionWorldSpace;
   TangentViewPos  = TBN * u_ViewPositionWorldSpace;
   TangentFragPos  = TBN * PositionWorldspace;



}

