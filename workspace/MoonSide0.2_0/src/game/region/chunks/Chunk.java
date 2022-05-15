package game.region.chunks;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import game.graphics.Graphics;
import game.graphics.ImageResource;
import game.region.Position;

/**a chunk of tiles**/
public class Chunk {

	//the final image of the completed chunk
	private ImageResource finalImage;
	
	//the tiles being draw onto the chunk
	private Tile[] tiles;
	
	//how many tiles wide and high the chunk is
	private int width;
	private int height;
	
	//how many tiles in the chunk
	private int size;
	
	//the amount of tiles already inserted into the chunk
	private short index;
	
	//the world position of the chunk
	private Position pos;
	
	//whether or not the chunk actually has texture inside it; if not, it does not need to be rendered or even made into a texture
	private boolean isEmpty;

	/**create a chunk of tiles that will be rendered at pos**/
	public Chunk(Position pos, int tilesX, int tilesY) {
		
		this.width = tilesX;
		this.height = tilesY;
		this.size = tilesX * tilesY;
		
		tiles = new Tile[size];
		index = 0;
		finalImage = null;
		this.pos = pos;
		isEmpty = true;
	}
	
	/**insert a tile and draw it onto the final image at index**/
	public void insert(Tile currentTile) {
		
		//if chunk is already full, dont add more
		if(index >= size) { System.out.println("Chunk already contains " + size + " tiles."); return; }
		
		//if this tile is not empty, then neither is the chunk
		if(!currentTile.isEmpty()) { isEmpty = false; }
		
		//add a tile and increase index
		tiles[index] = currentTile;
		index++;
		
		//if chunk has just filled up, create the final image
		if(index == size && !isEmpty) { createFinalImage(); }
		else if(index == size && isEmpty) { tiles = null; }
	}
	
	/**take all inserted tiles and draw a full chunk**/
	public void createFinalImage() {
		
		BufferedImage newImage = new BufferedImage(ChunkManager.tileWidth*ChunkManager.tilesInRow, ChunkManager.tileWidth*ChunkManager.tilesInRow, BufferedImage.TYPE_INT_ARGB);
		
		//create a transparent image
		Graphics2D g2 = newImage.createGraphics();
		g2.setColor(new Color(0, true));
		
		//draw each tile to transparent image
		int i = 0;
		for(int y=0; y<height; y++) {
			for(int x=0; x<width; x++) {
				if(!tiles[i].isEmpty()) {
					g2.drawImage(tiles[i].getImage().getImage(), null, x*ChunkManager.tileWidth, y*ChunkManager.tileWidth);}
				i++;
			}	
		}
		
		//set final image
		finalImage = new ImageResource(newImage);

		tiles = null;
	}
	
	/**draw the chunk where it goes**/
	public void render() { if(!isEmpty) { Graphics.drawImage(finalImage, pos.getRenX(), pos.getRenY(), ChunkManager.tileWidth*ChunkManager.tilesInRow, ChunkManager.tileWidth*ChunkManager.tilesInRow); } }
}
