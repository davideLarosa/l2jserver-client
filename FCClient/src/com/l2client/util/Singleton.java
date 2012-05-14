package com.l2client.util;


public final class Singleton {
	private static Singleton singleton = null;
	
	/**
	 * singleton private constructor
	 */
	private Singleton() {
		singleton = this;
	}

	/**
	 * Fetch the singleton instance (created in case not done so far)
	 * 
	 * @return The instance
	 */
	public static Singleton get() {
		if (singleton != null)
			return singleton;
		else {
			return new Singleton();
		}
	}
}
