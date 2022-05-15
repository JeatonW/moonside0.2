package game.region;

import game.entities.EntityManager;
import game.entities.HitBox;
import game.entities.Player;
import game.entities.Sign;
import game.graphics.gui.Dialogue;
import game.region.Regions.Region;
import game.region.chunks.ChunkManager;

public class RegionManager {
	
	public ChunkManager cm;
	public DoorManager dm;
	public EntityManager em;
	public Regions r;
	public Player player;

	/**begin the playstate by loading the debug room**/
	public RegionManager(Player player) {
		this.player = player;
		r = new Regions();
		load(r.debug);
	}
	
	/**load a new region (opening doors)**/
	public void load(Region r) {
		
		//reset the door and entity managers
		dm = new DoorManager();
		em = new EntityManager();

		//load the correct region
		String name = r.getName();
		if(name == "Debug") { loadDebug(); }
		else if(name == "Test House") { loadTestHouse(); }
		
		//update em for player
		player.setEM(em);
		
		//update the STM
		HitBox.stm = cm.getSTM();
		
		//cleanup
		System.gc();
	}
	
	/**DEBUG**/
	private void loadDebug() {
		cm = new ChunkManager(r.debug);
		
		em.add(player);
		
		Dialogue signDialogue = 
				//new Dialogue("Here is the inital dialogue before the question. Random text, going onto the next line for testing.",
				new Dialogue("I am asking a question?", 
				new Dialogue("Yes", "You answered yes.", 
						new Dialogue("This is the response after yes."), 
							"No", "You answered no.", 
						new Dialogue("This is the response after no.")
		));
		
		em.add(new Sign(new Position(300, 120), signDialogue));
		
		dm.addDoor(new Position(178, 236), r.test_house, new Position(78, 50), 8, 8, player, 2);
	}

	/**TESTHOUSE**/
	private void loadTestHouse() {
		cm = new ChunkManager(r.test_house);
		
		em.add(player);
		
		dm.addDoor(new Position(98, 66), r.debug, new Position(178, 228), 10, 10, player, 1);
	}
	
}
