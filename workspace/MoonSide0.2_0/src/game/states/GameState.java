package game.states;

import game.engine.KeyHandler;

/**game state skeleton**/
public abstract class GameState {
	public abstract void input(KeyHandler key);
	public abstract void update(boolean gsmPaused);
	public abstract void render();
}
