package game.engine;

import game.graphics.Renderer;

/**entry point**/
public class Main {
	public static void main(String[] args) {
		
		//create the window, then start the game in the window
		Renderer.init();
		GameLoop.start();
	}
}
