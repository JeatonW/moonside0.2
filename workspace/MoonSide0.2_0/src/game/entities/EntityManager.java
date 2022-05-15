package game.entities;

import java.util.ArrayList;
import java.util.Collections;

public class EntityManager {

	private ArrayList<Entity> entityList;
	private ArrayList<Entity> renderList;
	
	public EntityManager() {
		entityList = new ArrayList<Entity>();
		renderList = new ArrayList<Entity>();
	}
	
	/**add an entity to the entity list**/
	public void add(Entity e) {
		entityList.add(e);
		renderList.add(e);
	}
	
	//enemy list getter
	public ArrayList<Entity> getList() { return entityList; }
	
	/**UPDATE**/
	public void update() {
		
		//make sure entities are layered correctly
		sortByPosY();
		
		//update each entity on the screen one at a time
		for(int i=0; i<entityList.size(); i++) {
			Entity curEntity = entityList.get(i);
			if(curEntity.pos.isOnScreen()) {
				curEntity.update();
			}
		}
	}
	
	
	/**RENDER**/
	public void render() {
		
		//render each entity on the screen one at a time
		for(int i=0; i<renderList.size(); i++) {
			Entity curEntity = renderList.get(i);
			if(curEntity.pos.isOnScreen()) {
				curEntity.render();
			}
		}
	}
	
	/**npcs must be sorted by their Y position so that they render on top of one another correctly**/
	public void sortByPosY() {

		//cocktail sort would be fastest here because entities usually only pass over each other one at a time, and they only need to be
		//shifted one position up or down. so, usually a forward pass and a backwards pass is enough to fully sort the entity positions.
		boolean sorted = false;
		boolean flipflop = true;
		while(!sorted) {
			
			//if boolean sorted is undisturbed, the sort can stop
			sorted = true;

			//forward pass
			if(flipflop) {
				for(int i=0; i<renderList.size()-1; i++) {
					
					//swap if posy of first is greater than posy of second
					if(renderList.get(i).pos.y + renderList.get(i).h > renderList.get(i+1).pos.y + renderList.get(i+1).h) { Collections.swap(renderList, i, i+1); sorted = false; }
					flipflop = false;
				}	
			}

			//backward pass
			else {
				for(int i=renderList.size()-1; i>0; i--) {
					
					//swap if posy of second is less than posy of first
					if(renderList.get(i).pos.y + renderList.get(i).h < renderList.get(i-1).pos.y + renderList.get(i-1).h) { Collections.swap(renderList, i, i-1); sorted = false; }
					flipflop = true;
				}
			}
		}
	}
}
