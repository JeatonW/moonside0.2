package game.entities;

import game.graphics.Graphics;
import game.graphics.ImageResource;
import game.graphics.gui.Dialogue;
import game.region.Position;

public class Sign extends Entity {
	
	//image for the sign
	private ImageResource img;
	
	//dialogue for reading the sign
	private Dialogue dialogue;
	
	public Sign(Position pos, Dialogue dialogue) {
		super(pos, new HitBox(pos, 8, 16, 8), 16, 16);
		img = new ImageResource("/sprites/sign.png");
		this.dialogue = dialogue;
	}
	
	public Dialogue getDialogue() { return dialogue; }
	
	/**UPDATE**/
	public void update() {
		/*do nothing*/
	}
	
	/**RENDER**/
	public void render() {
		
		//draw sign
		Graphics.drawImage(img, pos.getRenX(), pos.getRenY(), w, h);
		
		//draw hitbox
		hb.render("purple");
	}
}
