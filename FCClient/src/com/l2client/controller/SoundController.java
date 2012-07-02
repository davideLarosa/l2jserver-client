package com.l2client.controller;

import paulscode.sound.Library;
import paulscode.sound.SoundSystem;
import paulscode.sound.SoundSystemConfig;
import paulscode.sound.SoundSystemException;
import paulscode.sound.codecs.CodecJOrbis;
import paulscode.sound.codecs.CodecWav;
import paulscode.sound.libraries.LibraryJavaSound;
import paulscode.sound.libraries.LibraryLWJGLOpenAL;

import com.jme3.math.Vector3f;

public final class SoundController {

	private static SoundController instance = null;

	private SoundSystem soundSystem;
	private String currentBgSound = "";

	private float masterVolume;
	
	private SoundController(){	
	}
	
	private void initialize(){
		Class libraryType;
		
		if (SoundSystem.libraryCompatible(LibraryJavaSound.class))
			libraryType = LibraryJavaSound.class; // Java Sound
		else if (SoundSystem.libraryCompatible(LibraryLWJGLOpenAL.class))
			libraryType = LibraryLWJGLOpenAL.class; // OpenAL
		else
			libraryType = Library.class; // "No Sound, Silent Mode"
		try {
			soundSystem = new SoundSystem(libraryType);
			SoundSystemConfig.setCodec("wav", CodecWav.class);
			SoundSystemConfig.setCodec("ogg", CodecJOrbis.class);
			masterVolume = soundSystem.getMasterVolume();
		} catch (SoundSystemException sse) {
			// Shouldn’t happen, but it is best to prepare for anything
			sse.printStackTrace();
			return;
		}
		// eliminate pauls code package to prefix sounds with the internal path
		// "sounds/"
		SoundSystemConfig.setSoundFilesPackage("");
	}
	
	public static SoundController getInstance(){
		if(instance  == null){
			instance = new SoundController();
			instance.initialize();
		}
		return instance;
	}
	
	public void cleanup(){
		if(soundSystem != null){
			soundSystem.stop(currentBgSound);
			soundSystem.interruptCommandThread();
			soundSystem.cleanup();
			
		}
	}
	
	public void playBackground(String uniqueSourceName, String filename, boolean doLoop){
		if(soundSystem != null){
			soundSystem.backgroundMusic( uniqueSourceName, filename, doLoop );
			currentBgSound = uniqueSourceName;
		}
	}
	
	/**
	 * Play a short effects sound and cleanup ressources
	 * @param filename	filename of the sound
	 * @param doLoop	loop on true
	 * @param pos		position of the sound
	 */
	public void playOnetime(String filename, boolean doLoop, Vector3f pos){
		if(soundSystem != null)
			soundSystem.quickPlay( false, filename, doLoop, pos.x, pos.y, pos.z, 
				SoundSystemConfig.ATTENUATION_ROLLOFF, SoundSystemConfig.getDefaultRolloff() );
	}
	
	/**
	 * Adds a sound to the sound manager and starts playing
	 * @param uniqueSourceName	id of the sound
	 * @param filename			file name of the file
	 * @param doLoop			loop on true
	 * @param pos				position in world coordinates
	 * @param volume			volume of the sound
	 */
	public void addAndPlay(String uniqueSourceName, String filename, boolean doLoop, Vector3f pos, float volume){
		addSound(uniqueSourceName, filename, doLoop, pos, volume);
		startSound(uniqueSourceName);
	}
	
	/**
	 * Adds a sound to the sound manager
	 * @param uniqueSourceName	id of the sound
	 * @param filename			file name of the file
	 * @param doLoop			loop on true
	 * @param pos				position in world coordinates
	 * @param volume			volume of the sound
	 */
	public void addSound(String uniqueSourceName, String filename, boolean doLoop, Vector3f pos, float volume){
		if(soundSystem != null){
			soundSystem.newSource(false, uniqueSourceName, filename, doLoop, pos.x, pos.y, pos.z, 
					SoundSystemConfig.ATTENUATION_ROLLOFF, SoundSystemConfig.getDefaultRolloff() );
			soundSystem.setVolume( uniqueSourceName, volume );
		}
	}
	
	/**
	 * Activates the sound and starts playing it
	 * 
	 * @param uniqueSourceName	id of the sound to be reactivated
	 */
	public void startSound(String uniqueSourceName){
		if(soundSystem != null){
			soundSystem.activate(uniqueSourceName);
			soundSystem.play(uniqueSourceName);
		}
	}
	
	/**
	 * Culls the sound (deactivating it)
	 * 
	 * @param uniqueSourceName	id of the sound
	 */
	public void stopSound(String uniqueSourceName){
		if(soundSystem != null)
			soundSystem.cull(uniqueSourceName);
	}
	
	/**
	 * Stops the sound and removes the resource from the soundmanager
	 * 
	 * @param uniqueSourceName	id of the sound
	 */
	public void removeSound(String uniqueSourceName){
		if(soundSystem != null){
			soundSystem.stop(uniqueSourceName);
			soundSystem.removeSource(uniqueSourceName);
		}
	}
	
	/**
	 * Sets the master volume (0.0 .. 1.0)
	 * @param vol	float value from 0.0f (mute) to 1.0f full
	 */
	public void setMaterVolume(float vol){
		if(soundSystem != null)
			soundSystem.setMasterVolume(vol);
		
	}
	
	/**
	 * Fades out the current background music and fades in the passed in file
	 * @param uniqueSourceName	the id of the sound file
	 * @param fileName			the file name of the sound file
	 * @param fadeOut			fade out time in mili seconds (5000 is 0.5 sec)
	 * @param fadeIn			fade in time in mili seconds (5000 is 0.5 sec)
	 */
	public void fadeBackground(String uniqueSourceName, String fileName, int fadeOut, int fadeIn){
		if(soundSystem != null) {
			soundSystem.fadeOutIn(uniqueSourceName, fileName, fadeOut, fadeIn);
			currentBgSound = uniqueSourceName;
		}
	}
	
	/**
	 * mute and unmute master sound volume
	 */
	public void toggleSound() {
		if (soundSystem != null) {
			if (masterVolume != 0.0f) {
				masterVolume = soundSystem.getMasterVolume();
				soundSystem.setMasterVolume(0.0f);
			} else {
				soundSystem.setMasterVolume(masterVolume);
				masterVolume = 0.0f;
			}
		}
	}
}
