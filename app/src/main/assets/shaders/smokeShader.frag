#version 300 es

precision mediump float;

in vec2 UV;
in vec3 L;


out vec4 fragColor;

uniform sampler2D textureNormalAlpha;
uniform sampler2D textureColourDepth;
uniform sampler2D textureCelShading;
uniform mat4 u_ViewProjectionMatrix;
flat in float distnaceToL;

const float attenuationConst0 = 1.0f;
const float attenuationConst1 = 0.5f;
const float attenuationConst2 = 0.1f;

vec3 Ka = vec3(0.1);
vec3 GenLight = vec3(1.0);

vec4 getColourDepthVector()
{
    vec4 C;

    C = (texture(textureColourDepth, UV ));

    return C;
}

vec3 qLamb(float x);
float getMax(float a, float b);


void main() {
    float originalZ = gl_FragCoord.z / gl_FragCoord.w;
    float att = (1.0f / (attenuationConst0 + attenuationConst1 * distnaceToL + attenuationConst2 * distnaceToL * distnaceToL));


    vec4 textureColourDepthAtUV = vec4(getColourDepthVector());
    vec3 C = textureColourDepthAtUV.rgb;
    float depth = textureColourDepthAtUV.a;

    vec4 textureNormalAlphaAtUV = texture(textureNormalAlpha, UV);
    vec3 N = textureNormalAlphaAtUV.xyz; //foin
    N = normalize(N * 2.0 - 1.0);
    float alpha = textureNormalAlphaAtUV.a;

     if(alpha == 0.0)
    {
        discard;
    }
    alpha = alpha;
    gl_FragDepth -= depth ;
    vec3 oppositeL = -L; // flip light vector as values in  L are inverted
	fragColor = vec4(vec3(Ka + C * GenLight * qLamb( getMax(0.0,dot(N, oppositeL)))) * att, alpha);
    //fragColor = vec4(vec3(att,0,0), alpha);
}
//TODO fix rotations on particles


vec3 qLamb(float x)
{
    vec4 q;
    vec2 calShadeUV = vec2(x, clamp(abs(distnaceToL), 0.0, 1.0));
    q = texture(textureCelShading, calShadeUV);

    return vec3(q);
}

float getMax(float a, float b)
{
    float max;

    if(a > b)
    {
        max = a;
    }
    else
    {
        max = b;
    }
    return max;
}