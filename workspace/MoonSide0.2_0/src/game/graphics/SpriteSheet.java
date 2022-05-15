package game.graphics;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;

/**a sprite is an image that an object displays while on the screen**/
public class SpriteSheet {
	
	private BufferedImage SPRITESHEET = null; //entire spritesheet
	private ImageResource[][] spriteArray; //each individual subsprite in a 2D array
	public int w; //width of each subsprite
	public int h; //height of each subsprite
	private int xCount; //# of subpsrites in one of the spritesheet's rows
	private int yCount; //# of subsprites in one of the spritesheet's columns
	
	/**load a sprite sheet with perfectly square subsprites**/
	public SpriteSheet(String path) {
		
		System.out.println("Loading: " + path + "...");
		SPRITESHEET = ImageResource.getBuffImg(path);
		
		loadSpriteArray();
	}
	
	/**load a sprite sheet with rectangular shaped subsprites**/
	public SpriteSheet(String path, int w, int h, int xCount, int yCount) {
		
		//each sprite sheet has a path, tile width, tile height, tile xcount, and tile ycount
		this.w = w;
		this.h = h;
		this.xCount = xCount;
		this.yCount = yCount;
		
		//load sprite sheet
		System.out.println("Loading " + path + "...");
		SPRITESHEET = ImageResource.getBuffImg(path);
		
		loadSpriteArray();
	}
	
	public int getW() { return xCount; }
	public int getH() { return yCount; }
	
	/**goes through each subsprite and transfers them into individual images**/
	public void loadSpriteArray() {
		spriteArray = new ImageResource[yCount][xCount];
		
		for(int y=0; y<yCount; y++) {
			for(int x=0; x<xCount; x++) {
				spriteArray[y][x] = new ImageResource(getSprite(x, y));
			}
		}
	}
	
	/**getters for any number of sprites at a time**/
	public BufferedImage getSpriteSheet() { return SPRITESHEET; } //getter for full sprite sheet
	public BufferedImage getSprite(int x, int y) { return SPRITESHEET.getSubimage(x * w, y * h, w, h); } //getter for individual sprites
	public ImageResource getImgRes(int i, int j) { return spriteArray[j][i]; }
	public ImageResource[] getSpriteArray(int i) { return spriteArray[i]; } //getter for a row of sprites
	public ImageResource[][] getSpriteArray2(int i) { return spriteArray; } //getter for entire array
}
