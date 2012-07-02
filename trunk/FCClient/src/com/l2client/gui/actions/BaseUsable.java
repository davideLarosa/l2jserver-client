package com.l2client.gui.actions;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Icon;

import com.jme3.input.InputManager;
import com.jme3.input.controls.AnalogListener;
import com.l2client.gui.IconManager;

/**
 * A base class for actions, which can have an icon representation an a
 * description. These actions could be used in action panels, or on skill
 * categories, etc.
 * 
 * Extend these in your implementation.
 * 
 */
abstract public class BaseUsable implements AnalogListener, ActionListener {

	Icon icon = null;
	int id = -1;
	String image = null;
	String name = null;
	private String description = null;
	private String category = null;
	private int actionID = -1;
	private int displayOrder = -1;
	private String type;

	public BaseUsable(int id, String actionName) {
		this.id = id;
		this.name = actionName;
	}

	public int getId() {
		return id;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public Icon getIcon() {
		if (icon != null)
			return icon;

		icon = IconManager.getInstance().getIcon(image);
		return icon;
	}

	public String getName() {
		return name;
	}

	public void setCategory(String string) {
		this.category = string;
	}

	public void setDescription(String string) {
		this.description = string;
	}

	public void setActionID(int id) {
		this.actionID = id;
	}

	public void setDisplayOrder(int order) {
		this.displayOrder = order;
	}

	public void setType(String string) {
		this.type = string;
	}

	public String getDescription() {
		return description;
	}

	public String getCategory() {
		return category;
	}

	public int getActionID() {
		return actionID;
	}

	public int getDisplayOrder() {
		return displayOrder;
	}

	public String getType() {
		return type;
	}

	/**
	 * bind your keys here in the concrete action
	 * 
	 * @param man
	 */
	public abstract void addKeyMapping(InputManager man);

	/**
	 * remove the bindings here in the concrete action
	 * 
	 * @param man
	 */
	public abstract void removeKeyMapping(InputManager man);

	/**
	 * This is the connection to any swing visible buttons, which triggers the
	 * Usable from a swing component and just calls the AnalogAction with its
	 * own id and a value of 1.0 and a time of 0
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		onAnalog(name, 1.0f, 0f);
	}

}
