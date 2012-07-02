package com.l2client.gui.transparent;

import java.awt.AlphaComposite;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.Icon;
import javax.swing.JLabel;

public class TransparentLabel extends JLabel {

	private static final long serialVersionUID = 1L;
	private float transparency = 0.5f;
	
	public TransparentLabel() {
		setOpaque(false);
	}

	public TransparentLabel(String text) {
		super(text);
		setOpaque(false);
	}

	public TransparentLabel(Icon image) {
		super(image);
		setOpaque(false);
	}

	public TransparentLabel(String text, int horizontalAlignment) {
		super(text, horizontalAlignment);
		setOpaque(false);
	}

	public TransparentLabel(Icon image, int horizontalAlignment) {
		super(image, horizontalAlignment);
		setOpaque(false);
	}

	public TransparentLabel(String text, Icon icon, int horizontalAlignment) {
		super(text, icon, horizontalAlignment);
		setOpaque(false);
	}

	public void paint(Graphics g){
		Graphics2D g2 = (Graphics2D)g.create();
		g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, transparency));
		super.paint(g2);
		g2.dispose();
	}

	public void setTransparency(float transparency) {
		this.transparency = transparency;
	}

	public float getTransparency() {
		return transparency;
	}
}
