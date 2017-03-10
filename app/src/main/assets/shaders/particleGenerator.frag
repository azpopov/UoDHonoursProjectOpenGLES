#version 300 es

precision mediump float;

// Interpolated values from the vertex shaders
in vec2 UV;
in vec4 particlecolor;

out vec4 FragColor;

uniform sampler2D u_TextureSampler;

void main(){
	// Output color = color of the texture at the specified UV
	FragColor = (texture(u_TextureSampler, UV ) * particlecolor) ;

}