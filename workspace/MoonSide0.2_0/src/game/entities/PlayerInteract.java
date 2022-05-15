package game.entities;

import java.util.ArrayList;

import game.graphics.gui.Dialogue;
import game.region.Position;

public class PlayerInteract {
	
	private EntityManager em;
	
	private HitBox hb;
	
	private Entity entity = null;
	
	private int w = 8;
	private int h = 8;
	
	public PlayerInteract(Position pos) {
		this.hb = new HitBox(pos, w, h);
	}
	
	public void setEM(EntityManager em) {
		this.em = em;
	}
	
	public void setPos(Position pos) { hb.pos = pos; }
	
	public Dialogue getDialogue() {
		if(entity == null) { return null; }
		else { return entity.getDialogue(); }
	}
	
	public void setDir(int dir) {
		
		if(dir == 0) {
			hb.xOffset = 4;
			hb.yOffset = 10;
		}
		else if(dir == 1) {
			hb.xOffset = 4;
			hb.yOffset = 22;
		}
		else if(dir == 2) {
			hb.xOffset = -6;
			hb.yOffset = 16;
		}
		else if(dir == 3) {
			hb.xOffset = 14;
			hb.yOffset = 16;
		}
		else if(dir == 4) {
			hb.xOffset = -6;
			hb.yOffset = 10;
		}
		else if(dir == 5) {
			hb.xOffset = 14;
			hb.yOffset = 10;
		}
		else if(dir == 6) {
			hb.xOffset = -6;
			hb.yOffset = 22;
		}
		else if(dir == 7) {
			hb.xOffset = 14;
			hb.yOffset = 22;
		}
	}
	
	public void update() {
		
		//if the player interact is colliding with an interactable entity,
		//set entity to the colliding entity
		entity = null;
		ArrayList<Entity> entityList = em.getList();
		for(int i=0; i<entityList.size(); i++) {
			Entity curEntity = entityList.get(i);
			if(curEntity.pos.isOnScreen()) {
				if(curEntity.getClass() == Sign.class) {
					if(hb.collideEnt(curEntity.hb)) {
						entity = curEntity;
						break;
					}
				}
			}
		}
	}
	
	public void render() {
		hb.render("orange");
	}
}
