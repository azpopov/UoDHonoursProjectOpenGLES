#version 300 es

precision mediump float;

in vec3 normalInterp;
in vec3 vertPos;
in vec2 UV;

out vec4 colour;

uniform int mode;
uniform vec3 u_LightPositionWorldSpace;
uniform int u_EmitMode;

uniform sampler2D u_DiffuseTextureSampler;


vec3 lightPos = vec3(1.0,1.0,1.0);
const vec3 ambientColor = vec3(0.1, 0.1, 0.1);
const vec3 diffuseColor = vec3(0.2, 0.2, 0.2);
const vec3 specColor = vec3(1.0, 1.0, 1.0);

void main() {
    vec4 colourTex = texture(u_DiffuseTextureSampler, UV);

    lightPos = u_LightPositionWorldSpace;
    vec3 normal = normalize(normalInterp);
    vec3 lightDir = normalize(lightPos - vertPos);

    float lambertian = max(dot(lightDir,normal), 0.0);
    float specular = 0.0;

  if(lambertian > 0.0) {
       vec3 viewDir = normalize(-vertPos);

       vec3 halfDir = normalize(lightDir + viewDir);
       float specAngle = max(dot(halfDir, normal), 0.0);
       specular = pow(specAngle, 16.0);
  }

  colour = vec4( ambientColor + lambertian*diffuseColor +
                        specular*specColor, 1.0) * colourTex;
}

