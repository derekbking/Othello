package com.othello.animation;

/**
 * Used mainly on animations to call code when the animations starts and finishes.
 * 
 * @author Derek King
 * @author Ethan Chase
 * @version 1.0.0
 */
public interface Callback
{
	/**
	 * A method that will be executed when the animation is completed.
	 */
    void onComplete();

	void onStart();
}
