package com.l2client.animsystem;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import com.l2client.animsystem.Channel.Channels;

/**
 * The Mediator is used to glue the different animation system parts together. At
 * startup it is fed with a subset of actions and transitions to be used by that
 * entity (several could have the same configuration, some could be different).
 * The external system should use some methods to provide data for animation
 * system updates. The methods setAnimationProvider(..), update(..),
 * setInput(..), callAction(..) and the constructor should be used by a calling
 * system.
 * 
 * 
 * 
 * Animationsystem
 * based on Game AI Wisdom 2 Animation Selection by Chris Heckers
 * 
 * Provided here is an animation system which allows control of multi channel animations by means of simple actions and input values. Transitions between animations are also supported.
 * It is not an animation library. The animation system sits on top af any lower level implementation and directing the execution of such a low level system. Currently a JMonkeyengine 2 implementation is provided as an example.
 * 
 * The system comprises of different components working together to achieve the desired animation selection.
 * 
 * @see Channel
 * The animation system was designed for multi channel animations. For example you could differentiate between upper body, lower body, left arm, right arm, etc. If you would need different channels tha the ones provided, the change Channel.java A Channel holds a lock for a level for a set time where no other animation of the same level can overide the lock easily.
 * 
 * Input
 * The whole system is driven by read only Input variables, they are nothing more than enumerations. @see InputProvider is a storage component to be used between an application and the animation system to exchange input values for an entity. The input Values you need would be implmented by creating your enums for input constants and adding feeding them to the animation system. The first value of the enum should be the initial/default value.
 * 
 * @see Action
 * Actions are the place where the whole selection takes place. An actions tries to get a lock on a channel and if gained is allowed to fill an animation system owned animation descriptor. Normally a default action is used and several other animation actions are provided for more important animations or completely different selection algorithms. New Actions should be based on Action.java and only override the evaluate(..)  method.
 * 
 * Transitions
 * The concept describes transitions to be based on input value changes by actions. My implementation uses plain animations, so if you would be in need of a more sophisticated transition selection system you should change the Transitions.java implementation, or overide it.
 * 
 * IAnimationProvider, Mediator
 * IAnimationProvider.java is used to bridge the low level implementation with the animation system. It is here where the low level animation should be set, where low level internal animations are created/looked up/loaded on demand/fetched and provide information like the animation length. The Mediator is used to glue the different animation system parts together. At startup it is fed with a subset of actions and transitions to be used by that entity (several could have the same configuration, some could be different). The external system should use some methods to provide data for animation system updates.
 * The methods setAnimationProvider(..), update(..), setInput(..), callAction(..) and the constructor should be used by a calling system.
 * 
 * 
 * Building your own animation system configuration.
 * 
 * -Decide which channels you need, if you need more than are provided in Channel.java, then change that class (normally should contain just any channels you would need). Perhaps you need a more sophisticated channel configuration, if not, leave it in place.
 * -Decide on the input values which should be driving your actions. For each input type create an enum so it can be used in your engine code to fill an InputProvider and used by the actions to look at the current inpt state.
 * -Decide on the actions you need. Normally you have some idle animation. This one would be the default action used on each frame (functioning like a fallback) other actions you need would be priorized by selecting a certian lock level. An idle normally is the lowest priority, so we give it a lock level of 0,
 * a taunt, or walk could be 1, an attack could try to lock on even higher, a hurt action would be even higher (no matter what we normally act very fast to being hurt..). Implement your actions by overridng Action.java. The actions do mostly the same, try to get a lock, decide on which animation name to choose and which parameters to be passed on to the low level system (looping, playbackrate, etc.)
 * -Decide on the transitions you would need for your system. Give it a try by specifying them by using current animation name, final animation name, transition animation. Should be sufficient for most configurations. 
 * -Implement a position in your engine where update calls can be received on each frame, the input hooks are routed to the mediator and the animationprovider information is filled out. Have a look at the JMEAnimationController for inspiration.
 * 
 * Building an animation system for JMonkeyengine 2
 * 
 * To give you some hints on how to integrate the animation system into your engine an example is provided based on the JMonkeyengine 2, the code can be found in the library folder src/../animsystem/example/.
 * 
 * As a starting point we will look at the ninja ogre model comming with jmonkey. The model comes with the following animations:
 * Attack1, Attack2, Attack3, Backflip, Block, Climb, Crouch, Death1, Death2, HighJump, Idle1, Idle2, Idle3, Jump, JumpNoHeight, Kick, SideKick, Spin, Stealth, Walk.
 * All animations are full body animations, as they provide values for all joints. But it could be also the case that attacks and idle affect the uper body only and the walk the lower body. We will create such a system just to show you how to set it up, although the model is not ready to be using the different channels. So we will have three channels, AllChannels, Upper and Lowerbody. Well better not, you can test this by changing the channels in some actions, but you will notice that the Ninja animations will look jerkey as they might get replaced by default animations at any time inbetween. So I left it out.
 * The system will be based on the following input values which will be created as enums: Movement=None,Walking; AttackType=None,Weapon; Acting=Open,Hidden; Target=None,Mid,High,Back. 
 * The available actions should be default, attack, defend, climb, jump, die. Default will somehow result in Idle1-3, Stealth, Walk, Crouch animations. Attack will deliver the Attack1-3, Kicks and Spin animations. Defend logically only a Block, Climb a climb animation. The jump action will result in any jump related animations. Having decided what actions there should be it is now the time to think about the locklevels/priorities of the animations. Basically the idle stuff comes at the lowest level so we give it a zero (0). An attack should be higher, but how much? Should it be possible to attack while in a jump? If so it should be higher, than a jump. So we give jump a one (1) and attack a two (2). Defend is the same as an attack but the revers, so we give it also a two(2). A jump is similar to a climb, in my system I would like to be able to interrup any climb by an atack or defend, so I give the climb also a locklevel of one (1). Death of course is one of the top priorities, if you are dead it should be instant, right? So we give it the highest priority, three (3) as it should be possible to drop an attack animation or whatever and start to play the death animation. Remember this was just my interpretation of what should be able to cancel what out. The choice could be completely different in your situation. Finally the actions are implemented, requesting a lock, filling out an animation bean with information.
 * In the current scenario there are no transitions. That's ok too. Or you could create animations which perhaps speed up from an idle before starting to full run.
 * To implement the location in the engine we would extend the Ogre MeshAnimationController with the needed methods and interfaces. In addition the entity class will be extended with a possibility to collect input values and pass them on to the controller. Direct actions calls should also be implemented, which is a different building block (on a playable character this would go into input wireing, on an entity into its entity AI code). In the demonstration the input passing is simulated by a gui interface.
 * 
 * Why do the animations sometimes look jerkey?
 * From what I can guess the example jme ninja animations are not animated well, the walk cycle for example look smooth, the hidden_idel not. So I guess it's just the quality of animations that counts.
 * 
 * 
 *
 */
public class Mediator {

	private static Logger log = Logger.getLogger(Mediator.class.getName());
	
	private Action defaultAction;
	private Transitions transitions;
	private IAnimationProvider animationProvider;
	private InputProvider lastInput = null;
	private InputProvider currentInput = null;
	private HashMap<String, Action> actionMap = new HashMap<String, Action>();
	protected HashMap<Channel.Channels, Channel> channelMap = new HashMap<Channel.Channels, Channel>();
	private ConcurrentHashMap<Action, InputProvider> nextActions = new ConcurrentHashMap<Action, InputProvider>();

	/**
	 * Ctor currently only supporting one channel, normally would use passed in channels, or create the corresponding
	 * @param actions
	 * @param transitions
	 */
	public Mediator(Action[] actions, Transitions transitions){
		
		initChannels();
		
		if(actions != null){
			if(actions.length>0){
				this.defaultAction  = actions[0];
				for(Action a : actions)
					actionMap.put(a.getClass().getSimpleName(), a);
			}
		}
		
		if(transitions != null){
			this.transitions = transitions;
		}
		lastInput = new InputProvider();
		currentInput = lastInput;
	}
	

	/**
	 * Overide this method with your requirements for animation channels if you change Channel.Channels
	 */
	protected void initChannels() {
		channelMap.put(Channels.AllChannels, new Channel(Channels.AllChannels));
		channelMap.put(Channels.LowerBody, new Channel(Channels.LowerBody));
		channelMap.put(Channels.UpperBody, new Channel(Channels.UpperBody));
		channelMap.put(Channels.LeftArm, new Channel(Channels.LeftArm));
		channelMap.put(Channels.RightArm, new Channel(Channels.RightArm));
		channelMap.put(Channels.LeftLeg, new Channel(Channels.LeftLeg));
		channelMap.put(Channels.RightLeg, new Channel(Channels.RightLeg));
		channelMap.put(Channels.Head, new Channel(Channels.Head));
	}


	public synchronized void update(float dt) {
		//update all channel
		for(Channel c : channelMap.values())
			c.update(dt);

		//eval default action + other actions
		HashMap<Channel,Animation> result = updateAnimActions();
		
		//now we have an array of animation objects (but for each channel only one)
		//assign the final result to the channels AND to the base system
		for(Animation a :result.values()){
			setAnimation(a);
		}
		result.clear();
	}
	
	public void setInput(InputProvider in ){
		if(in != null){
			lastInput = currentInput;
			currentInput = in;
			if(!currentInput.equals(lastInput))
				update(0f);
		}
	}

	/**
	 * used from within actor AI code to call actions wave/attack/flee/etc but uses last used input Array.
	 * @param action
	 * @param in 
	 */
	public void callAction(String action, InputProvider in){
		//lookup action
		Action act = actionMap.get(action);
		if(act != null){
			nextActions.put(act, in);		
log.finer("callAction:"+action);
		} else
			log.finer("callAction FAILED:"+action);
		
	}

	public InputProvider getInput(){
		if(currentInput != null)
			return currentInput;
		
		if(lastInput != null)
			return lastInput;
		
		return null;
	}
	
	/**
	 * tests default action only(?) //check for action transition calls
	 * @param in
	 */
	private HashMap<Channel,Animation> updateAnimActions(){
		HashMap<Channel,Animation> updateResults = new HashMap<Channel,Animation>();
		Animation anim = null;
		//evaluate the default one, this is always done
		if(defaultAction != null){
			anim = defaultAction.evaluate(this);
			if(anim != null)
				updateResults.put(anim.getChannel(), anim);
		}
		
		//now check if we have any calls for action
		Action[] actions = null;
		InputProvider[] inputs = null;
		synchronized (nextActions){
			if(nextActions.size()>0){
				actions = nextActions.keySet().toArray(new Action[0]);
				inputs = nextActions.values().toArray(new InputProvider[0]);
				nextActions.clear();
			}
		}
		if(actions != null && inputs != null){
			for(int i = 0; i<actions.length;i++){
				lastInput = currentInput;
				if(inputs[i] != null)
					currentInput = inputs[i];
				else
					currentInput = InputProvider.NOINPUT;
				
				anim = actions[i].evaluate(this);
				if(anim != null)
					updateResults.put(anim.getChannel(), anim);
			}
		}
		
		//whoever succeeds has the highest prio guaranteed (setLock/forceLock)
		return updateResults;
	}
	
	private void setAnimation(Animation anim){
		//TODO check for transition
		//TODO assign ev. transition or other action
		
		Channel c = anim.getChannel();
		if(c != null){
log.finest("Anim switch from "+c.getCurrentAnimation()+" to "+anim.getName());
		String tAnimation = transitions.getTransition(c.getCurrentAnimation(), anim.getName());
		Animation tAnim = null;
		if(tAnimation != null){
log.finer("Transition first :"+tAnimation);
			tAnim = getAnimation();
			tAnim.setName(tAnimation);
			tAnim.setBlendTime(anim.getBlendTime());
			anim.setBlendTime(0.1f);
			tAnim.setChannel(anim.getChannel());
			tAnim.setLevel(anim.getLevel());
			tAnim.setLooping(false);
		}
		c.setAnimation(anim, tAnim);
		} else
			log.severe("Channel not set on animation "+anim.getName());
	}
	
//CHANNEL CALLS
	public boolean setLock(Channels chan, int lockLevel) {
		Channel c = channelMap.get(chan);
		if(c != null){
			return c.setLock(lockLevel);
		} else
			return false;
	}
	
	public boolean forceLock(Channels chan, int lockLevel) {
		Channel c = channelMap.get(chan);
		if(c != null){
			return c.forceLock(lockLevel);
		} else
			return false;
	}
	
	public Channel getChannel(Channels chan){
		return channelMap.get(chan);
	}
//END OF CHANNEL CALLS
	
//ANIMATIONPROVIDER CALLS
	public void setAnimationProvider( IAnimationProvider provider ) {
		this.animationProvider = provider;
	}

	public Animation getAnimation() {
		return this.animationProvider.createAnimation();
	}	
//END OF ANIMATIONPROVIDER CALLS
}
