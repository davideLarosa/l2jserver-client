package com.l2client.gui.dnd;

import com.jme3.swingGui.JMEDesktop;
import com.jme3.swingGui.dnd.JMEDragSourceEvent;
import com.jme3.swingGui.dnd.JMEDropTargetEvent;
import com.l2client.gui.ActionManager;
import com.l2client.gui.actions.BaseUsable;

/**
 * A drag and drop usable action item for the shortcut slots.
 * Actions can be swapped, dropped and removed if dropped outside of DnDSlots
 */
public class DnDSlotAction extends DragAction {

	public DnDSlotAction(JMEDesktop desktop, BaseUsable baseUsable) {
		super(desktop, baseUsable);
	}

	private static final long serialVersionUID = 1L;

	/**
	 * Received a dropable, extract the transferable action and set the icon and action on this slot
	 */
	@Override
	public void drop(JMEDropTargetEvent e) { 
		TransferableAction t = (TransferableAction) e.getTransferable();

		BaseUsable usable=null;
		try {
			int id =  (Integer) t.getTransferData(TransferableAction.TRANSFER_FLAVOR);
			usable = ActionManager.getInstance().getAction(id);
		} catch (Exception e1) {				
			e1.printStackTrace();
		}

		if (usable != null) {
			DragAction source = (DragAction) e.getSource();
			onDrop(usable, source);
			e.dropComplete(true);
		}
	}
	
	/**
	 * if source is also a DnDSlot it was from the shortcut panel so we switch them, otherwise we just set the action
	 * @param usable	usable to be set
	 * @param source	source the usable came from in case of switch
	 */
	public void onDrop(BaseUsable usable, DragAction source){
		//refactor for cleaner separation of concern
		if(source instanceof DnDSlotAction){
			//action from the same panel, swap actions if other present
			source.setIcon(this.getIcon());
			source.setDelegateAction(delegate);
			this.setIcon(usable.getIcon());
			this.setDelegateAction(usable);
		}else{
			//action from drag only field, just set action and icon and enable	
			this.setIcon(usable.getIcon());
			this.setDelegateAction(usable);
		}
	}
	
	/**
	 * Drag n Drop ends, if it was not consumed it was dropped outside, so remove it.
	 */
	@Override
	public void dragDropEnd(JMEDragSourceEvent arg0) {
		if (!arg0.getDropSuccess()) {
			this.setIcon(null);
			this.setDelegateAction(null);
			this.setToolTipText(null);
		}
		super.dragDropEnd(arg0);
	}
}