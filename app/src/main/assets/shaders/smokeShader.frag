#version 300 es

precision mediump float;

in vec2 UV;
in vec3 L;


out vec4 fragColor;

uniform sampler2D textureNormalAlpha;
uniform sampler2D textureColourDepth;
uniform sampler2D textureCelShading;
flat in float distnaceToL;

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


    vec4 textureColourDepthAtUV = vec4(getColourDepthVector());
    vec3 C = textureColourDepthAtUV.rgb;
    float depth = textureColourDepthAtUV.a;

    vec4 textureNormalAlphaAtUV = texture(textureNormalAlpha, UV);
    vec3 N = textureNormalAlphaAtUV.xyz; //foin
    float alpha = textureNormalAlphaAtUV.a;

     if(alpha == 0.0)
    {
        discard;
    }
    alpha = alpha;
    gl_FragDepth -= depth ;
    vec3 oppositeL = -L; // flip light vector as values in  L are inverted
	fragColor = vec4(vec3(Ka + C * GenLight * qLamb( getMax(0.0,dot(N, oppositeL)))), alpha);
//	fragColor = vec4(vec3(Ka + C * L * qLamb( dot(N, L))), alpha);
	//fragColor = vec4(dot(N, oppositeL), 0.0, 0.0, 1.0);
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