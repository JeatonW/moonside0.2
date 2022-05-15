package game.engine;

import java.util.ArrayList;

import com.jogamp.newt.event.InputEvent;
import com.jogamp.newt.event.KeyEvent;
import com.jogamp.newt.event.KeyListener;
import com.jogamp.newt.opengl.GLWindow;

/**make a key handler to hold all the game's key states**/
public class KeyHandler implements KeyListener {

	/**a list of keys that can be in the pressed or unpressed state**/
	public ArrayList<Key> keys = new ArrayList<Key>();
	public class Key {
		public boolean pressed = false;
		public Key() { keys.add(this); }
		private void toggle(boolean pressed) { if(pressed != this.pressed) { this.pressed = pressed; } }
	}
		
	//all keys being used and the size of the list
	public Key up = new Key();
	public Key down = new Key();
	public Key left = new Key();
	public Key right = new Key();
	public Key accept = new Key();
	public Key back = new Key();
	public Key menu = new Key();
	public Key escape = new Key();
	
	/**upon construction, add this key handler object to the opengl window**/
	public KeyHandler(GLWindow window) { window.addKeyListener(this); }
	
	/**manual release for certain keys**/
	public void rAll() { for(int i = 0; i < keys.size(); i++) { keys.get(i).pressed = false; } }
	public void rBack() { back.pressed = false; }
	public void rAcc() { accept.pressed = false; }
	public void rEsc() { escape.pressed = false; }
	
	/**toggle a key's pressed state**/
	public void toggle(KeyEvent e, boolean pressed) {
		if(e.getKeyCode() == KeyEvent.VK_W) up.toggle(pressed);
		if(e.getKeyCode() == KeyEvent.VK_S) down.toggle(pressed);
		if(e.getKeyCode() == KeyEvent.VK_A) left.toggle(pressed);
		if(e.getKeyCode() == KeyEvent.VK_D) right.toggle(pressed);
		if(e.getKeyCode() == KeyEvent.VK_L) accept.toggle(pressed);
		if(e.getKeyCode() == KeyEvent.VK_SEMICOLON) back.toggle(pressed);
		if(e.getKeyCode() == KeyEvent.VK_QUOTE) menu.toggle(pressed);
		if(e.getKeyCode() == KeyEvent.VK_ESCAPE) escape.toggle(pressed);
	}
	
	/**if key is pressed, toggle key and avoid repeats from key being held**/
	@Override
	public void keyPressed(KeyEvent e) { if((InputEvent.AUTOREPEAT_MASK & e.getModifiers()) == 0) { toggle(e, true); } }

	/**if key is released, toggle key and avoid repeats from key being held**/
	@Override
	public void keyReleased(KeyEvent e) { if((InputEvent.AUTOREPEAT_MASK & e.getModifiers()) == 0) { toggle(e, false); } }
}

