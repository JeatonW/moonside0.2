package game.engine;

import game.graphics.EventListener;
import game.graphics.Graphics;
import game.graphics.Renderer;
import game.states.GameStateManager;

public class GameLoop {
	
	//there are one billion nano seconds in a second. system nanotime is measured in nano seconds.
	public static final int B = 1000000000;
	
	//state of the game loop
	private static boolean running = false;
	private static boolean tempPaused = false;
	private static boolean paused = false;
	private static boolean FPSchanged = true;
	
	//fps counter
	private static int frames = 0;
	public static int FPSCAP = 0;
	
	//the window, its input, and output
	private static WindowHandler win = null;
	private static KeyHandler key = null;
	private static GameStateManager gsm = null;
	private static Graphics g = null;
	
	public static void start() {
		
		/**thread that the game will run on**/
		Thread thread = new Thread() {
			
			//record time for current second so that fps can be measured
			double second_start = (double) System.nanoTime();
			int frameCount = 0;
			
			//what the game does while running
			public void run() {
				
				//game will stop when running is set to false
				running = true;
				
				/*create graphics and windows and stuff*/
				g = new Graphics();
				win = new WindowHandler(Renderer.window);
				key = new KeyHandler(Renderer.window);
				gsm = new GameStateManager(key);
				
				//cleanup memory space
				System.gc();
				
				//give the event listener gsm so that it may render gsm using opengl
				EventListener.gsm = gsm;
				
				//if there is an FPS cap, calculate how long each frame should last
				double frame_start_time;
				double time_per_frame;
				if(FPSCAP != 0) { time_per_frame = B / (double) FPSCAP; }
				else { time_per_frame = 0; }
				
				//game loop; each iteration is 1 frame
				while(running) {
					
					//pause the game loop while window is being moved
					while(tempPaused) { 
						tempPaused = false;
						try { Thread.sleep(100); }
						catch (InterruptedException e) { e.printStackTrace(); }
					}
					
					//pause the game loop while window is out of focus
					while(paused) {
						try { Thread.sleep(100); }
						catch (InterruptedException e) { e.printStackTrace(); }
					}
					
					//time at start of frame
					double now = (double) System.nanoTime();
					frame_start_time = now;
					
					//renderer takes input for fullscreen function (temporary)
					//Renderer.input(key);
					
					//do game stuff
					gsm.input(key);
					gsm.update();
					Renderer.render();
					
					//fps counter
					frameCount++;
					if(now >= second_start + B) {
						frames = frameCount;
						frameCount = 0;
						second_start = now;
						if(FPSchanged) {
							if(FPSCAP != 0) { time_per_frame = B / (double) FPSCAP; }
							else { time_per_frame = 0; }
							FPSchanged = false;
						}
					}
					
					//if there is an fps cap, wait until frame time is over before going to next frame
					if(FPSCAP != 0) {
						while(now - frame_start_time < time_per_frame) {
							now = (double) System.nanoTime();
						}
					}
				}
			}
		};
		thread.setName("GameLoop");
		thread.start();
	}
	
	/**get the number of frames that happened last second**/
	public static int getFPS() { return frames; }
	
	/**get fps cap**/
	public static String getFPSCap() {
		if(FPSCAP == 0) { return "Unlimited"; }
		return FPSCAP + "";
	}
	
	/**cycle through fps caps**/
	public static int f = 9;
	public static void cycleFPS() {
		f++;
		if(f == 10) { f = 0; }

		if(f == 0) { FPSCAP = 30; }
		if(f == 1) { FPSCAP = 60; }
		if(f == 2) { FPSCAP = 75; }
		if(f == 3) { FPSCAP = 100; }
		if(f == 4) { FPSCAP = 120; }
		if(f == 5) { FPSCAP = 144; }
		if(f == 6) { FPSCAP = 165; }
		if(f == 7) { FPSCAP = 240; }
		if(f == 8) { FPSCAP = 300; }
		if(f == 9) { FPSCAP = 0; }
		
		FPSchanged = true;
	}
	
	/**stop the game**/
	public static void closeGame() { running = false; }
	
	/**pause the game loop while the window is being moved across the user's screen**/
	public static void tempPauseLoop() { tempPaused = true; key.rAll(); }
	
	/**pause the game while the window is out of focus**/
	public static void pauseLoop() { paused = true; key.rAll(); }
	
	/**unpause the game when the window is brought back into focus**/
	public static void unPauseLoop() { paused = false; key.rAll(); }
	
		
}
