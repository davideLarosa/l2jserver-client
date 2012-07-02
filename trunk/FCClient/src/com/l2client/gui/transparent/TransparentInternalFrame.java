package com.l2client.gui.transparent;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JInternalFrame;
import javax.swing.plaf.basic.BasicInternalFrameUI;

public class TransparentInternalFrame extends JInternalFrame {

	private static final long serialVersionUID = 1L;
	private float transparency = 0.8f;
	
	private void init(){
		setOpaque(false);
		((BasicInternalFrameUI)getUI()).setNorthPane(null);
		setBorder(null);
		setBackground(new Color(0.2f,0.1f,0.1f,0.4f));
	}

	public TransparentInternalFrame() {
		super();
		init();
	}

	public TransparentInternalFrame(String title, boolean resizable,
			boolean closable, boolean maximizable, boolean iconifiable) {
		super(title, resizable, closable, maximizable, iconifiable);
		init();
	}

	public TransparentInternalFrame(String title, boolean resizable,
			boolean closable, boolean maximizable) {
		super(title, resizable, closable, maximizable);
		init();
	}

	public TransparentInternalFrame(String title, boolean resizable,
			boolean closable) {
		super(title, resizable, closable);
		init();
	}

	public TransparentInternalFrame(String title, boolean resizable) {
		super(title, resizable);
		init();
	}

	public TransparentInternalFrame(String title) {
		super(title);
		init();
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
