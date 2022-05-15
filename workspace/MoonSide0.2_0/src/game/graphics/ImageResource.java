package game.graphics;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;

import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.awt.AWTTextureIO;

public class ImageResource {

	//every image resource has an opengl texture and a buffered image
	private Texture texture = null;
	private BufferedImage img = null;
	
	/**upon construction, an image resource takes in a buffered image and creates an opengl texture**/
	public ImageResource(BufferedImage img) {
		this.img = img;
		if(img != null) { img.flush(); }
	}
	
	public ImageResource(String path) {
		this.img = getBuffImg(path);
		if(img != null) { img.flush(); }
	}
	
	/**load buffered image with string path, and return buffered image**/
	public static BufferedImage getBuffImg(String path) {
		URL url = ImageResource.class.getResource(path);
		BufferedImage img = null;
		try { img = ImageIO.read(url); }
		catch(IOException e) {
			System.out.print(path + " could not be loaded: ");
			e.printStackTrace();
		}
		return img;
	}
	
	/**if texture has already been used once but the buffered image has been updated, the opengl texture must be reset**/
	public void resetTexture() { texture = null; }
	
	/**get the opengl texture of this buffered img**/
	public Texture getTexture() {
		if(img == null) { return null; }
		if(texture == null) { texture = AWTTextureIO.newTexture(Renderer.getProfile(), img, true); }
		return texture;
	}
	
	/**retrieve the original buffered image from an image resource**/
	public BufferedImage getImage() { return this.img; }
	
	//dimension getters
	public int getW() { return this.img.getWidth(); }
	public int getH() { return this.img.getHeight(); }
}