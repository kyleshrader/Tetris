package com.github.fright01.game;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Block {
	public int x;
	public int y;
	private Color color;
	private int[] pixels;
	private static String imagePath = "/block.png";
	private static BufferedImage image;
	private static int image_width;
	private static int image_height;

	public Block (Color color) {
		this.color = color;
		this.x = -1;
		this.y = -1;
		if (image == null) loadImage();
		initPixels();
	}

	private void initPixels () {
		pixels = new int[image_width * image_height];
		for(int y = 0; y < image_height; y++)
			for(int x = 0; x < image_width; x++)
				switch(image.getRGB(x, y) & 0xFF / (255/3)) {
					default:
					case 0:
						pixels[x + y*image_width] = Color.black.getRGB();
						break;
					case 1:
						pixels[x+y*image_width] = getDarker(color, 20).getRGB();
						break;
					case 2:
						pixels[x+y*image_width] = color.getRGB();
						break;
					case 3:
						pixels[x+y*image_width] = getLighter(color, 20).getRGB();
						break;
				}
	}
	
	public int[] getPixels() {
		return pixels;
	}

	private static void loadImage () {
		try {
			image = ImageIO.read(Block.class.getResourceAsStream(imagePath));
			image_width = image.getWidth();
			image_height = image.getHeight();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static int getImageWidth () {
		if (image == null) loadImage();
		return image_width;
	}

	public static int getImageHeight () {
		if (image == null) loadImage();
		return image_height;
	}
	
	private Color getDarker(Color color, int diff) {
		int rr = color.getRed();
		int gg = color.getGreen();
		int bb = color.getBlue();
		if(rr >= diff) rr -= diff;
		if(gg >= diff) gg -= diff;
		if(bb >= diff) bb -= diff;
		return new Color(rr << 16 | gg << 8 | bb);
	}
	
	private Color getLighter(Color color, int diff) {
		int rr = color.getRed();
		int gg = color.getGreen();
		int bb = color.getBlue();
		if(rr <= 255 - diff) rr += diff;
		if(gg <= 255 - diff) gg += diff;
		if(bb <= 255 - diff) bb += diff;
		return new Color(rr << 16 | gg << 8 | bb);
	}
}