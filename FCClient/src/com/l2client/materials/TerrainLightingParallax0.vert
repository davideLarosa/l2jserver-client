uniform mat4 g_WorldViewProjectionMatrix;
uniform mat4 g_WorldViewMatrix;
uniform mat3 g_NormalMatrix;

uniform vec4 g_LightPosition;


attribute vec3 inPosition;
attribute vec3 inNormal;
attribute vec2 inTexCoord;
attribute vec4 inTangent;

varying vec2 texCoord;
varying vec3 viewDirection;
varying vec3 lightDirection;

void main(void)
{
   // Transform vertices and pass on texture coordinates
   vec4 pos = vec4(inPosition, 1.0);
   gl_Position = g_WorldViewProjectionMatrix * pos;
    
   texCoord = inTexCoord;

   // Transform vertex into viewspace
   vec4 vertexViewSpace = g_WorldViewMatrix * pos;
   
   // Get view and light directions in viewspace
   vec3 localViewDirection = -vertexViewSpace.xyz;
   vec3 localLightDirection = g_LightPosition.xyz;
   
   //Calculate tangent info - stored in colorbuffer
	vec3 normal = g_NormalMatrix * inNormal;	
	vec3 tangent = normalize(g_NormalMatrix * inTangent.xyz);
    vec3 binormal = cross(normal, tangent);
   
   //Transform localViewDirection into texture space
   viewDirection.x = dot( tangent, localViewDirection );
   viewDirection.y = dot( binormal, localViewDirection );
   viewDirection.z = dot( normal, localViewDirection );

   //Transform localLightDirection into texture space
   lightDirection.x = dot( tangent, localLightDirection );
   lightDirection.y = dot( binormal, localLightDirection );
   lightDirection.z = dot( normal, localLightDirection );      
}