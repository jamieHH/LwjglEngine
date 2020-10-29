#version 330

out vec4 out_Color;

//in vec2 pass_textureCoordinates;
//in vec4 pass_position;
//in vec3 surfaceNormal;
//in vec3 toLightVector[16];
//in vec3 toEnvLightVector[2];
//in vec3 toCameraVector;
//in float visibility;
in vec2 textureCoords;

uniform sampler2D positionsFrame;
uniform sampler2D normalsFrame;
uniform sampler2D albedoFrame;
uniform sampler2D specularFrame;
uniform vec3 viewPos;

//uniform sampler2D modelTexture;
uniform vec3 lightColor[16];
uniform vec3 lightPosition[16];
uniform vec3 envLightColor[2];
uniform vec3 envLightDirection[2];
uniform vec3 skyColor;
//uniform float shineDamper;
//uniform float reflectivity;
//


void main(void) {
    vec3 FragPos = texture(positionsFrame, textureCoords).rgb;
    vec3 Normal = texture(normalsFrame, textureCoords).rgb;
    vec3 Albedo = texture(albedoFrame, textureCoords).rgb;
    float Specular = texture(specularFrame, textureCoords).g;

    // try 1
    //retrieve data from G-buffer
    // then calculate lighting as usual
    vec3 lighting = Albedo * 0.2;// hard-coded ambient component
    vec3 viewDir = (viewPos - FragPos);
    for (int i = 0; i < 2; ++i)
    {
        // diffuse
        vec3 lightDir = normalize(envLightDirection[i] - FragPos);
        vec3 diffuse = max(dot(Normal, lightDir), 0.0) * Albedo * envLightColor[i];
        lighting += diffuse;
    }
    for (int i = 0; i < 16; ++i)
    {
        // diffuse
        vec3 lightDir = normalize(lightPosition[i] - FragPos);
        vec3 diffuse = max(dot(Normal, lightDir), 0.0) * Albedo * lightColor[i];
        lighting += diffuse;
    }
    out_Color = vec4(lighting, 1.0);


    // try 2
//    vec3 col = vec3(0);
//    for (int i = 0; i < 2; i++) {
//        vec3 lightDir = envLightDirection[i].xyz - FragPos.xyz;
//
//        Normal = normalize(Normal);
//        lightDir = normalize(lightDir);
//
//        vec3 eyeDir = normalize(viewPos - FragPos.xyz);
//        vec3 vHalfVector = normalize(lightDir.xyz + eyeDir);
//
//        float specAngle = pow(max(dot(Normal, vHalfVector), 0.0), 100);
//        col += max(dot(Normal, lightDir), 0.0) * Albedo + // diffuse
//        specAngle * 1.5;
//    }
//    for (int i = 0; i < 16; i++) {
//        vec3 lightDir = lightPosition[i].xyz - FragPos.xyz;
//
//        Normal = normalize(Normal);
//        lightDir = normalize(lightDir);
//
//        vec3 eyeDir = normalize(viewPos - FragPos.xyz);
//        vec3 vHalfVector = normalize(lightDir.xyz + eyeDir);
//
//        float specAngle = pow(max(dot(Normal, vHalfVector), 0.0), 100);
//        col += max(dot(Normal, lightDir), 0.0) * Albedo * lightColor[i] + // diffuse
//        specAngle * 1.5;
//    }
//    out_Color = vec4(col, 1);
}