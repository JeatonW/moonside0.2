package game.graphics;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class TextImage {
	
	//full image data
	private BufferedImage fullImg = null;
	private ImageResource fullImgRes = null;
	
	//full image dimensions
	private int w;
	private int h;
	
	//java graphics 2d is needed to draw onto a buffered image
	private Graphics2D g2;
	
	//transparent color
	private Color transparency = new Color(0, true);
	
	/**upon construction, a text image has a position, width, and height**/
	public TextImage(int width, int height) {
		this.w = width;
		this.h = height;
		
		//create an empty buffered image
		fullImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		g2 = fullImg.createGraphics();
		fullImgRes = new ImageResource(fullImg);
		g2.setColor(transparency);
	}
	public TextImage() {
		this.w = 73;
		this.h = 16;
		
		//create an empty buffered image
		fullImg = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		g2 = fullImg.createGraphics();
		fullImgRes = new ImageResource(fullImg);
		g2.setColor(transparency);
	}

	public int getW() { return w; }
	public int getH() { return h; }
	public ImageResource getImgRes() { return fullImgRes; }
	
	/**clear a section in the buffered image**/
	public void clearSection(int x, int y, int w, int h) {
		g2.setBackground(transparency);
		g2.clearRect(x, y, w, h);
	}
	
	/**draw a single letter onto the text image**/
	public void drawLetter(char letter, int x, int y) {
		g2.drawImage(Graphics.getFont().getLetterImage(letter), x, y, Graphics.getFont().getLetterWidth(letter), 16, null);
	}
	
	/**draw an entire word onto the text image**/
	public void drawWord(String word, float x, float y, String alignment) {
		
		int pixelsBetween;
		
		//write text aligned with left side
		if(alignment == "left") {
			
			//draw each letter
			for(int i = 0; i < word.length(); i++) {
				
				//draw the letter image
				if(word.charAt(i) >= 33 && word.charAt(i) <= 126) { 
					g2.drawImage(Graphics.getFont().getLetterImage(word.charAt(i)), (int) x, (int) y, Graphics.getFont().getLetterWidth(word.charAt(i)), 16, null);
				}
				
				//lower case a's do not have a pixel of space after them
				if(word.charAt(i) == 'a') { pixelsBetween = 0; }
				//but every other letter does
				else { pixelsBetween = 1; }
				
				//move draw position
				x += (Graphics.getFont().getLetterWidth(word.charAt(i))+pixelsBetween);
			}
		}
		
		//write text aligned with right side
		else if(alignment == "right") {
			
			//draw each letter
			for(int i = word.length()-1; i >= 0; i--) {
				
				//letter a has a weird tail and needs an extra pixel of space
				if(word.charAt(i) == 'a') { pixelsBetween = 0; }
				else { pixelsBetween = 1; }

				//move draw position
				x -= (Graphics.getFont().getLetterWidth(word.charAt(i))+pixelsBetween);
				
				//draw the current letter image
				if(word.charAt(i) >= 33 && word.charAt(i) <= 126) {
					g2.drawImage(Graphics.getFont().getLetterImage(word.charAt(i)), (int) x, (int) y, Graphics.getFont().getLetterWidth(word.charAt(i)), 16, null);
				}
			}
		}
	}
	
	private int nextX = 0;
	
	/**draw one character one at a time, each time moving forward on the x axis according to letter width. for drawing text one letter at a time in dialogue boxes.
	 * more efficient than drawing every single letter every frame**/
	public void drawNextLetter(char c, float y) {
		
		//draw the letter image
		if(c >= 33 && c <= 126) { 
			g2.drawImage(Graphics.getFont().getLetterImage(c), (int) nextX, (int) y, Graphics.getFont().getLetterWidth(c), 16, null);
		}
		
		int pixelsBetween;
		
		//lower case a's do not have a pixel of space after them
		if(c == 'a') { pixelsBetween = 0; }
		//but every other letter does
		else { pixelsBetween = 1; }
		
		//move draw position
		nextX += (Graphics.getFont().getLetterWidth(c) + pixelsBetween);
		
		//must refresh the image resource after every letter (or else image resource will remain blank because it is still using the original empty buffered image)
		fullImgRes.resetTexture();
	}
	
	/**determine if the next word will fit onto the text image**/
	public boolean wordFit(String text, int textIndex) {
		
		if(textIndex >= text.length()) { return true; }
		
		int x = nextX;
		
		while(true) {
			x += Graphics.getFont().getLetterWidth(text.charAt(textIndex));
			
		
			int pixelsBetween;
			
			//lower case a's do not have a pixel of space after them
			if(text.charAt(textIndex) == 'a') { pixelsBetween = 0; }
			//but every other letter does
			else { pixelsBetween = 1; }
			
			if(x >= w) { return false; }
			
			textIndex++;
			
			if(textIndex >= text.length() || text.charAt(textIndex) == ' ') {
				break;
			}
		}
		
		return true;
	}
	
	/**get the X position after the last letter that was written**/
	public int getNextLetterX() {
		return nextX;
	}
	
	public void resetNextLetter() { nextX = 0; }
	
	/**clear/reset the buffered image**/
	public void clearImg() {
		g2.setBackground(transparency);
		g2.clearRect(0, 0, w, h);
	}
}
