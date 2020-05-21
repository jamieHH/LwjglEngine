#version 400

in vec3 position;
in vec2 textureCoordinates;
in vec3 normal;
in mat4 transformationMatrix;

out vec2 pass_textureCoordinates;
out vec4 pass_position;
out vec3 surfaceNormal;
out vec3 toLightVector[16];
out vec3 toEnvLightVector[2];
out vec3 toCameraVector;
out float visibility;

uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;
uniform vec3 lightPosition[16];
uniform vec3 envLightDirection[2];

uniform int useFakeLighting;

const float density = 0.002;
const float gradient = 5.0;

void main(void) {
	vec4 worldPosition = transformationMatrix * vec4(position, 1.0);
	vec4 positionRelativeToCam = viewMatrix * worldPosition;
	gl_Position = projectionMatrix * positionRelativeToCam;
	pass_textureCoordinates = textureCoordinates;
    pass_position = worldPosition;

	vec3 actualNormal = normal;
	if (useFakeLighting > 0) {
	    actualNormal = vec3(0.0, 1.0, 0.0);
	}
	
	surfaceNormal = (transformationMatrix * vec4(actualNormal, 0.0)).xyz;
	for (int i = 0; i < 2; i++) {
        toEnvLightVector[i] = envLightDirection[i];
    }
	for (int i = 0; i < 16; i++) {
	    toLightVector[i] = lightPosition[i] - worldPosition.xyz;
	}
	toCameraVector = (inverse(viewMatrix) * vec4(0.0, 0.0, 0.0, 1.0)).xyz - worldPosition.xyz;

	float distance = length(positionRelativeToCam.xyz);
	visibility = exp(-pow((distance * density), gradient));
	visibility = clamp(visibility, 0.0, 1.0);
}