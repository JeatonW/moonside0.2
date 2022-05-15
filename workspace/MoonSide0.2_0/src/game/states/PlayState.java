package game.states;

import game.engine.GameLoop;
import game.engine.KeyHandler;
import game.entities.HitBox;
import game.entities.Player;
import game.graphics.Graphics;
import game.graphics.Renderer;
import game.graphics.gui.DialogueBox;
import game.graphics.gui.Menu;
import game.items.Item;
import game.party.Party;
import game.region.Position;
import game.region.RegionManager;
import game.region.Regions.Region;
import game.region.chunks.ChunkManager;

public class PlayState extends GameState {
	
	private Party party;
	
	private Player player;
	private KeyHandler key;
	private RegionManager rm;
	
	//door transition new region values
	private Region newRegion;
	private Position newRegionPos;
	private int newRegionDir;

	//did the player just enter or exit a door?
	private boolean enteringRegion = false;
	private boolean exitingRegion = false;
	
	//length of door transition
	private double door_time = 0.3;
	
	//screen darkness during screen transition
	private float blackScreenOpacity = 0;
	
	//start time of current screen transition
	private double transition_start_time = 0;
	
	private DialogueBox db;
	private Menu m;
	
	public PlayState(Party party, KeyHandler key) {
		
		this.party = party;
		
		//allow the playstate to manually release keys
		this.key = key;
		
		//spawn the player
		player = new Player(party.getSS(0), new Position(488, 216));

		party.addItem(Item.createItem("Cookie"));
		party.addItem(Item.createItem("Cookie"));
		party.addItem(Item.createItem("Cookie"));
		party.addItem(Item.createItem("Cookie"));
		party.addItem(Item.createItem("Cookie"));
		party.addItem(Item.createItem("Cookie"));
		party.addItem(Item.createItem("Cookie"));
		party.addItem(Item.createItem("Cookie"));
		party.addItem(Item.createItem("Cookie"));
		party.addItem(Item.createItem("Cookie"));
		party.addItem(Item.createItem("Cookie"));
		party.addItem(Item.createItem("Cookie"));
		party.addItem(Item.createItem("Cookie"));
		party.addItem(Item.createItem("Cookie"));
		party.addItem(Item.createItem("Cookie"));
		party.addItem(Item.createItem("Cookie"));
		
		//bind the world position to the player
		Position.setWorldVar(player.pos.x - Renderer.windowWidth / 2 + player.w / 2, player.pos.y - Renderer.windowHeight / 2 + player.h / 2);
		
		//create the region
		rm = new RegionManager(player);

		//create dialogue box
		db = new DialogueBox();
		
		//creat menu
		m = new Menu(party);
	}

	/**INPUT**/
	public void input(KeyHandler key) {
		
		//do not take in player input if in a screen transition
		if(exitingRegion || enteringRegion) { return; }
		
		//talk function opens dialogue box and pauses playstate
		if(!db.open && !m.open && key.accept.pressed) {
			key.rAll();
			if(!db.stop) {
				pause();
				db.start(player.pi.getDialogue());
			}
		}
		
		//menu function opens the menu and pauses playstate
		if(!m.open && !db.open && key.menu.pressed) {
			key.rAll();
			if(!m.stop) {
				pause();
				m.start();
			}
		}
		
		//menu input
		if(m.open) {
			m.input(key, this);
		}
		
		//dialogue input
		if(db.open) {
			db.input(key, this);
		}
		
		//player input
		if(!db.open && !m.open) {
			player.input(key);
		}
	}

	/**UPDATE**/
	public void update(boolean gsmPaused) {
		
		//do screen transitions
		doorTransitions();
		
		//do not take input during screen transitions
		if(exitingRegion || enteringRegion) { return; }
		
		//only update player and world while not in dialogue, menu, or settings UNLESS walk buffer is unfinished (ensure pixel alignment)
		if(!db.open && !m.open && !gsmPaused || (db.open && player.walkBuffer) || (m.open && player.walkBuffer) || (gsmPaused && player.walkBuffer)) {
		
			//update entities and the world
			rm.em.update();
			Position.setWorldVar(player.pos.x - Renderer.windowWidth / 2 + player.w / 2, player.pos.y - Renderer.windowHeight / 2 + player.h / 2);
			rm.cm.update(player.pos.x, player.pos.y);
		
			//check for door collisions
			rm.dm.update(this);
		}
		
		if(m.open && !gsmPaused || m.stop || m.start) {
			m.update();
		}
		
		//only update dialogue when in dialogue or stop animation
		if(db.open && !gsmPaused || db.stop || db.start) {
			db.update();
		}
	}

	/**RENDER**/
	public void render() {
		
		rm.cm.render(0); //render ground
		rm.cm.render(1); //render solid tiles
		rm.dm.render(); //render door hitbox
		rm.em.render(); //render entities
		rm.cm.render(2); //render foreground
		
		if(m.open || m.stop) {
			m.render();
		}
		
		//only render dialogue when in dialogue or stop animation
		if(db.open || db.stop) {
			db.render();
		}
		
		//screen transitions (black screen)
		if(exitingRegion || enteringRegion) {
			Graphics.setColor(0, 0, 0, blackScreenOpacity);
			Graphics.fillRect(0, 0, Renderer.unitsWide, Renderer.unitsHigh);
		}
	}
	
	/**during pauses, hault player movement**/
	public void pause() {
		player.forceWalkBuffer();
		player.setStaticAni();
		player.pause();
	}
	
	/**after pauses, resume player movement and dialogue**/
	public void unPause() {
		player.unPause();
		if(db.open) { db.unPause(); }
	}
	
	/**return the dialogue box's distance away from the starting height so that fps counter can stay above it**/
	public float getDialogueY() {
		return db.getY();
	}
	
	/**calculate black screen opacity during door transitions**/
	public void doorTransitions() {
		
		//if in door transition (exit), animate screen transition and nothing else
		if(exitingRegion) {
			double now = (double) System.nanoTime() / GameLoop.B;
			
			//black fades in for 1 door_time (from 0 to 1 black screen opacity)
			blackScreenOpacity = (float) ((now - transition_start_time) / door_time);
			if(blackScreenOpacity >= 1) {
				blackScreenOpacity = 1;
				exitingRegion = false;
				goToNewRegion();
			}
		}
	
		//if in door transition (enter), animate screen transition and nothing else
		if(enteringRegion) {
			double now = (double) System.nanoTime() / GameLoop.B;
			
			//remains solid black for 1 door_time (from 2 to 1 black screen opacity) and then
			//black fades away for 1 door_time (from 1 to 0 black screen opacity)
			blackScreenOpacity = (float) (2 - (now - transition_start_time) / door_time);
			if(blackScreenOpacity >= 1) {
				blackScreenOpacity = 1;
			}
			if(blackScreenOpacity <= 0) {
				blackScreenOpacity = 0;
				enteringRegion = false;
			}
		}
	}
	
	/**when a door is opened, release all keys, record new region values, and begin screen transition**/
	public void openDoor(Region newRegion, Position newRegionPos, int newRegionDir) {
		this.newRegion = newRegion;
		this.newRegionPos = newRegionPos;
		this.newRegionDir = newRegionDir;
		
		player.forceWalkBuffer();
		
		key.rAll();
		
		exitingRegion = true;
		transition_start_time = (double) System.nanoTime() / GameLoop.B;
		blackScreenOpacity = 0;
	}

	/**when a new region is entered, load new region, update world position, release all keys, and begin transition **/
	public void goToNewRegion() {
		rm.load(newRegion);
		player.setPos(newRegionPos);
		player.setDir(newRegionDir);
		
		Position.setWorldVar(player.pos.x - Renderer.windowWidth / 2 + player.w / 2, player.pos.y - Renderer.windowHeight / 2 + player.h / 2);
		rm.cm.update(player.pos.x, player.pos.y);
		
		key.rAll();
		
		enteringRegion = true;
		transition_start_time = (double) System.nanoTime() / GameLoop.B;
		blackScreenOpacity = 1;
	}
}
