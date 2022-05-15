package game.audio;

import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public class AudioResource {
	
	//audio clip
	private Clip clip;
	
	//volume of all audio
	private static float volume = -25f;
	
	//duration of audio clip
	private double durationInSeconds;
	
	public AudioResource(String path) {
		
		//open file
		File file = new File(path);
		AudioInputStream audioStream = null;
		try { audioStream = AudioSystem.getAudioInputStream(file); }
		catch (UnsupportedAudioFileException e) {
			System.out.print(path + " could not be loaded: ");
			e.printStackTrace();
		} 
		catch (IOException e) {
			System.out.print(path + " could not be loaded: ");
			e.printStackTrace();
		}
		
		//create clip
		Clip clip = null;
		try { clip = AudioSystem.getClip(); }
		catch (LineUnavailableException e) {
			System.out.print(path + " could not be loaded: ");
			e.printStackTrace();
		}
		this.clip = clip;

		//open the clip
		try { this.clip.open(audioStream); }
		catch (LineUnavailableException | IOException e) {
			System.out.print(path + " could not play: ");
			e.printStackTrace();
		}
		
		//determine clip duration
		AudioFormat format = audioStream.getFormat();
		long frames = audioStream.getFrameLength();
		durationInSeconds = (double) frames / format.getFrameRate();  
	}
	
	//getters
	public double getDuration() { return durationInSeconds; }
	
	/**play audio clip**/
	public void play() {
		clip.setFramePosition(0);
		FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
		gainControl.setValue(volume);
		clip.start();
	}
}
