package game.entities;

import game.graphics.gui.Dialogue;
import game.region.Position;

public abstract class Entity {
	
	//position of player sprite
	public Position pos;
	
	//hitbox of player
	public HitBox hb;
	
	//dimensions of entity
	public int w;
	public int h;
	
	public Entity(Position pos, HitBox hb, int w, int h) {
		this.pos = pos;
		this.hb = hb;
		this.w = w;
		this.h = h;
	}
	
	public abstract Dialogue getDialogue();
	public abstract void update();
	public abstract void render();
}
