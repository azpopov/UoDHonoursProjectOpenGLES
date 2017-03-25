#version 300 es

precision highp float;
// This is the output vertex colour sent to the rasterizer
in vec3 LightDirectionCameraspace;
in vec3 fragNormal;
in vec3 fragEyeDirectionCameraspace;
in vec4 fragPositionWorldSpace;
in vec2 UV;
in vec4 ShadowCoord;

out vec4 outputColor;

vec3 global_ambient = vec3(0.06, 0.06, 0.16);
vec3 specular_albedo = vec3(1.0, 1.0, 1.0);
float shininess = 1.0f;
vec3 emissive = vec3(0);

uniform sampler2D u_DiffuseTextureSampler;
uniform sampler2D u_ShadowTexture;
uniform vec3 u_LightPositionWorldSpace;
uniform int u_EmitMode;

float powCustom(float subject, float toPowerOf);

float shadowSimple()
{
	vec4 shadowMapPosition = ShadowCoord / ShadowCoord.w;

	float distanceFromLight = texture(u_ShadowTexture, shadowMapPosition.st).z;

	//add bias to reduce shadow acne (error margin)
	float bias = 0.0005;

	//1.0 = not in shadow (fragmant is closer to light than the value stored in shadow map)
	//0.0 = in shadow
	return float(distanceFromLight > shadowMapPosition.z - bias);
}

void main()
{
	vec4 colourTex;
	vec3 fNormal = normalize(fragNormal);

	colourTex = texture(u_DiffuseTextureSampler, UV);


	if(u_EmitMode == 1)
	{
		emissive = vec3(1.f,1.f,1.f);
	}

	float distanceToLight = length(LightDirectionCameraspace);	// For attenuation
	vec3 viewDirection = normalize(-fragPositionWorldSpace.xyz / fragPositionWorldSpace.w);
	vec3 fLightDirection = normalize(LightDirectionCameraspace);

	float diffuse = max(dot(fNormal, fLightDirection), 0.0);

	vec3 reflectionDirection = normalize(reflect(-fLightDirection, fNormal));
	vec3 specular = powCustom(max(dot(reflectionDirection, viewDirection), 0.0), shininess) * specular_albedo;


	// Calculate the attenuation factor;
	float attenuation_k = 1.0;
    float attenuation = 1.0 / (1.0 + attenuation_k * powCustom(distanceToLight, 3.0f));
    float shadow = 1.0;
    if (ShadowCoord.w > 0.0) {

    			shadow = shadowSimple();

    			//scale 0.0-1.0 to 0.2-1.0
    			//otherways everything in shadow would be black
    			shadow = (shadow * 0.8) + 0.2;
    		}
	//outputColor = vec4(attenuation*(specular+diffuse) + emissive + global_ambient, 1.0) * (colourTex);
	//outputColor = vec4(ShadowCoord.w,0,0,1);
	vec4 shadowMapPosition = ShadowCoord / ShadowCoord.w;
	//outputColor = vec4(texture(u_ShadowTexture, shadowMapPosition.st).y,0.0, 0.0, 1.0);
	outputColor = vec4(max(dot(reflectionDirection, viewDirection), 0.0),0.0,0.0,1.0);
}

float powCustom(float subject, float toPowerOf)
{
    float result = 0.f;

    for(int i = 0; i < int(toPowerOf); i++)
    {
        result *= result;
    }

    return result;
}

