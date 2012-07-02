package com.l2client.animsystem;

import java.util.HashMap;

/**
 * The whole system is driven by read only Input variables, they are nothing
 * more than enumerations. @see InputProvider is a storage component to be used
 * between an application and the animation system to exchange input values for
 * an entity. The input Values you need would be implemented by creating your
 * enums for input constants and adding feeding them to the animation system.
 * The first value of the enum should be the initial/default value.
 * 
 */
public class InputProvider {
	/**
	 * This is a helper for resetting the input provider values all to none (and Acting.open)
	 *
	 */
	public static InputProvider NOINPUT = new InputProvider();

	private HashMap<Class, Enum> inputValues = new HashMap<Class, Enum>();

	@SuppressWarnings("unchecked")
	public final <T extends Enum<T>> T getInput(Class<T> enumType) {
		T ret = (T) inputValues.get(enumType);
		if (ret != null)
			return ret;
		else {
			return enumType.getEnumConstants()[0];
		}
	}

	public void setInput(Enum<?> e) {
		inputValues.put(e.getClass(), e);
	}

	public int hashCode() {
		return inputValues.hashCode();
	}

	public boolean equals(Object anObject) {
		if (this == anObject) {
			return true;
		}
		if (anObject instanceof InputProvider) {
			return inputValues.equals((InputProvider) anObject);
		}

		return false;
	}
}
