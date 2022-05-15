package game.items;

import game.graphics.TextImage;

/**NOT DONE**/

public abstract class Item {
	
	private String name;
	private TextImage textImg;
	
	public Item(String name) {
		this.name = name;
		this.textImg = new TextImage(70, 16);
		this.textImg.drawWord(name, 0, 0, "left");
	}
	
	public static Item createItem(String name) {
		if(name == "Cookie") {
			return new Cookie(name);
		}
		else {
			return null;
		}
	}
	
	public String getName() { return name; }
	public TextImage getTextImage() { return textImg; }
}

abstract class Consumable extends Item {

	private int healHP;
	private int healPP;
	
	public Consumable(String name, int healHP, int healPP) {
		super(name);
		this.healHP = healHP;
		this.healPP = healPP;
	}
	
}

class Cookie extends Consumable {
	
	public Cookie(String name) {
		super(name, 10, 0);
	}
}
