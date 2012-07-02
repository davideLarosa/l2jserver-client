uniform mat4 g_WorldViewProjectionMatrix;
uniform mat4 g_WorldViewMatrix;
uniform mat3 g_NormalMatrix;
uniform mat4 g_ViewMatrix;
uniform mat4 g_ProjectionMatrix;

uniform vec4 g_LightColor;
uniform vec4 g_LightPosition;
uniform vec4 g_LightDirection;
uniform vec4 g_AmbientLightColor;

uniform float m_Shininess;

attribute vec3 inPosition;
attribute vec3 inNormal;
attribute vec2 inTexCoord;
attribute vec4 inTangent;

varying vec2 texCoord;
varying vec3 viewDirection;
varying vec3 lightDirection;

void main(void)
{
//gl_Vertex	inPosition
//gl_Normal	inNormal
//gl_Color	inColor
//gl_MultiTexCoord0	inTexCoord
//gl_ModelViewMatrix	g_WorldViewMatrix
//gl_ProjectionMatrix	g_ProjectionMatrix
//gl_ModelViewProjectionMatrix	g_WorldViewProjectionMatrix
//gl_NormalMatrix	g_NormalMatrix

   /* Transform vertices and pass on texture coordinates */
   //gl_Position = ftransform();
   
   //gl_Position = g_ProjectionMatrix * g_WorldViewMatrix * vec4(inPosition, 1.0);
   gl_Position = g_WorldViewProjectionMatrix * vec4(inPosition, 1.0);;
   
   gl_TexCoord[0] = gl_TextureMatrix[0] * gl_MultiTexCoord0;
   texCoord = inTexCoord;
   //gl_TexCoord[0] = gl_TextureMatrix[0] * inTexCoord;
   
   /* Transform vertex into viewspace */
   vec4 vertexViewSpace = gl_ModelViewMatrix * gl_Vertex;
   //vec4 vertexViewSpace = g_WorldViewMatrix * inPosition;
   
   /* Get view and light directions in viewspace */
   vec3 localViewDirection = -vertexViewSpace.xyz;
   vec3 localLightDirection = gl_LightSource[0].position.xyz;

   /* Calculate tangent info - stored in attributes */
	//vec3 normal = gl_NormalMatrix * gl_Normal;
	vec3 normal = g_NormalMatrix * inNormal;
	vec3 tangent = normalize(g_NormalMatrix * inTangent.xyz);
    vec3 binormal = cross(normal, tangent);
   
   /* Transform localViewDirection into texture space */
   viewDirection.x = dot( tangent, localViewDirection );
   viewDirection.y = dot( binormal, localViewDirection );
   viewDirection.z = dot( normal, localViewDirection );

   /* Transform localLightDirection into texture space */
   lightDirection.x = dot( tangent, localLightDirection );
   lightDirection.y = dot( binormal, localLightDirection );
   lightDirection.z = dot( normal, localLightDirection );
}