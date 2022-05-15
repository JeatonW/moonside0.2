package game.engine;

import com.jogamp.newt.event.WindowEvent;
import com.jogamp.newt.event.WindowListener;
import com.jogamp.newt.event.WindowUpdateEvent;
import com.jogamp.newt.opengl.GLWindow;

public class WindowHandler implements WindowListener {
	
	/**upon construction, a window handler is added to the window**/
	public WindowHandler(GLWindow window) { window.addWindowListener(this); }
	
	@Override
	/**if the window is destroyed, stop the program**/
	public void windowDestroyed(WindowEvent e) { GameLoop.closeGame(); }
	
	@Override
	/**if the window is moved, pause the game loop**/
	public void windowMoved(WindowEvent e) { GameLoop.tempPauseLoop(); }

	@Override
	public void windowDestroyNotify(WindowEvent e) { /*do nothing*/ }
	@Override
	public void windowGainedFocus(WindowEvent e) { GameLoop.unPauseLoop(); }
	@Override
	public void windowLostFocus(WindowEvent e) { GameLoop.pauseLoop(); }
	@Override
	public void windowRepaint(WindowUpdateEvent e) { /*do nothing*/ }
	@Override
	public void windowResized(WindowEvent e) { /*do nothing*/ }
}
