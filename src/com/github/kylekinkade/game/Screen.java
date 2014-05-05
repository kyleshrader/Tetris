package com.github.fright01.game;

import java.awt.Color;
import java.awt.Graphics;

public class Screen {
	public int width;
	public int height;
	public int[] pixels;

	public Screen (int width, int height) {
		this.width = width;
		this.height = height;

		pixels = new int[width * height];
	}

	public void drawBoard (Board board) {
		for (int i = 0; i < board.getPixels().length; i++) {
			pixels[i] = board.getPixels()[i];
		}
	}
	
	public void drawScore(Graphics g, int score) {
		g.drawString(score+"", 10, 10);
	}

	public void drawBlock (Block block, int xPosition, int yPosition) {
		if (block == null) return;

		Color tempColor;
		for (int y = 0; y < Block.getImageHeight(); y++) {
			int yPixel = y + yPosition * Block.getImageHeight();

			for (int x = 0; x < Block.getImageWidth(); x++) {
				int xPixel = x + xPosition * Block.getImageWidth();
				if ((xPixel) + (yPixel * width) < 0 || (xPixel) + (yPixel * width) >= pixels.length) continue;
				tempColor = new Color(block.getPixels()[x + y * Block.getImageWidth()]);
				if (tempColor.equals(Color.black)) continue;

				pixels[(xPixel) + (yPixel * width)] = tempColor.getRGB();
			}
		}
	}
}
