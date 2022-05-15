package game.entities;

import java.util.ArrayList;

import game.graphics.Animations;
import game.graphics.SpriteSheet;
import game.graphics.gui.Dialogue;
import game.region.Position;

/**NOT DONE OR IMPLEMENTED YET**/

public class Followers {

	private ArrayList<Follower> followers;
	private Entity leader;
	
	public Followers(Entity leader) {
		followers = new ArrayList<Follower>();
		this.leader = leader;
	}
	
	public void addFollower() {
		if(followers.size() == 0) {
			followers.add(new Follower(new SpriteSheet("/sprites/ness.png"), leader, new Position(leader.pos)));
		}
	}
	
	class Follower extends Entity {
		
		Entity following;
		
		private Animations ani;
		
		public Follower(SpriteSheet ss, Entity following, Position pos) {
			super(pos, new HitBox(pos, 16, 16, 8), 16, 24);
			this.ani = new Animations(ss);
			ani.setStatic(1, 1);
			this.following = following;
		}

		//required method
		public Dialogue getDialogue() { return null; }

		public void update() {}

		public void render() {
			ani.render(pos.getRenX(), pos.getRenY());
		}
	}
}
