#version 300 es

precision mediump float;

in vec3 normalInterp;
in vec3 vertPos;
in vec2 UV;
in vec3 TangentLightPos;
in vec3 TangentViewPos;
in vec3 TangentFragPos;

out vec4 colour;

uniform int u_EmitMode;

uniform sampler2D u_DiffuseTextureSampler;
uniform sampler2D u_NormalTextureSampler;

const vec3 ambientColor = vec3(0.2, 0.2, 0.2);
const vec3 diffuseColor = vec3(0.2, 0.2, 0.2);
const vec3 specColor = vec3(1.0, 1.0, 1.0);

void main() {
    vec4 colourTex = texture(u_DiffuseTextureSampler, UV);
     // Obtain normal from normal map in range [0,1]
     vec3 normal = texture(u_NormalTextureSampler, UV).rgb;
     // Transform normal vector to range [-1,1]
     normal = normalize(normal * 2.0 - 1.0);

    vec3 lightDir = normalize(TangentLightPos - TangentFragPos);

    float lambertian = max(dot(lightDir,normal), 0.0);
    float specular = 0.0;

  if(lambertian > 0.0) {
       vec3 viewDir = normalize(TangentViewPos-TangentFragPos);

       vec3 halfDir = normalize(lightDir + viewDir);
       float specAngle = max(dot(halfDir, normal), 0.0);
       specular = pow(specAngle, 16.0);
  }

  colour = vec4( ambientColor + lambertian*diffuseColor +
                        specular*specColor, 1.0) * colourTex;
}

