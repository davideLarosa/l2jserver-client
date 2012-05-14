package com.l2client.component;

import java.util.ArrayList;

import com.jme3.system.NanoTimer;

public abstract class ComponentSystem {
	
	protected ArrayList<Component> components = new ArrayList<Component>();
	//FIXME when budget is small and we update only a subset of all components we get a time delay on the rest of the components !!!
	public float timeBudget = 0.1f;
	
	public abstract void onUpdateOf(Component c, float tpf);
	
	private int nextComponent=0;
	private float usedBudget =0f;
	
	public void update(float tpf){
		usedBudget = 0f;
		Component c = null;
		Component started = null;
		
		NanoTimer timer = new NanoTimer();
		do{
			c = getComponent();
			if(c != null) {
				if(started != null){
					if(c!=started){
						onUpdateOf(c, tpf);
					} else {
//System.out.println(this.getClass()+" used budget up to:"+usedBudget);
						return;
					}
				} else {
					started = c;
					onUpdateOf(c, tpf);
				}
			}
			else
				return;
			usedBudget += timer.getTimeInSeconds();
//System.out.println("used time budget:"+usedBudget);
		} while( usedBudget <timeBudget);
System.out.println(this.getClass()+" used complete budget:"+usedBudget);
	}
	
	private Component getComponent(){
		synchronized (components) {
			int size = components.size();
			if(nextComponent>=size)
				nextComponent = 0;
			if(size <=0)
				return null;
			else {
				return components.get(nextComponent++);
			}
		}
		
	}

	public void addComponentForUpdate(Component c){
		synchronized (components) {
			components.add(c);
		}
	}
	
	public void removeComponentForUpdate(Component c){
		synchronized (components) {
			components.remove(c);
		}
	}
}
