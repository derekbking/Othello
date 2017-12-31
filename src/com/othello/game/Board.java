package com.othello.game;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.othello.Othello;
import com.othello.animation.impl.MoveAnimation;

/**
 * This class keeps track of the state of the game such as how many pieces that black and white team have placed and where those pieces are located.
 *
 * @author Derek King
 * @version 1.0.0
 */
public class Board
{
	/**
	 * The amount of columns that the game will have. This value should not change once it's set in the constructor.
	 */
	private final int width;

	/**
	 * The amount of rows that the game will have. This value should not change once it's set in the constructor.
	 */
	private final int height;

	/**
	 * A reference to the rules class which will determine which moves are valid and 
	 */
	private final Rules rules;

	/**
	 * If a winner has been calculated then this variable will represent the team who was won and the JBoard class will see this change and display a message to the user.
	 */
	private Team winner;

	/**
	 * The team who is currently moving.
	 */
	private Team turn;

	/**
	 * A map that stores pieces a set point. The JBoard class uses this map to render the pieces on the screen.
	 */
	private final Map<Point, Piece> pointToPiece = new HashMap<>();

	/**
	 * The amount of white pieces on the board.
	 */
	private int white;

	/**
	 * The amount of black pieces on the board.
	 */
	private int black;
	
	private boolean running = false;

	public Board(Rules rules, Team startingTurn, int width, int height)
	{
		this.rules = rules;
		this.turn = startingTurn;
		this.width = width;
		this.height = height;
	}

	public void setWinner(Team winner)
	{
		this.winner = winner;
	}

	public Team getWinner()
	{
		return winner;
	}

	public void setTurn(Team team)
	{
		this.turn = team;
	}

	public Team getTurn()
	{
		return turn;
	}
	
	public void setRunning(boolean running)
	{
		this.running = running;
	}
	
	public boolean getRunning()
	{
		return running;
	}

	public List<Point> getPoints()
	{
		List<Point> points = new ArrayList<>();
		
		for (int x = 1; x <= width; x++)
		{
			for (int y = 1; y <= height; y++)
			{
				points.add(new Point(x, y));
			}
		}
		
		return points;
	}

	public Map<Point, Piece> getPointToPieces()
	{
		return pointToPiece;
	}
	
	public Piece placePiece(Point point, Piece piece)
	{
		synchronized (getPointToPieces())
		{
			pointToPiece.put(point, piece);
			updateCount();
			Othello.getInstance().repaint();
			
			return piece;
		}
	}
	
	public Piece getPiece(Point point)
	{
		if (pointToPiece.containsKey(point))
		{
			return pointToPiece.get(point);
		}
		return null;
	}
	
	public void move(Point target)
	{
		if (running) return;
		if (!rules.canPlace(getTurn(), target)) return;
		
		running = true;
		rules.move(getTurn(), target);
		updateCount();
	}
	
	public void turnFinish(Piece piece)
	{
		boolean containsTeam = false;
		
		for (Entry<Piece, MoveAnimation> entry : Othello.getInstance().getLastBoard().getAnimatingPieces().entrySet())
		{
			if (entry.getKey().getTeam() == piece.getTeam())
			{
				containsTeam = true;
				break;
			}
		}
		
		if (!containsTeam)
		{
			turn = turn == Team.BLACK ? Team.WHITE : Team.BLACK;
			rules.updateValidMoves();
			running = false;

			if (turn == Team.BLACK)
			{
				rules.runAI();
			}
		}
	}
	
	public Team updateCount()
	{
		white = 0;
		black = 0;
		for (Point point : getPoints())
		{
			Piece piece = getPointToPieces().get(point);
			
			if (piece == null) continue;
			
			if (piece.getTeam() == Team.WHITE) white++;
			else black ++;
		}
		
		if (white == 0) return Team.BLACK;
		if (black == 0) return Team.WHITE;
		
		if (white + black == width * height) return white > black ? Team.WHITE : Team.BLACK;
		
		return null;
	}
	
	public int getWhite()
	{
		return white;
	}
	
	public int getBlack()
	{
		return black;
	}
	
	public int getWidth()
	{
		return width;
	}
	
	public int getHeight()
	{
		return height;
	}
	
	public Rules getRules()
	{
		return rules;
	}
}
