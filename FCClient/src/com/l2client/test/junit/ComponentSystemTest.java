/*
 * Copyright (c) 2009-2010 jMonkeyEngine
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * * Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 *
 * * Neither the name of 'jMonkeyEngine' nor the names of its contributors
 *   may be used to endorse or promote products derived from this software
 *   without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.l2client.test.junit;

import java.util.Random;

import junit.framework.TestCase;

import com.jme3.math.Vector3f;
import com.l2client.component.Component;
import com.l2client.component.ComponentSystem;
import com.l2client.component.PositioningSystem;
import com.l2client.component.SimplePositionComponent;

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
		
		PositioningSystem ps = PositioningSystem.get();
		SimplePositionComponent pos = new SimplePositionComponent();
		pos.currentPos = new Vector3f(10,0,0);
		pos.startPos.set(pos.currentPos);
		pos.goalPos = new Vector3f(0,0,0);
		pos.walkSpeed = 2f;
		ps.addComponentForUpdate(pos);
		for(int i = 0; i<11;i++){
			ps.update(1f);
		}
		assertEquals(pos.currentPos, pos.goalPos);
		
   }

  
}
