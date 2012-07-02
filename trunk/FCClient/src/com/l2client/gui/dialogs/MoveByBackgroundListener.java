package com.l2client.gui.dialogs;

import java.awt.Container;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JComponent;
import javax.swing.JInternalFrame;

/**
 * This is a listener which can be added to a JPanel, etc. It will make the JPanel draggable by dragging on a free space of the panel
 *
 */
public class MoveByBackgroundListener implements MouseListener,
		MouseMotionListener {
	
	JComponent target;
	JInternalFrame frame;
	private Point start_drag;
	private Point start_loc;
	
	public MoveByBackgroundListener(JComponent tgt, JInternalFrame frm){
		this.target = tgt;
		this.frame = frm;
	}

    public static JInternalFrame getFrame(Container target) {
        if(target instanceof JInternalFrame) {
            return (JInternalFrame)target;
        }
        return getFrame(target.getParent());
    }

	@Override
	public void mouseClicked(MouseEvent e) {
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}

	@Override
	public void mousePressed(MouseEvent e) {
		this.start_drag = this.getScreenLocation(e);
		this.start_loc = getFrame(this.target).getLocation();

	}

	private Point getScreenLocation(MouseEvent e) {
		Point tgt = this.target.getLocationOnScreen();
		return new Point((int)tgt.x+e.getPoint().x,(int)tgt.y+e.getPoint().y);
	}

	@Override
	public void mouseReleased(MouseEvent e) {
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		Point curr = this.getScreenLocation(e);
		Point off = new Point((int)curr.x-(int)start_drag.x, 
				(int)curr.y-(int)start_drag.y);
		Point new_loc = new Point(start_loc.x+off.x, start_loc.y+off.y);
		JInternalFrame frame = getFrame(target);
		frame.setLocation(new_loc);
	}

	@Override
	public void mouseMoved(MouseEvent e) {
	}

}
