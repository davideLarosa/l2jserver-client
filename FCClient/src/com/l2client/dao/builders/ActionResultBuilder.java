package com.l2client.dao.builders;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import au.com.bytecode.opencsv.CSVReader;

import com.l2client.gui.actions.AttackAction;
import com.l2client.gui.actions.BaseUsable;
import com.l2client.gui.actions.CompanionAction;
import com.l2client.gui.actions.EmoteAction;
import com.l2client.gui.actions.PickItemAction;
import com.l2client.gui.actions.SitStandAction;
import com.l2client.gui.actions.UseMagicSkillAction;
import com.l2client.gui.actions.WalkRunAction;

public class ActionResultBuilder {
	private static Logger logger = Logger.getLogger(ActionResultBuilder.class.getName());
	
	
	enum ActionTypes {
		AttackAction("AttackAction"), 
		CompanionAction("CompanionAction"),
		EmoteAction("EmoteAction"),
		PickItemAction("PickItemAction"),
		SitStandAction("SitStandAction"),
		WalkRunAction("WalkRunAction"),
		UseMagicSkillAction("UseMagicSkillAction")
		;
		
		private String type;

		ActionTypes(String str) {
			type = str;
		}
	};

	public static BaseUsable[] buildActions(ResultSet rs) {
		ArrayList<BaseUsable> ret = new ArrayList<BaseUsable>();
		try {
			while (rs.next()) {

				BaseUsable b = getUsable(rs.getInt("ID"), rs
						.getString("CLIENTACTION"), rs.getString("NAME"));
				if (b != null) {
					b.setCategory(rs.getString("CATEGORY"));
					b.setType(rs.getString("TYPE"));
					b.setDescription(rs.getString("DESCRIPTION"));
					b.setImage(rs.getString("IMAGE"));
					b.setActionID(rs.getInt("ACTIONID"));
					b.setDisplayOrder(rs.getInt("DISPLAYORDER"));

					ret.add(b);
				}
			}
		} catch (Exception e) {
			logger.log(Level.SEVERE,"Error in loadig Actions from DAO", e);
		}
		return ret.toArray(new BaseUsable[0]);
	}
	
	public static BaseUsable[] buildActions(CSVReader rs) {
		ArrayList<BaseUsable> ret = new ArrayList<BaseUsable>();
		try {
			String[] line = rs.readNext();
			while (line != null) {
				if (line.length == 9) {
					BaseUsable b = getUsable(Integer.valueOf(line[1]), line[8],
							line[2]);
					if (b != null) {
						b.setCategory(line[0]);
						b.setType(line[5]);
						b.setDescription(line[4]);
						b.setImage(line[3]);
						b.setActionID(Integer.valueOf(line[6]));
						b.setDisplayOrder(Integer.valueOf(line[7]));

						ret.add(b);
					}
				} else
					logger.warning("Only "+line.length+" items of 9:"+line);
				line = rs.readNext();
			}
		} catch (Exception e) {
			logger.log(Level.SEVERE, "Error in loadig Actions from DAO", e);
		}
		return ret.toArray(new BaseUsable[0]);
	}

	// TODO currently no differentiation of type
	private static BaseUsable getUsable( int id,
			String clientAction, String name) {

		BaseUsable b = null;
		if (clientAction != null && name != null) {
			switch (ActionTypes.valueOf(clientAction)) {
			case AttackAction:
				b = new AttackAction(id, name);
				break;
			case CompanionAction:
				b = new CompanionAction(id, name);
				break;
			case EmoteAction:
				b = new EmoteAction(id, name);
				break;
			case PickItemAction:
				b = new PickItemAction(id, name);
				break;
			case SitStandAction:
				b = new SitStandAction(id, name);
				break;
			case WalkRunAction:
				b = new WalkRunAction(id, name);
				break;
			case UseMagicSkillAction:
				b = new UseMagicSkillAction(id, name);
				break;
			default:
				logger.severe("Table ACTIONS contains an action ("+id+") without implemented CLIENTACTION ("+clientAction+") in ActionResultBuilder");
			}
		} else
		{
			logger.warning("Table ACTIONS contains an action ("+id+") with null in column CLIENTACTION ("+clientAction+")or NAME ("+name+")");
		}
		return b;
	}
}
