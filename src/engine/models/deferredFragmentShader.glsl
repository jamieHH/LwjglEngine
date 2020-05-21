#version 330

layout (location = 0) out vec4 out_Color;
layout (location = 1) out vec4 out_Color2;
layout (location = 2) out vec4 out_Color3;

in vec2 pass_textureCoordinates;
in vec4 pass_position;
in vec3 surfaceNormal;
in vec3 toLightVector[16];
in vec3 toEnvLightVector[2];
in vec3 toCameraVector;
in float visibility;

uniform sampler2D modelTexture;
uniform vec3 lightColor[16];
uniform vec3 envLightColor[2];
uniform float shineDamper;
uniform float reflectivity;
uniform vec3 skyColor;

void main(void) {
	vec3 unitNormal = normalize(surfaceNormal);
    vec3 unitVectorToCamera = normalize(toCameraVector);
    vec3 totalDiffuse = vec3(0.0);
    vec3 totalSpecular = vec3(0.0);

    for (int i = 0; i < 2; i++) {
        float distance = length(toEnvLightVector[i]);
        float attFactor = clamp(10.0 / distance, 0.0, 1.0);
        vec3 unitLightVector = normalize(toEnvLightVector[i]);
        float nDotl = dot(unitNormal, unitLightVector);
        float brightness = max(nDotl, 0.0);
        vec3 lightDirection = -unitLightVector;

        // Blinn-Phong specular
        vec3 halfDir = normalize(-lightDirection + unitVectorToCamera);
        float specAngle = max(dot(halfDir, unitNormal), 0.0);
        float dampedFactor = pow(specAngle, shineDamper * 4.0);

        totalDiffuse += (brightness * envLightColor[i]) / attFactor;
        totalSpecular += (dampedFactor * reflectivity * envLightColor[i]) / attFactor;
    }
    for (int i = 0; i < 16; i++) {
        float distance = length(toLightVector[i]);
        float attFactor = clamp(10.0 / distance, 0.0, 1.0);
        vec3 unitLightVector = normalize(toLightVector[i]);
        float nDotl = dot(unitNormal, unitLightVector);
        float brightness = max(nDotl, 0.0);
        vec3 lightDirection = -unitLightVector;

        // Blinn-Phong specular
        vec3 halfDir = normalize(-lightDirection + unitVectorToCamera);
        float specAngle = max(dot(halfDir, unitNormal), 0.0);
        float dampedFactor = pow(specAngle, shineDamper * 4.0);

        totalDiffuse += (brightness * lightColor[i]) * attFactor;
        totalSpecular += (dampedFactor * reflectivity * lightColor[i]) * attFactor;
	}
	totalDiffuse = max(totalDiffuse, 0.2);

	vec4 textureColor = texture(modelTexture, pass_textureCoordinates);
	if (textureColor.a < 0.5) {
        discard;
    }

	out_Color = vec4(totalDiffuse, 1.0) * textureColor + vec4(totalSpecular, 1.0);
    out_Color = mix(vec4(skyColor, 1.0), out_Color, visibility);

    //out_Color2 = pass_position; //position
    //out_Color2 = vec4(totalDiffuse, 1.0); //diffuse
    out_Color3 = vec4(reflectivity, reflectivity, reflectivity, 1.0); //specular
    //out_Color2 = texture(modelTexture, pass_textureCoordinates); //texture
    out_Color2 = vec4(normalize(surfaceNormal), 1.0); //normals
}