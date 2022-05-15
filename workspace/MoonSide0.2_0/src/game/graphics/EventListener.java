package game.graphics;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;

import game.states.GameStateManager;

/**basically, use opengl and draw**/
public class EventListener implements GLEventListener{ 
	
	public static GL2 gl2 = null;
	public static GL gl = null;
	public static GameStateManager gsm = null;
	
	public void display(GLAutoDrawable drawable) {
		gl2 = drawable.getGL().getGL2();
		gl2.glClear(GL2.GL_COLOR_BUFFER_BIT);
		if(gsm != null) { gsm.render(); }
		gl2.glFlush();
	}

	public void dispose(GLAutoDrawable drawable) {}

	public void init(GLAutoDrawable drawable) {
		gl2 = drawable.getGL().getGL2();
		gl2.setSwapInterval(0);
		gl2.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MAG_FILTER, GL2.GL_NEAREST);
		gl2.glClearColor(0, 0, 0, 1);
		gl2.glEnable(GL2.GL_TEXTURE_2D);
		gl2.glFlush();
	}

	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
		GL2 gl2 = drawable.getGL().getGL2();
		gl2.glMatrixMode(GL2.GL_PROJECTION);
		gl2.glLoadIdentity();
		gl2.glOrtho(0, Renderer.unitsWide, Renderer.unitsHigh, 0, -1, 1);
		gl2.glMatrixMode(GL2.GL_MODELVIEW);
		gl2.glFlush();
	}

}

