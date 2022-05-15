package game.states;

import game.audio.AudioResource;
import game.engine.GameLoop;
import game.engine.KeyHandler;
import game.entities.HitBox;
import game.graphics.Graphics;
import game.graphics.Renderer;
import game.graphics.TextImage;
import game.graphics.gui.Cursor;
import game.graphics.gui.TickBox;
import game.party.Party;
import game.region.chunks.ChunkManager;

/**create an array of states**/
public class GameStateManager {
	
	//initialize game states
	public static final short OPENING = 0;
	public static final short FILESELECT = 1;
	public static final short NEWGAME = 2;
	public static final short PLAY = 3;
	public static final short BATTLE = 4;
	public static final short EVENT = 5;
	
	private KeyHandler key;
	private PlayState ps;
	private Cursor cursor;
	
	private Party party;
	
	//enter settings menu (pause game)
	private boolean pauseGame = false;
	
	//text image for "FPS:"
	private TextImage fpsCounterImg;
	
	//how many options are in the pause menu
	private int optionCount = 6;
	
	//settings menu text images
	private TextImage options;
	private TextImage showFPS;
	private TextImage FPSCap;
	private TextImage fullScreen;
	private TextImage renderHitBoxes;
	private TextImage renderSolidTileMap;
	private TextImage exitGame;
	
	//tickboxes for the pause menu
	private TickBox tickBox[] = new TickBox[optionCount];
	
	//option to show fps counter
	private boolean optionShowFPS = false;
	
	//settings menu gui values
	private int xOffset = 18;
	private int yOffset = 8;

	//did the player just pause or unpause?
	private boolean pauseTransition = false;
	private boolean unPauseTransition = false;
	
	//length of pause transition
	private double pause_time = 0.1;
	
	//pause screen opacity
	private float pauseScreenOpacity = 0;
	
	//start time of pause transition
	private double pause_start_time = 0;
	
	//cursor accept audio in settings menu
	private AudioResource cursor1Audio;
	private AudioResource cursor2Audio;
	
	public GameStateManager(KeyHandler key) {
		
		this.party = new Party();
		party.joinParty("Ness");
		
		this.key = key;
		this.cursor = new Cursor("horizontal", 10, 10, 16, 16, 1, optionCount);
		ps = new PlayState(party, key);
		
		//audio clip for accept key
		cursor1Audio = new AudioResource("res/soundfx/cursor_1.wav");
		cursor2Audio = new AudioResource("res/soundfx/cursor_2.wav");
		
		//fps counter image
		fpsCounterImg = new TextImage(40, 16);
		fpsCounterImg.drawWord("FPS:", 0, 0, "left");
		
		//pause menu option text images
		options = new TextImage(120, 16*6);
		options.drawWord("Show FPS", 0, 0, "left");
		options.drawWord("FPS Cap:", 0, 16, "left");
		options.drawWord("Fullscreen", 0, 16*2, "left");
		options.drawWord("Render HitBoxes", 0, 16*3, "left");
		options.drawWord("Render Solid Tile Map", 0, 16*4, "left");
		options.drawWord("Exit Game", 0, 16*5, "left");
		
		//pause menu option tickboxes
		tickBox[0] = new TickBox(optionShowFPS, 63, 10);
		tickBox[1] = null;
		tickBox[2] = new TickBox(false, 62, 10+16*2);
		tickBox[3] = new TickBox(false, 90, 10+16*3);
		tickBox[4] = new TickBox(false, 111, 10+16*4);
		tickBox[5] = null;
	}
	
	/**INPUT**/
	public void input(KeyHandler key) {
		
		//do not take in input during pause transitions
		if(pauseTransition || unPauseTransition) { return; }
		
		//if pause key is pressed, pause/unpause.
		checkPauseKey();
		
		//do not give input to anything other than the pause menu while the game is paused
		if(pauseGame) {
			pauseMenu();
			return;
		}
		
		//send input to playstate
		ps.input(key);
	}
	
	/**UPDATE**/
	public void update() {
		
		//perform pause transitions if in transition phase
		pauseTransitions();
		
		//do not update anything other than the pause menu while the game is paused
		if(pauseGame) {
			cursor.update();
		}
		
		//send update to playstate
		ps.update(pauseGame);
	}
	
	/**RENDER**/
	public void render() {
		
		//render current state
		ps.render();
		
		//render pause menu
		if(pauseGame) {
			
			//black screen
			Graphics.setColor(0, 0, 0, pauseScreenOpacity/1.15f);
			Graphics.fillRect(0, 0, Renderer.unitsWide, Renderer.unitsHigh);
			
			//option names
			Graphics.setColor(1, 1, 1, pauseScreenOpacity);
			Graphics.drawTextImage(options, xOffset, yOffset);
			
			//tickboxes
			Graphics.setColor(1, 1, 1, pauseScreenOpacity);
			tickBox[0].render();
			Graphics.setColor(1, 1, 1, pauseScreenOpacity);
			tickBox[2].render();
			Graphics.setColor(1, 1, 1, pauseScreenOpacity);
			tickBox[3].render();
			Graphics.setColor(1, 1, 1, pauseScreenOpacity);
			tickBox[4].render();
			
			//fps cap
			Graphics.setColor("lime");
			Graphics.drawWord(GameLoop.getFPSCap(), xOffset+40, yOffset+16, "left");
			Graphics.resetColor();

			//cursor
			Graphics.setColor(1, 1, 1, pauseScreenOpacity);
			cursor.render();
		}
		else {
			
			//render fps counter
			if(optionShowFPS) {
				float yOffset = ps.getDialogueY();
				Graphics.setColor("transparentblack");
				Graphics.fillRect(2, 180-18-yOffset, 48, 16);
				Graphics.drawTextImage(fpsCounterImg, 6, 180-16-yOffset);
				String fps = ("" + GameLoop.getFPS());
				Graphics.drawWord(fps, 47, 180-16-yOffset, "right");
			}
		}
	}
	
	/**input to pause menu**/
	public void pauseMenu() {
		
		//give input to cursor so it can move
		cursor.input(key);
		
		//if accept is pressed, reset keys, perform option action, and toggle tickbox if needed.
		if(key.accept.pressed) {
			key.rAll();
			int option = cursor.getY();
			if(tickBox[option] != null) { tickBox[option].toggle(); }
			if(option == 0) {
				optionShowFPS = !optionShowFPS;
				if(tickBox[0].getState()) { cursor1Audio.play(); }
				else { cursor2Audio.play(); }
			}
			else if(option == 1) {
				GameLoop.cycleFPS();
				cursor1Audio.play();
			}
			else if(option == 2) {
				Renderer.fullScreen();
				if(tickBox[2].getState()) { cursor1Audio.play(); }
				else { cursor2Audio.play(); }
			}
			else if(option == 3) {
				HitBox.renderHitBoxes();
				if(tickBox[3].getState()) { cursor1Audio.play(); }
				else { cursor2Audio.play(); }
			}
			else if(option == 4) {
				ChunkManager.renderSTM();
				if(tickBox[4].getState()) { cursor1Audio.play(); }
				else { cursor2Audio.play(); }
			}
			else if(option == 5) { GameLoop.closeGame(); }
		}
	}
	
	/**check to see if the pause key has been pressed, and if so, start pause transition**/
	public void checkPauseKey() {
		
		//pause the game, release all keys, and start pause timer
		if(key.escape.pressed && !pauseGame) {
			pauseGame = true;
			key.rAll();
			ps.pause();
			cursor.reset();
			pause_start_time = (double) System.nanoTime() / GameLoop.B;
			pauseTransition = true;
			pauseScreenOpacity = 0;
		}
		
		//unpause the game, release all keys, and start pause timer
		else if(key.escape.pressed && pauseGame) {
			pause_start_time = (double) System.nanoTime() / GameLoop.B;
			unPauseTransition = true;
			pauseScreenOpacity = 1;
		}
	}
	
	/**calculate pause screen opacity during pause transitions**/
	public void pauseTransitions() {
		
		//increase pause screen opacity until it gets to 1 after opening pause menu
		if(pauseTransition) {
			double now = (double) System.nanoTime() / GameLoop.B;
			pauseScreenOpacity = (float) ((now - pause_start_time) / pause_time);
			
			//after pause screen opacity reaches one, end transition
			if(pauseScreenOpacity >= 1) {
				pauseScreenOpacity = 1;
				pauseTransition = false;
			}
		}
		
		//decrease pause screen opacity until it gets to 0 after closing pause menu
		if(unPauseTransition) {
			double now = (double) System.nanoTime() / GameLoop.B;
			pauseScreenOpacity = (float) (1 - (now - pause_start_time) / pause_time);

			//after pause screen opacity reaches zero, end transition and unpause the game
			if(pauseScreenOpacity <= 0) {
				pauseGame = false;
				key.rAll();
				ps.unPause();
				pauseScreenOpacity = 0;
				unPauseTransition = false;
			}
		}
	}
}