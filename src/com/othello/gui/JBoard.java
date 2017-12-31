package com.othello.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.SwingUtilities;

import com.othello.Othello;
import com.othello.animation.Animation;
import com.othello.animation.Callback;
import com.othello.animation.impl.MoveAnimation;
import com.othello.game.Board;
import com.othello.game.Piece;
import com.othello.game.Team;
import com.othello.game.Vector2D;

public class JBoard extends JComponent
{
	private static final double RIGHT_MARGIN = .20f;

	private final Board board;

	private final Map<Piece, MoveAnimation> animatingPieces = new HashMap<>();

	public JBoard(Board board)
	{
		this.board = board;
		new MouseUtil();
	}

	@Override
	public Dimension getPreferredSize() {
		return new Dimension(900, 720);
	}

	@Override
	public void paintComponent(Graphics g)
	{
		((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		paintBoard(g);
		paintInfo(g);
		paintPieces(g);
		paintValidMoves(g);
		paintWinner(g);
	}

	public Map<Piece, MoveAnimation> getAnimatingPieces()
	{
		return animatingPieces;
	}

	private void paintBoard(Graphics g)
	{
		for (Point point : board.getPoints())
		{
			drawSlot(g, point);
		}
	}

	private void paintPieces(Graphics g)
	{
		synchronized (board.getPointToPieces())
		{
			board.getPointToPieces().entrySet().stream().filter(entry -> !(animatingPieces.containsKey(entry.getValue()) || entry.getValue().hasNextAnimation())).forEach(entry ->
			{
				g.setColor(entry.getValue().getTeam().getColor());
				drawPiece(g, (Point) entry.getKey().clone());
			});
		}

		for (Point point : board.getPoints())
		{
			Piece piece = board.getPiece(point);

			if (piece == null) continue;

			queueNextAnimation(piece);

			if (animatingPieces.containsKey(piece))
			{
				MoveAnimation animation = animatingPieces.get(piece);

				g.setColor(animation.getTeam().getColor());
				paintPiece(g, animation.getLocation().getX(), animation.getLocation().getY(), getPieceSize(), getPieceSize());
				SwingUtilities.invokeLater(this::repaint);
			}
		}
	}
	
	private void paintValidMoves(Graphics g)
	{
		synchronized (board.getRules().getValidMoves())
		{
			for (Point point : board.getRules().getValidMoves())
			{
				System.out.println("Point: " + point);
				g.setColor(new Color(20, 20, 20, 25));
				drawPiece(g, (Point) point.clone());
			}
		}
	}

	private void drawSlot(Graphics g, Point point)
	{
		//Because the top left point of the board is (1, 1) we need to translate all points to the left and up by one so that they are all in the correct position.
		//The point being used in this method is mutable, but because the board class serves a new point and not the one used in the map it does not matter if the object is transformed.
		point.translate(-1, -1);

		g.setColor(new Color(19, 127, 63));
		g.fillRect((int) ((point.getX()) * getSlotSize()), (int) ((point.getY()) * getSlotSize()), (int) getSlotSize(), (int) getSlotSize());
		g.setColor(Color.WHITE);
		g.drawRect((int) ((point.getX()) * getSlotSize()), (int) ((point.getY()) * getSlotSize()), (int) getSlotSize(), (int) getSlotSize());
	}

	private void drawPiece(Graphics g, Point point)
	{
		point.translate(-1, -1);

		double offset = (getSlotSize() - getPieceSize()) / 2;
		double x = point.getX() * getSlotSize() + offset;
		double y = point.getY() * getSlotSize() + offset;

		paintPiece(g, (int) (x), (int) (y), (int) getPieceSize(), (int) getPieceSize());
	}

	private void paintPiece(Graphics g, double x, double y, double width, double height)
	{
		g.fillOval((int) x, (int) y, (int) width, (int) height);
	}

	private void queueNextAnimation(Piece piece)
	{
		if (!animatingPieces.containsKey(piece) && piece.hasNextAnimation())
		{
			Animation animation = piece.getNextAnimation();

			animation.addCallback(new Callback()
			{
				@Override
				public void onComplete()
				{
					animatingPieces.remove(piece);
					queueNextAnimation(piece);
					SwingUtilities.invokeLater(JBoard.this::repaint);
				}

				@Override
				public void onStart() {}
			});

			animatingPieces.put(piece, (MoveAnimation) animation);
			Othello.getInstance().getAnimationManager().animate(animation);
			SwingUtilities.invokeLater(this::repaint);
		}
	}

	private void paintInfo(Graphics g)
	{
		g.setColor(new Color(19, 127, 63));
		g.fillRect((int) getBoardWidth(), 0, (int) getInfoWidth(), (int) getTotalHeight());

		paintTeamInfo(g, Team.WHITE);
		paintTeamInfo(g, Team.BLACK);
	}

	private void paintWinner(Graphics g)
	{
		Team winner = board.getWinner();

		if (winner != null)
		{
			String string = "Team " + (Team.WHITE == winner ? "white" : "black") + " won";
			g.setFont(new Font("Lato", 0, 20));

			double width = g.getFontMetrics().stringWidth(string);
			double height = g.getFontMetrics().getHeight();

			g.setColor(Color.BLACK);
			width *= 1.5;
			height *= 1.75;
			g.fillOval((int) ((getBoardWidth() / 2) - (width / 2)) - 2, (int) ((getTotalHeight() / 2) - (height / 2)) - 2, (int) width + 4, (int) height + 4);
			g.setColor(Color.WHITE);
			g.fillOval((int) ((getBoardWidth() / 2) - (width / 2)), (int) ((getTotalHeight() / 2) - (height / 2)), (int) width, (int) height);
			width /= 1.5;
			height /= 1.75;
			g.setColor(Color.black);
			g.drawString(string, (int) ((getBoardWidth() / 2) - (width / 2)), (int) ((getTotalHeight() / 2) + (height / 4)));
		}
	}

	private void paintTeamInfo(Graphics g, Team team)
	{
		Color oppositeColor = team == Team.WHITE ? Team.BLACK.getColor() : Team.WHITE.getColor();
		Team turn = board.getTurn();
		String text = String.valueOf(team == Team.WHITE ? board.getWhite() : board.getBlack());
		double size = getInfoWidth() / 2;
		double y = (Team.BLACK == team ? (getTotalHeight() - (getTotalHeight() * .05) - size) : (getTotalHeight() * .05));

		if (turn == team)
		{
			g.setColor(new Color(0, 255, 0));
			paintPiece(g, (getBoardWidth() + (getInfoWidth() - size) / 2) - 2, y - 2, (int) size + 4, (int) size + 4);
		}

		g.setColor(team.getColor());
		paintPiece(g, getBoardWidth() + (getInfoWidth() - size) / 2, y, (int) size, (int) size);
		g.setColor(oppositeColor);
		g.setFont(new Font("Lato", 0, turn == team ? (int) ((size / 4) * 1.25) : (int) (size / 4)));

		g.drawString(text, (int) (getBoardWidth() + ((getInfoWidth() - size) / 2) + (size / 2) - (g.getFontMetrics().stringWidth(text) / 2)), (int) (y + (size / 2) + (g.getFontMetrics().getHeight() / 4)));
	}

	public double getPieceSize()
	{
		Dimension dimension = getSize();

		return Piece.getPieceSize(new Dimension((int) (dimension.getWidth() * (1 - RIGHT_MARGIN)), (int) (dimension.getHeight())), board.getWidth());
	}

	/**
	 * Will return the size of the slot that the pieces go in, this slot is 25% bigger than the pieces size.
	 * 
	 * @return The size of the slots on the board.
	 */
	public double getSlotSize()
	{
		return getPieceSize() * 1.25;
	}

	public double getInfoWidth()
	{
		return getTotalWidth() - getBoardWidth();
	}

	public double getBoardWidth()
	{
		return getTotalWidth() * (1 - RIGHT_MARGIN);
	}

	public double getTotalWidth()
	{
		return (Math.min(getSize().getWidth(), getSize().getHeight() / (1 - RIGHT_MARGIN)));
	}

	public double getTotalHeight()
	{
		return (Math.min(getSize().getWidth() * (1 - RIGHT_MARGIN), getSize().getHeight()));
	}

	public Vector2D getGuiCoordsFromPoint(Point point)
	{
		return new Vector2D((point.getX() - 1) * getSlotSize(), (point.getY() - 1) * getSlotSize());
	}

	public Point getPointFromGuiCoords(int x, int y)
	{
		return new Point((int) (x / (getSlotSize())) + 1, (int) (y / (getSlotSize()) + 1));
	}

	class MouseUtil implements MouseListener
	{
		private Point pendingPoint;

		public MouseUtil()
		{
			addMouseListener(this);
		}

		@Override
		public void mouseClicked(MouseEvent e)
		{
		}

		@Override
		public void mousePressed(MouseEvent e)
		{
			pendingPoint = getPointFromGuiCoords(e.getX(), e.getY());
		}

		@Override
		public void mouseReleased(MouseEvent e)
		{
			if (board.getWinner() != null)
			{
				Othello.getInstance().restart();
			}

			if (pendingPoint.equals(getPointFromGuiCoords(e.getX(), e.getY())))
			{
				board.move(pendingPoint);
			}
			pendingPoint = null;
		}

		@Override
		public void mouseEntered(MouseEvent e)
		{
		}

		@Override
		public void mouseExited(MouseEvent e)
		{
		}
	}
}
