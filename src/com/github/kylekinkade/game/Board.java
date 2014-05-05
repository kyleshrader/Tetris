package com.github.fright01.game;

import java.awt.Color;

public class Board {
	private int width;
	private int height;
	private Block[] blocks;
	private int[] pixels;
	private Color backgroundColor = new Color(25, 25, 25);
	private int rowsCleared;

	public Board (int width, int height) {
		this.width = width;
		this.height = height;
		blocks = new Block[width * height];
		pixels = new int[(width * Block.getImageWidth()) * (height * Block.getImageHeight())];
		rowsCleared = 0;
		update();
	}

	public void destroyRow (int row) {
		if (row >= 0 && row < height) {
			for (int x = 0; x < width; x++) {
				removeBlock(x, row);
			}
			
			for(int y = row; y > 0; y--) {
				for(int x = 0; x < width; x++) {
					blocks[x + y * width] = blocks[x + (y - 1) * width];
				}
			}
			
			update();
		}
		rowsCleared++;
	}

	public void addBlock (Block block, int x, int y) {
		if (x >= 0 && x < width && y >= 0 && y < height) {
			blocks[x + y * width] = block;
			update();
			checkRow(y);
		}
	}

	public void removeBlock (int x, int y) {
		if (x >= 0 && x < width && y >= 0 && y < height) {
			blocks[x + y * width] = null;
			update();
		}
	}
	
	public void checkRow(int row) {
		for(int x = 0; x < width; x++) {
			if(blocks[x + row * width] == null) return;
		}
		destroyRow(row);
	}

	public void clear () {
		blocks = new Block[width * height];
		rowsCleared = 0;
		update();
	}

	public int[] getPixels () {
		return pixels;
	}

	public int getWidth () {
		return width;
	}

	public int getHeight () {
		return height;
	}
	
	public int getRowsCleared() {
		return rowsCleared;
	}

	public void render (Screen screen) {
		screen.drawBoard(this);
	}

	private void update () {
		
		
		// draw each block to the board, null blocks will be drawn as 'backgroundColor'.
		for (int yBlock = 0; yBlock < height; yBlock++) {
			for (int xBlock = 0; xBlock < width; xBlock++) {
				drawBlock(blocks[xBlock + yBlock * width], xBlock, yBlock);
			}
		}
	}

	private void drawBlock (Block block, int xPosition, int yPosition) {
		Color tempColor;
		for (int y = 0; y < Block.getImageHeight(); y++) {
			int yPixel = y + yPosition * Block.getImageHeight();

			for (int x = 0; x < Block.getImageWidth(); x++) {
				int xPixel = x + xPosition * Block.getImageWidth();

				if (block == null) tempColor = backgroundColor;
				else tempColor = new Color(block.getPixels()[x + y * Block.getImageWidth()]);
				if (tempColor.equals(Color.black)) tempColor = backgroundColor;

				pixels[(xPixel) + (yPixel * width * Block.getImageHeight())] = tempColor.getRGB();
			}
		}
	}

	public boolean willCollide (Piece piece, int xSpeed, int ySpeed) {
		for (int y = 0; y < piece.getBlocks().length; y++) {
			for (int x = 0; x < piece.getBlocks()[y].length; x++) {
				if(piece.getBlocks()[y][x] == null) continue;
				int xPos = piece.x + x + xSpeed;
				if (xPos < 0 || xPos >= width) return true;

				int yPos = piece.y + y + ySpeed;
				if (yPos >= height) return true;
				if(yPos < 0) continue;
				
				if (blocks[(xPos) + (yPos) * width] != null) return true;
			}
		}
		return false;
	}

	public boolean addPiece(Piece piece) {
		for (int y = 0; y < piece.getBlocks().length; y++) {
			for (int x = 0; x < piece.getBlocks()[y].length; x++) {
				if(piece.getBlocks()[y][x] == null) continue;
				addBlock(piece.getBlocks()[y][x], piece.x + x, piece.y + y);
				if(piece.y + y <= 0) return true;
			}
		}
		return false;
	}
}
