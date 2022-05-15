package game.region.chunks;

import java.util.ArrayList;
import game.graphics.SpriteSheet;
import game.region.Position;

/**a layer of chunks**/
public class ChunkMap {
	
	//grid of chunks
	public ArrayList<ArrayList<Chunk>> chunks;

	/**upon the construction of a chunk map, a grid of chunks is created**/
	public ChunkMap(String d, SpriteSheet sprite, int tilesX, int posx, int posy, int width, int height, int tileColumns, int layer) {
		
		chunks = new ArrayList<ArrayList<Chunk>>();
		
		//remove all commas in data
		String[] data = d.split(",");
		
		//number of chunks in a column and a row
		int chunksCol = (int) Math.floor(width / ChunkManager.tilesInCol);
		int chunksRow = (int) Math.floor(height / ChunkManager.tilesInRow);
		
		//if there is a chunk that is partially cut off because the map size is not divisible by four; this modulus gives the partial chunk its dimensions.
		int colRem = width % ChunkManager.tilesInCol;
		int rowRem = height % ChunkManager.tilesInRow;

		//the first chunk is always rendered at 0,0
		int xOffset = 0;
		int yOffset = 0;

		//where the map should begin taking tiles from the tile map
		int tileXOffset = posx;
		int tileYOffset = posy;
		
		//create several rows of chunks to form a map grid
		for(int y=posy; y<posy+chunksRow; y++) {
			
			//add a row of chunks
			chunks.add(new ArrayList<Chunk>());

			//for every NORMAL chunk in x direction, create and add to current row of chunks
			for(int x=posx; x<posx+chunksCol; x++) {
				Chunk curChunk = new Chunk(new Position((float) xOffset, (float) yOffset), ChunkManager.tilesInCol, ChunkManager.tilesInRow);
				curChunk = fillChunk(data, sprite, curChunk, ChunkManager.tilesInCol, ChunkManager.tilesInRow, tilesX, tileColumns, tileXOffset, tileYOffset, width, height);
				chunks.get(y-posy).add(curChunk);
				
				//adjust coordinates
				xOffset += ChunkManager.chunkWidth;
				tileXOffset += ChunkManager.tilesInCol;
			}
			
			//if there is a PARTIAL chunk at the end of the row that isnt wide enough to complete a full chunk
			if(colRem > 0) {
				Chunk curChunk = new Chunk(new Position((float) xOffset, (float) yOffset), colRem, ChunkManager.tilesInRow);
				curChunk = fillChunk(data, sprite, curChunk, colRem, ChunkManager.tilesInRow, tilesX, tileColumns, tileXOffset, tileYOffset, width, height);
				chunks.get(y-posy).add(curChunk);
			}
			
			//adjust coordinates
			xOffset = 0;
			tileXOffset = posx;
			yOffset += ChunkManager.chunkWidth;
			tileYOffset += ChunkManager.tilesInRow;
		}
		
		//if the chunks in the last row are not tall enough to be completed chunks
		if(rowRem > 0) {
			
			//add a row of chunks
			chunks.add(new ArrayList<Chunk>());
			
			//for every PARTIAL chunk in the x direction that isnt tall enough to complete a full chunk
			for(int x=posx; x<posx+chunksCol; x++) {
				Chunk curChunk = new Chunk(new Position((float) xOffset, (float) yOffset), ChunkManager.tilesInCol, rowRem);
				curChunk = fillChunk(data, sprite, curChunk, ChunkManager.tilesInCol, rowRem, tilesX, tileColumns, tileXOffset, tileYOffset, width, height);
				chunks.get(chunksRow).add(curChunk);
				
				//adjust coordinates
				xOffset += ChunkManager.chunkWidth;
				tileXOffset += ChunkManager.tilesInCol;
			}
			
			//a singular PARTIAL chunk that is neither tall or wide enough to complete a full chunk
			if(colRem > 0) {
				Chunk curChunk = new Chunk(new Position((float) xOffset, (float) yOffset), colRem, rowRem);
				curChunk = fillChunk(data, sprite, curChunk, colRem, rowRem, tilesX, tileColumns, tileXOffset, tileYOffset, width, height);
				chunks.get(chunksRow).add(curChunk);
			}
		}
		
		//reset offsets
		xOffset = yOffset = 0;
		tileXOffset = tileYOffset = 0;
	}
	
	/**fill a chunk with blocks**/
	public Chunk fillChunk(String data[], SpriteSheet sprite, Chunk curChunk, int colRem, int rowRem, int tilesX, int tileColumns, int tileXOffset, int tileYOffset, int width, int height) {
		
		//do for every tile in a chunk's column
		for(int y=0; y<rowRem; y++) {
			
			//do for every tile in a chunk's row
			for(int x=0; x<colRem; x++) {
				
				//current block index
				int i = ((tileYOffset + y) * tilesX + tileXOffset + x);
				
				//insert block into chunk
				int temp = Integer.parseInt(data[i].replaceAll("\\s+",""));
				if(temp!= 0) { curChunk.insert(new Tile(sprite.getImgRes((int) ((temp-1)%tileColumns), (int) ((temp-1)/tileColumns)), new Position((int) ((i%width)*ChunkManager.tileWidth), (int) ((i/height)*ChunkManager.tileHeight)), ChunkManager.tileWidth, ChunkManager.tileHeight)); }
				else { curChunk.insert(new Tile()); }
			}	
		}
		return curChunk;
	}
}
