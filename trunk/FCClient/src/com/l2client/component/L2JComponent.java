package com.l2client.component;

import com.l2client.model.l2j.ItemInstance;
import com.l2client.model.network.EntityData;

/**
 * Component for L2J-Server specific data, currently stores the EntityData and a flag if this is a player
 *
 */
public class L2JComponent implements Component {

	public EntityData l2jEntity = null;
	public ItemInstance l2jItem = null;
	public boolean isPlayer = false;
}
