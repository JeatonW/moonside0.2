package game.graphics.gui;

import game.audio.AudioResource;
import game.engine.KeyHandler;
import game.graphics.Animations;
import game.graphics.Graphics;
import game.graphics.SpriteSheet;

public class Cursor {

	//cursor animation
	private Animations ani;
	
	//position on the screen that the cursor will start at
	private int startX, startY;
	
	//how far the cursor will move from input
	private int xGap, yGap;
	
	//number of positions in each direction
	private int xTotal, yTotal;
	
	//current position
	private int x = 0, y = 0;
	
	//cursor direction (horizontal or vertical)
	private String dir;
	
	private AudioResource clickAudio;
	
	/**a cursor that does not move**/
	public Cursor(String dir, int startX, int startY) {
		this.dir = dir;
		this.startX = startX;
		this.startY = startY;
		this.xGap = this.yGap = this.xTotal = this.yTotal = 0;
		
		//create audio for when cursor moves
		clickAudio = new AudioResource("res/soundfx/click.wav");
		
		//determine cursor direction (horizontal or vertical)
		int animationDir = 0;
		if(dir == "horizontal") { animationDir = 0; }
		else if(dir == "vertical") { animationDir = 1; }
		else { System.out.println("Invalid cursor direction."); }
		
		//create cursor animation
		ani = new Animations(new SpriteSheet("/gui/cursor.png", 8, 8, 2, 2));
		ani.setAnimation(animationDir, 0.25f);
	}
	
	/**a cursor that moves when player inputs directional keys**/
	public Cursor(String dir, int startX, int startY, int xGap, int yGap, int xTotal, int yTotal) {
		this.dir = dir;
		this.startX = startX;
		this.startY = startY;
		this.xGap = xGap;
		this.yGap = yGap;
		this.xTotal = xTotal;
		this.yTotal = yTotal;

		//create audio for when cursor moves
		clickAudio = new AudioResource("res/soundfx/click.wav");

		//determine cursor direction (horizontal or vertical)
		int animationDir = 0;
		if(dir == "horizontal") { animationDir = 0; }
		else if(dir == "vertical") { animationDir = 1; }
		else { System.out.println("Invalid cursor direction."); }
		
		//create cursor animation
		ani = new Animations(new SpriteSheet("/gui/cursor.png", 8, 8, 2, 2));
		ani.setAnimation(animationDir, 0.25f);
	}
	
	//getters
	public int getX() { return x; }
	public int getY() { return y; }

	//setters
	public void setStartX(int x) { this.startX = x; }
	public void setStartY(int y) { this.startY = y; }
	
	/**reset cursor timing and position**/
	public void reset() {

		//determine cursor direction (horizontal or vertical)
		int animationDir = 0;
		if(dir == "horizontal") { animationDir = 0; }
		else if(dir == "vertical") { animationDir = 1; }
		else { System.out.println("Invalid cursor direction."); }
		
		//reset ani and position
		ani.setAnimation(animationDir, 0.25f);
		x = y = 0;
	}
	
	/**reset cursor timing**/
	public void resetAni() {

		//determine cursor direction (horizontal or vertical)
		int animationDir = 0;
		if(dir == "horizontal") { animationDir = 0; }
		else if(dir == "vertical") { animationDir = 1; }
		else { System.out.println("Invalid cursor direction."); }
		
		//reset ani
		ani.setAnimation(animationDir, 0.25f);
	}
	
	/**INPUT**/
	public void input(KeyHandler key) {
		
		//dont allow opposite movement keys at the same time
		if(key.up.pressed && key.down.pressed || key.left.pressed && key.right.pressed) {
			key.rAll();
			return;
		}
		
		//go up
		if(key.up.pressed && yTotal > 1) {
			key.rAll();
			clickAudio.play();
			y--;
			if(y == -1) { y = yTotal - 1; }
			resetAni();
		}
		
		//go down
		if(key.down.pressed && yTotal > 1) {
			key.rAll();
			clickAudio.play();
			y++;
			if(y == yTotal) { y = 0; }
			resetAni();
		}
		
		//go left
		if(key.left.pressed && xTotal > 1) {
			key.rAll();
			clickAudio.play();
			x--;
			if(x == -1) { x = xTotal - 1; }
			resetAni();
		}
		
		//go right
		if(key.right.pressed && xTotal > 1) {
			key.rAll();
			clickAudio.play();
			x++;
			if(x == xTotal) { x = 0; }
			resetAni();
		}
		
	}
	
	/**UPDATE**/
	public void update() {
		ani.update();
	}
	
	/**RENDER**/
	public void render() { render(0, 0); } //render with no offset
	public void render(float xOffset, float yOffset) { //render with offset (if gui box is moving or something)
		ani.render((startX + xOffset + x * xGap), (startY + yOffset + y * yGap));
		Graphics.resetColor();
	}
}
