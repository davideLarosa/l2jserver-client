varying vec2 texCoord;


varying vec3 viewDirection;
varying vec3 lightDirection;


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

vec4 calculateDiffuseBlend(vec2 pCoords, vec4 a)
{
//SPLATTING
  vec4 t1  = texture2D(m_DiffuseMap_0, pCoords*m_DiffuseMap_0_scale),
  t2 = texture2D(m_DiffuseMap_1 , pCoords*m_DiffuseMap_1_scale),
  t3 = texture2D(m_DiffuseMap_2 , pCoords*m_DiffuseMap_2_scale),
  t4 = texture2D(m_DiffuseMap_3 , pCoords*m_DiffuseMap_3_scale);
  return a.z *t4 + ((1.0 - a.z) *(a.y * t3 + (1.0 - a.y) * (a.x * t2 + (1.0 - a.x) * t1)));
//SPLATTING
}

    
void main(void)

{


//PARALLAX
   // Normalize view and light directions(need per-pixel normalized length) 
   vec3 normalizedViewDirection = normalize( viewDirection );
   vec3 normalizedLightDirection = normalize( lightDirection );
   

   vec4  alpha  = texture2D(m_AlphaMap, texCoord.xy);
  
     // Oblivion style parallax has height in the alpha of the diffuse map (only on some) 
   float height = 0.01 + (1.0 - calculateDiffuseBlend(texCoord, alpha).a); //0.02; //
//   vec2 newTexcoord = texCoord.xy - normalizedViewDirection.xy * height;
   vec2 newTexcoord = texCoord.xy - normalizedViewDirection.xy * height;
//   vec4 baseColor = splattBaseColor( newTexcoord, alpha);
//   vec4 baseColor = calculateDiffuseBlend(texCoord, alpha);
   vec4 baseColor = calculateDiffuseBlend(newTexcoord, alpha);
   vec3 norm = calculateNormal( newTexcoord, alpha ).xyz;
   
   
   // Calculate diffuse - Extract and expand normal and calculate dot angle to lightdirection 
   vec3  normal = normalize( ( norm * 2.0 ) - 1.0 );
   float NDotL = dot( normal, normalizedLightDirection ); 

      
//   // Calculate specular - Calculate reflection vector and dot angle to viewdirection  
//   vec3  reflections = normalize( ( ( 2.0 * normal ) * NDotL ) - normalizedLightDirection ); 
//   float RDotV = max( 0.0, dot( reflections, normalizedViewDirection ) );
            
   // Sum up lighting models with OpenGL provided light/material properties 
   vec4  totalAmbient   = ( gl_FrontLightModelProduct.sceneColor + gl_FrontLightProduct[0].ambient ) * baseColor; 
   vec4  totalDiffuse   = gl_FrontLightProduct[0].diffuse * max( 0.0, NDotL ) * baseColor; 
//   vec4  totalSpecular  = gl_FrontLightProduct[0].specular * specularColor * ( pow( RDotV, gl_FrontMaterial.shininess ) );   

   // Set final pixel color as sum of lighting models 
   gl_FragColor = totalAmbient + totalDiffuse;// + totalSpecular;
   // OK gl_FragColor = vec4(texCoord.x, texCoord.y,(texCoord.x+texCoord.y)*0.5,1.0);
   //gl_FragColor = alpha; 
   // OK gl_FragColor = vec4(newTexcoord.x, newTexcoord.y,(newTexcoord.x+newTexcoord.y)*0.5,1.0);; 
   //gl_FragColor = vec4(1.0, 0,0,1.0);
   //gl_FragColor = vec4(baseColor.x, baseColor.y, baseColor.z, 1);
   //gl_FragColor = vec4(aVec.x, aVec.y,aVec.z,1.0);
//PARALLAX

}