#version 300 es
// Pixel shader to generate the Depth Map
// Used for shadow mapping - generates depth map from the light's viewpoint
precision highp float;

layout(location = 0) out float fragmentdepth;

void main() {
    fragmentdepth = gl_FragCoord.z;
}