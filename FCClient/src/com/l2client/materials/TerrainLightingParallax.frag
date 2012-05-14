
uniform float m_Shininess;

varying vec4 AmbientSum;
varying vec4 DiffuseSum;
varying vec4 SpecularSum;

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


vec3 getNormal(vec2 pCoords, vec4 a)
{
   vec3 n1 = texture2D( m_NormalMap_0, pCoords*m_DiffuseMap_0_scale ).xyz,
      n2 = texture2D( m_NormalMap_1, pCoords*m_DiffuseMap_1_scale ).xyz,
      n3 = texture2D( m_NormalMap_2, pCoords*m_DiffuseMap_2_scale ).xyz,
      n4 = texture2D( m_NormalMap_3, pCoords*m_DiffuseMap_3_scale ).xyz;
	return (a.z * n4) + 
	(
		(1.0-a.z)*
		(
			(a.y* n3) +
			(
				(1.0 - a.y) * 
				(
					(a.x * n2)+
					(
						(1.0 - a.x)*n1
					)
				)
			)
		)
	);
}

// TODO check that version with 1, 2 and 3 maps only displays correct result
  vec4 getNormal2(vec2 texCoord, vec4 alphaBlend){
    vec4 diffuseColor = texture2D(m_NormalMap_0, texCoord * m_DiffuseMap_0_scale);
    diffuseColor *= (1.0 - alphaBlend.r);
    #ifdef DIFFUSEMAP_1
      vec4 diffuseColor1 = texture2D(m_NormalMap_1, texCoord * m_DiffuseMap_1_scale);
      diffuseColor = (1.0 - alphaBlend.g) * ((alphaBlend.r * diffuseColor1)+diffuseColor);
      #ifdef DIFFUSEMAP_2
        vec4 diffuseColor2 = texture2D(m_NormalMap_2, texCoord * m_DiffuseMap_2_scale);
        diffuseColor = (1.0 - alphaBlend.b)*((alphaBlend.g*diffuseColor2)+diffuseColor);
        #ifdef DIFFUSEMAP_3
          vec4 diffuseColor3 = texture2D(m_NormalMap_3, texCoord * m_DiffuseMap_3_scale);
          diffuseColor += (alphaBlend.b * diffuseColor3);
        #endif
      #endif
    #endif
    return diffuseColor;
  }

vec4 splattBaseColor(vec2 pCoords, vec4 a)
{
  vec4 t1  = texture2D(m_DiffuseMap_0, pCoords*m_DiffuseMap_0_scale),
  t2 = texture2D(m_DiffuseMap_1 , pCoords*m_DiffuseMap_1_scale),
  t3 = texture2D(m_DiffuseMap_2 , pCoords*m_DiffuseMap_2_scale),
  t4 = texture2D(m_DiffuseMap_3 , pCoords*m_DiffuseMap_3_scale);
  return a.z *t4 + ((1.0 - a.z) *(a.y * t3 + (1.0 - a.y) * (a.x * t2 + (1.0 - a.x) * t1)));
}

// TODO check that version with 1, 2 and 3 maps only displays correct result
  vec4 splattBaseColor2(vec2 texCoord, vec4 alphaBlend){
    vec4 diffuseColor = texture2D(m_DiffuseMap_0, texCoord * m_DiffuseMap_0_scale);
    diffuseColor *= (1.0 - alphaBlend.r);
    #ifdef DIFFUSEMAP_1
      vec4 diffuseColor1 = texture2D(m_DiffuseMap_1, texCoord * m_DiffuseMap_1_scale);
      diffuseColor = (1.0 - alphaBlend.g) * ((alphaBlend.r * diffuseColor1)+diffuseColor);
      #ifdef DIFFUSEMAP_2
        vec4 diffuseColor2 = texture2D(m_DiffuseMap_2, texCoord * m_DiffuseMap_2_scale);
        diffuseColor = (1.0 - alphaBlend.b)*((alphaBlend.g*diffuseColor2)+diffuseColor);
        #ifdef DIFFUSEMAP_3
          vec4 diffuseColor3 = texture2D(m_DiffuseMap_3, texCoord * m_DiffuseMap_3_scale);
          diffuseColor += (alphaBlend.b * diffuseColor3);
        #endif
      #endif
    #endif
    return diffuseColor;
  }

    
void main(void)

{


//PARALLAX  
   
      /* Normalize view and light directions(need per-pixel normalized length) */
   vec3 normalizedViewDirection = normalize( viewDirection );
   vec3 normalizedLightDirection = normalize( lightDirection );

   vec4  alpha  = texture2D(m_AlphaMap, texCoord);
  
     /* Oblivion style parallax has height in the alpha of the diffuse map (only on some) */
     //using a height of 0.02 without heightmaps will create a wired result, so basically it is 0
     //retest it with maps having a height in the 
   float height = 0.0 + (1.0 - splattBaseColor2( texCoord, alpha).a); //0.02; //
   vec2 newTexcoord = texCoord - normalizedViewDirection.xy * height;
   vec4 baseColor = splattBaseColor2( newTexcoord, alpha);
   //vec4 baseColor = splattBaseColor( texCoord, alpha);
   vec3 norm = getNormal( newTexcoord, alpha).xyz;
   
   /* Calculate diffuse - Extract and expand normal and calculate dot angle to lightdirection */
   vec3  normal = normalize( ( norm * 2.0 ) - 1.0 );
   float NDotL = dot( normal, normalizedLightDirection ); 
      
//   /* Calculate specular - Calculate reflection vector and dot angle to viewdirection  */
//   vec3  reflection = normalize( ( ( 2.0 * normal ) * NDotL ) - normalizedLightDirection ); 
//   float RDotV = max( 0.0, dot( reflection, normalizedViewDirection ) );
            
   /* Sum up lighting models with OpenGL provided light/material properties */
   vec4  totalAmbient   = ( gl_FrontLightModelProduct.sceneColor + gl_FrontLightProduct[0].ambient ) * baseColor; 
   vec4  totalDiffuse   = gl_FrontLightProduct[0].diffuse * max( 0.0, NDotL ) * baseColor; 
   //vec4  totalSpecular  = gl_FrontLightProduct[0].specular * specularColor * ( pow( RDotV, gl_FrontMaterial.shininess ) );   

   /* Sum up lighting models with hardcoded lighting properties(for debugging) */
//   vec4  totalAmbient   = vec4(0.2, 0.2, 0.2, 1.0) * baseColor; 
//   vec4  totalDiffuse   = vec4(1.0, 1.0, 1.0, 1.0) * max( 0.0, NDotL ) * baseColor; 
//   vec4  totalSpecular  = vec4(1.0, 1.0, 1.0, 1.0) * specularColor * ( pow( RDotV, 25.0 ) );   

   /* Set final pixel color as sum of lighting models */
    gl_FragColor = totalAmbient + totalDiffuse; // + totalSpecular;
    //gl_FragColor = baseColor;
//PARALLAX

}