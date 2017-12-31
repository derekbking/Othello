package com.othello.animation;

import java.util.*;

/**
 * This class will call all of the registered animations when the should be called. It has the ability to run them
 * concurrently or run one at a time depending on what the animation is for.
 * 
 * @author Derek King
 * @author Ethan Chase
 * @version 1.0.0
 */
public class AnimationManager extends Thread
{
	/**
	 * A list of animations. This list only stores non concurrent animations.
	 */
    private final List<Animation> animations = new ArrayList<>();
    /**
     * This is used to prevent concurrent modification exceptions.
     */
    private final List<Animation> animationQueue = new ArrayList<>();
    /**
     * This is used to prevent concurrent modification exceptions.
     */
    private final List<Animation> animationRemovalQueue = new ArrayList<>();
    
    /**
     * The main method of this class. It is always running and will call animations as needed.
     */
    @Override
    public void run()
    {
        while (true)
        {
            synchronized (this)
            {
                animations.addAll(animationQueue);
                animations.removeAll(animationRemovalQueue);

                animationQueue.clear();
                animationRemovalQueue.clear();

                animations.forEach(Animation::run);
            }
        }
    }

    /**
     * The list of components could be in the process of being iterated over so this method is synchronized the verify that
     * list is not currently being used.
     * 
     * @param animation The animation that should be added to the animation queue.
     */
    public synchronized void animate(Animation animation)
    {
    	if (!animationQueue.contains(animation))
    	{
    		animationQueue.add(animation);
    	}
    }

    /**
     * If an animation should be stopped or removed because it is complete this method should be called. Again this is
     * synchronized because the main loop of this class could be using the list already and a concurrent modification
     * exception would be produced.
     * 
     * @param animation The animation that will be canceled.
     */
    public synchronized void cancelAnimation(Animation animation)
    {
    	if (animations.contains(animation))
    	{
            animationRemovalQueue.add(animation);
    	}
    }
}
