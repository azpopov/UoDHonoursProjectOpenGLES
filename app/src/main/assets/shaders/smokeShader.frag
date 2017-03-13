#version 300 es

precision mediump float;

in vec2 UV;
in vec3 L;

out vec4 fragColor;

uniform sampler2D textureNormalDepth;
uniform sampler2D textureColourAlpha;


vec3 Ka = vec3(0.2);

vec3 getC();
vec3 qLamb(float x);

void main() {
    float originalZ = gl_FragCoord.z / gl_FragCoord.w;


    vec3 C = getC();

    vec4 textureNormalDepthAtUV = texture(textureNormalDepth, UV);
    vec3 N = textureNormalDepthAtUV.xyz;
    float alpha = (texture(textureColourAlpha, UV )).a;

	fragColor = vec4(Ka + C * L * qLamb( dot(N, L)), alpha);
}

vec3 getC()
{
    vec3 C;

    C = (texture(textureColourAlpha, UV )).xyz;

    return C;
}

vec3 qLamb(float x)
{
    vec3 q;

    if(x >= 0.5)
    {
        q = vec3(1.0);
    }
    else
    {
        q = vec3(0.0);
    }

    return q;
}