package com.othello.animation.impl;

import com.othello.animation.Animation;
import com.othello.animation.Callback;
import com.othello.game.Team;
import com.othello.game.Vector2D;

/**
 * The move animation class extends animation and allows for other classes to access getLocation which is a point generated based on the progress the animation should have made based on the time from the start of the animation.
 * 
 * @author Derek King
 * @version 1.0.0
 */
public class MoveAnimation extends Animation
{
	/**
	 * How long the animation should take to animate from start to finish.
	 */
	private final long duration;
	
	/**
	 * How long the animation will wait before calling onComplete so that animations don't start immediately after the current one is completed.
	 */
	private final long delay;
	
	/**
	 * The starting vector of the animation
	 */
	private final Vector2D start;
	
	/**
	 * The end vector of the animation
	 */
	private final Vector2D end;
	
	/**
	 * The team of the piece being animated.
	 */
	private final Team team;
	
	/**
	 * How many pixels the animation should cover going along each axis.
	 */
	private final Vector2D delta;
	
	/**
	 * This location vector is updated every time the run method is called by the animation manager.
	 */
	private final Vector2D location;

	/**
	 * The constructor for the move animation class. Many of the arguments here are stored for use later additionally the totalDeltaX and totalDeltaY is calculated here.
	 * 
	 * @param duration How long the animation will last.
	 * @param delay How many milliseconds that will pass before the onComplete method is called for each of the callbacks.
	 * @param start The start position of the animation.
	 * @param end The end position of the animation.
	 * @param team The team this animation is being run for.
	 * @param callbacks An array of callbacks that will be called when the animation starts and ends.
	 */
	public MoveAnimation(long duration, long delay, Vector2D start, Vector2D end, Team team, Callback... callbacks)
	{
		super(callbacks);
		
		this.duration = duration;
		this.delay = delay;
		this.start = start;
		this.end = end;
		this.team = team;
		
		this.delta = new Vector2D(end.getX() - start.getX(), end.getY() - start.getY());
		this.location = new Vector2D(start.getX(), start.getY());
	}
	
	/**
	 * This method is called by the animation manager and allows the location variable to be updated.
	 */
	@Override
	public void run()
	{
		super.run();
		
		double progress = (System.currentTimeMillis() - (getStartTime() + delay)) / (float) duration;
        double x = (delta.getX() * Math.max(0.0, (Math.min(progress, 1.0)))) + start.getX();
        double y = (delta.getY() * Math.max(0.0, (Math.min(progress, 1.0)))) + start.getY();

        location.set(x, y);
        
        if (progress >= 1.0F)
        {
        	location.set(end);
        	onComplete();
        }
	}
	
	/**
	 * This method returns the most recent calculated location for the animation.
	 * 
	 * @return The current location of the animation.
	 */
	public Vector2D getLocation()
	{
		return location;
	}
    
	/**
	 * How long the animation should last.
	 * 
	 * @return The duration of the animation.
	 */
    public long getDuration()
    {
    	return duration;
    }

	/**
	 * The team used to get what color the animating piece should be rendering as.
	 *
	 * @return The team of the piece being animated.
	 */
	public Team getTeam()
    {
    	return team;
    }
}
