package game.graphics.gui;

import game.audio.AudioResource;
import game.engine.GameLoop;
import game.engine.KeyHandler;
import game.graphics.Graphics;
import game.graphics.ImageResource;
import game.graphics.TextImage;
import game.party.Party;
import game.states.PlayState;

/**NOT DONE**/

public class Menu {
	//panel image
	private ImageResource panel;
	
	//position of panel image on screen
	private int panelX = 10;
	private int panelY;
	
	//position of text image on screen
	private int textX = panelX + 20;
	private int textY;
	
	//starting position of cursor on screen
	private int cursorX = panelX + 12;
	private int cursorY;
	
	//distance from start (slide animation)
	private float y = 0;
	private int maxY;
	
	//slide animation time
	private double slide_time = 0.15f;
	private double slide_start_time;
	
	//states
	public boolean open = false;
	public boolean start = false;
	public boolean stop = false;
	public boolean stopAll = false;

	//cursor audio
	private AudioResource cursor1Audio;
	private AudioResource cursor2Audio;
	
	//image for text on menu panel
	private TextImage textImg;
	
	//cursor for menu
	private Cursor cursor;
	
	//other panels that the menu can open
	private Goods goods;
	
	public Menu(Party party) {
		
		//create panel image
		panel = new ImageResource("/gui/menu_panel.png");
		
		//determine y values based off of panel height
		maxY = panel.getH();
		panelY = -panel.getH();
		textY = panelY + 7;
		cursorY = panelY + 9;
		
		//create audio clips
		cursor1Audio = new AudioResource("res/soundfx/cursor_1.wav");
		cursor2Audio = new AudioResource("res/soundfx/cursor_2.wav");
		
		//create text image
		textImg = new TextImage(70, 30);
		textImg.drawWord("Goods", 0, 0, "left");
		textImg.drawWord("Equip", 40, 0, "left");
		textImg.drawWord("PSI", 0, 14, "left");
		textImg.drawWord("Status", 40, 14, "left");
		
		//create cursor
		cursor = new Cursor("horizontal", cursorX, cursorY, 40, 14, 2, 2);
		
		//create panels that the menu can open
		goods = new Goods(party);
	}
	
	/**open menu panel**/
	public void start() {
		y = 0;
		slide_start_time = (double) System.nanoTime() / GameLoop.B;
		start = true;
		open = true;
		cursor1Audio.play();
		cursor.reset();
	}
	
	/**close menu panel if in menu panel**/
	public void stop() {
		y = maxY;
		slide_start_time = (double) System.nanoTime() / GameLoop.B;
		stop = true;
		open = false;
		cursor2Audio.play();
	}
	
	/**close all panels**/
	public void stopAll() {
		if(goods.open) {
			goods.stopAll();
			stopAll = true;
		}
		stop();
	}
	
	/**INPUT**/
	public void input(KeyHandler key, PlayState ps) {
		
		//do not accept input if in animation
		if(start || stop) { return; }
		
		//reset cursor if returning to menu
		if(key.back.pressed) {
			if(goods.open) {
				cursor.reset();
			}
		}
		
		//close entire menu and all panels
		if(key.menu.pressed) {
			key.rAll();
			stopAll();
			ps.unPause();
		}
		
		//if goods is open, only accept input for goods
		if(goods.open) {
			goods.input(key);
			return;
		}
		
		//open next panel depending on cursor pos
		if(key.accept.pressed) {
			int cursorPosX = cursor.getX();
			int cursorPosY = cursor.getY();
			
			if(cursorPosX == 0 && cursorPosY == 0) {
				goods.start();
			}
		}
		
		//close menu if in menu
		if(key.back.pressed) {
			key.rAll();
			stop();
			ps.unPause();
		}
		
		//give cursor input
		cursor.input(key);
	}
	
	
	/**UPDATE**/
	public void update() {
		
		//start animation
		if(start) {
			double now = (double) System.nanoTime() / GameLoop.B;
			y = (float) ((now - slide_start_time) * maxY / slide_time);
			
			//end start animation
			if(y >= maxY) {
				y = maxY;
				start = false;
			}
		}
		
		//stop animation
		if(stop) {
			double now = (double) System.nanoTime() / GameLoop.B;
			y = (float) (maxY - (now - slide_start_time) * maxY / slide_time);
			
			//end stop animation
			if(y <= 0) {
				y = 0;
				stop = false;
				stopAll = false;
			}
		}
		
		//update goods if it is opened and skip menu cursor
		if(goods.open) {
			goods.update();
			return;
		}
		
		//update cursor
		cursor.update();
	}
	
	/**RENDER**/
	public void render() {
		
		//render menu
		Graphics.drawImage(panel, panelX, panelY+y, panel.getW(), panel.getH());
		Graphics.drawTextImage(textImg, textX, textY+y);
		
		//render goods if it is open and skip menu cursor
		if(goods.open) {
			goods.render();
			return;
		}
		
		//render cursor
		if(!stopAll) { cursor.render(0, y); }
	}
}
