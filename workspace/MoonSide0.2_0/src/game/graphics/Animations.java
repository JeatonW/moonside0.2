package game.graphics;

import game.engine.GameLoop;

public class Animations {
	
	//spritesheet and number of sprites on x and y axis
	private SpriteSheet ss;
	private int aniCount;
	private int aniLength;
	
	//current animation (row of sprites) selected
	private ImageResource[] currentAni;
	
	//which image in the current animation we are currently on
	private int aniIndex;
	private ImageResource currentImg;
	
	//how long it takes before the image is changed
	private float ani_time;
	private double cur_time;
	private double start_time;
	
	//whether or not the entity is a static image or being animated
	private boolean isStatic = false;
	
	/**a set of animations uses a spritesheet and counts num of sprite in the x and y axis**/
	public Animations(SpriteSheet ss) {
		this.ss = ss;
		aniCount = ss.getH();
		aniLength = ss.getW();
	}
	
	/**select an animation from the spritesheet and give the animation a time between each image**/
	public void setAnimation(int y, float t) {
		currentAni = ss.getSpriteArray(y);
		currentImg = ss.getImgRes(0, y);
		ani_time = t;
		start_time = (double) System.nanoTime() / GameLoop.B;
		isStatic = false;
		aniIndex = 0;
	}
	
	/**set the entity's animation to a static image**/
	public void setStatic(int y, int x) {
		currentAni = ss.getSpriteArray(y);
		currentImg = ss.getImgRes(x, y);
		ani_time = 0;
		isStatic = true;
		aniIndex = x;
	}
	
	/**if the entity was already in an animation, but we want to set a static image within that specific animation**/
	public void setCurStatic(int x) {
		currentImg = currentAni[x];
		ani_time = 0;
		isStatic = true;
		aniIndex = x;
	}
	
	/**if the entity's animation is not static, change to the next image after specified time**/
	public void update() {
		
		if(!isStatic) {
			cur_time = (double) System.nanoTime() / GameLoop.B;
			
			//if enough time has passed, restart timer and go to next image
			if(cur_time - start_time > ani_time) {
				aniIndex++;
				if(aniIndex == aniLength) { aniIndex = 0; }
				currentImg = currentAni[aniIndex];
				start_time = cur_time;
			}
		}
	}
	
	/**draw current image of the animation**/
	public void render(float x, float y) {
		Graphics.drawImage(currentImg, x, y, ss.w, ss.h);
	}
}
