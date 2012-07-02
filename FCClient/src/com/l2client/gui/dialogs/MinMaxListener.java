package com.l2client.gui.dialogs;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * This is a listener for minimization and restoring of a frame, e.g show hide chatpanel on mouseenter/mousexit events
 * The listener resizes the passed in frame to a minimum size of 100x25 displaying only the label, when a mouse moves in
 * the panel is restored to its original size making it available. on exiting the panel, the frame is minimized an displays
 * the label again.
 * 
 */
public class MinMaxListener implements MouseListener {

	private Point originalPosition;
	private JInternalFrame frame;
	private JPanel min;
	private JPanel max;
	private Dimension originalSize;
	private JDesktopPane desktop;

	// private boolean minPlaned = false;

	public MinMaxListener(JInternalFrame frame, String label, JPanel max, JDesktopPane desktopPane) {
		this.frame = frame;
		this.max = max;
//		this.min = new JPanel();
		this.min = new JPanel();
		this.min.setLayout(null);
		JLabel label1 = new JLabel();
		this.min.add(label1);
		//FIXME get size from text!
		label1.setText(label);
		label1.setBounds(0, 0, 80, 20);
		min.setPreferredSize(new java.awt.Dimension(100, 25));
		min.setSize(100,25);
		min.setVisible(false);
		min.addMouseListener(this);
		this.desktop = desktopPane;
		switchToMini();
	}

	@Override
	public void mouseClicked(MouseEvent e) {
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		if (min.isVisible()) {
			switchToNormal();
		}
	}

	private void switchToNormal() {
		if(min.isVisible()){
		frame.setVisible(false);
		frame.remove(min);
		min.setVisible(false);
		frame.add(max);
		// show the frame again
		frame.setSize(originalSize);
		frame.setLocation(originalPosition);
		frame.pack();
		frame.setVisible(true);
//		System.out.println("MAXsize:"+frame.getSize());
		desktop.repaint();
		desktop.revalidate();
		}
	}

	private void switchToMini() {

		// nuke the old frame and build a new one
		originalPosition = frame.getLocation();
		originalSize = frame.getSize();
//		System.out.println("originalsize:"+originalSize);
		frame.setVisible(false);
		frame.remove(max);
		frame.add(min);
		min.setVisible(true);

		// show the frame again
		frame.setSize(min.getWidth(),30+20);
		frame.pack();
		frame.setLocation(new Point(originalPosition.x, desktop.getHeight() - frame.getHeight()));
		frame.setVisible(true);
//		System.out.println("MINsize:"+frame.getSize());
		desktop.repaint();
		desktop.revalidate();

	}

	@Override
	public void mouseExited(MouseEvent e) {
		if (!min.isVisible()) {
			switchToMini();
		}
	}

	@Override
	public void mousePressed(MouseEvent e) {
	}

	@Override
	public void mouseReleased(MouseEvent e) {
	}

}
