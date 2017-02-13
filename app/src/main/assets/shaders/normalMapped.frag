#version 100

precision mediump float;
// Interpolated values from the vertex shaders
varying vec2 UV;
varying vec3 PositionWorldspace;
varying vec3 EyeDirectionCameraspace;
varying vec3 LightDirectionCameraspace;
varying vec3 LightDirectionTangentspace;
varying vec3 EyeDirectionTangentspace;

// Values that stay constant for the whole mesh.
uniform sampler2D u_DiffuseTextureSampler;
uniform sampler2D u_NormalTextureSampler;
uniform sampler2D u_SpecularTextureSampler;
uniform mat4 u_ViewMatrix;
uniform mat4 u_ModelMatrix;
uniform mat3 u_MV3x3;
uniform vec3 u_LightPositionWorldspace;

void main(){

	// Light emission properties
	// You probably want to put them as uniforms
	vec3 LightColor = vec3(1,1,1);
	float LightPower = 40.0;

	// Material properties
	vec3 MaterialDiffuseColor = texture2D( u_DiffuseTextureSampler, UV ).rgb;
	vec3 MaterialAmbientColor = vec3(0.1,0.1,0.1) * MaterialDiffuseColor;
	vec3 MaterialSpecularColor = texture2D( u_SpecularTextureSampler, UV ).rgb * 0.3;

	// Local normal, in tangent space. V tex coordinate is inverted because normal map is in TGA (not in DDS) for better quality
	vec3 TextureNormal_tangentspace = normalize(texture2D( u_NormalTextureSampler, vec2(UV.x,-UV.y) ).rgb*2.0 - 1.0);

	// Distance to the light
	float distance = length( u_LightPositionWorldspace - PositionWorldspace );

	// Normal of the computed fragment, in camera space
	vec3 n = TextureNormal_tangentspace;
	// Direction of the light (from the fragment to the light)
	vec3 l = normalize(LightDirectionTangentspace);
	// Cosine of the angle between the normal and the light direction,
	// clamped above 0
	//  - light is at the vertical of the triangle -> 1
	//  - light is perpendicular to the triangle -> 0
	//  - light is behind the triangle -> 0
	float dotN = n.x + n.y + n.z;
    if (dotN < 0.0)
        dotN = 0.0;
    else if (dotN > 1.0)
        dotN = 1.0;
    float cosTheta = dotN;
	// Eye vector (towards the camera)
	vec3 E = normalize(EyeDirectionTangentspace);
	// Direction in which the triangle reflects the light
	vec3 R = reflect(-l,n);
	// Cosine of the angle between the Eye vector and the Reflect vector,
	// clamped to 0
	//  - Looking into the reflection -> 1
	//  - Looking elsewhere -> < 1
    float dotER = E.x * R.x + E.y * R.y + E.z * R.z;
    if (dotER < 0.0)
        dotER = 0.0;
    else if (dotER > 1.0)
        dotER = 1.0;
    float cosAlpha = dotER;
	gl_FragColor  = vec4(
		// Ambient : simulates indirect lighting
		MaterialAmbientColor +
		// Diffuse : "color" of the object
		MaterialDiffuseColor * LightColor * LightPower * cosTheta / (distance*distance) +
		// Specular : reflective highlight, like a mirror
		MaterialSpecularColor * LightColor * LightPower * (cosAlpha* cosAlpha* cosAlpha* cosAlpha* cosAlpha) / (distance*distance),1.0);

}
