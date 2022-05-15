package game.graphics;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.util.texture.Texture;

/**translate graphics2d functions to opengl functions**/
public class Graphics {
	
	//rgba values
	private static float red = 1, green = 1, blue = 1, alpha = 1;
	
	//main font of the gamme
	private static Font font;
	
	/**graphics incorporate the game font into its methods**/
	public Graphics() { font = new Font(); }
	
	//getters
	public static Font getFont() { return font; }
	
	/**draw a text image onto the screen**/
	public static void drawTextImage(TextImage textImg, float x, float y) {
		drawImage(textImg.getImgRes(), x, y, textImg.getW(), textImg.getH());
	}
	
	/**draw an image onto the screen**/
	public static void drawImage(ImageResource img, float x, float y, float width, float height) {
		GL2 gl2 = EventListener.gl2;
		
		Texture texture = img.getTexture();
		
		if(texture != null) { gl2.glBindTexture(GL2.GL_TEXTURE_2D, texture.getTextureObject()); }
		
		gl2.glEnable(GL2.GL_BLEND);
		gl2.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_ALPHA);
		
		gl2.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MAG_FILTER, GL2.GL_NEAREST);
		
		gl2.glColor4f(red, green, blue, alpha);
		gl2.glBegin(GL2.GL_QUADS);
			gl2.glTexCoord2f(0, 0);
			gl2.glVertex2f(x, y);
			gl2.glTexCoord2f(1, 0);
			gl2.glVertex2f(x+width, y);
			gl2.glTexCoord2f(1, 1);
			gl2.glVertex2f(x+width, y+height);
			gl2.glTexCoord2f(0, 1);
			gl2.glVertex2f(x, y+height);
		gl2.glEnd();
		gl2.glFlush();
		
		gl2.glBindTexture(GL2.GL_TEXTURE_2D, 0);
	}
	
	/**draw an image on to the screen with the center of the image at x, y and at a degree of t**/
	public static void drawImageR(ImageResource img, float x, float y, float width, float height, float t) {
		GL2 gl2 = EventListener.gl2;
		
		x -= width / 2;
		y -= height / 2;
		
		Texture texture = img.getTexture();
		
		if(texture != null) { gl2.glBindTexture(GL2.GL_TEXTURE_2D, texture.getTextureObject()); }
		
		gl2.glTranslatef(x+width/2, y+height/2, 0);
		gl2.glRotatef(t, 0, 0, 1);
		
		gl2.glEnable(GL2.GL_BLEND);
		gl2.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_ALPHA);
		
		gl2.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MAG_FILTER, GL2.GL_NEAREST);
		
		gl2.glColor4f(red, green, blue, alpha);
		gl2.glBegin(GL2.GL_QUADS);
			gl2.glTexCoord2f(0, 0);
			gl2.glVertex2f(-width/2, -height/2);
			gl2.glTexCoord2f(1, 0);
			gl2.glVertex2f(width/2, -height/2);
			gl2.glTexCoord2f(1, 1);
			gl2.glVertex2f(width/2, height/2);
			gl2.glTexCoord2f(0, 1);
			gl2.glVertex2f(-width/2, height/2);
		gl2.glEnd();
		gl2.glFlush();
		
		gl2.glRotatef(-t, 0, 0, 1);
		gl2.glTranslatef(-(x+width/2), -(y+height/2), 0);
		
		gl2.glFlush();
		
		gl2.glBindTexture(GL2.GL_TEXTURE_2D, 0);
	}
	
	/**draw an entire word onto the screen**/
	public static void drawWord(String word, float x, float y, String alignment) {
		
		int pixelsBetween;
		
		//write text aligned with left side
		if(alignment == "left") {
			
			//draw each letter
			for(int i = 0; i < word.length(); i++) {
				
				//draw the letter image
				if(word.charAt(i) >= 33 && word.charAt(i) <= 126) { 
					drawImage(font.getLetterImgRes(word.charAt(i)), x, y, font.getLetterWidth(word.charAt(i)), 16);
				}
				
				//lower case a's do not have a pixel of space after them
				if(word.charAt(i) == 'a') {
					if(i+1 < word.length()) {
						if(word.charAt(i+1) == '.' || word.charAt(i+1) == '!' || word.charAt(i+1) == '?') {
							pixelsBetween = 1;
						}
						else { pixelsBetween = 0; }
					}
					else { pixelsBetween = 0; }
				}
				//but every other letter does
				else { pixelsBetween = 1; }
				
				//move draw position
				x += (font.getLetterWidth(word.charAt(i))+pixelsBetween);
				y += 0;
			}
		}
		
		//write text aligned with right side
		else if(alignment == "right") {
			
			//draw each letter
			for(int i = word.length()-1; i >= 0; i--) {
				
				//letter a has a weird tail and needs an extra pixel of space
				if(word.charAt(i) == 'a') {
					if(i-1 >= 0) {
						if(word.charAt(i-1) == '.' || word.charAt(i-1) == '!' || word.charAt(i-1) == '?') {
							pixelsBetween = 1;
						}
						else { pixelsBetween = 0; }
					}
					else { pixelsBetween = 0; }
				}
				else { pixelsBetween = 1; }

				//move draw position
				x -= (font.getLetterWidth(word.charAt(i))+pixelsBetween);
				y -= 0;
				
				//draw the current letter image
				if(word.charAt(i) >= 33 && word.charAt(i) <= 126) {
					drawImage(font.getLetterImgRes(word.charAt(i)), x, y, font.getLetterWidth(word.charAt(i)), 16);
				}
			}
		}
	}
	
	/**draw an empty ellipse**/
	public static void drawEllipse(float cx, float cy, float rx, float ry) {
		GL2 gl2 = EventListener.gl2;
		
		int num_segments = (int) (rx + ry + 30) / 2;
		System.out.println(num_segments);
		
	    float theta = (float) (2 * 3.1415926 / (float) num_segments); 
	    float c = (float) Math.cos(theta);//precalculate the sine and cosine
	    float s = (float) Math.sin(theta);
	    float t;

	    float x = 1; //we start at angle = 0 
	    float y = 0; 

	    gl2.glColor4f(red, green, blue, alpha);
	    gl2.glBegin(GL2.GL_LINE_LOOP); 
	    for(int i = 0; i < num_segments; i++) 
	    {
	    	
	        //apply radius and offset
	    	gl2.glVertex2f(x * rx + cx, y * ry + cy);//output vertex 

	        //apply the rotation matrix
	        t = x;
	        x = c * x - s * y;
	        y = s * t + c * y;
	    } 
	    gl2.glEnd();
		gl2.glFlush();
	    
	    resetColor();
	}
	
	/**draw an empty rectangle at specified coordinates and dimensions**/
	public static void drawRect(float x, float y, float width, float height) {
		GL2 gl2 = EventListener.gl2;
		
		gl2.glColor4f(red, green, blue, alpha);
		gl2.glBegin(GL2.GL_LINES);
			gl2.glVertex2f(x, y);
			gl2.glVertex2f(x+width, y);
		gl2.glEnd();
		gl2.glFlush();
		gl2.glBegin(GL2.GL_LINES);
			gl2.glVertex2f(x+width, y);
			gl2.glVertex2f(x+width, y+height);
		gl2.glEnd();
		gl2.glFlush();
		gl2.glBegin(GL2.GL_LINES);
			gl2.glVertex2f(x+width, y+height);
			gl2.glVertex2f(x, y+height);
		gl2.glEnd();
		gl2.glFlush();
		gl2.glBegin(GL2.GL_LINES);
			gl2.glVertex2f(x, y+height);
			gl2.glVertex2f(x, y);
		gl2.glEnd();
		gl2.glFlush();
		
		resetColor();
	}
	
	/**draw a filled rectangle at specified coordinates and dimensions**/
	public static void fillRect(float x, float y, float width, float height) {
		GL2 gl2 = EventListener.gl2;
		
		gl2.glColor4f(red, green, blue, alpha);
		gl2.glBegin(GL2.GL_QUADS);
			gl2.glVertex2f(x, y);
			gl2.glVertex2f(x+width, y);
			gl2.glVertex2f(x+width, y+height);
			gl2.glVertex2f(x, y+height);
		gl2.glEnd();
		gl2.glFlush();
		
		resetColor();
	}
	
	/**create a rectangle that is only 1 pixel**/
	public static void drawDot(float x, float y) { fillRect(x, y, 1, 1); }
	
	/**reset color back to full RGBA**/
	public static void resetColor() { red = green = blue = alpha = 1; }
	
	/**set a graphic to contain these RGBA values**/
	public static void setColor(float r, float g, float b, float a) {
		red = Math.max(0, Math.min(1, r));
		green = Math.max(0, Math.min(1, g));
		blue = Math.max(0, Math.min(1, b));
		alpha = Math.max(0, Math.min(1, a));
	}
	
	/**set a graphic to contain the RGBA values of this color**/
	public static void setColor(String color) {
		
		//the rgb values for each possible color
		switch(color) {
			
			case "lightred":
				red = 1;
				green = 0.35f;
				blue = 0.35f;
				break;
				
			case "lightblue":
				red = 0.5f;
				green = 0.5f;
				blue = 1;
				break;
			
			case "red":
				red = 1;
				green = 0;
				blue = 0;
				break;
				
			case "green":
				red = 0;
				green = 0.5f;
				blue = 0;
				break;
				
			case "lime":
				red = 0;
				green = 1;
				blue = 0;
				break;
				
			case "lightlime":
				red = 0.25f;
				green = 1;
				blue = 0.25f;
				break;
				
			case "blue":
				red = 0;
				green = 0;
				blue = 1;
				break;
				
			case "cyan":
				red = 0;
				green = 0.5f;
				blue = 0.5f;
				break;
				
			case "magenta":
				red = 0.5f;
				green = 0;
				blue = 0.5f;
				break;
				
			case "purple":
				red = 0.25f;
				green = 0;
				blue = 0.25f;
				break;
				
			case "indigo":
				red = 0.25f;
				green = 0;
				blue = 0.75f;
				break;
				
			case "yellow":
				red = 0.5f;
				green = 0.5f;
				blue = 0;
				break;
				
			case "orange":
				red = 1f;
				green = 0.5f;
				blue = 0;
				break;
				
			case "grey":
				red = 0.35f;
				green = 0.35f;
				blue = 0.35f;
				alpha = 1f;
				break;
				
			case "darkgrey":
				red = 0.25f;
				green = 0.25f;
				blue = 0.25f;
				alpha = 1f;
				break;
				
			case "lightgrey":
				red = 0.75f;
				green = 0.75f;
				blue = 0.75f;
				alpha = 1f;
				break;
				
			case "black":
				red = 0;
				green = 0;
				blue = 0;
				alpha = 1;
				break;
				
			case "white":
				red = 1;
				green = 1;
				blue = 1;
				alpha = 1;
				break;
				
			case "transparentgrey":
				red = 0.5f;
				green = 0.5f;
				blue = 0.5f;
				alpha = 0.5f;
				break;
				
			case "transparentblack":
				red = 0f;
				green = 0f;
				blue = 0f;
				alpha = 0.5f;
				break;
				
			case "transparentdarkblack":
				red = 0f;
				green = 0f;
				blue = 0f;
				alpha = 0.75f;
				break;
			
			default:
				red = 1;
				green = 1;
				blue = 1;
				alpha = 1;
				break;
		}
	}
}
