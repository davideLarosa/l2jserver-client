#define ATTENUATION

varying vec2 texCoord;
varying vec3 AmbientSum;
varying vec4 DiffuseSum;
varying vec3 SpecularSum;

  uniform vec4 g_LightDirection;
  varying vec3 vViewDir;
  varying vec4 vLightDir;
  varying vec3 lightVec;

uniform float m_AlphaDiscardThreshold;
uniform float m_Shininess;

uniform vec4 g_LightPosition;
#ifdef DIFFUSEMAP_0
  uniform sampler2D m_DiffuseMap_0;
#endif
#ifdef DIFFUSEMAP_1
  uniform sampler2D m_DiffuseMap_1;
#endif
#ifdef DIFFUSEMAP_2
  uniform sampler2D m_DiffuseMap_2;
#endif
#ifdef DIFFUSEMAP_3
  uniform sampler2D m_DiffuseMap_3;
#endif


#ifdef DIFFUSEMAP_0_SCALE
  uniform float m_DiffuseMap_0_scale;
#endif
#ifdef DIFFUSEMAP_1_SCALE
  uniform float m_DiffuseMap_1_scale;
#endif
#ifdef DIFFUSEMAP_2_SCALE
  uniform float m_DiffuseMap_2_scale;
#endif
#ifdef DIFFUSEMAP_3_SCALE
  uniform float m_DiffuseMap_3_scale;
#endif


#ifdef ALPHAMAP
  uniform sampler2D m_AlphaMap;
#endif

#ifdef NORMALMAP_0
  uniform sampler2D m_NormalMap_0;
#endif
#ifdef NORMALMAP_1
  uniform sampler2D m_NormalMap_1;
#endif
#ifdef NORMALMAP_2
  uniform sampler2D m_NormalMap_2;
#endif
#ifdef NORMALMAP_3
  uniform sampler2D m_NormalMap_3;
#endif


vec3 calculateNormal(vec2 pCoords, vec4 a)
{

   vec3 n1 = texture2D( m_NormalMap_0, pCoords*m_DiffuseMap_0_scale ).xyz,
      n2 = texture2D( m_NormalMap_1, pCoords*m_DiffuseMap_1_scale ).xyz,
      n3 = texture2D( m_NormalMap_2, pCoords*m_DiffuseMap_2_scale ).xyz,
      n4 = texture2D( m_NormalMap_3, pCoords*m_DiffuseMap_3_scale ).xyz;

	return (a.z * n4) + ((1.0-a.z)*((a.y* n3) +((1.0 - a.y) * ((a.x * n2)+((1.0 - a.x)*n1)))));
}

vec4 calculateDiffuse(vec2 pCoords, vec4 a)
{
//SPLATTING
  vec4 t1  = texture2D(m_DiffuseMap_0, pCoords*m_DiffuseMap_0_scale),
  t2 = texture2D(m_DiffuseMap_1 , pCoords*m_DiffuseMap_1_scale),
  t3 = texture2D(m_DiffuseMap_2 , pCoords*m_DiffuseMap_2_scale),
  t4 = texture2D(m_DiffuseMap_3 , pCoords*m_DiffuseMap_3_scale);
  return a.z *t4 + ((1.0 - a.z) *(a.y * t3 + (1.0 - a.y) * (a.x * t2 + (1.0 - a.x) * t1)));
//SPLATTING
}

vec3 calculateNormal2(vec2 pCoords, vec4 blend)
{
	//all black parts (no color)
    vec4 t1 = texture2D(m_NormalMap_0, pCoords * m_DiffuseMap_0_scale);
	vec4 ret = t1;
    #ifdef DIFFUSEMAP_1
      t1 = texture2D(m_NormalMap_1, pCoords * m_DiffuseMap_1_scale);
      ret = mix( ret, t1, blend.r );
      #ifdef DIFFUSEMAP_2
	      t1 = texture2D(m_NormalMap_2, pCoords * m_DiffuseMap_2_scale);
	      ret = mix( ret, t1, blend.g );
        #ifdef DIFFUSEMAP_3
         	t1 = texture2D(m_NormalMap_3, pCoords * m_DiffuseMap_1_scale);
          	ret = mix( ret, t1, blend.b );
        #endif
      #endif
    #endif
    return ret.xyz;
}

vec4 calculateDiffuse2(vec2 pCoords, vec4 blend)
{
	//all black parts (no color)
    vec4 t1 = texture2D(m_DiffuseMap_0, pCoords * m_DiffuseMap_0_scale);
	vec4 ret = t1;
    #ifdef DIFFUSEMAP_1
      t1 = texture2D(m_DiffuseMap_1, pCoords * m_DiffuseMap_1_scale);
      ret = mix( ret, t1, blend.r );
      #ifdef DIFFUSEMAP_2
	      t1 = texture2D(m_DiffuseMap_2, pCoords * m_DiffuseMap_2_scale);
	      ret = mix( ret, t1, blend.g );
        #ifdef DIFFUSEMAP_3
         	t1 = texture2D(m_DiffuseMap_3, pCoords * m_DiffuseMap_1_scale);
          	ret = mix( ret, t1, blend.b );
        #endif
      #endif
    #endif
    return ret;
}

float tangDot(in vec3 v1, in vec3 v2){
    float d = dot(v1,v2);
        d = 1.0 - d*d;
        return step(0.0, d) * sqrt(d);
}

float lightComputeDiffuse(in vec3 norm, in vec3 lightdir, in vec3 viewdir){
    #ifdef MINNAERT
        float NdotL = max(0.0, dot(norm, lightdir));
        float NdotV = max(0.0, dot(norm, viewdir));
        return NdotL * pow(max(NdotL * NdotV, 0.1), -1.0) * 0.5;
    #else
        return max(0.0, dot(norm, lightdir));
    #endif
}

float lightComputeSpecular(in vec3 norm, in vec3 viewdir, in vec3 lightdir, in float shiny){
    // NOTE: check for shiny <= 1 removed since shininess is now 
    // 1.0 by default (uses matdefs default vals)
    #ifdef LOW_QUALITY
       // Blinn-Phong
       // Note: preferably, H should be computed in the vertex shader
       vec3 H = (viewdir + lightdir) * vec3(0.5);
       return pow(max(tangDot(H, norm), 0.0), shiny);
    #elif defined(WARDISO)
        // Isotropic Ward
        vec3 halfVec = normalize(viewdir + lightdir);
        float NdotH  = max(0.001, tangDot(norm, halfVec));
        float NdotV  = max(0.001, tangDot(norm, viewdir));
        float NdotL  = max(0.001, tangDot(norm, lightdir));
        float a      = tan(acos(NdotH));
        float p      = max(shiny/128.0, 0.001);
        return NdotL * (1.0 / (4.0*3.14159265*p*p)) * (exp(-(a*a)/(p*p)) / (sqrt(NdotV * NdotL)));
    #else
       // Standard Phong
       vec3 R = reflect(-lightdir, norm);
       return pow(max(tangDot(R, viewdir), 0.0), shiny);
    #endif
}

vec2 computeLighting(in vec3 wvNorm, in vec3 wvViewDir, in vec3 wvLightDir){
   float diffuseFactor = lightComputeDiffuse(wvNorm, wvLightDir, wvViewDir);
   float specularFactor = lightComputeSpecular(wvNorm, wvViewDir, wvLightDir, m_Shininess);

   //HQ_ATTENUATION
    //float att = clamp(1.0 - g_LightPosition.w * length(lightVec), 0.0, 1.0);
    float att = vLightDir.w;

   specularFactor *= diffuseFactor;
   return vec2(diffuseFactor, specularFactor) * vec2(att);
}

void main(){
    vec2 newTexCoord;
     vec4 specularColor = vec4(1.0);
     //oblivion parallax values are stored in the diffuse maps alpha, not in the normal maps..
     //not done here
newTexCoord = texCoord;    
	  vec4 alpha = texture2D(m_AlphaMap, newTexCoord);
      vec4 diffuseColor = calculateDiffuse2(newTexCoord, alpha);

        float spotFallOff = 1.0;

          // allow use of control flow
          if(g_LightDirection.w != 0.0){
          vec3 L       = normalize(lightVec.xyz);
          vec3 spotdir = normalize(g_LightDirection.xyz);
          float curAngleCos = dot(-L, spotdir);             
          float innerAngleCos = floor(g_LightDirection.w) * 0.001;
          float outerAngleCos = fract(g_LightDirection.w);
          float innerMinusOuter = innerAngleCos - outerAngleCos;
          spotFallOff = (curAngleCos - outerAngleCos) / innerMinusOuter;
              if(spotFallOff <= 0.0){
                  gl_FragColor.rgb = AmbientSum * diffuseColor.rgb;
                  gl_FragColor.a   = 1.0;
                  return;
              }else{
                  spotFallOff = clamp(spotFallOff, 0.0, 1.0);
              }
             }

      vec3 normal = calculateNormal2(newTexCoord, alpha);;
      normal = normalize((normal * vec3(2.0) - vec3(1.0)));


       vec4 lightDir = vLightDir;
       lightDir.xyz = normalize(lightDir.xyz);
       vec3 viewDir = normalize(vViewDir);

       vec2   light = computeLighting(normal, viewDir, lightDir.xyz) * spotFallOff;

       // Workaround, since it is not possible to modify varying variables
       vec4 SpecularSum2 = vec4(SpecularSum, 1.0);


       gl_FragColor.rgb =  AmbientSum       * diffuseColor.rgb  +
                           DiffuseSum.rgb   * diffuseColor.rgb  * vec3(light.x) +
                           SpecularSum2.rgb * specularColor.rgb * vec3(light.y);

    gl_FragColor.a = 1.0;

    }