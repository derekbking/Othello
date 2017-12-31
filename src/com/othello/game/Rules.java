package com.othello.game;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.othello.Othello;
import com.othello.animation.Callback;
import com.othello.animation.impl.MoveAnimation;
import com.othello.gui.JBoard;

public class Rules
{
	private Board board;
	private final Move[] directions =
		{
				new Move(1, 0),
				new Move(1, 1),
				new Move(0, 1),
				new Move(-1, 1),
				new Move(-1, 0),
				new Move(-1, -1),
				new Move(0, -1),
				new Move(1, -1)
		};
	
	private final List<Point> validMoves = new ArrayList<>();

	public void setBoard(Board board)
	{
		this.board = board;
	}

	public void updateValidMoves()
	{
		synchronized (getValidMoves())
		{
			validMoves.clear();

			for (Point point : board.getPoints())
			{
				if (!board.getRules().canPlace(board.getTurn(), point)) continue;
				
				validMoves.add(point);
			}
			
			System.out.println(validMoves.size());
		}
	}
	
	public List<Point> getValidMoves()
	{
		return validMoves;
	}
	
	public boolean canPlace(Team team, Point target)
	{
		return team == board.getTurn() && !(target.getX() < 0 || target.getX() > board.getWidth() || target.getY() > board.getHeight()) && !board.getPointToPieces().containsKey(target) && !getMoves(target).isEmpty();
	}

	private int getMoveLength(Point point, Move move, Team team)
	{
		int length = 0;
		Point lastPoint = point;
		while (true)
		{
			lastPoint = new Point((int) (lastPoint.getX() + move.getDx()), (int) (lastPoint.getY() + move.getDy()));
			Piece piece = board.getPiece(lastPoint);
			
			length++;
			
			if (piece == null) return -1;
			if (piece.getTeam() == team) return length;
		}
	}

	private Map<Move, Integer> getMoves(Point point)
	{
		Map<Move, Integer> validMoves = new HashMap<>();

		for (Move move : directions)
		{
			Point target = new Point((int) (point.getX() + move.getDx()), (int) (point.getY() + move.getDy()));
			Piece piece = board.getPiece(target);
			
			if (piece != null && piece.getTeam() != board.getTurn() && getMoveLength(point, move, board.getTurn()) >= 2)
			{
				validMoves.put(move, getMoveLength(point, move, board.getTurn()));
			}
		}

		if (validMoves.isEmpty())
		{
			board.updateCount();

			//board.setWinner(board.getWhite() > board.getBlack() ? Team.WHITE : Team.BLACK);
		}

		return validMoves;
	}
	
	public void runAI()
	{
		Point bestPoint = null;
		int flipped = -1;
		
		for (Point point : board.getPoints())
		{
			if (board.getPiece(point) != null) continue;
			
			int i = -1;
			
			for (Entry<Move, Integer> entry : getMoves(point).entrySet())
			{
				i += entry.getValue();
			}
			
			if (i > flipped)
			{
				flipped = i;
				bestPoint = point;
			}
		}
		
		System.out.println("Best: " + bestPoint);
		
		if (bestPoint != null)
		{
			board.move(bestPoint);
		}
	}

	public void move(Team team, Point target)
	{
		placePiece(team, target, 0);

		Map<Move, Integer> moves = getMoves(target);

		int index = 0;
		for (Entry<Move, Integer> move : moves.entrySet())
		{
			Point lastPoint = (Point) target.clone();
			for (int i = 0; i < move.getValue(); i++)
			{				
				lastPoint = new Point((int) (lastPoint.getX() + move.getKey().getDx()), (int) (lastPoint.getY() + move.getKey().getDy()));
				Piece piece = board.getPiece(lastPoint);

				if (piece != null && piece.getTeam() != board.getTurn())
				{
					final Team finalTeam = board.getTurn();
					final int finalIndex = index;

					placePiece(finalTeam, lastPoint, (finalIndex) * 45);
					piece.setTeam(finalTeam);
					index++;
				}
			}
		}
		Othello.getInstance().getLastBoard().repaint();
	}

	private void placePiece(Team team, Point target, long delay)
	{
		JBoard jBoard = Othello.getInstance().getLastBoard();
		Piece piece = board.getPiece(target);

		Vector2D targetPieceLocation = jBoard.getGuiCoordsFromPoint(target);
		targetPieceLocation.translate(((jBoard.getSlotSize() - Piece.getPieceSize(Othello.getInstance().getLastBoard().getSize(), board.getWidth())) / 2), ((jBoard.getSlotSize() - Piece.getPieceSize(Othello.getInstance().getLastBoard().getSize(), board.getWidth())) / 2));

		if (piece != null)
		{
			piece.queueAnimation(new MoveAnimation(275, delay, targetPieceLocation, getTeamSpawn(piece.getTeam()), piece.getTeam(), new Callback()
			{
				@Override
				public void onComplete()
				{
					board.setWinner(board.updateCount());
					Othello.getInstance().repaint();
				}

				@Override
				public void onStart() {}
			}));
			
			piece.setTeam(team);
			
			MoveAnimation moveAnimation = new MoveAnimation(275, piece == null ? delay : 0, getTeamSpawn(team), targetPieceLocation, team, new Callback()
			{
				@Override
				public void onComplete()
				{
					board.setWinner(board.updateCount());
					Othello.getInstance().repaint();
				}

				@Override
				public void onStart() {}
			});
			
			moveAnimation.setFinalCallback(new Callback()
			{
				@Override
				public void onComplete()
				{
					board.turnFinish(piece);
				}

				@Override
				public void onStart() {}
			});
			
			piece.queueAnimation(moveAnimation);
		}
		else
		{
			final Piece finalPiece = new Piece(team);
			MoveAnimation moveAnimation = new MoveAnimation(275, piece == null ? delay : 0, getTeamSpawn(team), targetPieceLocation, team, new Callback()
			{
				@Override
				public void onComplete()
				{
					board.setWinner(board.updateCount());
					Othello.getInstance().repaint();
				}

				@Override
				public void onStart() {}
			});
			
			moveAnimation.setFinalCallback(new Callback()
			{
				@Override
				public void onComplete()
				{
					board.turnFinish(finalPiece);
				}

				@Override
				public void onStart() {}
			});
			
			finalPiece.queueAnimation(moveAnimation);
			board.placePiece(target, finalPiece);
		}
	}

	private Vector2D getTeamSpawn(Team team)
	{
		double slotSize = Othello.getInstance().getLastBoard().getSlotSize();
		JBoard jBoard = Othello.getInstance().getLastBoard();
		
		return new Vector2D(jBoard.getBoardWidth() + (jBoard.getInfoWidth() - jBoard.getPieceSize()) / 2, slotSize * (team == Team.WHITE ? 0.5 : board.getHeight() - 1.5));
	}
}
