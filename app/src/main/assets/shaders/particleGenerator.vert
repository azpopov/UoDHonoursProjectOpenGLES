#version 300 es

precision mediump float;

// Input vertex data, different for all executions of this shader.
layout(location = 0) in vec3 squareVertices;
layout(location = 1) in vec4 xyzs; // Position of the center of the particule and size of the square
layout(location = 2) in vec4 color; // Position of the center of the particule and size of the square

// Output data ; will be interpolated for each fragment.
out vec2 UV;
out vec4 particlecolor;

// Values that stay constant for the whole mesh.
uniform vec3 u_CameraRightWorldSpace;
uniform vec3 u_CameraUpWorldSpace;
uniform mat4 u_ViewProjectionMatrix; // Model-View-Projection matrix, but without the Model (the position is in BillboardPos; the orientation depends on the camera)

void main()
{
	float particleSize = xyzs.w; // because we encoded it this way.
	vec3 particleCenter_wordspace = xyzs.xyz;

	vec3 vertexPosition_worldspace =
		particleCenter_wordspace
		+ u_CameraRightWorldSpace * squareVertices.x * particleSize
		+ u_CameraUpWorldSpace * squareVertices.y * particleSize;

	// Output position of the vertex
	gl_PointSize = 10.0;
	gl_Position = u_ViewProjectionMatrix * vec4(vertexPosition_worldspace, 1.0);

	// UV of the vertex. No special space for this one.
	UV = squareVertices.xy + vec2(0.5, 0.5);
	particlecolor = color;

}