package game.graphics.gui;

import game.audio.AudioResource;
import game.engine.GameLoop;
import game.engine.KeyHandler;
import game.graphics.Graphics;
import game.graphics.ImageResource;
import game.graphics.Renderer;
import game.graphics.TextImage;
import game.states.PlayState;

public class DialogueBox {
	
	//slide animation distance from start
	private float y = 0;
	private final int maxY = 50;
	
	//if dialogue box is open or not
	public boolean open = false;
	
	//text position offset
	private final int textX = 16;
	private final int textY = Renderer.unitsHigh + 5;
	
	//gap in between lines of text in the dialogue box
	private final int textYGap = 14;
	
	//bullet point position offset
	private final int bulletX = textX - 5;
	private final int bulletY = textY + 4;
	
	//horizontal cursor (questions) position offset
	private int cursorX = 18;
	private int cursorY = Renderer.unitsHigh+7;
	
	//slide animation time
	private double slide_time = 0.15f;
	private double slide_start_time;

	//states
	public boolean start = false; //start slide animation
	public boolean stop = false; //stop slide animation
	public boolean writing = false; //text is being written
	public boolean waiting = false; //waiting for player input
	public boolean cycleLines = false; //shifting text up when dialogue box is full
	public boolean done = false; //text is done being written
	public boolean skipText = false; //finish current text immediately
	public boolean question = false; //player was just asked a question
	public boolean answer = false; //waiting for player's answer to question
	public boolean response = false; //response to player's answer
	
	//dialogue gotten from entity
	private String curText;
	private Dialogue curDialogue;
	
	//bullet points that are drawn before each line of text
	private ImageResource bullet_point;
	
	//whether or not bullet points are drawn for a specific line index
	private boolean[] bullet_point_on = {true, true, true};
	
	//text images for each line of text
	private TextImage[] line = new TextImage[3];
	
	//which line we are currently writing, and the max (3) amount of lines that can fit in the dialogue box
	private int lineIndex = 0;
	private final int maxLines = 3;
	
	//how far we are into writing the current text
	public int textIndex;
	
	//how long in between each letter being written
	private double letter_time = 0.01f;
	private double letter_start_time;
	
	//audio clips
	private AudioResource cursor1Audio;
	private AudioResource cursor2Audio;
	private AudioResource textAudio;
	
	//how long in between each audio clip while writing text
	private double text_audio_time = 0.03f;
	private double text_audio_start_time;
	
	//vertical cursor that is rendered when waiting for user input
	private Cursor cursorVer;
	private Cursor cursorHor;
	
	public DialogueBox() {
		
		//create audio resources
		cursor1Audio = new AudioResource("res/soundfx/cursor_1.wav");
		cursor2Audio = new AudioResource("res/soundfx/cursor_2.wav");
		textAudio = new AudioResource("res/soundfx/text.wav");
		
		//create bullet point image
		bullet_point = new ImageResource ("/font/bullet_point.png");
		
		//create cursor
		cursorVer = new Cursor("vertical", Renderer.unitsWide-16, Renderer.unitsHigh+37);
		cursorHor = new Cursor("horizontal", cursorX, cursorY, 60, 0, 2, 0);
	}
	
	/**begin dialogue box slide start animation**/
	public void start(Dialogue dialogue) {
		
		//get dialogue from entity
		if(dialogue == null) { curDialogue = new Dialogue("No problem here."); curText = curDialogue.getText(); }
		else { curDialogue = dialogue; curText = dialogue.getText(); }
		
		//slide animation
		y = 0;
		slide_start_time = (double) System.nanoTime() / GameLoop.B;
		start = true;
		
		//dialogue box is now open
		open = true;
		
		//play audio
		cursor1Audio.play();

		//reset text
		line[0] = new TextImage(Renderer.unitsWide-textX*3, 16);
		line[1] = null;
		line[2] = null;
		
		//start at line 0 and letter 0
		lineIndex = 0;
		textIndex = 0;
	}
	
	//y displacement getter (for fps counter)
	public float getY() { return y; }

	/**reset text write times after game is unpaused so that text is not skipped**/
	public void unPause() { letter_start_time = text_audio_start_time = (double) System.nanoTime() / GameLoop.B; }
	
	/**begin dialogue box slide stop animation**/
	public void stop() {
		
		//slide animation
		y = maxY;
		slide_start_time = (double) System.nanoTime() / GameLoop.B;
		stop = true;
		
		//dialogue box is no longer open
		open = false;
		
		//play audio
		cursor2Audio.play();
	}
	
	/**INPUT**/
	public void input(KeyHandler key, PlayState ps) {
		
		//if dialogue box is waiting and player hits accept, go to next dialogue
		if(waiting && key.accept.pressed) {
			key.rAll();
			waiting = false;
			
			//if there is no more room on the dialogue box, we must cycle lines
			if(lineIndex+1 >= maxLines) {
				cycleLines = true;
			}
			
			//if there is more room on the dialogue box, continue to next line as normal
			else {
				lineIndex++;
				line[lineIndex] = new TextImage(Renderer.unitsWide-textX*3, 16);
				bullet_point_on[lineIndex] = true;
				
				//reset letter and audio times
				letter_start_time = text_audio_start_time = (double) System.nanoTime() / GameLoop.B;
			}
		}
		
		//if dialogue box is waiting for a player's answer to a question and player hits accept
		if(answer && key.accept.pressed) {
			key.rAll();
			answer = false;
			response = true;

			//next line will be answer options without a bullet point
			line[lineIndex] = new TextImage(Renderer.unitsWide-textX*3, 16);
			letter_start_time = text_audio_start_time = (double) System.nanoTime() / GameLoop.B;
			bullet_point_on[lineIndex] = true;
		}
		
		//skip text if accept is pressed while writing text
		//if(!waiting && writing && key.accept.pressed) {
			//key.rAll();
			//skipText = true;
		//}
		
		//if accept was pressed and writing is over, stop dialogue box
		if(done && key.accept.pressed) {
			key.rAll();
			done = false;
			ps.unPause();
			stop();
		}

		//if waiting for player's answer, give horizontal cursor input
		if(answer) { cursorHor.input(key); }
	}

	/**UPDATE**/
	public void update() {
		
		//calc distance for slide up (start) animation
		if(start) {
			double now = (double) System.nanoTime() / GameLoop.B;
			y = (float) ((now - slide_start_time) * maxY / slide_time);
			
			//end slide animation, begin dialogue
			if(y >= maxY) {
				y = maxY;
				start = false;
				writing = true;
				letter_start_time = text_audio_start_time = (double) System.nanoTime() / GameLoop.B;
			}
		}
		
		//calc distance for slide down (stop) animation
		if(stop) {
			double now = (double) System.nanoTime() / GameLoop.B;
			y = (float) (maxY - (now - slide_start_time) * maxY / slide_time);
			
			//end slide animation
			if(y <= 0) {
				y = 0;
				stop = false;
			}
		}

		//if a question was just asked
		if(question) {
			
			//if there is no more room on the dialogue box, we must cycle lines
			if(lineIndex+1 >= maxLines) {
				cycleLines = true;
			}
			
			//if there is more room on the dialogue box, continue to next line as normal
			else {
				lineIndex++;
				
				//next line will be answer options without a bullet point
				line[lineIndex] = new TextImage(Renderer.unitsWide-textX*3, 16);
				line[lineIndex].drawWord(curDialogue.getOptionOne(), 10, 0, "left");
				line[lineIndex].drawWord(curDialogue.getOptionTwo(), 70, 0, "left");
				bullet_point_on[lineIndex] = false;
				
				//go into answer state
				question = false;
				answer = true;
				
				//set cursor position to what line the answer options were written on
				cursorHor.setStartY(cursorY + 14*lineIndex);
				cursorHor.reset();
			}
		}
		
		//cycle lines
		if(cycleLines) {
			
			//shift all lines (and bullet points) up by one
			line[0] = line[1];
			line[1] = line[2];
			line[2] = new TextImage(Renderer.unitsWide-textX*3, 16);
			bullet_point_on[0] = bullet_point_on[1];
			bullet_point_on[1] = bullet_point_on[2];
			
			//determine whether third line has bullet point or not
			if(textIndex != 0) { bullet_point_on[2] = false; }
			else { bullet_point_on[2] = true; }
			
			//current line index is now third line
			lineIndex = 2;
			
			//lines have now been cycled. switch to false
			cycleLines = false;
			
			//reset letter and audio times
			letter_start_time = text_audio_start_time = (double) System.nanoTime() / GameLoop.B;
			
			//if cycled lines in order to fit answer options onto the screen...
			if(question) {
				
				//next line will be answer options without a bullet point
				line[lineIndex].drawWord(curDialogue.getOptionOne(), 10, 0, "left");
				line[lineIndex].drawWord(curDialogue.getOptionTwo(), 70, 0, "left");
				bullet_point_on[lineIndex] = false;

				//go into answer state
				question = false;
				answer = true;

				//set cursor position to what line the answer options were written on
				cursorHor.setStartY(cursorY + 14*lineIndex);
			}
			
			//skip writing code when lines were just cycled
			return;
		}
		
		//if the player just responded to a question, get the text that corresponds to the player's answer
		if(response) {
			response = false;
			
			curText = curDialogue.getText(cursorHor.getX());
			curDialogue.setChosenOption(cursorHor.getX());
		}
		
		//while writing text...
		if(writing && !waiting && !question && !answer) {
			
			//calculate time since last letter written AND last text audio
			double now = (double) System.nanoTime() / GameLoop.B;
			double timeSpentAudio = now - text_audio_start_time;
			double timeSpentLetter = now - letter_start_time;
			
			//play audio every text_audio_time
			if(timeSpentAudio >= text_audio_time) {
				textAudio.play();
				text_audio_start_time = (double) System.nanoTime() / GameLoop.B;
			}
			
			//write a letter every letter_time
			if(timeSpentLetter >= letter_time) {
				
				//determine if multiple letters should be drawn this frame (for low framerates)
				int lettersAdding = 0;
				while(timeSpentLetter > letter_time) {
					timeSpentLetter -= letter_time;
					lettersAdding++;
				}
				
				//if skip text is true, add all letters that are left in curText
				if(skipText) {
					skipText = false;
					lettersAdding = curText.length() - textIndex;
				}
				
				//for every letter being written this frame...
				for(int i=0; i<lettersAdding; i++) {
					
					//draw letter to textimage and go to next character
					if(textIndex >= 0) { line[lineIndex].drawNextLetter(curText.charAt(textIndex), 0); }
					textIndex++;
					
					//if current text has finished...
					if(textIndex >= curText.length()) {
						
						//end writing phase if there's no more dialogue to get
						if(curDialogue.getNext() == null) {
							writing = false;
							done = true;
							cursorVer.resetAni();
						}
						
						//if there is dialogue to get, go to next dialogue
						else {
							curDialogue = curDialogue.getNext();
							curText = curDialogue.getText();
							
							//questions will go into the question state, while normal text will go to the waiting state
							if(curDialogue.isQuestion()) { question = true; }
							else { waiting = true; }
							
							//reset cursor timing every wait phase
							cursorVer.resetAni();
							
							//reset text index after getting a new dialogue
							textIndex = 0;
						}
						
						//do not try to add more letters if there aren't any more letters to add
						break;
					}
					
					//check if the next word can fit on this line, and if it cant...
					if(textIndex > 0 && textIndex < curText.length() && curText.charAt(textIndex-1) == ' ') {
						if(!line[lineIndex].wordFit(curText, textIndex-1)) {
							
							//go to next line
							lineIndex++;
							
							//cycle lines if there's no more room on the dialogue box
							if(lineIndex >= 3) {
								cycleLines = true;
								break;
							}
							
							//if there is room on the dialogue box, create new lines normally
							else {
								line[lineIndex] = new TextImage(Renderer.unitsWide-textX*3, 16);
								bullet_point_on[lineIndex] = false;
							}
						}
					}
				}
				
				//reset letter_start_time after letters have been drawn this frame
				letter_start_time = (double) System.nanoTime() / GameLoop.B - timeSpentLetter;
			}
		}

		//update vertical cursor if waiting for user input
		if(waiting || !writing && !start) { cursorVer.update(); }
		
		//update horizontal cursor if waiting for answer to question
		if(answer) { cursorHor.update(); }
	}
	
	/**RENDER**/
	public void render() {

		//render black box
		Graphics.setColor("black");
		Graphics.fillRect(0, Renderer.unitsHigh-y, Renderer.unitsWide, maxY);
		
		//render line one
		if(line[0] != null) {
			if(bullet_point_on[0]) { Graphics.drawImage(bullet_point, bulletX, bulletY-y, bullet_point.getW(), bullet_point.getH()); }
			Graphics.drawTextImage(line[0], textX, textY-y);
		}
		
		//render line two
		if(line[1] != null) {
			if(bullet_point_on[1]) { Graphics.drawImage(bullet_point, bulletX, bulletY-y+textYGap, bullet_point.getW(), bullet_point.getH()); }
			Graphics.drawTextImage(line[1], textX, textY-y+textYGap);
		}

		//render line three
		if(line[2] != null) {
			if(bullet_point_on[2]) { Graphics.drawImage(bullet_point, bulletX, bulletY-y+textYGap*2, bullet_point.getW(), bullet_point.getH()); }
			Graphics.drawTextImage(line[2], textX, textY-y+textYGap*2);
		}
		
		//render cursor if waiting for user input
		if(waiting || !writing && !start) { cursorVer.render(0, -y); }

		//render horizontal cursor if waiting for answer to question
		if(answer) { cursorHor.render(0, -y); }
	}
}
