package game.graphics.gui;

import game.audio.AudioResource;
import game.engine.KeyHandler;
import game.graphics.Graphics;
import game.graphics.ImageResource;
import game.items.Item;
import game.party.Party;

/**NOT DONE**/

public class Goods {
	
	private Party party;
	private Item[][] curInv;
	
	private int panelX = 142;
	private int panelY = 32;
	
	private int textX = panelX + 21;
	private int textY = panelY + 13;
	
	private int cursorX = panelX + 13;
	private int cursorY = panelY + 15;
	
	private ImageResource panel;
	
	public boolean open = false;
	
	private Cursor cursor;

	//cursor audio
	private AudioResource cursor1Audio;
	private AudioResource cursor2Audio;
	

	public Goods(Party party) {
		this.party = party;
		
		panel = new ImageResource("/gui/goods_panel.png");
		cursor = new Cursor("horizontal", cursorX, cursorY, 70, 14, 2, 8);
		
		//create audio clips
		cursor1Audio = new AudioResource("res/soundfx/cursor_1.wav");
		cursor2Audio = new AudioResource("res/soundfx/cursor_2.wav");
	}
	
	public void start() {
		open = true;
		curInv = party.getInventory(0);
		cursor.reset();
		cursor1Audio.play();
	}
	
	public void stop() {
		open = false;
		cursor2Audio.play();
	}
	
	public void stopAll() {
		open = false;
	}
	
	/**INPUT**/
	public void input(KeyHandler key) {
		if(key.back.pressed) {
			key.rAll();
			stop();
		}
		
		cursor.input(key);
	}
	
	/**UPDATE**/
	public void update() {
		cursor.update();
	}
	
	/**RENDER**/
	public void render() {
		Graphics.drawImage(panel, panelX, panelY, panel.getW(), panel.getH());
		renderInv();
		cursor.render();
	}
	
	private void renderInv() {
		
		for(int y=0; y<8; y++) {
			for(int x=0; x<2; x++) {
				Item curItem = curInv[x][y];
				if(curItem != null) {
					Graphics.drawTextImage(curItem.getTextImage(), textX+x*70, textY+y*14);
				}
			}
		}
	}
}
