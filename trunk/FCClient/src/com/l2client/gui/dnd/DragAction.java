package com.l2client.gui.dnd;

import java.awt.dnd.DnDConstants;

import javax.swing.ImageIcon;

import com.jme3.swingGui.JMEDesktop;
import com.jme3.swingGui.dnd.JMEDndException;
import com.jme3.swingGui.dnd.JMEDragAndDrop;
import com.jme3.swingGui.dnd.JMEDragGestureEvent;
import com.jme3.swingGui.dnd.JMEDragGestureListener;
import com.jme3.swingGui.dnd.JMEDragSourceEvent;
import com.jme3.swingGui.dnd.JMEDragSourceListener;
import com.jme3.swingGui.dnd.JMEDropTargetEvent;
import com.jme3.swingGui.dnd.JMEDropTargetListener;
import com.jme3.swingGui.dnd.JMEMouseDragGestureRecognizer;
import com.l2client.app.Singleton;
import com.l2client.gui.actions.BaseUsable;

/**
 * A standard draggable action
 */
public class DragAction extends ActionButton implements JMEDragGestureListener,
		JMEDragSourceListener, JMEDropTargetListener{

	private static final long serialVersionUID = 1L;

	private JMEDragAndDrop dndSupport;

	public DragAction(JMEDesktop desktop, BaseUsable baseUsable) {
		if (baseUsable != null) {
			setDelegateAction(baseUsable);
			this.setIcon(baseUsable.getIcon());
		}
		this.dndSupport = desktop.getDragAndDropSupport();
		new JMEMouseDragGestureRecognizer(dndSupport, this,
				DnDConstants.ACTION_COPY_OR_MOVE, this);
	}

	@Override
	public void dragGestureRecognized(JMEDragGestureEvent dge) {
		if (this.delegate == null) {
			// nothing to transfer
			return;
		}
		
		TransferableAction transferable = new TransferableAction(delegate.getId());

		try {
			dndSupport.startDrag(dge, (ImageIcon) this.getIcon(), transferable, this);
		} catch (JMEDndException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void dragDropEnd(JMEDragSourceEvent arg0) { 
System.out.println("dragDropEnd DragAction completed:"+arg0.getDropSuccess());
Singleton.get().getGuiController().rewire();
	}

	@Override
	public void dragEnter(JMEDragSourceEvent arg0) {
System.out.println("dragEnter DragAction");		
	}

	@Override
	public void dragExit(JMEDragSourceEvent arg0) {
System.out.println("dragExit DragAction");	
	}

	@Override
	public void dragEnter(JMEDropTargetEvent e) {	}

	@Override
	public void dragExit(JMEDropTargetEvent e) {	}

	@Override
	public void dragOver(JMEDropTargetEvent e) {	}

	@Override
	public void drop(JMEDropTargetEvent e) {	}

}
