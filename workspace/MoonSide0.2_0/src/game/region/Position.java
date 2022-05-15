package game.region;

import game.graphics.Renderer;

/**used to determine the world-coordinates of the user's screen, world-coordinates of entities, and entity's positions on the screen**/
public class Position {
	
	//the entity's world-coordinates
	public float x;
	public float y;
	
	//the world-coordinates of the screen.
	//more specifically, the world-coordinates of the top-left corner of the screen
	public static float screenX;
	public static float screenY;
	
	/**upon construction, creates coordinates for an entity**/
	public Position(float x, float y) { this.x = x; this.y = y; }
	
	/**upon construction, creates a duplicate of given coords**/
	public Position(Position pos) { this.x = pos.x; this.y = pos.y; }
	
	//get the entity's position on the screen (where it should be rendered)
	public float getRenX() { return x - screenX; }
	public float getRenY() { return y - screenY; }
	
	/**return true if the entity is on the screen or close to it**/
	public boolean isOnScreen() {
		
		//screen borders (but a bit further away)
		float x1 = screenX - 40;
		float x2 = screenX + Renderer.unitsWide + 40;
		float y1 = screenY - 40;
		float y2 = screenY + Renderer.unitsHigh + 40;
		
		if(x < x1 || x > x2 || y < y1 || y > y2) { return false; }
		else { return true; }
	}
	
	/**set the world's position on the screen**/
	public static void setWorldVar(float wx, float wy) { screenX = wx; screenY = wy;}
}
