package com.othello.game;

import java.awt.Point;

public class BoardFactory
{
	public static Board createStandardBoard()
	{  
		int size = 8;
		
		Rules rules = new Rules();
		Board board = new Board(rules, Team.WHITE, size, size);
		
		rules.setBoard(board);
				
		board.placePiece(new Point(board.getWidth() / 2, board.getHeight() / 2), new Piece(Team.BLACK));
		board.placePiece(new Point((board.getWidth() / 2) + 1, board.getHeight() / 2), new Piece(Team.WHITE));
		board.placePiece(new Point(board.getWidth() / 2, (board.getHeight() / 2) + 1), new Piece(Team.WHITE));
		board.placePiece(new Point((board.getWidth() / 2) + 1, (board.getHeight() / 2) + 1), new Piece(Team.BLACK));
		
		return board;
	}
	
	public static Board createTestBoard()
	{  
		int size = 8;
		
		Rules rules = new Rules();
		Board board = new Board(rules, Team.WHITE, size, size);
		
		rules.setBoard(board);

		board.getPoints().stream().filter(point -> !(point.getX() == board.getWidth() / 2 && point.getY() == board.getHeight() / 2)).forEach(point ->
				board.placePiece(point, new Piece(point.getX() == 1 || point.getX() == board.getWidth() || point.getY() == 1 || point.getY() == board.getHeight() ? Team.WHITE : Team.BLACK)));

		return board;
	}
}
