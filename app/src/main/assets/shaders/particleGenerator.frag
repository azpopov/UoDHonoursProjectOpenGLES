#version 100

// Interpolated values from the vertex shaders
varying vec2 UV;
varying vec4 particlecolor;

uniform sampler2D u_TextureSampler;

void main(){
	// Output color = color of the texture at the specified UV
	gl_FragColor = texture2D( u_TextureSampler, UV ) * particlecolor;

}