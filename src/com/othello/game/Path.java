package com.othello.game;

import java.util.*;

/**
 * The path class gives a list of moves and jumps that are valid.
 * 
 * @author Derek King
 * @version 1.0.0
 */
public class Path
{
	/**
	 * The list of moves that make this path.
	 */
	private final List<Move> moves = new ArrayList<>();

	/**
	 * The constructor for the path allowing a list of moves to be passed in. Each move will be parsed for jumps and handled accordingly if there is a jump.
     *
	 * @param moves The list of moves that this class should be initialized with.
	 */
    public Path(List<Move> moves)
    {
        moves.forEach(this::addMove);
    }

    /**
     * If there are no moves in the path yet then an empty list will be looped through.
     */
    public Path()
    {
        this(Collections.emptyList());
    }

    /**
     * This method will add the move to the list of moves and the jump to the list of jumps if it is a jump.
     *
     * @param move The move that should be added to this path.
     */
    public void addMove(Move move)
    {
        moves.add(move);
    }

    /**
     * This is used when iterating over the path and will remove the head of the move list.
     */
    public void step()
    {
        moves.remove(0);
    }

    /**
     * A getter for the list of moves in the path.
     * 
     * @return The list of moves in the path.
     */
    public List<Move> getMoves()
    {
        return moves;
    }
    
    public Move getLastMove()
    {
        if (moves.size() == 0)
        {
            return null;
        }

        return moves.get(moves.size() - 1);
    }

    /**
     * This method will create a copy of the path.
     * 
     * @return An exact copy of the path.
     */
    public Path copy()
    {
        return new Path(this.getMoves());
    }
    
    public boolean containsMove(Move move)
    {
    	for (Move currentMove : moves)
    	{
    		if (currentMove.equals(move)) return true;
    	}
    	return false;
    }
}
