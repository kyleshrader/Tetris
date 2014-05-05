package com.github.fright01.game;

import java.awt.Color;

public class Piece {
	private Block[][] blocks;
	public int x;
	public int y;
	public int width;
	public int height;

	public Piece (Color color, int[][] shape) {
		height = shape.length;
		width = shape[0].length;
		blocks = new Block[height][width];
		for (int y = 0; y < shape.length; y++)
			for (int x = 0; x < shape[y].length; x++)
				if (shape[y][x] == 1) blocks[y][x] = new Block(color);
				else blocks[y][x] = null;
		x = 0;
		y = 0;
	}

	public Piece (Piece piece) {
		this.blocks = piece.getBlocks();
		this.x = piece.x;
		this.y = piece.y;
		this.width = piece.width;
		this.height = piece.height;
	}

	public void rotateClockwise () {
		int h = blocks.length;
		int w = blocks[0].length;
		Block[][] newBlocks = new Block[w][h];
		for (int i = 0; i < w; i++) {
			for (int j = 0; j < h; j++) {
				newBlocks[i][j] = blocks[h - 1 - j][i];
			}
		}

		blocks = newBlocks;
	}

	public void rotateCounterClockwise () {
		int h = blocks.length;
		int w = blocks[0].length;
		Block[][] newBlocks = new Block[w][h];
		for (int i = 0; i < w; i++) {
			for (int j = 0; j < h; j++) {
				newBlocks[i][j] = blocks[j][w - 1 - i];
			}
		}

		blocks = newBlocks;
	}

	public void tick () {
		y++;
	}

	public Block[][] getBlocks () {
		return blocks;
	}

	public void render (Screen screen) {
		for (int y = 0; y < blocks.length; y++) {
			for (int x = 0; x < blocks[y].length; x++) {
				screen.drawBlock(blocks[y][x], this.x + x, this.y + y);
			}
		}
	}

	public void moveDown (int ySpeed) {
		y += ySpeed;
	}

	public void moveLeftRight (int xSpeed) {
		x += xSpeed;
	}
}