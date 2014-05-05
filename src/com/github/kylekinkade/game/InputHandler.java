package com.github.fright01.game;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class InputHandler implements KeyListener {
	public Key rotateRight = new Key();
	public Key rotateLeft = new Key();
	public Key right = new Key();
	public Key left = new Key();
	public Key down = new Key();
	public Key drop = new Key();
	public Key swap = new Key();
	public Key menu = new Key();
	
	public InputHandler(Game game) {
		game.addKeyListener(this);
	}
	
	class Key {
		private int pressCount = 0;
		private boolean pressed = false;
		
		public void toggle(boolean pressed) {
			this.pressed = pressed;  
		}
		
		public boolean isPressed() {
			return pressed;
		}
		
		public int getPressCount() {
			return pressCount;
		}
	}

	public void keyPressed (KeyEvent e) {
		toggleKey(e.getKeyCode(), true);
	}

	public void keyReleased (KeyEvent e) {
		toggleKey(e.getKeyCode(), false);
	}
	
	public void toggleKey(int keyCode, boolean pressed) {
//		rotateRight		VK_E, VK_UP
//		rotateLeft 		VK_Q
//		right			VK_D, VK_RIGHT
//		left			VK_A, VK_LEFT
//		down			VK_S, VK_DOWN
//		drop			VK_SPACE, VK_CONTROL
//		swap			VK_SHIFT
//		menu			VK_ENTER, VK_ESCAPE
		// TODO: make .ini to edit keys
		if(keyCode == KeyEvent.VK_E || keyCode == KeyEvent.VK_UP) rotateRight.toggle(pressed);
		if(keyCode == KeyEvent.VK_Q) rotateLeft.toggle(pressed);
		if(keyCode == KeyEvent.VK_D || keyCode == KeyEvent.VK_RIGHT) right.toggle(pressed);
		if(keyCode == KeyEvent.VK_A || keyCode == KeyEvent.VK_LEFT) left.toggle(pressed);
		if(keyCode == KeyEvent.VK_S || keyCode == KeyEvent.VK_DOWN) down.toggle(pressed);
		if(keyCode == KeyEvent.VK_SPACE || keyCode == KeyEvent.VK_CONTROL) drop.toggle(pressed);
		if(keyCode == KeyEvent.VK_SHIFT) swap.toggle(pressed);
		if(keyCode == KeyEvent.VK_ENTER || keyCode == KeyEvent.VK_ESCAPE) menu.toggle(pressed);
	}

	public void keyTyped (KeyEvent e) {}

}
