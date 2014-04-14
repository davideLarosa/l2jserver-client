package com.l2client.model.l2j;

import com.jme3.math.Vector3f;

public class ItemInstance {

	public int objectId;
	public int charId = -1;
	public int itemId;
	public String name = "undefined";
	public int slot;
	public boolean stackable = false;
	public long count;
	public int itemType;
	public int customType1;
	public int equipped;
	public int bodyPart;
	public int enchantLevel;
	public int customType2;
	public int augmentationId;
	public int mana;
	public int remainingTime;
	public int attackElementType;
	public int attackElementPower;
	public int attackElementAttr1;
	public int attackElementAttr2;
	public int attackElementAttr3;
	public int attackElementAttr4;
	public int attackElementAttr5;
	public int attackElementAttr6;
	public Vector3f worldPosition;
	
	@Override
	public String toString(){
		return "id:"+itemId+" type:"+itemType+" slot:"+slot+" equipped:"+equipped+" bodyPart:"+bodyPart+
				" attackElementType:"+attackElementType+" attackElementPower:"+attackElementPower+
				" attr1:"+attackElementAttr1+" attr2:"+attackElementAttr2+" attr3:"+attackElementAttr3+
				" attr4:"+attackElementAttr4+" attr5:"+attackElementAttr5+" attr6:"+attackElementAttr6;
	}

}
