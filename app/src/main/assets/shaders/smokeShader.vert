#version 300 es

layout(location = 0) in vec3 squareVertices;
layout(location = 1) in vec4 xyzs; // Position of the center of the particule and size of the square

out vec2 UV;
out vec3 LightDirectionCameraSpace;
out vec3 L;

uniform mat4 u_ViewMatrix;
uniform vec3 u_LightPositionWorldSpace;
uniform vec3 u_CameraRightWorldSpace;
uniform vec3 u_CameraUpWorldSpace;
uniform mat4 u_ViewProjectionMatrix;

uniform int u_LVariation;
flat out float distnaceToL;

vec3 getL();
vec3 getLNonVarying();
void main() {

    float particleSize = xyzs.w; // because we encoded it this way.
    vec3 particleCenter_wordspace = xyzs.xyz;

    vec3 vertexPosition_worldspace =
    		particleCenter_wordspace
    		+ u_CameraRightWorldSpace * squareVertices.x * particleSize
    		+ u_CameraUpWorldSpace * squareVertices.y * particleSize;


	gl_Position = u_ViewProjectionMatrix * vec4(vertexPosition_worldspace, 1.0);
	if(u_LVariation == 1)
	{
      L = getL();
    }
    else
    {
        L=getLNonVarying();
    }
    distnaceToL = length(L);
	UV = squareVertices.xy + vec2(0.5, 0.5);
}


vec3 getL()
{
    vec3 L;
    vec3 particleCenter_wordspace = xyzs.xyz;

    vec3 vertexPosition_cameraspace = ( u_ViewMatrix * vec4(particleCenter_wordspace,1)).xyz;

    vec3 LightPosition_cameraspace = ( u_ViewMatrix * vec4(u_LightPositionWorldSpace,1)).xyz;

    L = LightPosition_cameraspace - vertexPosition_cameraspace;
    return L;
}

vec3 getLNonVarying()
{
 vec3 L;
    vec3 particleCenter_wordspace = xyzs.xyz;
    vec3 LightPosition_cameraspace = ( u_ViewMatrix * vec4(u_LightPositionWorldSpace,1)).xyz;

    L = LightPosition_cameraspace;
    return L;
}