#version 300 es

precision highp float;

layout(location = 0) in vec4 a_Position;
layout(location = 1) in vec2 a_UV;
layout(location = 2) in vec3 a_Normal;

// Output data ; will be interpolated for each fragment.
out vec2 UV;
out vec4 fragPositionWorldSpace;
out vec3 fragEyeDirectionCameraspace;
out vec3 LightDirectionCameraspace;
out vec3 fragNormal;

// Values that stay constant for the whole mesh.
uniform mat4 u_MVPMatrix;
uniform mat4 u_ViewMatrix;
uniform mat4 u_ModelMatrix;
uniform vec3 u_LightPositionWorldSpace;
uniform mat3 u_NormalMatrix;

void main(){
	// Output position of the vertex, in clip space : MVP * position
	gl_Position =  u_MVPMatrix * a_Position;

	// Position of the vertex, in worldspace : ModelMatrix * position
	fragPositionWorldSpace = u_ModelMatrix * a_Position;

	// Vector that goes from the vertex to the camera, in camera space.
	// In camera space, the camera is at the origin (0,0,0).
	vec3 vertexPosition_cameraspace = ( u_ViewMatrix * (u_ModelMatrix * a_Position)).xyz;
	fragEyeDirectionCameraspace = vec3(0.f, 0.f, 3.f) - vertexPosition_cameraspace;

	// Vector that goes from the vertex to the light, in camera space. ModelMatrix is ommited because it's identity.
	vec3 LightPosition_cameraspace = ( u_ViewMatrix * vec4(u_LightPositionWorldSpace,1)).xyz;
	LightDirectionCameraspace = LightPosition_cameraspace - vertexPosition_cameraspace;


    vec3 surfaceNormal = normalize(u_NormalMatrix * a_Normal);		// Modify the normals by the normal-matrix (i.e. to model-view (or eye) coordinates )
	fragNormal = surfaceNormal;
	// UV of the vertex. No special space for this one.
	UV = a_UV;
}

