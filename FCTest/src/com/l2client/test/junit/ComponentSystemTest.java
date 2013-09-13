package com.l2client.test.junit;

import java.util.Random;

import junit.framework.TestCase;

import com.jme3.math.Vector3f;
import com.l2client.app.Singleton;
import com.l2client.component.Component;
import com.l2client.component.ComponentSystem;
import com.l2client.component.PositioningComponent;
import com.l2client.component.PositioningSystem;

public class ComponentSystemTest extends TestCase {
	
	private int count = 0;
	
	private class TestComponent implements Component{

	}
	
	private class TestSystem extends ComponentSystem{

		@Override
		public void onUpdateOf(Component c, float tpf) {
			count++;
			long l = System.currentTimeMillis() + 5;
			Random r = new Random(l);
			l = r.nextLong();
		}
		
	}

	public void testSelf(){
		TestSystem sys = new TestSystem();
		
		//add 5 components
		sys.addComponentForUpdate(new TestComponent());
		sys.addComponentForUpdate(new TestComponent());
		sys.addComponentForUpdate(new TestComponent());
		sys.addComponentForUpdate(new TestComponent());
		sys.addComponentForUpdate(new TestComponent());
		count = 0;
		//Test for very small update window, at least one should be updated
		sys.timeBudget = 0.0000000001f;
		sys.update(0.01f);
		assertEquals(1, count);
		count = 0;
		//test for very large update window, no more than the 5 passed should be processed (each once)
		sys.timeBudget = 1f;
		sys.update(0.01f);
		assertEquals(5, count);
		
		Singleton single = Singleton.get();
		single.init(null);
		PositioningSystem ps = Singleton.get().getPosSystem();
		PositioningComponent pos = new PositioningComponent();
		pos.position = new Vector3f(10,0,0);
		pos.startPos.set(pos.position);
		pos.goalPos = new Vector3f(0,0,0);
		pos.walkSpeed = 2f;
		pos.maxSpeed = pos.walkSpeed;
		ps.addComponentForUpdate(pos);
		for(int i = 0; i<11;i++){
			ps.update(1f);
		}
		assertEquals(pos.position, pos.goalPos);
		
   }

  
}
