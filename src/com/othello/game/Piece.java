package com.othello.game;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;

import com.othello.animation.Animation;

public class Piece
{
	private final List<Animation> animationQueue = new ArrayList<>();

	private Team team;
	
	public Piece(Team team)
	{
		this.team = team;
	}
	
	public void setTeam(Team team)
	{
		this.team = team;
	}
	
	public Team getTeam()
	{
		return team;
	}
	public void queueAnimation(Animation animation)
	{
		animationQueue.add(animation);
	}
	
	public boolean hasNextAnimation()
	{
		return !animationQueue.isEmpty();
	}
	
	public Animation getNextAnimation()
	{
		if (animationQueue.isEmpty()) return null;
		
		Animation animation = animationQueue.get(0);
		animationQueue.remove(0);
		
		return animation;
	}

	public static double getPieceSize(Dimension boardSize, int boardLength)
	{
		return (Math.min(boardSize.getWidth(), boardSize.getHeight()) / boardLength) / 1.25;
	}
}
