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

// JME3 lights in world space
void lightComputeDir(in vec3 worldPos, in vec4 color, in vec4 position, out vec4 lightDir){
    float posLight = step(0.5, color.w);
    vec3 tempVec = position.xyz * sign(posLight - 0.5) - (worldPos * posLight); 
    lightDir = vec4(normalize(tempVec), 1.0);
}

void main(void)
{


   vec4 pos = vec4(inPosition, 1.0);
   gl_Position = g_WorldViewProjectionMatrix * pos;
   texCoord = inTexCoord;

   vec3 wvPosition = (g_WorldViewMatrix * pos).xyz;
   vec3 wvNormal  = normalize(g_NormalMatrix * inNormal);
   vec3 viewDir = normalize(-wvPosition);

   vec4 wvLightPos = (g_ViewMatrix * vec4(g_LightPosition.xyz,clamp(g_LightColor.w,0.0,1.0)));
   wvLightPos.w = g_LightPosition.w;
   vec4 lightColor = g_LightColor;

   vec3 wvTangent = normalize(g_NormalMatrix * inTangent.xyz);
   vec3 wvBinormal = cross(wvNormal, wvTangent);

   mat3 tbnMat = mat3(wvTangent, wvBinormal * -inTangent.w,wvNormal);
     
  // lighter
   viewDirection  = viewDir * tbnMat;
   vec4 outLt = vec4(1.0);
   lightComputeDir(wvPosition, lightColor, wvLightPos, outLt);
   lightDirection.xyz = (outLt.xyz * tbnMat).xyz;
   lightDirection.xyz = outLt.xyz;

   // darker
   lightDirection.x = dot( wvTangent, wvLightPos.xyz );
   lightDirection.y = dot( wvBinormal, wvLightPos.xyz );
   lightDirection.z = dot( wvNormal, wvLightPos.xyz );

   viewDirection.x = dot( wvTangent, viewDir.xyz );
   viewDirection.y = dot( wvBinormal, viewDir.xyz );
   viewDirection.z = dot( wvNormal, viewDir.xyz );
   
   viewDirection  = viewDir * tbnMat;
   lightDirection.xyz = (outLt.xyz * tbnMat).xyz;


//   // Transform vertices and pass on texture coordinates
//   vec4 pos = vec4(inPosition, 1.0);
//   gl_Position = g_WorldViewProjectionMatrix * pos;
    
//   texCoord = inTexCoord;
  
//   // Transform vertex into viewspace 
//   vec4 vertexViewSpace = g_WorldViewMatrix * gl_Vertex;
   
//   // Get view and light directions in viewspace 
//   vec3 localViewDirection = -vertexViewSpace.xyz;
//   vec3 localLightDirection = g_LightPosition.xyz;
   
//   // Calculate tangent info - stored in colorbuffer 
//	vec3 normal = gl_NormalMatrix * inNormal;
//	vec3 tangent = normalize(g_NormalMatrix * inTangent.xyz);
//    vec3 binormal = cross(normal, tangent);
   
//   // Transform localViewDirection into texture space 
//   viewDirection.x = dot( tangent, localViewDirection );
//   viewDirection.y = dot( binormal, localViewDirection );
//   viewDirection.z = dot( normal, localViewDirection );

//   // Transform localLightDirection into texture space 
//   lightDirection.x = dot( tangent, localLightDirection );
//   lightDirection.y = dot( binormal, localLightDirection );
//   lightDirection.z = dot( normal, localLightDirection );
}