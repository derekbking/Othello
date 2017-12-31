package com.othello;

import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GraphicsEnvironment;
import java.awt.Toolkit;
import java.io.IOException;
import java.net.URLClassLoader;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import com.othello.animation.AnimationManager;
import com.othello.game.Board;
import com.othello.game.BoardFactory;
import com.othello.gui.JBoard;

/**
 * The main class for Othello. A new JFrame will be created and the JBoard JComponent will be added to render the game into a window.
 *
 * @author Derek King
 * @version 1.0.0
 */
public class Othello extends JFrame
{
    /**
     * A reference to the main class in case the JFrame needs to be re-rendered from another class or any public variables need to be referenced.
     */
	private static Othello instance;

    /**
     * The main animation manager which runs on a separate thread from the main game. It will keep track of all of the running animations and run them.
     */
	private final AnimationManager animationManager = new AnimationManager();

    /**
     * Used to remove the old JBoard from the main JFrame and allows a new one to be created.
     */
	private JBoard lastBoard;

    /**
     * The constructor for the JFrame this will set the title of the window to the name of the game as well as initialize various classes needed for the game to function.
     *
     * @param name
     */
	public Othello(String name)
	{
		super(name);
		
		try
		{
			GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(Font.createFont(Font.TRUETYPE_FONT, URLClassLoader.getSystemResourceAsStream("Lato-Light.ttf")));
		}
		catch (FontFormatException | IOException e)
		{
			System.out.println("Unable to load font Lato-Light.ttf");
		}
		
		instance = this;
		animationManager.start();

		restart();

		setLocation((int) Toolkit.getDefaultToolkit().getScreenSize().getWidth() / 2 - getWidth() / 2, (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight() / 2 - getHeight() / 2);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setVisible(true);
	}

    /**
     * Allows this class to be accessed from any class even if they don't have a reference passed in by using Othello.getInstance()
     *
     * @return
     */
	public static Othello getInstance()
	{
		return instance;
	}

    /**
     * A getter for the animation manager.
     *
     * @return The main animation manager for the game.
     */
	public AnimationManager getAnimationManager()
	{
		return animationManager;
	}

    /**
     * A getter for the last JBoard that was added to the JFrame.
     *
     * @return The last JBoard added to the JFrame
     */
	public JBoard getLastBoard()
	{
		return lastBoard;
	}

    /**
     * Will reset the required variables and initialize new variables to start the game again.
     */
	public void restart()
	{
		if (lastBoard != null)
		{
			remove(lastBoard);
		}
		
		Board board = BoardFactory.createStandardBoard();
		lastBoard = new JBoard(board);

		add(lastBoard);
		pack();
	}

    /**
     * The main method for the game which will create a new instance of the Othello class.
     *
     * @param args No arguments parsed for this application.
     */
	public static void main(String... args)
	{
		SwingUtilities.invokeLater(() -> new Othello("Othello"));
	}
}
