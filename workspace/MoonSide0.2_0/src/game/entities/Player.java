package game.entities;

import java.util.ArrayList;

import game.engine.GameLoop;
import game.engine.KeyHandler;
import game.graphics.Animations;
import game.graphics.Graphics;
import game.graphics.SpriteSheet;
import game.graphics.gui.Dialogue;
import game.region.Position;
import game.region.chunks.ChunkManager;

public class Player extends Entity {
	
	//width and height of player sprite
	public int w = 16;
	public int h = 24;
	
	//current animation of the player
	private Animations ani;
	
	//get entity manager for player so it can check entity collision
	private EntityManager em;
	
	public PlayerInteract pi;
	
	//how long time-wise the player will move before being able to input
	//another direction (walk cycles). this is to maintain pixel alignment.
	private double walk_cur_time = 0;
	
	//how many pixels will player move in one second? ***player must be fast enough to walk at least 1 pixel per cycle
	private int walkSpeed = 80;
	//how far the player will move in a single frame (convert from time to distance)
	private float frameDist = 0;
	
	//which direction a player is moving during a walk cycle
	private boolean up = false, down = false, left = false, right = false;
	
	//if we are currently in the walk buffer phase. walk buffers happen when the player lets go of a movement key. character must continue walking to the next whole pixel to avoid pixel misalignment.
	public boolean walkBuffer = false;
	
	//used to know whether or not positioning needs to be corrected in order to not get stuck at the end point of a diagonal wall that has a solid square tile connected to it
	private boolean collideDiagLast = false;
	
	//how must distance left before the next pixel
	private float bufferDist = 0f;
	
	//keep track of the direction of the last walk cycle so we know to change the animation if need be
	private boolean[] lastDir = {false, false, false, false};
	
	/**the entity that the player controls**/
	public Player(SpriteSheet ss, Position pos) {
		super(pos, new HitBox(pos, 16, 16, 8), 16, 24);
		
		pi = new PlayerInteract(pos);
		pi.setDir(1);
		
		//player animations
		ani = new Animations(ss);
		ani.setStatic(1, 1);
	}
	
	public Dialogue getDialogue() { return null; }
	
	public void setPos(Position pos) {
		this.pos = pos;
		this.hb.pos = pos;
		pi.setPos(pos);
	}
	
	public void setDir(int dir) {
		up = lastDir[0] = down = lastDir[1] = left = lastDir[2] = right = lastDir[3] = false;
		ani.setStatic(dir, 1);
		pi.setDir(dir);
		walk_cur_time = 0;
	}
	
	public void setStaticAni() {
		ani.setCurStatic(1);
	}
	
	public void pause() {
		up = lastDir[0] = down = lastDir[1] = left = lastDir[2] = right = lastDir[3] = false;
		walk_cur_time = 0;
	}
	
	public void setEM(EntityManager em) {
		this.em = em;
		pi.setEM(em);
	}
	
	public void unPause() {
		ani.setCurStatic(1);
	}
	
	public void forceWalkBuffer() {
		walkBuffer = true;
	}
	
	/**INPUT**/
	public void input(KeyHandler key) {

		//calculate the distance the player should move on this frame
		calcFrameDist();
		
		//do not take inputs during a walk buffer
		if(!walkBuffer) {
			
			//read key inputs and determine player direction
			if(key.up.pressed && !key.down.pressed) { up = true; }
			else { up = false; }
			if(key.down.pressed && !key.up.pressed) { down = true; }
			else { down = false; }
			if(key.left.pressed && !key.right.pressed) { left = true; }
			else { left = false; }
			if(key.right.pressed && !key.left.pressed) { right = true; }
			else { right = false; }
		}
	}
	
	/**UPDATE**/
	public void update() {

		//will calculate walk buffer distance if we are in a walk buffer
		calcBuffDist();
		
		//will move the player based on frame distance
		move();
		
		//make sure player interact is where the player is facing
		setPIDir();
		
		//update the pi (check collision)
		pi.update();
		
		//will animate the player's sprites
		animate();
		
		//keep track of this frame's walk direction
		if(!walkBuffer) {
			lastDir[0] = up;
			lastDir[1] = down;
			lastDir[2] = left;
			lastDir[3] = right;
		}
	}
	
	/**RENDER**/
	public void render() {
		
		//render player
		ani.render(pos.getRenX(), pos.getRenY());
		
		//render hitbox
		hb.render("red");
		
		//render player interact
		pi.render();
		
	}
	
	/**calculates the distance the player should move on this frame**/
	public void calcFrameDist() {
		
		//how long was this frame? if this was the FIRST frame of a new area, frameDist should be 0 (lastwalk = curwalk)
		double last_walk_cur_time;
		double now = (double) System.nanoTime() / GameLoop.B;
		if(walk_cur_time != 0) { last_walk_cur_time = walk_cur_time; }
		else { last_walk_cur_time = now; }
		walk_cur_time = now;
		
		//normal movement
		if(!walkBuffer) {
			
			//determine distance walked this frame based on walking speed (diagonal or straight)
			if(up && left || up && right || down && left || down && right) { frameDist = (float) ((walk_cur_time - last_walk_cur_time) * walkSpeed*0.75f); }
			else { frameDist = (float) ((walk_cur_time - last_walk_cur_time) * walkSpeed); }
		}
		
		//automated movement during walk buffer
		if(walkBuffer) {
			
			//determine distance walked this frame based on walking speed (diagonal or straight)
			if(lastDir[0] && lastDir[2] || lastDir[0] && lastDir[3] || lastDir[1] && lastDir[2] || lastDir[1] && lastDir[3]) { frameDist = (float) ((walk_cur_time - last_walk_cur_time) * walkSpeed*0.75f); }
			else { frameDist = (float) ((walk_cur_time - last_walk_cur_time) * walkSpeed); }
		}
	}
	
	/**calculates the exact distance the player needs to walk to get to the next whole pixel**/
	public void calcBuffDist() {
		
		//calculate buffer distance if direction has changed, but don't calculate if the player was standing still
		if((up != lastDir[0] || down != lastDir[1] || left != lastDir[2] || right != lastDir[3]) && !walkBuffer) {
			if(lastDir[0] || lastDir[1] || lastDir[2] || lastDir[3]) {
				
				//walk buffer has been enabled
				walkBuffer = true;
				
				//the largest distance to the next pixel in any direction takes priority.
				//buffer distances of 1 are ignored (they count as 0 in the negative direction)
				bufferDist = 0;
				if(lastDir[0]) {
					float temp = pos.y - (float) Math.floor(pos.y);
					if(temp > bufferDist && temp != 1) { bufferDist = temp; }
				}
				if(lastDir[1]) {
					float temp = 1 - (pos.y - (float) Math.floor(pos.y));
					if(temp > bufferDist && temp != 1) { bufferDist = temp; }
				}
				if(lastDir[2]) {
					float temp = pos.x - (float) Math.floor(pos.x);
					if(temp > bufferDist && temp != 1) { bufferDist = temp; }
				}
				if(lastDir[3]) {
					float temp = 1 - (pos.x - (float) Math.floor(pos.x));
					if(temp > bufferDist && temp != 1) { bufferDist = temp; }
				}
				
				//at lower framerates (larger frame distances), average buffer distance should be spoofed according to frame distance
				//to display smoother movement (the game wont waste an entire frame at 15fps to move the player only 1 pixel)
				bufferDist += Math.floor(frameDist);
			}
		}
	}
	
	/**moves the player**/
	public void move() {
		
		//normal movement; check for collision and move player with player's current direction
		if(!walkBuffer) {
			if(up) { calcMove(-frameDist, 'y', left, right); }
			if(down) { calcMove(frameDist, 'y', right, left); }
			if(left) { calcMove(-frameDist, 'x', up, down); }
			if(right) { calcMove(frameDist, 'x', down, up); }
		}
		
		//automated movement during walk buffer
		if(walkBuffer) {
			
			//buffer counter
			if(bufferDist - frameDist <= 0.001) {
				frameDist = bufferDist;
				walkBuffer = false;
				bufferDist = 0;
			}
			else {
				bufferDist -= frameDist;
			}
			
			//move with the player's previous direction
			if(lastDir[0]) { calcMove(-frameDist, 'y', lastDir[2], lastDir[3]); }
			if(lastDir[1]) { calcMove(frameDist, 'y', lastDir[3], lastDir[2]); }
			if(lastDir[2]) { calcMove(-frameDist, 'x', lastDir[0], lastDir[1]); }
			if(lastDir[3]) { calcMove(frameDist, 'x', lastDir[1], lastDir[0]); }
			
			//if walkbuffer just ended, ensure that the player position is exactly a whole pixel (int)
			if(!walkBuffer) {
				pos.x = Math.round(pos.x);
				pos.y = Math.round(pos.y);
			}
		}
	}
	
	/**calculates where the player should move based on frameDist and collision**/
	public void calcMove(float frameDistAndDir, char axis, boolean downUpDiag, boolean upDownDiag) {
		
		//determine if the player will collide with a tile due to movement, and if so, what tile type.
		//(also gathers coordinates for diagonal tiles to use in calculations)
		int tileType = 0;
		int collisionAnswer[] = new int[3];
		if(axis == 'x') { collisionAnswer = hb.collideTile(frameDistAndDir, 0); }
		else if(axis == 'y') { collisionAnswer = hb.collideTile(0, frameDistAndDir); }
		tileType = collisionAnswer[0];
		
		//determine if the player will collide with any entity on the screen due to movement.
		//remembers which entity is colliding so player can align to the edge of the entity.
		boolean collidingEntity = false;
		Entity entity = null;
		
		//get the entity list and check for collision of every entity on the screen EXCEPT for player
		//(do not check collision with self)
		ArrayList<Entity> entityList = em.getList();
		for(int i=0; i<entityList.size(); i++) {
			Entity curEntity = entityList.get(i);
			if(curEntity.pos.isOnScreen() && curEntity != this) {
				
				//if collision is found, remember entity and break loop
				if(axis == 'x') {
					collidingEntity = hb.collideEnt(curEntity.hb, frameDistAndDir, 0);
					entity = curEntity;
					if(collidingEntity) { break; }
				}
				if(axis == 'y') {
					collidingEntity = hb.collideEnt(curEntity.hb, 0, frameDistAndDir);
					entity = curEntity;
					if(collidingEntity) { break; }
				}
			}
			
			//if no collision was found
			collidingEntity = false;
		}
		
		//no collision
		if(tileType == 0 && !collidingEntity) {
			
			//move normally
			if(axis == 'x') { pos.x += frameDistAndDir; }
			else if(axis == 'y') { pos.y += frameDistAndDir; }

			//used for diagonal endpoint to square tile special case. this frame had no collision with a diagonal tile.
			collideDiagLast = false;
		}
		
		//if colliding with solid square tile, or both types of diagonal tiles, stop moving
		else if(tileType == 1 || tileType == 4 || tileType == 5) {
			
			//in all normal cases (normal square tile collision, double diagonal collision) stop moving. align to tile
			if(tileType != 5 && tileType != 4 || !collideDiagLast) {
				if(axis == 'x') { pos.x = (int) Math.round(pos.x/8)*8; }
				else if(axis == 'y') { pos.y = (int) Math.round(pos.y/8)*8; }
			}

			//after slipping off of a diagonal wall endpoint with a solid square tile attached, do not get caught on the solid square tile.
			//align to the tile in order to move past it
			else {
				if(axis == 'x') {
					pos.x += frameDistAndDir;
					pos.y = (int) Math.round(pos.y/8)*8;
				}
				else if(axis == 'y') {
					pos.y += frameDistAndDir;
					pos.x = (int) Math.round(pos.x/8)*8;
				}
			}

			//used for diagonal endpoint to square tile special case. this frame had no collision with a diagonal tile.
			collideDiagLast = false;
		}
		
		//if colliding with down-up tile
		else if(tileType == 2) {

			//used to calculate distance between player hitbox and diagonal line for alignment
			int tileX1 = collisionAnswer[1];
			int tileY1 = collisionAnswer[2];
			int tileX2 = tileX1+ChunkManager.tileWidth;
			int tileY2 = tileY1+ChunkManager.tileHeight;
			float hbX1 = pos.x;
			float hbX2 = pos.x + hb.w;
			float hbY1 = pos.y + hb.yOffset;
			float hbY2 = pos.y + hb.yOffset + hb.h;
			float distToDiagLine = 0;
			
			//calculate the distance to the diagonal line
			if(axis == 'x') {
				if(frameDistAndDir == 0) { distToDiagLine = 0; }
				else if(frameDistAndDir > 0) { distToDiagLine = ChunkManager.tileWidth - hbX2 + tileX1 - hbY2 + tileY1 + frameDistAndDir*0.75f; }
				else { distToDiagLine = -(ChunkManager.tileWidth - tileY2 + hbY1 - tileX2 + hbX1) + frameDistAndDir*0.75f; }
			}
			else if(axis == 'y') {
				if(frameDistAndDir == 0) { distToDiagLine = 0; }
				else if(frameDistAndDir > 0) { distToDiagLine = ChunkManager.tileHeight - hbY2 + tileY1 - hbX2 + tileX1 + frameDistAndDir*0.75f; }
				else { distToDiagLine = -(ChunkManager.tileHeight - tileX2 + hbX1 - tileY2 + hbY1) + frameDistAndDir*0.75f; }
			}

			//if walking in only one direction (not perpendicular to diagonal line)
			if(!downUpDiag && !upDownDiag) {

				//calculate movement in x and y directions
				calcDiagMove(entityList, axis, frameDistAndDir, (-frameDistAndDir*0.75f), distToDiagLine);

				//used for diagonal endpoint to square tile special case. this frame DID have collision with a diagonal tile.
				collideDiagLast = true;
			}
			
			//if walking perpendicular to the diagonal line
			//this code will be ran twice (calcMove is called once for each direction); once in the x direction and once in the y direction
			else if(downUpDiag) {
				
				//player is not being pushed away from the diagonal line, so we only need to move to diagonal line
				//with framedist not included
				distToDiagLine -= frameDistAndDir*0.75f;

				//align x direction to diagonal line
				if(axis == 'x') {
					if(Math.abs(distToDiagLine) < 0.1f) {
						distToDiagLine = 0;
						pos.x = Math.round(pos.x);
					}
					pos.x += distToDiagLine;
				}
				
				//align y direction to diagonal line
				else if(axis == 'y') {
					if(Math.abs(distToDiagLine) < 0.1f) {
						distToDiagLine = 0;
						pos.y = Math.round(pos.y);
					}
					pos.y += distToDiagLine;
				}
			}
		}
		
		//if colliding with up-down tile
		else if(tileType == 3) {

			//used to calculate distance between player hitbox and diagonal line for alignment
			int tileX1 = collisionAnswer[1];
			int tileY1 = collisionAnswer[2];
			int tileX2 = tileX1+ChunkManager.tileWidth;
			int tileY2 = tileY1+ChunkManager.tileHeight;
			float hbX1 = pos.x;
			float hbX2 = pos.x + hb.w;
			float hbY1 = pos.y + hb.yOffset;
			float hbY2 = pos.y + hb.yOffset + hb.h;
			float distToDiagLine = 0;

			//calculate the distance to the diagonal line
			if(axis == 'x') {
				if(frameDistAndDir == 0) { distToDiagLine = 0; }
				else if(frameDistAndDir > 0) { distToDiagLine = ChunkManager.tileWidth - hbX2 + tileX1 - tileY2 + hbY1 + frameDistAndDir*0.75f; }
				else { distToDiagLine = -(ChunkManager.tileWidth - hbY2 + tileY1 - tileX2 + hbX1) + frameDistAndDir*0.75f; }
			}
			else if(axis == 'y') {
				if(frameDistAndDir == 0) { distToDiagLine = 0; }
				else if(frameDistAndDir > 0) { distToDiagLine = ChunkManager.tileHeight - hbY2 + tileY1 - tileX2 + hbX1 + frameDistAndDir*0.75f; }
				else { distToDiagLine = -(ChunkManager.tileHeight - hbX2 + tileX1 - tileY2 + hbY1) + frameDistAndDir*0.75f; }
			}
			
			//if walking in only one direction (not perpendicular to diagonal line)
			if(!upDownDiag && !downUpDiag) {
				
				//calculate movement in x and y directions
				calcDiagMove(entityList, axis, frameDistAndDir, frameDistAndDir*0.75f, distToDiagLine);

				//used for diagonal endpoint to square tile special case. this frame DID have collision with a diagonal tile.
				collideDiagLast = true;
			}

			//if walking perpendicular to the diagonal line
			//this code will be ran twice (calcMove is called once for each direction); once in the x direction and once in the y direction
			else if(upDownDiag) {
				
				//player is not being pushed away from the diagonal line, so we only need to move to diagonal line
				//with framedist not included
				distToDiagLine -= frameDistAndDir*0.75f;

				//align x direction to diagonal line
				if(axis == 'x') {
					if(Math.abs(distToDiagLine) < 0.1f) {
						distToDiagLine = 0;
						pos.x = Math.round(pos.x);
					}
					pos.x += distToDiagLine;
				}
				
				//align y direction to diagonal line
				else if(axis == 'y') {
					if(Math.abs(distToDiagLine) < 0.1f) {
						distToDiagLine = 0;
						pos.y = Math.round(pos.y);
					}
					pos.y += distToDiagLine;
				}
			}
		}
		
		//entity collision
		if(collidingEntity) {

			//if direction is right, align to the left side of the other entity. if direction is left, align to the right.
			if(axis == 'x') {
				if(frameDistAndDir > 0) { pos.x = (int) (entity.hb.pos.x + entity.hb.xOffset - hb.w - hb.xOffset); }
				else if(frameDistAndDir < 0) { pos.x = (int) (entity.hb.pos.x + entity.hb.xOffset + entity.hb.w - hb.xOffset); }
			}
			
			//if direction is up, align to the bottom side of the other entity. if direction is down, align to the top.
			if(axis == 'y') {
				if(frameDistAndDir > 0) { pos.y = (int) (entity.hb.pos.y + entity.hb.yOffset - hb.h - hb.yOffset); }
				else if(frameDistAndDir < 0) { pos.y = (int) (entity.hb.pos.y + entity.hb.yOffset + entity.hb.h - hb.yOffset); }
			}
		}
	}
	
	/**if walking along a diagonal tile, calculate the distance travelled along the main axis (actual direction being input) and the non-main axis (direction being pushed)**/
	public void calcDiagMove(ArrayList<Entity> entityList, char mainAxis, float frameDistAndDir, float frameDistAndDir2, float distToDiagLine) {
		
		//check for tile collision on non-main axis
		int collisionAnswer2[];
		if(mainAxis == 'x') { collisionAnswer2 = hb.collideTile(0, frameDistAndDir2); }
		else { collisionAnswer2 = hb.collideTile(frameDistAndDir2, 0); }
		int tileType2 = collisionAnswer2[0];
		
		//check for entity collision on non-main axis
		boolean collidingEntity2 = false;
		Entity entity2 = null;
		for(int i=0; i<entityList.size(); i++) {
			Entity curEntity = entityList.get(i);
			if(curEntity.pos.isOnScreen() && curEntity != this) {
				if(mainAxis == 'x') { collidingEntity2 = hb.collideEnt(curEntity.hb, 0, frameDistAndDir2); }
				else { collidingEntity2 = hb.collideEnt(curEntity.hb, frameDistAndDir2, 0); }
				entity2 = curEntity;
				if(collidingEntity2) { break; }
			}
			collidingEntity2 = false;
		}
		
		//if there is no collision on non-main axis
		if(tileType2 != 1 && !collidingEntity2) {
			
			//move normally along non-main axis
			//align to diagonal line on main axis
			if(mainAxis == 'x') {
				pos.y += frameDistAndDir2;
				pos.x += distToDiagLine;
			}
			else {
				pos.x += frameDistAndDir2;
				pos.y += distToDiagLine;
			}
		}
		
		//if there is tile collision on non-main axis, align to tile on both axes
		else if(tileType2 == 1) {
			pos.x = (int) Math.round(pos.x/8)*8;
			pos.y = (int) Math.round(pos.y/8)*8;
		}
		
		//if there is entity collision on non-main axis
		else if(collidingEntity2) {
			
			//align to entity on non-main axis and calculate distance to entity
			float distToEnt;
			if(mainAxis == 'x') {
				float prevY = pos.y;
				if(frameDistAndDir2 > 0) { pos.y = (int) (entity2.hb.pos.y + entity2.hb.yOffset - hb.h - hb.yOffset); }
				else if(frameDistAndDir2 < 0) { pos.y = (int) (entity2.hb.pos.y + entity2.hb.yOffset + entity2.hb.h - hb.yOffset); }
				distToEnt = prevY - pos.y;
			}
			else {
				float prevX = pos.x;
				if(frameDistAndDir2 < 0) { pos.x = (int) (entity2.hb.pos.x + entity2.hb.xOffset + entity2.hb.w - hb.xOffset); }
				if(frameDistAndDir2 > 0) { pos.x = (int) (entity2.hb.pos.x + entity2.hb.xOffset - hb.w - hb.xOffset); }
				distToEnt = prevX - pos.x;
			}
			
			//if the diagonal line is further away than the entity, move a bit towards the diagonal line
			//otherwise, move in equal distances in both directions (or dont move at all if touching entity)
			if(frameDistAndDir < 0 && frameDistAndDir2 > 0 || frameDistAndDir > 0 && frameDistAndDir2 < 0) {
				if(distToEnt == 0 && Math.abs(distToDiagLine + frameDistAndDir2) > 0) { distToDiagLine += frameDistAndDir2; }
				else { distToDiagLine = distToEnt; }
			}
			else {
				if(distToEnt == 0 && Math.abs(distToDiagLine - frameDistAndDir2) > 0) { distToDiagLine -= frameDistAndDir2; }
				else { distToDiagLine = -distToEnt; }
			}
			
			//align to diagonal line on main axis
			if(mainAxis == 'x') {
				pos.x += distToDiagLine;
				pos.x = (int) Math.round(pos.x);
			}
			else {
				pos.y += distToDiagLine;
				pos.y = (int) Math.round(pos.y);
			}
		}
	}
	
	/**set the player interact to be in the same direction the player is facing**/
	public void setPIDir() {

		if(!walkBuffer) {
			if(up != lastDir[0] || down != lastDir[1] || left != lastDir[2] || right != lastDir[3]) {
		
				if(up) { //up and up diagonals
					if(left) { pi.setDir(4); }
					else if(right) { pi.setDir(5); }
					else { pi.setDir(0); }
				}
				if(down) { //down and down diagonals
					if(left) { pi.setDir(6); }
					else if(right) { pi.setDir(7); }
					else { pi.setDir(1); }
				}
				
				//left and right
				if(left && !up && !down) { pi.setDir(2); }
				if(right && !up && !down) { pi.setDir(3); }
			}
		}
	}
	
	/**animates the player's sprites**/
	public void animate() {
		
		//set animation (if needed) and update the player animation. only set animation if last direction does not match current direction
		if(!walkBuffer) {
			if(up != lastDir[0] || down != lastDir[1] || left != lastDir[2] || right != lastDir[3]) {
				
				//static image (no movement)
				if(!up && !down && !left && !right) { ani.setCurStatic(1); }
				
				//non static (animate)
				else {
					if(up) { //up and up diagonals
						if(left) { ani.setAnimation(4, 0.15f); }
						else if(right) { ani.setAnimation(5, 0.15f); }
						else { ani.setAnimation(0, 0.15f); }
					}
					if(down) { //down and down diagonals
						if(left) { ani.setAnimation(6, 0.15f); }
						else if(right) { ani.setAnimation(7, 0.15f); }
						else { ani.setAnimation(1, 0.15f); }
					}
					//left and right
					if(left && !up && !down) { ani.setAnimation(2, 0.15f); }
					if(right && !up && !down) { ani.setAnimation(3, 0.15f); }
				}
			}
		}
		ani.update();
	}
}
