package game.graphics.gui;

import java.awt.image.BufferedImage;

import game.graphics.Graphics;
import game.graphics.ImageResource;

public class TickBox {
	
	//position of tickbox on the screen
	private int x, y;
	
	//on and off images
	private ImageResource on;
	private ImageResource off;
	
	//whether tickbox is ticked or not
	private boolean toggle = false;
	
	public TickBox(boolean state, int x, int y) {
		this.toggle = state;
		this.x = x;
		this.y = y;
		
		BufferedImage sprites = ImageResource.getBuffImg("/gui/tick_box.png");
		on = new ImageResource(sprites.getSubimage(0, 0, 8, 8));
		off = new ImageResource(sprites.getSubimage(8, 0, 8, 8));
	}
	
	/**check if tickbox is on or off**/
	public boolean getState() { return toggle; }
	
	/**toggle the tickbox's on/off state**/
	public void toggle() { toggle = !toggle; }
	
	/**RENDER**/
	public void render() {
		if(toggle) { Graphics.drawImage(on, x, y, 8, 8); }
		else { Graphics.drawImage(off, x, y, 8, 8); }
		Graphics.resetColor();
	}
}
