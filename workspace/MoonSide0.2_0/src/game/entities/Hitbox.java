package game.entities;

import game.graphics.Graphics;
import game.region.Position;
import game.region.chunks.ChunkManager;
import game.region.chunks.SolidTileMap;

public class HitBox {
	
	//dimensions of a hitbox
	public Position pos;
	public int w;
	public int h;
	public int xOffset;
	public int yOffset;
	
	//data structure that contains every solid tile in the region to be used for tile collision
	public static SolidTileMap stm;
	
	//whether or not hitboxes should be rendered
	private static boolean render = false;
	
	/**create a hitbox with no offset**/
	public HitBox(Position pos, int w, int h) {
		this.pos = pos;
		this.xOffset = this.yOffset = 0;
		this.w = w;
		this.h = h;
	}

	/**create a hitbox with only y offset**/
	public HitBox(Position pos, int yOffset, int w, int h) {
		this.pos = pos;
		this.xOffset = 0;
		this.yOffset = yOffset;
		this.w = w;
		this.h = h;
	}
	
	/**turn hitbox rendering on**/
	public static void renderHitBoxes() { render = !render; }
	
	/**RENDER**/
	public void render(String color) {
		if(render) {
			Graphics.setColor(color);
			Graphics.fillRect(pos.getRenX()+xOffset, pos.getRenY()+yOffset, w, 1);
			Graphics.setColor(color);
			Graphics.fillRect(pos.getRenX()+xOffset, pos.getRenY()+yOffset, 1, h);
			Graphics.setColor(color);
			Graphics.fillRect(pos.getRenX()+xOffset+w-1, pos.getRenY()+yOffset, 1, h);
			Graphics.setColor(color);
			Graphics.fillRect(pos.getRenX()+xOffset, pos.getRenY()+yOffset+h-1, w, 1);
		}
	}
	
	/**return 0 if not colliding. 1 if colliding with solid square tile. 2 if down-up diagonal, 3 if up-down diagonal, 4 if colliding with both types of diagonals at the same time. 5 if colliding with a diagonal and ONLY 1 square.
	 * dx & dy are the entity's displacement next frame. set to 0 if entity is not moving in that direction. if colliding with a singular diagonal tile, return coordinates of diagonal tile as well**/
	public int[] collideTile(float dx, float dy) {
		
		// [ tileType, coordX, coordY ]
		int answer[] = new int[3];

		//whether or not the player is colliding with a specific type of tile this frame
		boolean downUp = false;
		boolean upDown = false;
		int square = 0;
		
		//dimensions of this object
		float ax = pos.x + dx + xOffset + this.w / 2;
		float ay = pos.y + dy + yOffset + this.h / 2;

		//find how many tiles the entity is touching
		int touchX = (int) Math.floor((float) this.w / ChunkManager.tileWidth);
		int touchY = (int) Math.floor((float) this.h / ChunkManager.tileHeight);
		
		//check to see if the current tile is off of the map. treat map borders as solid square tiles
		if(pos.x + dx + w + xOffset > ChunkManager.getWestBorder()) { square++; }
		if(pos.x + dx + xOffset < ChunkManager.getEastBorder()) { square++; }
		if(pos.y + dy + h + yOffset > ChunkManager.getSouthBorder()) { square++; }
		if(pos.y + dy + yOffset < ChunkManager.getNorthBorder()) { square++; }
		
		//check collision for every tile that the entity is touching
		for(int y=0; y<=touchY; y++) {
			for(int x=0; x<=touchX; x++) {
				
				//convert pixel based coordinates to tile based coordinates
				int posx = (int) (pos.x + dx - (pos.x + dx + xOffset) % ChunkManager.tileWidth + x * ChunkManager.tileWidth);
				int posy = (int) (pos.y + dy + yOffset - (pos.y + dy + yOffset) % ChunkManager.tileHeight + y * ChunkManager.tileHeight);
				
				//check to see if the current tile is found in the stm. if it is, see if dimensions overlap
				int tileType = stm.search(posx/ChunkManager.tileWidth, posy/ChunkManager.tileHeight);
				
				if(tileType != 0) {

					//dimensions of tile
					float bx = posx + ChunkManager.tileWidth / 2;
					float by = posy + ChunkManager.tileHeight / 2;
					
					//if dimensions overlap, the objects are colliding
					if(Math.abs(ax-bx) < (float) this.w / 2 + ChunkManager.tileWidth / 2) {
						if(Math.abs(ay-by) < (float) this.h / 2 + ChunkManager.tileHeight / 2) {
							
							//if type is 1, num of squares colliding is +1
							if(tileType == 1) { square++; }
							
							//if type is 2 or 3, we have to check if the player is colliding with a diagonal line inside the tile.
							else if(tileType == 2) {
								if(collideLineRect(posx, posy+ChunkManager.tileHeight, posx+ChunkManager.tileWidth, posy, pos.x+dx+xOffset, pos.y+dy+yOffset, this.w, this.h)) {
									downUp = true;
									answer[1] = posx;
									answer[2] = posy;
								}
							}
							else if(tileType == 3) {
								if(collideLineRect(posx, posy, posx+ChunkManager.tileWidth, posy+ChunkManager.tileHeight, pos.x+dx+xOffset, pos.y+dy+yOffset, this.w, this.h)) {
									upDown = true;
									answer[1] = posx;
									answer[2] = posy;
								}
							}
						}
					}
				}
			}
		}
		
		//tiletype 4
		if(downUp && upDown) {
			answer[0] = 4;
			answer[1] = -1;
			answer[2] = -1;
			return answer;
		}
		
		//tiletype 5 (for diagonal wall endpoints)
		else if(square == 1 && (downUp || upDown)) {
			answer[0] = 5;
			answer[1] = -1;
			answer[2] = -1;
			return answer;
		}
		
		//tiletype 1
		else if(square > 0) {
			answer[0] = 1;
			answer[1] = -1;
			answer[2] = -1;
			return answer;
		}
		
		//tiletype 2
		else if (downUp && !upDown) {
			answer[0] = 2;
			return answer;
		}
		
		//tiletype 3
		else if (!downUp && upDown) {
			answer[0] = 3;
			return answer;
		}
		
		//if no tile was found to collide, there was no collision (tiletype 0)
		answer[0] = 0;
		answer[1] = -1;
		answer[2] = -1;
		return answer;
	}
	
	/**returns true if a line is colliding with a rectangle. x and y represents line endpoints. 
	 * rx and ry represent rectangle top left corner point. rw and rh represent rectangle width and height**/
	public boolean collideLineRect(float x1, float y1, float x2, float y2, float rx, float ry, float rw, float rh) {

		//if the line is colliding with any of the square's edges, then the line and square are colliding
		boolean left =   collideLineLine(x1,y1,x2,y2, rx,    ry,    rx,    ry+rh);
		boolean right =  collideLineLine(x1,y1,x2,y2, rx+rw, ry,    rx+rw, ry+rh);
		boolean top =    collideLineLine(x1,y1,x2,y2, rx,    ry,    rx+rw, ry);
		boolean bottom = collideLineLine(x1,y1,x2,y2, rx,    ry+rh, rx+rw, ry+rh);
		
		if(top || bottom || left || right) { return true; }
		else { return false; }
		
	}
	
	/**returns true if two lines are colliding. 1 and 2 represent first line endpoints, while 3 and 4 represent second line endpoints**/
	public boolean collideLineLine(float x1, float y1, float x2, float y2, float x3, float y3, float x4, float y4) {
		
		//calculate direction of the lines
		float uA = ((x4-x3)*(y1-y3) - (y4-y3)*(x1-x3)) / ((y4-y3)*(x2-x1) - (x4-x3)*(y2-y1));
		float uB = ((x2-x1)*(y1-y3) - (y2-y1)*(x1-x3)) / ((y4-y3)*(x2-x1) - (x4-x3)*(y2-y1));
		
		//if uA and uB are both between 0 and 1, lines are colliding
		if(uA >= 0 && uA <= 1 && uB >= 0 && uB <= 1) { return true; }
		else { return false; }
	}
	
	/**returns true if this hitbox is colliding with another entity's hitbox**/
	public boolean collideEnt(HitBox other) { return collideEnt(other, 0, 0); }
	public boolean collideEnt(HitBox other, float dx, float dy) {
		
		//dimensions of this object
		float ax = pos.x + xOffset + dx + this.w / 2;
		float ay = pos.y + yOffset + dy + this.h / 2;
		
		//dimensions of other object
		float bx = other.pos.x + other.xOffset + other.w / 2;
		float by = other.pos.y + other.yOffset + other.h / 2;
		
		//if dimensions overlap, entities are colliding
		if(Math.abs(ax-bx) < (float) this.w / 2 + other.w / 2) {
			if(Math.abs(ay-by) < (float) this.h / 2 + other.h / 2) {
				return true;
			}
		}
		
		return false;
	}
}
