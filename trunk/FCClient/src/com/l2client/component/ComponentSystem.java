package com.l2client.component;

import java.util.ArrayList;
import java.util.logging.Logger;

import com.jme3.system.NanoTimer;

/**
 * Abstract class of a component system basic functionality.
 * A component system has knowledge of components which it should updat, and only of those.
 * Components are added by the addComponentForUpdate and removed by removeComponentForUpdate, 
 * this can be done in your own Entity factories or Entity creation code.
 * <br>
 * To integrate a system implement the onUpdateOf method with your specific component update 
 * functionality (for example calculate a new position of a mover component) and add a call to
 * Yoursystem.update() to your general update loop
 * <br>
 * A ComponenSystem has a budget specified. During update the components are updated round robin 
 * until the budget is used up or the first one has been reached again (all been once updated).
 * <br>
 * Different systems could have different budgets defined.
 * <br>
 * For a general storage system for components have a look at @see EntityManager
 * <br>
 * Here is an example on how an entity could be initialized
 * <br>
 * <pre>
 		Entity ent = Singleton.get().getEntityManager().createEntity(e.getObjectId());
		SimplePositionComponent pos = new SimplePositionComponent();
		L2JComponent l2j = new L2JComponent();
		VisualComponent vis = new VisualComponent();
		EnvironmentComponent env = new EnvironmentComponent();
		
		//done here extra as in update values will be left untouched
		pos.startPos.set(e.getX(), e.getY(), e.getZ());
		pos.currentPos.set(pos.startPos);
		pos.goalPos.set(pos.currentPos);
		pos.walkSpeed = e.getWalkSpeed();
		pos.runSpeed = e.getRunSpeed();
		pos.running = e.isRunning();
		pos.heading = e.getHeading();
		pos.targetHeading = pos.heading;
		
		updateComponents(e, ent, pos, l2j, env, vis);
		
		Singleton.get().getEntityManager().addComponent(ent.getId(), env);
		Singleton.get().getEntityManager().addComponent(ent.getId(), l2j);
		Singleton.get().getEntityManager().addComponent(ent.getId(), pos);		
		Singleton.get().getEntityManager().addComponent(ent.getId(), vis);

		
		
		Singleton.get().getPosSystem().addComponentForUpdate(pos);
		JmeUpdateSystem.get().addComponentForUpdate(pos);
		Singleton.get().getAnimSystem().addComponentForUpdate(env);
		</pre>
 */
//TODO check components not having had enough processing time get an extra dt during update (store last tpf for that system if not all ways processed)
public abstract class ComponentSystem {
	
	protected static Logger log = Logger.getLogger(ComponentSystem.class.getName());
	
	/**
	 * List of registered components, could be of different type
	 */
	protected ArrayList<Component> components = new ArrayList<Component>();

	/**
	 * Maximum time in seconds to use for a system as a budget
	 */
	public float timeBudget = 0.05f;
	
	/**
	 * Implement here your component processing
	 * @param c		A component to be updated
	 * @param tpf	the delta time since the last call in seconds
	 */
	public abstract void onUpdateOf(Component c, float tpf);
	
	/**
	 * internal count of next component to be processed
	 */
	private int nextComponent=0;
	
	/**
	 * the so far used budget in the update loop
	 */
	private float usedBudget =0f;
	
	/**
	 * Calls onUpdateOf on registered components as often as the budget time allows it until the budgeted time 
	 * is used up, or all components have been processed in onUpdateOf. During the next update the remaining components are processed
	 * @param tpf	delta time since the last update
	 */
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
						log.finest(this.getClass()+" used budget up to:"+usedBudget+" of "+timeBudget);					
						return;
					}
				} else {
					started = c;
					onUpdateOf(c, tpf);
				}
			}
			else
				return;
			usedBudget = timer.getTimeInSeconds();
			log.finest(this.getClass()+" used time budget:"+usedBudget);
		} while( usedBudget <timeBudget);
		log.severe(this.getClass()+" used complete budget:"+usedBudget+" of "+timeBudget);
	}
	
	/**
	 * fetch the next component to be processed going round robin
	 * @return null or the next component
	 */
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

	/**
	 * add a component to be updated by this system
	 * @param c	the component to be used in updates
	 */
	public void addComponentForUpdate(Component c){
		synchronized (components) {
			if(components.contains(c)){
System.out.println("Component already added "+c);
//				return;
			}
			components.add(c);
		}
	}
	
	/**
	 * remove a component to be removed from this system
	 * FIXME check this is working correctly, what if we delete one which is currently updated, better queue for removal.
	 * @param c	the component to be removed
	 */
	public void removeComponentForUpdate(Component c){
		synchronized (components) {
			components.remove(c);
		}
	}
	
	void dumpComponents(){
		synchronized (components) {
			System.out.println("DUMP start");
			for(Component c: components){
				System.out.println("DUMP of "+c);
			}
			
		}
	}
}
