#version 300 es

precision mediump float;

in vec2 UV;
in vec3 L;


out vec4 fragColor;

uniform sampler2D textureNormalAlpha;
uniform sampler2D textureColourDepth;


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
    gl_FragDepth -= depth ;

	fragColor = vec4(vec3(Ka + C * GenLight * qLamb( getMax(0.0,dot(N, L)))), alpha);
//	fragColor = vec4(vec3(Ka + C * L * qLamb( dot(N, L))), alpha);
	//fragColor = vec4(N, alpha);
}



vec3 qLamb(float x)
{
    vec3 q;

    if(x < 0.5)
    {
        q = vec3(1.0);
    }
    else
    {
        q = vec3(0.0);
    }

    return q;
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