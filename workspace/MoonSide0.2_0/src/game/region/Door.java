package game.region;

import game.entities.HitBox;
import game.entities.Player;
import game.graphics.Graphics;
import game.region.Regions.Region;
import game.states.PlayState;

/**when collided with, a door brings the player to a new area**/
public class Door {
	
	//door position in this region
	public Position pos;

	//door hitbox
	public HitBox hb;
	
	//what pos the player will end up in the new area
	private Position regionPos;
	
	//what area the player is going to
	private Region region;
	
	//need player for collision checks
	private Player player;
	
	//what direction the player is facing after coming through the door in the new region
	int playerDirection;
	
	public Door(Position pos, Position regionPos, Region region, int w, int h, Player player, int playerDirection) {
		hb = new HitBox(pos, w, h);
		this.pos = pos;
		this.regionPos = regionPos;
		this.region = region;
		this.player = player;
		this.playerDirection = playerDirection;
	}
	
	//getters
	public Region getRegion() { return region; }
	public Position getRegionPos() { return regionPos; }
	
	/**UPDATE**/
	public void update(PlayState ps) {
		
		//check if it is colliding with the player. if it is, open the door
		if(hb.collideEnt(player.hb)) {
			ps.openDoor(region, regionPos, playerDirection);
		}
		
	}

	/**RENDER**/
	public void render() { 
		
		//render hitbox
		hb.render("blue");
	}
}