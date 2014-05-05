package com.github.fright01.game;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;

public class Game extends Canvas implements Runnable {
	private static int NANOS_PER_SECOND = 1000000000;

	// Game Logic
	private boolean running = false;
	private boolean debug = false;

	// Game Settings
	private static final int BOARD_WIDTH = 10;
	private static final int BOARD_HEIGHT = 20;
	private static final String GAME_NAME = "Testris";

	// Display
	private static final int SCREEN_WIDTH = 60;
	private static final int SCREEN_HEIGHT = 120;
	private static final int SCALE = 4;
	private JFrame frame;
	private Dimension screen_size = new Dimension(SCREEN_WIDTH * SCALE, SCREEN_HEIGHT * SCALE);
	private Screen screen;
	private BufferedImage image = new BufferedImage(SCREEN_WIDTH, SCREEN_HEIGHT, BufferedImage.TYPE_INT_RGB);
	private int[] pixels = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();
	private List<Color> colors;
	private int colorPtr = 0;

	// Game Variables
	private int state = 0;
	private int speed;
	private int score;
	private Piece currentPiece;
	private Piece nextPiece; // Piece
	private Piece swapPiece;
	private List<int[][]> shapes = new ArrayList<int[][]>();
	private Board board;
	private InputHandler input;

	// Timers
	private int totalTicks;
	private long roundStart;

	public Game () {
		this.setMinimumSize(screen_size);
		this.setPreferredSize(screen_size);
		this.setMaximumSize(screen_size);

		frame = new JFrame(GAME_NAME);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		frame.setLayout(new BorderLayout());
		frame.add(this, BorderLayout.CENTER);
		// frame.setUndecorated(true);
		frame.setResizable(false);
		frame.pack();

		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}

	private void init () {
		board = new Board(BOARD_WIDTH, BOARD_HEIGHT);
		input = new InputHandler(this);
		screen = new Screen(SCREEN_WIDTH, SCREEN_HEIGHT);
		initColors();
		initShapes();
	}

	private void initColors () {
		colors = new ArrayList<Color>();
		for (int i = 0; i < (360 / 30); i++) {
			colors.add(Color.getHSBColor(i * 30f / 360f, 0.85f, 0.85f));
		}
	}

	private void initShapes () {
		shapes.add(new int[][] { { 1, 0 }, { 1, 0 }, { 1, 1 } }); // L
		shapes.add(new int[][] { { 0, 1 }, { 0, 1 }, { 1, 1 } }); // Flipped L
		shapes.add(new int[][] { { 1, 1, 0 }, { 0, 1, 1 } });     // Z
		shapes.add(new int[][] { { 0, 1, 1 }, { 1, 1, 0 } });     // Flipped Z
		shapes.add(new int[][] { { 1 }, { 1 }, { 1 }, { 1 } });   // Line
		shapes.add(new int[][] { { 1, 1 }, { 1, 1 } });           // Square
		shapes.add(new int[][] { { 0, 1, 0 }, { 1, 1, 1 } });     // T
	}

	public void run () {
		init();

		final int TICKS_PER_SECOND = 60;
		final double NANOS_PER_TICK = (double) (NANOS_PER_SECOND) / (double) (TICKS_PER_SECOND);
		long lastTick = System.nanoTime();

		int passes = 0;
		int ticks = 0;
		int frames = 0;
		long lastReset = System.nanoTime();
		double delta = 0;
		while (running) {
			boolean ticked = false;

			long now = System.nanoTime();
			delta += (now - lastTick) / NANOS_PER_TICK;
			lastTick = now;
			passes++;

			if (debug) {
				ticked = true;
				try {
					Thread.sleep(1);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

			while (delta >= 1) {
				delta--;
				ticks++;
				ticked = true;
				tick();
			}

			if (ticked) {
				frames++;
				render();
			}

			if (System.nanoTime() - lastReset > NANOS_PER_SECOND) {
				System.out.println(String.format("Passes: %d, Ticks: %d, Frames: %d", passes, ticks, frames));
				lastReset = System.nanoTime();
				passes = 0;
				ticks = 0;
				frames = 0;
			}
		}
	}

	private void tick () {
		totalTicks++;
		switch (state) {
			default:
			case 0: // New Game
				initRound();
				state = 1;
				break;
			case 1: // Play Game
				tick_playing();
				break;
		}
	}

	private void initRound () {
		board.clear();
		speed = 1;
		score = 0;
		currentPiece = getRandomPiece();
		nextPiece = getRandomPiece();
		swapPiece = null;
		roundStart = System.nanoTime();
		currentPiece.x = board.getWidth() / 2 - 1;
		currentPiece.y = -2;
	}

	// The last seen state of the keys pressed value.
	private boolean lastState_rotateRight = false;
	private boolean lastState_rotateLeft = false;
	private boolean lastState_right = false;
	private boolean lastState_left = false;
	private boolean lastState_down = false;
	private boolean lastState_drop = false;
	private boolean lastState_swap = false;
	private boolean lastState_menu = false;

	private void tick_playing () {
		if (totalTicks++ % 60 == 0) {
			if (!board.willCollide(currentPiece, 0, 1)) {
				currentPiece.moveDown(1);
			} else {
				destroyCurrentPiece();
			}
		}

		int ySpeed = 0;
		// Down keypress
		if (!lastState_down) {
			if (input.down.isPressed()) {
				lastState_down = true;
				ySpeed += 1;
			}
		} else if (!input.down.isPressed()) lastState_down = false;
		if (ySpeed != 0) {
			if (!board.willCollide(currentPiece, 0, ySpeed)) {
				currentPiece.moveDown(ySpeed);
			} else {
				destroyCurrentPiece();
			}
		}

		int xSpeed = 0;
		// Right keypress
		if (!lastState_right) {
			if (input.right.isPressed()) {
				lastState_right = true;
				xSpeed += 1;
			}
		} else if (!input.right.isPressed()) lastState_right = false;

		// Left keypress
		if (!lastState_left) {
			if (input.left.isPressed()) {
				lastState_left = true;
				xSpeed += -1;
			}
		} else if (!input.left.isPressed()) lastState_left = false;

		if (xSpeed != 0 && !board.willCollide(currentPiece, xSpeed, 0)) {
			currentPiece.moveLeftRight(xSpeed);
		}

		// Rotate-Right keypress
		if (!lastState_rotateRight) {
			if (input.rotateRight.isPressed()) {
				lastState_rotateRight = true;
				Piece p = new Piece(currentPiece);
				p.rotateClockwise();
				if (!board.willCollide(p, 0, 0)) currentPiece.rotateClockwise();
			}
		} else if (!input.rotateRight.isPressed()) lastState_rotateRight = false;

		// Drop keypress
		if (!lastState_drop) {
			if (input.drop.isPressed()) {
				lastState_drop = true;
				while (!board.willCollide(currentPiece, 0, 1)) {
					currentPiece.moveDown(1);
				}
				destroyCurrentPiece();
			}
		} else if (!input.drop.isPressed()) lastState_drop = false;
	}
	
	private void destroyCurrentPiece() {

		if(board.addPiece(currentPiece))
			board.clear();
		currentPiece = nextPiece;
		nextPiece = getRandomPiece();
		currentPiece.x = board.getWidth() / 2 - 1;
		currentPiece.y = -2;
	}

	private Piece getRandomPiece () {
		return new Piece(colors.get(colorPtr++ % colors.size()), shapes.get((int) (System.currentTimeMillis() % shapes.size())));
	}

	private void render () {
		BufferStrategy bs = this.getBufferStrategy();
		if (bs == null) {
			createBufferStrategy(3);
			return;
		}

		board.render(screen);
		if (currentPiece != null) {
			currentPiece.render(screen);
		}

		for (int i = 0; i < screen.pixels.length; i++) {
			pixels[i] = screen.pixels[i];
		}

		Graphics g = bs.getDrawGraphics();
		g.drawImage(image, 0, 0, this.getWidth(), this.getHeight(), null);
		g.setColor(Color.white);
		screen.drawScore(g, board.getRowsCleared() * 10);
		g.dispose();
		bs.show();
	}

	public void start () {
		running = true;
		new Thread(this).start();
	}

	public void stop () {
		running = false;
	}

	public static void main (String[] args) {
		new Game().start();
	}

}
