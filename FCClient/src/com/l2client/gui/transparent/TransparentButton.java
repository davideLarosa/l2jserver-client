package com.l2client.gui.transparent;

import java.awt.AlphaComposite;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.Icon;
import javax.swing.JButton;

public class TransparentButton extends JButton {

	private static final long serialVersionUID = 1L;
	private float transparency = 0.5f;

    public TransparentButton() {
        super(null, null);
        setOpaque(false);
    }

    public TransparentButton(Icon icon) {
        super(null, icon);
        setOpaque(false);
    }
    
	public TransparentButton(String text){
		super(text);
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
