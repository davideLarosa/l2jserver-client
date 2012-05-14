package com.l2client.gui.dnd;

import javax.swing.Icon;
import javax.swing.JButton;

import com.l2client.gui.actions.BaseUsable;

/**
 * A swing button executing a jme action on push, otherwise reacting like a normal swing button
 */
public class ActionButton extends JButton {

	private static final long serialVersionUID = 1L;

	protected BaseUsable delegate = null;

	public void setIcon(Icon pIcon) {
		if(pIcon != null){
			this.setEnabled(true);
		} else
			this.setEnabled(false);
		super.setIcon(pIcon);
	}
	
	/**
	 * Removes the former delegate and places the new one into the actionlisteners
	 * @param pDelegate JMEAction to be placed into the actionlisteners
	 */
	public void setDelegateAction(BaseUsable pDelegate){
		if(delegate != null){
			removeActionListener(delegate);
		}
		delegate = pDelegate;
		if(delegate != null){
			addActionListener(pDelegate);
			setToolTipText(delegate.getName());
		}else{
			setToolTipText(null);
		}
	}	
}
