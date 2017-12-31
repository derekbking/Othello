package com.othello.animation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.othello.Othello;

/**
 * This is the base animation class that all animations should extend. This class is abstract because the functionality of the animation has not been implemented in this class.
 * 
 * @author Derek King
 * @version 1.0.0
 */
public abstract class Animation implements Runnable
{
	/**
	 * A list of callbacks that will be called once the animation is completed.
	 */
	private final List<Callback> callbacks;
	
	private Callback finalCallback;
	
	/**
	 * This system time stored when the animation starts. This value is used when animating the object.
	 */
	private long start;
	
	/**
	 * The constructor for the animation.
	 * 
	 * @param callbacks The callbacks that will be called once the animation is complete.
	 */
	public Animation(Callback... callbacks)
	{
		this.callbacks = new ArrayList<>(Arrays.asList(callbacks));
	}

	/**
	 * Adds a callback to the list of callbacks that are called when the animations starts and finishes.
	 *
	 * @param callback The callback that should be executed upon the start and complete events.
	 */
	public void addCallback(Callback callback)
	{
		this.callbacks.add(callback);
	}
	
	public void setFinalCallback(Callback callback)
	{
		finalCallback = callback;
	}
	
	/**
	 * When this class is extended the derived classes should still called super.run so that the start time is stored and the callbacks receive the onStart event.
	 */
	@Override
	public void run()
	{
		if (start == 0)
		{
			onStart();
			start = System.currentTimeMillis();
		}
	}
	
	/**
	 * A getter for the time in milliseconds for the start time.
	 * 
	 * @return The time that the animation started.
	 */
	public long getStartTime()
	{
		return start;
	}

	/**
	 * This method calls the onStart method for the registered callbacks.
	 */
	protected void onStart()
	{
		callbacks.forEach(Callback::onStart);
	}
	
	/**
	 * This method will remove this animation from the animation manager and call the onComplete event in the callbacks.
	 */
	protected void onComplete()
	{
		callbacks.forEach(Callback::onComplete);
		if (finalCallback != null)
		{
	        finalCallback.onComplete();
		}
        Othello.getInstance().getAnimationManager().cancelAnimation(this);
	}
}
