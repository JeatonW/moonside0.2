package game.party;

import game.graphics.SpriteSheet;
import game.items.Item;

/**NOT DONE OR IMPLEMENTED YET**/

public class Party {
	
	private PlayerCharacter[] party = {null, null, null, null};
	private PlayerCharacter ness;
	
	private int partySize = 0;
	
	/**by creating the party we create all characters that could be in the party**/
	public Party() {
		ness = new PlayerCharacter("Ness", 0);
	}
	
	/**add a player to the party according to their name**/
	public void joinParty(String name) {
		if(name == "Ness") {
			party[partySize] = ness;
			party[partySize].ss = new SpriteSheet("/sprites/ness.png", 16, 24, 2, 16);
		}
		
		partySize++;
		sortParty();
	}
	
	/**sort the party based off of their party priority**/
	private void sortParty() {
		
		if(partySize == 1) { return; }
		
		boolean sorted = false;
		while(!sorted) {
			sorted = true;
			for(int i=0; i<partySize-1; i++) {
				if(party[i].priority > party[i+1].priority) {
					sorted = false;
					PlayerCharacter temp = party[i];
					party[i] = party[i+1];
					party[i+1] = temp;
				}
			}
		}
	}
	
	//getters
	public SpriteSheet getSS(int character) { return party[character].ss; }
	public Item[][] getInventory(int character) { return party[character].inventory; }
	
	/**attempt to add an item to the lowest possible priority's inventory. return false if entire party inventory is full**/
	public boolean addItem(Item item) {
		for(int i=0; i<partySize; i++) {
			if(addItem(item, i)) { return true; }
		}
		return false;
	}
	
	/**attempt to add an item to a specified character's inventory. return false if inventory is full**/
	public boolean addItem(Item item, int character) { return party[character].addItem(item); }
	
	class PlayerCharacter {
		
		private int priority;
		private String name;
		private SpriteSheet ss;
		
		private int xSize = 2;
		private int ySize = 8;
		private Item[][] inventory = new Item[xSize][ySize];
		
		private PlayerCharacter(String name, int priority) {
			this.name = name;
			this.priority = priority;
		}
		
		private boolean addItem(Item item) {
			for(int y=0; y<ySize; y++) {
				for(int x=0; x<xSize; x++) {
					if(inventory[x][y] == null) {
						inventory[x][y] = item;
						return true;
					}
				}
			}
			return false;
		}
	}
}
