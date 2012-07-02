package com.l2client.model.network;

/**
 * NPC specific data model. Npcs include a template they are instantiated from.
 */
public class NpcData extends EntityData {
	private int templateId;

	/**
	 * template id
	 * @return	Int value representing the template id of this entity
	 */
	public final int getTemplateId() {
		return templateId;
	}

	/**
	 * stores the template id, will decrease the number by 1000000 if it is above that value
	 * @param templateId
	 */
	public final void setTemplateId(int templateId) {
		//decrease fake id from AbstractNpcInfo in l2jserver
		if(templateId > 1000000)
			templateId -= 1000000;
		
		this.templateId = templateId;
	}
}
