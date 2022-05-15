package game.region;

import java.util.ArrayList;

import game.entities.Player;
import game.region.Regions.Region;
import game.states.PlayState;

/**handles all the doors for the region**/
public class DoorManager {
	
	//a list of every door in the current area
	ArrayList<Door> doors = new ArrayList<Door>();
	
	/**add a door to the current list of doors for the area**/
	public void addDoor(Position pos, Region region, Position regionPos, int w, int h, Player player, int playerDirection) { doors.add(new Door(pos, regionPos, region, w, h, player, playerDirection)); }
	
	/**UPDATE**/
	public void update(PlayState ps) {
		
		//if a door is on the screen, update the door
		for(int i=0; i<doors.size(); i++) {
			if(doors.get(i).pos.isOnScreen()) {
				doors.get(i).update(ps);
			}
		}
	}
	
	/**RENDER**/
	public void render() {

		//if a door is on the screen, render its hitbox
		for(int i=0; i<doors.size(); i++) {
			if(doors.get(i).pos.isOnScreen()) {
				doors.get(i).render();
			}
		}
	}
}
