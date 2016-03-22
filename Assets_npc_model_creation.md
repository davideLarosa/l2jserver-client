# NPC or PC Models #

The models used for the game engine are made up of parts to create a diversity of similar creatures, but different looks, or equipment sets with a minimum of effort to model.

![http://l2jserver-client.googlecode.com/svn/wiki/manygoblins.jpg](http://l2jserver-client.googlecode.com/svn/wiki/manygoblins.jpg)

To achieve this goal the 3d model to be used in the engine should have not only one mesh, but several meshes to choose from. The modelling package should be able to export your 3d model into the ogre 3d mesh xml format.

Currently a mesh model used for an npc or pc must have at least the following parts:

  * only one skeleton
  * one or more material
  * one or more weapon definition
  * one or more body
  * optionally one or more diverse body parts (this is the magic, the more parts the better)


---



# The skeleton #


The skeleton file is a plain xml ogre 3d skeleton definition, but just that. Any model comming from a modelling package should have the following setup for bone hierarchy used for its models. It is important to have bones named shield and weapon, for these will serve as attachment points for the specified items.

![http://l2jserver-client.googlecode.com/svn/wiki/witchelfskeleton.jpg](http://l2jserver-client.googlecode.com/svn/wiki/witchelfskeleton.jpg)

```
    <bone id="0" name="bone_pelvis">
    <bone id="1" name="bone_rthigh">
    <bone id="2" name="bone_rlowerleg">
    <bone id="3" name="bone_rfoot">
    <bone id="4" name="bone_abs">
    <bone id="5" name="bone_torso">
    <bone id="6" name="bone_head">
    <bone id="7" name="bone_jaw">
    <bone id="8" name="bone_eyebrow">
    <bone id="9" name="bone_rclavical">
    <bone id="10" name="bone_rupperarm">
    <bone id="11" name="bone_relbow">
    <bone id="12" name="bone_rhand">
    <bone id="13" name="bone_lclavical">
    <bone id="14" name="bone_lupperarm">
    <bone id="15" name="bone_lelbow">
    <bone id="16" name="bone_lhand">
    <bone id="17" name="bone_lthigh">
    <bone id="18" name="bone_llowerleg">
    <bone id="19" name="bone_lfoot">
    <bone id="20" name="bone_weapon01">
    <bone id="21" name="bone_weapon">
    <bone id="22" name="bone_weapon02">
    <bone id="23" name="bone_weapon03">
    <bone id="24" name="bone_shield01">
    <bone id="25" name="bone_shield">
    <boneparent bone="bone_rthigh" parent="bone_pelvis" />
    <boneparent bone="bone_rlowerleg" parent="bone_rthigh" />
    <boneparent bone="bone_rfoot" parent="bone_rlowerleg" />
    <boneparent bone="bone_abs" parent="bone_pelvis" />
    <boneparent bone="bone_torso" parent="bone_abs" />
    <boneparent bone="bone_head" parent="bone_torso" />
    <boneparent bone="bone_jaw" parent="bone_head" />
    <boneparent bone="bone_eyebrow" parent="bone_head" />
    <boneparent bone="bone_rclavical" parent="bone_torso" />
    <boneparent bone="bone_rupperarm" parent="bone_rclavical" />
    <boneparent bone="bone_relbow" parent="bone_rupperarm" />
    <boneparent bone="bone_rhand" parent="bone_relbow" />
    <boneparent bone="bone_lclavical" parent="bone_torso" />
    <boneparent bone="bone_lupperarm" parent="bone_lclavical" />
    <boneparent bone="bone_lelbow" parent="bone_lupperarm" />
    <boneparent bone="bone_lhand" parent="bone_lelbow" />
    <boneparent bone="bone_lthigh" parent="bone_pelvis" />
    <boneparent bone="bone_llowerleg" parent="bone_lthigh" />
    <boneparent bone="bone_lfoot" parent="bone_llowerleg" />
    <boneparent bone="bone_weapon01" parent="bone_rhand" />
    <boneparent bone="bone_weapon" parent="bone_rhand" />
    <boneparent bone="bone_weapon02" parent="bone_rhand" />
    <boneparent bone="bone_weapon03" parent="bone_rhand" />
    <boneparent bone="bone_shield01" parent="bone_lhand" />
    <boneparent bone="bone_shield" parent="bone_lhand" />
```

As you can imagine the description above comes from a skeleton.xml file. The skeletons files created for your models should be named consistent as later on
all skeleton files with the same name will end up copied to one engine specific skeleton file(last one wins). Each model directory should contain only one skeleton file.
You could for example have one model using a normal man sized skeleton for all man sized models, one squat skeleton for dwarfs, an a huge skeleton used for all models bigger than man size.

The naming and id counts of all bones in the skeleton files should be the same. This makes it easier to exchange animations. For example the Dwarf model uses the same animations as th Human Warrior. Though the goblin and the bestigors do not. they CAN use the same animations as all human sized models use, but they would seem to look a little bit to tight for the big ones, or to broad for the smaller ones. So it if often better to create an animation set for bigger creatures. Please have a look at the animation asset creation page for further details.

# The material #

A model can have several material instances. They are plaintext ogre 3d material defintions. To give the models more variety copy over any .material file over and change the texture entries for example. The secondary material file will be picked up be the compiler and will be choosen at random during instancing.
For example copy the file figure.material to figure\_2.material in the model folder and then change the texture entry to a different texture file.

# The weapon definition #

The weapon definition is the place where the animation set, the primary hand and the offhand equipment is defined. The weapon defintion is a plain text file with an .weapon ending.

## weapon\_1.weapon ##
```
#example content:
primary;weapon_orc_chopa
offhand;goblin_shield;1
anim;W_Goblin_Mace
#endof example content
```

The first line is the primary hand entry. It is a must. It starts by the string "primary;". Thereafter comes the name of the mesh model to be used as a weapon in the primary hand.

The second line is the offhand entry. It is optional. It starts with the string "offhand;". Therafter it mentions the mesh model to be used as the offhand model. This could be a weapon (for two weapon fighters) a shield, or anything else. Thereafter comes ";1" or ";0". It states if the offhand model is optional or not. On optional offhand models some models will have an offhand weapon instance and some will not.

The third and last line is the animation entry. It is a must. It starts with the string "anim;" Thereafter comes the name of the animation set to be used with this weapon. An optional second animation set thereafter can be also applied as the default set, wen the first one just has some different animations and the rest comes from a second set (not implemented so far)

# The body/body parts #

The mesh files are plain xml ogre 3d mesh files containing only the mesh, no skeleton, no animation. A 3d mesh model should at least have one body. Separating the body in several body parts with several instances for each part will give more diversity. Name the files for each body parts the same with a numbered ending. The engine will put any similar named meshes into the same body set. "hands\_1.mesh.xml" and "hands\_2.mesh.xml" would be two instances of hands to chose from when assembling a 3d npc model.

![http://l2jserver-client.googlecode.com/svn/wiki/goblinmodelled.jpg](http://l2jserver-client.googlecode.com/svn/wiki/goblinmodelled.jpg)

```
mesh>
  <submeshes>
    <submesh material="figure" name="hands_1" usesharedvertices="false" use32bitindexes="false">
      <faces count="192">
        <face v1="0" v2="1" v3="2" />
        <face v1="3" v2="4" v3="5" />
        <face v1="5" v2="4" v3="6" />
        <face v1="1" v2="7" v3="8" />
        ..
        ..
      </faces>
      <geometry vertexcount="150">
        <vertexbuffer positions="true" normals="true" colours_diffuse="false" texture_coords="1" texture_coords_dimensions_0="2" >
          <vertex>
            <position x="0.840946" y="-0.029734" z="0.481624" />
            <normal x="0.051002" y="0.710099" z="-0.702252" />
            <texcoord u="0.423388" v="0.786134" />
          </vertex>
          ..
      </geometry>
      <boneassignments>
        <vertexboneassignment vertexindex="0" boneindex="16" weight="1.0" />
        <vertexboneassignment vertexindex="1" boneindex="16" weight="1.0" />
        ..
        ..
      </boneassignments>
    </submesh>
  </submeshes>
  <skeletonlink name="std_1.0.skeleton"/>
</mesh>
```


It is important for the submesh to have a material. The file (named like the material with a ".material" ending) will be used by the engine and should be placed in the model directory.

The file should have a boneassignment section. the bone indices should be the same as provided in the .skeleton.xml. So in the above example the boneindex of 16 would be bone\_lhand (left hand bone).

The file should have a skeletonlink entry with the file name (without .xml ending) of the skeleton to be used.

# Preparing the files #

Once you have prepared all your assets in the `FCModelData/_raw/meshes` folder (one subfolder for each npc) and also prepared all animations in the `_raw/anims` folder (one subolder per animation set) run the Compiler class over it. This will create all needed npc data in the `_target` folder. Pack this folders content and provide it to the game in the classpath of your game for the models to be found.

Adjust the file FCData/data/db/npc.csv to assign any of the models as npcs. For player models this is a bit more complex and will be described on a different page (to be done).

During startup the game reads the file megaset.csv which is the blueprint for your model assembly.

# The Future #

Currently the assembled model are all just loaded and attached. They still are seperate meshes and thus each part is one draw call. The finally assembled model should be backed into one mesh, merging meshes and textures so only one drawcall would be needed per npc.

Add a simple expansion system for upgrades. For example basically I want the standard goblin model, but with some parts missing, and some special upgraded parts. Currently you would have to copy all goblin model parts over to your new upgraded goblin and add all those files the new model needs and remove any unwanted. If you change something on the standard goblin, you would have to keep in mind to change the upgraded one too. Better would be some kind of reference to the other parts used here and just place the new files in the upgraded goblin directory.