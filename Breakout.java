/*
 * File: Breakout.java
 * -------------------
 * Name:
 * Section Leader:
 * 
 * This file will eventually implement the game of Breakout.
 */

import acm.graphics.*;
import acm.program.*;
import acm.util.*;
import java.applet.*;
import java.awt.*;
import java.awt.event.*;

public class Breakout extends GraphicsProgram {

/** Width and height of application window in pixels */
	public static final int APPLICATION_WIDTH = 400;
	public static final int APPLICATION_HEIGHT = 600;

/** Dimensions of game board (usually the same) */
	private static final int WIDTH = APPLICATION_WIDTH;
	private static final int HEIGHT = APPLICATION_HEIGHT;

/** Dimensions of the paddle */
	private static final int PADDLE_WIDTH = 60;
	private static final int PADDLE_HEIGHT = 10;

/** Offset of the paddle up from the bottom */
	private static final int PADDLE_Y_OFFSET = 30;

/** Number of bricks per row */
	private static final int NBRICKS_PER_ROW = 3;

/** Number of rows of bricks */
	private static final int NBRICK_ROWS = 3;

/** Separation between bricks */
	private static final int BRICK_SEP = 4;

/** Width of a brick */
	private static final int BRICK_WIDTH =
	  (WIDTH - (NBRICKS_PER_ROW - 1) * BRICK_SEP) / NBRICKS_PER_ROW;

/** Height of a brick */
	private static final int BRICK_HEIGHT = 8;

/** Radius of the ball in pixels */
	private static final int BALL_RADIUS = 10;

/** Offset of the top brick row from the top */
	private static final int BRICK_Y_OFFSET = 70;

/** Number of turns */
	private static final int NTURNS = 3;
	
/** Ball velocity at y axis */
	private static final int YVEL = 3;

/** Runs the Breakout program. */
	public void run() {
		/* You fill this in, along with any subsidiary methods */
		setup();
/*		while (!gameOver()) {
			
		} */
		while (!gameOver) {
			waitForClick();
			setupBall();
			while (true) {
				int i = turnLeft;
				moveBall();
				if (i != turnLeft) {
					remove(ball);
					ball = null;
					break;
				}
				checkForCollision();
				if (brickLeft == 0) break;
				pause(10);
			}
			if (turnLeft == 0 || brickLeft == 0) {
				gameOver = true;
				removeAll();			
			}
		}
		GLabel lose = new GLabel("GAME OVER");
		GLabel win = new GLabel("WIN!!!");
		lose.setFont("Serif-24");
		win.setFont("Serif-24");
		if (turnLeft == 0) {
			add(lose, (WIDTH - lose.getWidth()) / 2, (HEIGHT + lose.getAscent()) / 2);
		} else if (brickLeft == 0) {
			add(win, (WIDTH - win.getWidth()) / 2, (HEIGHT + win.getAscent()) / 2);
		}
		
		
	}
	/**
	 *Setup the bricks and paddle
	 */
	private void setup() {
		setupAppWindow();
		setupBricks();
		setupPaddle();
		addMouseListeners();
	}
	
	/**
	 * Setup application windows
	 */
	private void setupAppWindow() {
		resize(APPLICATION_WIDTH, APPLICATION_HEIGHT);
	}
	
	
	/**
	 * Setup bricks
	 */
	private void setupBricks() {
		for (int i = 0; i < NBRICK_ROWS; i++) {
			for (int j =0; j < NBRICKS_PER_ROW; j++) {
				GRect rect = new GRect(BRICK_WIDTH, BRICK_HEIGHT);
				rect.setFilled(true);
				add(rect, 
					BRICK_SEP / 2 + j * (BRICK_WIDTH + BRICK_SEP), 
					BRICK_Y_OFFSET + i * (BRICK_HEIGHT + BRICK_SEP));
				switch (i / 2) {
				case 0: rect.setColor(Color.RED);
						break;
				case 1: rect.setColor(Color.ORANGE);
						break;
				case 2: rect.setColor(Color.YELLOW);
						break;
				case 3: rect.setColor(Color.GREEN);
						break;
				case 4: rect.setColor(Color.CYAN);
						break;
				}
			}
			
		}
	}
	
	/**
	 * Setup paddle
	 */
	private void setupPaddle() {
		paddle = new GRect(PADDLE_WIDTH, PADDLE_HEIGHT);
		paddle.setFilled(true);
		add(paddle, (WIDTH - PADDLE_WIDTH) / 2, (HEIGHT - PADDLE_Y_OFFSET));		
	}
	
	/**
	 * record mouse x position when mouse enter the canvas
	 */
	public void mouseEntered(MouseEvent e) {
		lastX = e.getX();
	}
	
	
	/**
	 * Make paddle follow mouse and only move in x axis
	 */
	public void mouseMoved(MouseEvent e) {
		if ((paddle.getX() + e.getX() - lastX) >= 0 &&
			 (paddle.getX() + e.getX() - lastX) <= WIDTH - PADDLE_WIDTH) {
		paddle.move(e.getX() - lastX, 0);
		lastX = e.getX();
		}
	}
	
	/**
	 * Set the ball at random vx
	 */
	private void setupBall() {
		if (ball == null) {
			ball = new GOval(2 * BALL_RADIUS, 2 * BALL_RADIUS);
			ball.setFilled(true);
			add(ball, (WIDTH - 2 * BALL_RADIUS) / 2, HEIGHT - PADDLE_Y_OFFSET - 2 * BALL_RADIUS);
			vx = rgen.nextDouble(1.0, 3.0);
			vy = YVEL;
			if (rgen.nextBoolean(0.5)) vx = -vx;
		}
	}
	
	/**
	 * move ball
	 */
	private void moveBall() {
		ball.move(vx, -vy);	
		checkForWall();
	}
	
	/**
	 * Check if ball hit the wall. Then ball bouncing.
	 */
	private void checkForWall() {
		if (ball.getX() < 0) {
			vx = -vx;
		} else if (ball.getX() > WIDTH - 2 * BALL_RADIUS) {
			vx = - vx;
		} else if (ball.getY() < 0) {
			vy = -vy;
		} else if (ball.getY() > HEIGHT - 2 * BALL_RADIUS) {
			turnLeft--;
		}
	}
	
	/**
	 * Check if ball has collision with bricks. 
	 *
	 */
	private void checkForCollision() {
		GObject collider;		//Collision point
		collider = getCollidingObject();
		if (collider != null) {
			if (collider == paddle) {
				vy = -vy;
				double diff = ball.getY() + 2 * BALL_RADIUS - paddle.getY();
				ball.move(0, -diff);
			} else {
				remove(collider);
				vy = -vy;
				brickLeft --;
			}
		}
	}
	
	/**
	 * Get colliding object. Check 4 points of the ball outline
	 * (x, y)
	 * (x + 2r, y)
	 * (x, y + 2r)
	 * (x + 2r, y + 2r)
	 */
	private GObject getCollidingObject() {
		while (true) {
			if (getElementAt(ball.getX(), ball.getY()) != null) {
				return getElementAt(ball.getX(), ball.getY());
			} else if (getElementAt(ball.getX() + 2 * BALL_RADIUS, ball.getY()) != null) {
				return getElementAt(ball.getX() + 2 * BALL_RADIUS, ball.getY());
			} else if (getElementAt(ball.getX(), ball.getY() + 2 * BALL_RADIUS) != null) {
				return getElementAt(ball.getX(), ball.getY() + 2 * BALL_RADIUS);
			} else if (getElementAt(ball.getX() + 2 * BALL_RADIUS, ball.getY() + 2 * BALL_RADIUS) != null) {
				return getElementAt(ball.getX() + 2 * BALL_RADIUS, ball.getY() + 2 * BALL_RADIUS);
			} else return null;
		}
	}
	
	/** instance variable*/
	private RandomGenerator rgen = RandomGenerator.getInstance();		/* random generator*/
	private GRect paddle;		/* paddle*/
	private GOval ball;			/* ball*/
	private double vx;		/* ball's x axis velocity*/
	private double vy;		/* ball's y axis velocity*/
	private double lastX;		/* mouse last x position*/
	private boolean gameOver = false;		/* gameover flag*/
	private int turnLeft = NTURNS;		/* how many turn left*/
	private int brickLeft = NBRICKS_PER_ROW * NBRICK_ROWS;
	//private GRect brick;

}
