package com.l2client.gui.transparent;

import java.awt.AlphaComposite;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JTextField;
import javax.swing.text.Document;

public class TransparentTextField extends JTextField {


	private static final long serialVersionUID = 1L;
	private float transparency = 0.5f;
	
	public TransparentTextField() {
		super();
		setOpaque(false);
	}

	public TransparentTextField(String text) {
		super(text);
		setOpaque(false);
	}

	public TransparentTextField(int columns) {
		super(columns);
		setOpaque(false);
	}

	public TransparentTextField(String text, int columns) {
		super(text, columns);
		setOpaque(false);
	}

	public TransparentTextField(Document doc, String text, int columns) {
		super(doc, text, columns);
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
