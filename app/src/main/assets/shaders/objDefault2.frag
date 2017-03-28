#version 300 es

precision mediump float;

in vec3 normalInterp;
in vec3 vertPos;
in vec2 UV;

out vec4 colour;

uniform int mode;
uniform vec3 u_LightPositionWorldSpace;
uniform vec3 u_ViewPositionWorldSpace;
uniform int u_EmitMode;
uniform int u_Option;

uniform sampler2D u_DiffuseTextureSampler;


const vec3 ambientColor = vec3(0.3, 0.3, 0.3);
const vec3 diffuseColor = vec3(0.2, 0.2, 0.2);
const vec3 specColor = vec3(1.0, 1.0, 1.0);

const float attenuationConst0 = 1.0f;
const float attenuationConst1 = 0.f;
const float attenuationConst2 = 0.1f;
void main() {
    vec4 colourTex = texture(u_DiffuseTextureSampler, UV);

    if(u_Option == 1 && colourTex.a < 0.1)
    {
        discard;
    }

    vec3 lightPos = u_LightPositionWorldSpace;
    vec3 normal = normalize(normalInterp);
    vec3 lightDir = normalize(lightPos - vertPos);

    float lambertian = max(dot(lightDir,normal), 0.0);
    float specular = 0.0;

    float dist = length(lightPos - vertPos);
    float att = (1.0f / (attenuationConst0 + attenuationConst1 * dist + attenuationConst2 * dist * dist));


  if(lambertian > 0.0) {
       vec3 viewDir = normalize(u_ViewPositionWorldSpace - vertPos);

       vec3 halfDir = normalize(lightDir + viewDir);
       float specAngle = max(dot(halfDir, normal), 0.0);
       specular = pow(specAngle, 6.0);
  }

  colour = vec4( ambientColor + att *
               (lambertian*diffuseColor +
                specular*specColor), 1.0) * colourTex;
}

