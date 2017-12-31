package com.othello.game;

import java.awt.Color;

public enum Team
{
	BLACK(Color.BLACK),
	WHITE(Color.WHITE);
	
	private final Color color;
	
	Team(Color color)
	{
		this.color = color;
	}
	
	public Color getColor()
	{
		return color;
	}
}
