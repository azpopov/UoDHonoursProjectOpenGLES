#version 100

attribute vec4 a_Position;
attribute vec2 a_UV;
attribute vec3 a_Normal;
attribute vec3 a_Tangent;
attribute vec3 a_BiTangent;

// Output data ; will be interpolated for each fragment.
varying vec2 UV;
varying vec3 PositionWorldspace;
varying vec3 EyeDirectionCameraspace;
varying vec3 LightDirectionCameraspace;
varying vec3 LightDirectionTangentspace;
varying vec3 EyeDirectionTangentspace;

// Values that stay constant for the whole mesh.
uniform mat4 u_MVP;
uniform mat4 u_ViewMatrix;
uniform mat4 u_ModelMatrix;
uniform mat3 u_MV3x3Matrix;
uniform vec3 u_LightPositionWorldSpace;


void main(){

	// Output position of the vertex, in clip space : MVP * position
	gl_Position =  u_MVP * a_Position;

	// Position of the vertex, in worldspace : ModelMatrix * position
	PositionWorldspace = (u_ModelMatrix * a_Position).xyz;

	// Vector that goes from the vertex to the camera, in camera space.
	// In camera space, the camera is at the origin (0,0,0).
	vec3 vertexPosition_cameraspace = ( u_ViewMatrix * u_ModelMatrix * a_Position).xyz;
	EyeDirectionCameraspace = vec3(0,0,0) - vertexPosition_cameraspace;

	// Vector that goes from the vertex to the light, in camera space. ModelMatrix is ommited because it's identity.
	vec3 LightPosition_cameraspace = ( u_ViewMatrix * vec4(u_LightPositionWorldSpace,1)).xyz;
	LightDirectionCameraspace = LightPosition_cameraspace + EyeDirectionCameraspace;

	// UV of the vertex. No special space for this one.
	UV = a_UV;

	// model to camera = ModelView
	vec3 vertexTangent_cameraspace = u_MV3x3Matrix * a_Tangent;
	vec3 vertexBitangent_cameraspace = u_MV3x3Matrix * a_BiTangent;
	vec3 vertexNormal_cameraspace = u_MV3x3Matrix * a_Normal;

    mat3 tempMatrix = mat3(
                      		vertexTangent_cameraspace,
                      		vertexBitangent_cameraspace,
                      		vertexNormal_cameraspace
                      	);

    highp vec3 i0 = vertexTangent_cameraspace;
    highp vec3 i1 = vertexBitangent_cameraspace;
    highp vec3 i2 = vertexNormal_cameraspace;

      highp mat3 outMatrix = mat3(
                      vec3(i0.x, i1.x, i2.x),
                      vec3(i0.y, i1.y, i2.y),
                      vec3(i0.z, i1.z, i2.z)
                      );
	//mat3 TBN = mat3(transpose(tempMatrix)); // You can use dot products instead of building this matrix and transposing it. See References for details.


	LightDirectionTangentspace = outMatrix * LightDirectionCameraspace;
	EyeDirectionTangentspace =  outMatrix * EyeDirectionCameraspace;


}

