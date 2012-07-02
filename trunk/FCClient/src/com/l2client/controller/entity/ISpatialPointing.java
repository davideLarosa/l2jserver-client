package com.l2client.controller.entity;


public interface ISpatialPointing {
	public int getSize();
	public int getX();
	public int getZ();
	public int getLastX();
	public int getLastZ();
	public void updateLast();
}
