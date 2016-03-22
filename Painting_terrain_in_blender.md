# Introduction #

This is a short draft of painting terrain tiles in blender. Some internal blender tools are used.


# Converting Terrain tiles from l2j to blender #
> create .obj with java converter
> import .obj into blender which also adds uv coords by top projection

# Editing terrain tiles #
> requirements, blender setup, addons splat and link active

![http://l2jserver-client.googlecode.com/svn/wiki/TerrainWalkthrough_html_79956eb8.gif](http://l2jserver-client.googlecode.com/svn/wiki/TerrainWalkthrough_html_79956eb8.gif)

tiles are in named directories (exporter does this)
terrain tiles are present in the blender file as a group name.ter
terrain tiles start at 0/0 and go down to 256/-256 (exporter and converter do this for you)

![http://l2jserver-client.googlecode.com/svn/wiki/TerrainWalkthrough_html_5bff144d.gif](http://l2jserver-client.googlecode.com/svn/wiki/TerrainWalkthrough_html_5bff144d.gif)

save the image in the uv editor as the splat.png in the blend file directory
light up the scene a bit if you like and start painting the blend mask by switching to texture paint in the 3d view

![http://l2jserver-client.googlecode.com/svn/wiki/TerrainWalkthrough_html_8436733.gif](http://l2jserver-client.googlecode.com/svn/wiki/TerrainWalkthrough_html_8436733.gif)

be sure to only select color by red, green, blue, in mix/add mode and white with erase alpha

![http://l2jserver-client.googlecode.com/svn/wiki/TerrainWalkthrough_html_m1c17d50.gif](http://l2jserver-client.googlecode.com/svn/wiki/TerrainWalkthrough_html_m1c17d50.gif)

don't forget to save your uv map or this will be lost
switch back to object mode and add the splatting shadernodes by using view\_3d -> object -> add 4x splatt
the result of that script can be seen in the node editor (currently empty)
it adds 6 materials, 4 diffuse splat, one blend material and the final shadernode material
now before doing much work of looking up the textures for the different splatting layers, we first add the surrounding tiles, which already have textures we could reuse to save us work.
The first time you edit a new area you must of course initially add some textures.
so now add the surrounding tiles with view\_3d -> object -> Link tiles, this takes some time.
it should now look somewhat like this:

![http://l2jserver-client.googlecode.com/svn/wiki/TerrainWalkthrough_html_14d9b822.gif](http://l2jserver-client.googlecode.com/svn/wiki/TerrainWalkthrough_html_14d9b822.gif)

the view still shows the blend maps, but this is good, as now you will see where they eventually will not match at the borders.
now we will assign the textures based on the surrounding textures (and ev. by choosing new ones) to the shadernode materials.
to do so select the object you are editing, go to material and select the SplatBlendMask named material go to textures
scroll down to images and do not use open or new but the image to the left showing the scene textures and select the newly painted splat blend texture.

![http://l2jserver-client.googlecode.com/svn/wiki/TerrainWalkthrough_html_m7a56c74b.gif](http://l2jserver-client.googlecode.com/svn/wiki/TerrainWalkthrough_html_m7a56c74b.gif)

now back to material and change the textures for the Splat01 through splat04 materials. You do not have to use all 4, the less you use, the better the performance afterwards.
on th example above I will just use two textures, and leave the rest unassigned.
for my splat01 texture I use the already Linked (hence the L in front of the textures names) texture from an adjecant tile.
than switch back to material, choose the next one Splat02, go to textures and assign the linked grass texture in the image slot.
as i only use two materials for splatting in my example here (feel free to assign 03 and 04 by repeating the step above) I now switch over to the master material named SplatMaterial

![http://l2jserver-client.googlecode.com/svn/wiki/TerrainWalkthrough_html_m6fcb48f4.gif](http://l2jserver-client.googlecode.com/svn/wiki/TerrainWalkthrough_html_m6fcb48f4.gif)

This is what the node editor for the terrain splat material looks like (notice the two white, untouched materials for blue and alpha channel)

![http://l2jserver-client.googlecode.com/svn/wiki/TerrainWalkthrough_html_30ab13df.gif](http://l2jserver-client.googlecode.com/svn/wiki/TerrainWalkthrough_html_30ab13df.gif)

now finally switch the display over do GLSL mode to see the result (can be done earlier too of course to verify the choice of textures or border matching) (and remove any left over material from you object, the only one assigned should be the SplatMaterial

![http://l2jserver-client.googlecode.com/svn/wiki/TerrainWalkthrough_html_m7af22192.gif](http://l2jserver-client.googlecode.com/svn/wiki/TerrainWalkthrough_html_m7af22192.gif)

don't forget to save your file

# Conclusion #

you now have a ready to export terrain tile created in blender which matches up to its neighbouring tiles.