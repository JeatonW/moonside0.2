package game.region.chunks;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import game.graphics.ImageResource;
import game.graphics.SpriteSheet;
import game.region.Regions.Region;

/**the map is rendered in chunks of tiles rather than one tile at a time because opengl's render method is expensive. manages chunk maps**/
public class ChunkManager {
	
	//only the chunks visible on the screen should be rendered
	protected short leftMostChunk, rightMostChunk, topMostChunk, bottomMostChunk;
	private final short chunksFromCenterToSide = 3;
	private final short chunksFromCenterToTop = 2;

	//edges of the map (start of black area)
	private static int northBorder;
	private static int southBorder;
	private static int eastBorder;
	private static int westBorder;
	
	//chunk, tile, and block dimensions
	public static final short tileWidth = 8;
	public static final short tileHeight = tileWidth;
	public static final short chunkWidth = tileWidth * 8;
	public static final short tilesInRow = 8;
	public static final short tilesInCol = tilesInRow;
	private int regionW;
	private int regionH;
	
	//whether or not the STM should be rendered
	private static boolean renderSTM = false;
	
	//chunk map array and solidblockmap for collision
	private ArrayList<ChunkMap> cmap;
	private SolidTileMap stm;
	
	public ChunkManager(Region r) {
		if(r.getX() == -1) { createCM(r.getPath()); }
		else { createCM(r.getPath(), r.getX(), r.getY(), r.getW(), r.getH()); }
	}
	
	/**turn render STM on**/
	public static void renderSTM() { renderSTM = !renderSTM; }

	/**a chunk map that contains the entirety of a single tile map**/
	public void createCM(String path, int regionX, int regionY, int regionW, int regionH) {
		cmap = new ArrayList<ChunkMap>();
		
		//path to tileset
		String imagePath;
		
		//chunk map dimensions
		int tileColumns, tileCount, tilesX, tilesY; int layers = 0;

		Document doc = getDoc(path);
		doc.getDocumentElement().normalize();
		NodeList list = doc.getElementsByTagName("tileset");
		Element eElement = (Element) list.item(0);

		//find tilemap attributes
		imagePath = eElement.getAttribute("name");
		tileColumns = Integer.parseInt(eElement.getAttribute("columns"));
		tileCount = Integer.parseInt(eElement.getAttribute("tilecount"));
		eElement = (Element) doc.getElementsByTagName("map").item(0);
		tilesX = Integer.parseInt(eElement.getAttribute("width"));
		tilesY = Integer.parseInt(eElement.getAttribute("height"));
		layers = doc.getElementsByTagName("layer").getLength();
		
		this.regionW = regionW;
		this.regionH = regionH;

		createMapBorders(regionX, regionY, regionW, regionH);
		addChunkMaps(doc, imagePath, layers, tileColumns, tileCount, tilesX, tilesY, regionX, regionY, regionW, regionH);
	}
	
	/**a chunk map that is only a part of a single tile map**/
	public void createCM(String path) {
		cmap = new ArrayList<ChunkMap>();
		
		//path to tileset
		String imagePath;
		
		//chunk map dimensions
		int tileColumns, tileCount, tilesX, tilesY; int layers = 0;
		
		Document doc = getDoc(path);
		doc.getDocumentElement().normalize();
		NodeList list = doc.getElementsByTagName("tileset");
		Element eElement = (Element) list.item(0);

		//find tilemap attributes
		imagePath = eElement.getAttribute("name");
		tileColumns = Integer.parseInt(eElement.getAttribute("columns"));
		tileCount = Integer.parseInt(eElement.getAttribute("tilecount"));
		eElement = (Element) doc.getElementsByTagName("map").item(0);
		tilesX = Integer.parseInt(eElement.getAttribute("width"));
		tilesY = Integer.parseInt(eElement.getAttribute("height"));
		layers = doc.getElementsByTagName("layer").getLength();
		
		this.regionW = tilesX;
		this.regionH = tilesY;

		createMapBorders(0, 0, regionW, regionH);
		addChunkMaps(doc, imagePath, layers, tileColumns, tileCount, tilesX, tilesY, 0, 0, tilesX, tilesY);
	}
	
	/**retrieve solid tile map**/
	public SolidTileMap getSTM() { return stm; }
	
	/**handle the xml map file**/
	public Document getDoc(String path) {
		
		//try to build the map using the tilemap XML and tileset
		try {
			DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = builderFactory.newDocumentBuilder();
			URL url = ImageResource.class.getResource(path);
			return builder.parse(new File(url.toURI()));
		}
		
		//if there was a problem, show error
		catch(Exception e) { System.out.println("ChunkManager could not be loaded: "); e.printStackTrace(); return null; }
	}

	/**figure out what the borders of the map are so entities can't walk through them**/
	public void createMapBorders(int regionX, int regionY, int regionW, int regionH) {
		northBorder = 0;
		southBorder = regionH * tileHeight;
		eastBorder = 0;
		westBorder = regionW * tileWidth;
	}
	
	//border getters
	public static int getNorthBorder() { return northBorder; }
	public static int getSouthBorder() { return southBorder; }
	public static int getEastBorder() { return eastBorder; }
	public static int getWestBorder() { return westBorder; }
	
	/**add all chunk maps to the chunk manager**/
	private void addChunkMaps(Document doc, String imagePath, int layers, int tileColumns, int tileCount, int tilesX, int tilesY, int regionX, int regionY, int regionW, int regionH) {
		
		//chunk map sprite
		SpriteSheet sprite;
		
		//layer data
		String[] layerData = new String[4];
		
		//create a new spritesheet for the tile set
		sprite = new SpriteSheet("/tile/" + imagePath + ".png", tileWidth, tileHeight, tileColumns, tileCount/tileColumns);
		
		//create  a chunkmap for each layer
		for(short i=0; i<layers; i++) {
			
			//get the data of the current layer
			layerData[i] = ((Element) doc.getElementsByTagName("layer").item(i)).getElementsByTagName("data").item(0).getTextContent();
			
			//if on solid layer, create an arraylist for collision detection
			if(i == 1) { stm = new SolidTileMap(layerData[i], tilesX, tilesY, regionX, regionY, regionW, regionH); }
			
			//create chunkmap for current layer
			cmap.add(new ChunkMap(layerData[i], sprite, tilesX, regionX, regionY, regionW, regionH, tileColumns, i));
		}
	}
	
	/**UPDATE**/
	public void update(float x, float y) {
		
		//determine how close chunks have to be to the player in order to get rendered
		//if the edge of the map is closer than the edge of the screen, dont render chunks that dont exist
		leftMostChunk = (short) (Math.floor(x/chunkWidth) - chunksFromCenterToSide);
		rightMostChunk = (short) (leftMostChunk + chunksFromCenterToSide * 2 + 1);
		topMostChunk = (short) (Math.floor(y/chunkWidth) - chunksFromCenterToTop);
		bottomMostChunk = (short) (topMostChunk + chunksFromCenterToTop * 2 + 1);
		
		//if there is a partial chunk on the edge of the map
		int partialChunkX = 0;
		int partialChunkY = 0;
		if(((float)regionW)%tileWidth != 0) { partialChunkX = 1; }
		if(((float)regionH)%tileHeight != 0) { partialChunkY = 1; }
		
		//clamp the render bounds to the edge of the map
		leftMostChunk = (short) Math.max(leftMostChunk, 0);
		rightMostChunk = (short) Math.min(rightMostChunk, regionW/tileWidth + partialChunkX);
		topMostChunk = (short) Math.max(topMostChunk, 0);
		bottomMostChunk = (short) Math.min(bottomMostChunk, regionH/tileHeight + partialChunkY);
	}
	
	/**RENDER**/
	public void render(int layer) {
		if(cmap.size() == 0) { return; }
		
		//only render the chunks visibile to the player
		for(int y=topMostChunk; y<bottomMostChunk; y++) {
			for(int x=leftMostChunk; x<rightMostChunk; x++) {
				
				//render specified layer. STM layer must be turned on to render STM layer.
				if(layer != 1 || renderSTM) { cmap.get(layer).chunks.get(y).get(x).render(); }
			}
		}
	}
}