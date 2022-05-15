package game.graphics;

import java.awt.Toolkit;

import com.jogamp.newt.opengl.GLWindow;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLProfile;

import game.engine.KeyHandler;

public class Renderer {
	
	//instantiate window and profile
	public static GLWindow window = null;
	private static GLProfile profile = null;
	
	//fullscreen state
	private static boolean fullscreen = false;
	
	//window dimensions
	public static int windowWidth = 320;
	public static int windowHeight = 180;
	public static final int unitsWide = 320;
	public static final int unitsHigh = 180;

	//user's screen dimensions
	public static int screenWidth = (int) Toolkit.getDefaultToolkit().getScreenSize().getWidth();
	public static int screenHeight = (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight();
	
	/**a window is created**/
	public static void init() {
		
		//allow opengl capabilities on this profile
		GLProfile.initSingleton();
		profile = GLProfile.get(GLProfile.GL2);
		GLCapabilities  caps = new GLCapabilities(profile);
		
		window = GLWindow.create(caps); //create an opengl window
		window.setPosition(screenWidth/2 - windowWidth*2, screenHeight/2 - windowHeight*2); //position of the window on user's screen (center)
		window.setSize(windowWidth*4, windowHeight*4); //set window size
		window.setResizable(false); //user cannot resize window
		window.setTitle("MoonSide 0.2_0"); //title
		window.addGLEventListener(new EventListener()); //add gleventlistener to opengl window
		window.setVisible(true); //make window visible
	}
	
	public static GLProfile getProfile() { return profile; }
	
	/**how the window reacts to input**/
	public static void fullScreen() {

		//turn fullscreen off
		if(fullscreen) {
			window.setFullscreen(false);
			window.setPointerVisible(true);
			fullscreen = false;
		}
		
		//turn fullscreen on
		else {
			window.setFullscreen(true);
			window.setPointerVisible(false);
			fullscreen = true;
		}
	}

	/**render whats in the window**/
	public static void render() {
		
		//make sure window and eventlistener exist
		if(window == null) { return; }
		
		//display window
		window.display();
	}
}
