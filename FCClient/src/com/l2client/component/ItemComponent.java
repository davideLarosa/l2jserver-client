package com.l2client.component;

import com.jme3.math.Vector3f;

public class ItemComponent implements Component {
	public int itemId = -1;
	public int objectId = -1;
	public int charId = -1;
	public Vector3f worldPosition = null;
	public boolean stackable = false;
	public long count = 0L;
	
	public String toString(){
		StringBuilder build = new StringBuilder();
		build.append(this.getClass().getSimpleName()).append(" ")
		.append("id:").append(itemId)
		.append(" object:").append(objectId)
		.append(" pos:").append(worldPosition);
		return build.toString();
	}
}
