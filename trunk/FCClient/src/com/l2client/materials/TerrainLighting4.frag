
uniform float m_Shininess;

varying vec4 AmbientSum;
varying vec4 DiffuseSum;
varying vec4 SpecularSum;

varying vec3 vNormal;
varying vec2 texCoord;
varying vec3 vPosition;
varying vec3 vnPosition;
varying vec3 vViewDir;
varying vec4 vLightDir;
varying vec4 vnLightDir;
varying vec4 lightVec;
varying vec4 spotVec;


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


float tangDot(in vec3 v1, in vec3 v2){
    float d = dot(v1,v2);
    #ifdef V_TANGENT
        d = 1.0 - d*d;
        return step(0.0, d) * sqrt(d);
    #else
        return d;
    #endif
}


float lightComputeDiffuse(in vec3 norm, in vec3 lightdir, in vec3 viewdir){
    return max(0.0, dot(norm, lightdir));
}

float lightComputeSpecular(in vec3 norm, in vec3 viewdir, in vec3 lightdir, in float shiny){
    if (shiny <= 1.0){
        return 0.0;
    }

       // Standard Phong
       vec3 R = reflect(-lightdir, norm);
       return pow(max(tangDot(R, viewdir), 0.0), shiny);

}

vec2 computeLighting(in vec3 wvPos, in vec3 wvNorm, in vec3 wvViewDir, in vec3 wvLightDir){
   float diffuseFactor = lightComputeDiffuse(wvNorm, wvLightDir, wvViewDir);
   float specularFactor = lightComputeSpecular(wvNorm, wvViewDir, wvLightDir, m_Shininess);
   specularFactor *= step(1.0, m_Shininess);

   float att = vLightDir.w;

   return vec2(diffuseFactor, specularFactor) * vec2(att);
}

  vec4 calculateDiffuseBlend(in vec2 texCoord) {
    vec4 alphaBlend   = texture2D( m_AlphaMap, texCoord.xy );

    vec4 diffuseColor = texture2D(m_DiffuseMap_0, texCoord * m_DiffuseMap_0_scale);
    diffuseColor *= alphaBlend.r;
    #ifdef DIFFUSEMAP_1
      vec4 diffuseColor1 = texture2D(m_DiffuseMap_1, texCoord * m_DiffuseMap_1_scale);
      diffuseColor = mix( diffuseColor, diffuseColor1, alphaBlend.g );
      #ifdef DIFFUSEMAP_2
        vec4 diffuseColor2 = texture2D(m_DiffuseMap_2, texCoord * m_DiffuseMap_2_scale);
        diffuseColor = mix( diffuseColor, diffuseColor2, alphaBlend.b );
        #ifdef DIFFUSEMAP_3
          vec4 diffuseColor3 = texture2D(m_DiffuseMap_3, texCoord * m_DiffuseMap_3_scale);
          diffuseColor = mix( diffuseColor, diffuseColor3, alphaBlend.a );
        #endif
      #endif
    #endif
    return diffuseColor;
  }

  vec3 calculateNormal(in vec2 texCoord) {
    vec3 normal = vec3(0,0,1);
    vec4 normalHeight = vec4(0,0,0,0);
    vec3 n = vec3(0,0,0);

    vec4 alphaBlend = texture2D( m_AlphaMap, texCoord.xy );

    #ifdef NORMALMAP_0
      normalHeight = texture2D(m_NormalMap_0, texCoord * m_DiffuseMap_0_scale);
      n = (normalHeight.xyz * vec3(2.0) - vec3(1.0));
      n.z = sqrt(1.0 - (n.x * n.x) - (n.y * n.y));
      n.y = -n.y;
      normal += n * alphaBlend.r;
    #endif

    #ifdef NORMALMAP_1
      normalHeight = texture2D(m_NormalMap_1, texCoord * m_DiffuseMap_1_scale);
      n = (normalHeight.xyz * vec3(2.0) - vec3(1.0));
      n.z = sqrt(1.0 - (n.x * n.x) - (n.y * n.y));
      n.y = -n.y;
      normal += n * alphaBlend.g;
    #endif

    #ifdef NORMALMAP_2
      normalHeight = texture2D(m_NormalMap_2, texCoord * m_DiffuseMap_2_scale);
      n = (normalHeight.xyz * vec3(2.0) - vec3(1.0));
      n.z = sqrt(1.0 - (n.x * n.x) - (n.y * n.y));
      n.y = -n.y;
      normal += n * alphaBlend.b;
    #endif

    #ifdef NORMALMAP_3
      normalHeight = texture2D(m_NormalMap_3, texCoord * m_DiffuseMap_3_scale);
      n = (normalHeight.xyz * vec3(2.0) - vec3(1.0));
      n.z = sqrt(1.0 - (n.x * n.x) - (n.y * n.y));
      n.y = -n.y;
      normal += n * alphaBlend.a;
    #endif

    return normalize(normal);
  }

void main(){

    //----------------------
    // diffuse calculations
    //----------------------

            vec4 diffuseColor = calculateDiffuseBlend(texCoord);


        float spotFallOff = 1.0;
        if(spotVec.w!=0.0){
              vec3 L=normalize(lightVec.xyz);
              vec3 spotdir = normalize(spotVec.xyz);
              float curAngleCos = dot(-L, spotdir);             
              float innerAngleCos = spotVec.w;
              float outerAngleCos = lightVec.w;
              float innerMinusOuter = innerAngleCos - outerAngleCos;
              spotFallOff = clamp((curAngleCos - outerAngleCos) / innerMinusOuter, 0.0, 1.0);
              if(spotFallOff<=0.0){
                  gl_FragColor =  AmbientSum * diffuseColor;
                  return;
              }
        }
    
    //---------------------
    // normal calculations
    //---------------------

        vec3 normal = calculateNormal(texCoord);


    //-----------------------
    // lighting calculations
    //-----------------------
    vec4 lightDir = vLightDir;
    lightDir.xyz = normalize(lightDir.xyz);

    vec2 light = computeLighting(vPosition, normal, vViewDir.xyz, lightDir.xyz)*spotFallOff;

    vec4 specularColor = vec4(1.0);

    //--------------------------
    // final color calculations
    //--------------------------
    gl_FragColor =  AmbientSum * diffuseColor +
                    DiffuseSum * diffuseColor  * light.x +
                    SpecularSum * specularColor * light.y;

    //gl_FragColor.a = alpha;
}