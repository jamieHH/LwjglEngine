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
//uniform float shineDamper;
//uniform float reflectivity;
//


void main(void) {

    // retrieve data from G-buffer
    vec3 FragPos = texture(positionsFrame, textureCoords).rgb;
    vec3 Normal = texture(normalsFrame, textureCoords).rgb;
    vec3 Albedo = texture(albedoFrame, textureCoords).rgb;
    float Specular = texture(specularFrame, textureCoords).g;

    // then calculate lighting as usual
    vec3 lighting = Albedo * 0.1; // hard-coded ambient component
    vec3 viewDir = normalize(viewPos - FragPos);
    for(int i = 0; i < 16; ++i)
    {
        // diffuse
        vec3 lightDir = normalize(lightPosition[i] - FragPos);
        vec3 diffuse = max(dot(Normal, lightDir), 0.0) * Albedo * lightColor[i];
        lighting += diffuse;
    }
//    for(int i = 0; i < 2; ++i)
//    {
//        // diffuse
//        vec3 lightDir = normalize(envLightDirection[i] - FragPos);
//        vec3 diffuse = max(dot(Normal, lightDir), 0.0) * Albedo * envLightColor[i];
//        lighting += diffuse;
//    }

    out_Color = vec4(lighting, 1.0);
    out_Color = vec4(1, 1, 0, 1.0);

//	vec3 unitNormal = normalize(surfaceNormal);
//    vec3 unitVectorToCamera = normalize(toCameraVector);
//    vec3 totalDiffuse = vec3(0.0);
//    vec3 totalSpecular = vec3(0.0);
//
//    for (int i = 0; i < 2; i++) {
//        float distance = length(toEnvLightVector[i]);
//        float attFactor = clamp(10.0 / distance, 0.0, 1.0);
//        vec3 unitLightVector = normalize(toEnvLightVector[i]);
//        float nDotl = dot(unitNormal, unitLightVector);
//        float brightness = max(nDotl, 0.0);
//        vec3 lightDirection = -unitLightVector;
//
//        // Blinn-Phong specular
//        vec3 halfDir = normalize(-lightDirection + unitVectorToCamera);
//        float specAngle = max(dot(halfDir, unitNormal), 0.0);
//        float dampedFactor = pow(specAngle, shineDamper * 4.0);
//
//        totalDiffuse += (brightness * envLightColor[i]) / attFactor;
//        totalSpecular += (dampedFactor * reflectivity * envLightColor[i]) / attFactor;
//    }
//    for (int i = 0; i < 16; i++) {
//        float distance = length(toLightVector[i]);
//        float attFactor = clamp(10.0 / distance, 0.0, 1.0);
//        vec3 unitLightVector = normalize(toLightVector[i]);
//        float nDotl = dot(unitNormal, unitLightVector);
//        float brightness = max(nDotl, 0.0);
//        vec3 lightDirection = -unitLightVector;
//
//        // Blinn-Phong specular
//        vec3 halfDir = normalize(-lightDirection + unitVectorToCamera);
//        float specAngle = max(dot(halfDir, unitNormal), 0.0);
//        float dampedFactor = pow(specAngle, shineDamper * 4.0);
//
//        totalDiffuse += (brightness * lightColor[i]) * attFactor;
//        totalSpecular += (dampedFactor * reflectivity * lightColor[i]) * attFactor;
//	}
//	totalDiffuse = max(totalDiffuse, 0.2);
//
//	vec4 textureColor = texture(modelTexture, pass_textureCoordinates);
//	if (textureColor.a < 0.5) {
//        discard;
//    }
//
//	out_Color = vec4(totalDiffuse, 1.0) * textureColor + vec4(totalSpecular, 1.0);
//    out_Color = mix(vec4(skyColor, 1.0), out_Color, visibility);
}