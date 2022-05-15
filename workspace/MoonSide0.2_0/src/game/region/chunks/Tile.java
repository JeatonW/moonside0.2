package game.region.chunks;

import game.graphics.ImageResource;
import game.region.Position;

/**a block is a tile that has certain properties depending on what type it is**/
public class Tile {
	
	//a block has a width, height, position, and image
	protected int w;
	protected int h;
	protected ImageResource img;
	protected Position pos;
	
	//some blocks are empty
	boolean empty;
	
	/**upon construction, create an empty block**/
	public Tile() { this.empty = true; }
	
	/**upon construction, create a block with an image, position, width, and height**/
	public Tile(ImageResource img, Position pos, int w, int h) {
		
		//setters
		this.empty = false;
		this.img = img;
		this.pos = pos;
		this.w = w;
		this.h = h;
	}

	//getters
	public boolean isEmpty() { return empty; }
	public float getPosX() { return pos.x; }
	public float getPosY() { return pos.y; }
	public ImageResource getImage() { return img; }
}
