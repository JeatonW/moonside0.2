package game.graphics;

import java.awt.image.BufferedImage;

/**create a 2D array of letter images**/
public class Font {
	
	//array of buffered images
	private BufferedImage[][] fontArray;
	
	//array of image resources
	private ImageResource[][] fontResArray;
	
	//how many x and y tiles there are in the fontsheet
	private final int TILES = 16;
	
	/**load font sheet and turn it into an array of letter images**/
	public Font() {
		System.out.println("Loading /font/fontsheet.png...");
		loadFontArray(ImageResource.getBuffImg("/font/fontsheet.png"));
	}
	
	/**create an array of letter images, and return it**/
	public void loadFontArray(BufferedImage FONTSHEET) {
		
		//define size of 2D array
		fontArray = new BufferedImage[TILES][TILES];
		fontResArray = new ImageResource[TILES][TILES];
		
		//create a space letter object
		Letter space = new Letter(FONTSHEET, ' ');
		
		//create an array of letter objects
		Letter[] letters = createLetters(FONTSHEET);
		
		//create a blank letter object for unused tiles
		Letter blank = new Letter(FONTSHEET, (char) 0);
		
		//assign each element in the 2D array to its proper image;
		//extra tiles are assigned to blank image
		int i = 0;
		for(int y=0; y<TILES; y++) {
			for(int x=0; x<TILES; x++) {
				
				//get image for space
				if(i == 0) {
					fontArray[0][0] = space.getLetterImage((char) 32);
					fontResArray[0][0] = null;
				}
				
				//get image for ASCII symbols
				else if(i >= 1 && i <= 94) {
					fontArray[x][y] = letters[i-1].getLetterImage((char) (i+33));
					fontResArray[x][y] = new ImageResource(fontArray[x][y]);
				}
				
				//get image for blank tiles
				else {
					fontArray[x][y] = blank.getLetterImage((char) 0);
					fontResArray[x][y] = null;
				}
				
				i++;
			}
		}
	}
	
	/**create an array of letter objects and return it**/
	public Letter[] createLetters(BufferedImage FONTSHEET) {
		
		//start at the first ASCII symbol
		int placement = 33;
		
		//there are 94 ASCII symbols other than space, so
		//create an array of size 94
		Letter[] letters = new Letter[94];
		
		//for each ASCII symbol, create a new letter
		for(int i=0; i<94; i++) {
			letters[i] = new Letter(FONTSHEET, (char) (placement+i));
		}
		
		return letters;
	}
	
	/**return the ascii value of a given char**/
	public static int getLetterVal(char letter) {
		
		//spaces
		if(letter == 32) { return 0; }
				
		//ASCII symbols
		else if(letter >= 33 && letter <= 126) { return (letter-32); }
				
		//blanks
		else { return 0; }
	}
	
	/**get a letter's buffered image**/
	public BufferedImage getLetterImage(char letter) {
		
		//get the ascii value of the char
		int letterVal = getLetterVal(letter);
		
		//find the position of the letter in the font array and return it
		return fontArray[letterVal % TILES][letterVal / TILES];	
	}
	
	/**get a letter's image resource**/
	public ImageResource getLetterImgRes(char letter) {
		
		//get the ascii value of the char
		int letterVal = getLetterVal(letter);
		
		//find the position of the letter image res in the array and return it
		return fontResArray[letterVal % TILES][letterVal / TILES];	
	}
	
	/**get the pixel width of an entire string of letter images**/
	public int getStringWidth(String string) {
		int total = 0;
		
		//total = width of every word + 1 pixel of space between each letter (except after a)
		for(int i=0; i<string.length(); i++) {
			total += getLetterWidth(string.charAt(i));
			if(string.charAt(i) != 'a') { total += 1; }
		}
		return total;
	}
	
	/**find the position of the last letter of the last word that comes before width x**/
	public int getLastLetter(String string, int x) {
		int total = 0;
		int lastLetter = 0;
		for(int i=0; i<string.length(); i++) {
			total += getLetterWidth(string.charAt(i));
			if(string.charAt(i) != 'a') { total += 1; }
			if(total > x) { break; }
			if(string.charAt(i) == ' ') { lastLetter = i-1; }
		}
		return lastLetter;
	}
	
	/**return a letter's width**/
	public int getLetterWidth(char letter) {
		
		//get the ascii value of the char
		int letterVal = getLetterVal(letter);
		
		//find the letter in the font array and get return the letter's width
		return fontArray[letterVal % TILES][letterVal / TILES].getWidth();
	}
}

/**take in the font sheet and give each letter a sub image and width**/
class Letter {
	
	private BufferedImage FONTSHEET = null;
	private int wLetter;
	private int hLetter = 16;
	private final int TILESIZE = 16;

	/**initialize the width for each letter**/
	public Letter(BufferedImage FONTSHEET, char letter) {
		
		this.FONTSHEET = FONTSHEET;
		
		//take in a letter and set its width
		switch(letter) {
		
		//9 pixels
		case'%':
			this.wLetter = 9;
			break;
			
		//7 pixels
		case'M':
		case'W':
		case'm':
		case'w':
		case'&': //cents
			this.wLetter = 7;
			break;
			
		//6 pixels
		case'A':
		case'V':
		case']': //gamma
			this.wLetter = 6;
			break;

		//5 pixels
		case'B':
		case'C':
		case'D':
		case'G':
		case'H':
		case'K':
		case'N':
		case'O':
		case'P':
		case'Q':
		case'R':
		case'S':
		case'T':
		case'U':
		case'X':
		case'Y':
		case'a':
		case'v':
		case'4':
		case'$':
		case'+':
		case'=':
		case'|': //music
		case'[': //alpha
		case'_': //omega
			this.wLetter = 5;
			break;
			
		//4 pixels
		case'E':
		case'F':
		case'J':
		case'L':
		case'Z':
		case'b':
		case'c':
		case'd':
		case'e':
		case'g':
		case'h':
		case'k':
		case'n':
		case'o':
		case'p':
		case'q':
		case's':
		case'u':
		case'x':
		case'y':
		case'z':
		case'0':
		case'2':
		case'3':
		case'5':
		case'6':
		case'7':
		case'8':
		case'9':
		case'/':
		case'?':
		case'\\'://beta
		case'^': //sigma
		case'<': //left quotation
		case'>': //right quotation
			this.wLetter = 4;
			break;
			
		//3 pixels
		case'f':
		case'r':
		case't':
		case'\"'://quotation
		case'(':
		case')':
		case'*':
			this.wLetter = 3;
			break;
			
		//2 pixels
		case'j':
		case'1':
		case'#': //dot
		case'\''://apostrophe
		case',':
		case'-':
		case';':
		case'{': //left square bracket
		case'}': //right square bracket
		case' ': //space
			this.wLetter = 2;
			break;
			
		//1 pixel
		case'I':
		case'i':
		case'l':
		case'!':
		case'.':
		case':':
			this.wLetter = 1;
			break;
		
		//blank tiles
		default:
			this.wLetter = 0;
		}
	}
	
	//getters
	public int getWidth() { return this.wLetter; }
	public int getHeight() { return this.hLetter; }
	
	/**take a letter from the font sheet**/
	public BufferedImage getSubImg(int x, int y) {
		BufferedImage subImg;
		
		//only give a letter an image if it is not blank
		if(wLetter > 0) { subImg = FONTSHEET.getSubimage(x * TILESIZE, y * TILESIZE, this.wLetter, this.hLetter); }
		else { subImg = null; }
		return subImg;
	}
	
	/**get the image resource for a letter**/
	public BufferedImage getLetterImage(char letter) {
		
		int value, x, y;
		
		//pinpoint the location of every ASCII symbol on the font sheet
		if(letter >= 33 && letter <= 126) {
			value = letter;
			value -= 33;
		}
		
		//blank tiles and spaces refer to 0,0 (a blank letter)
		//however, spaces have width, while blanks do not
		else{ value = 0; }
		
		//convert tile-based coordinates to pixel-based
		x = value % TILESIZE;
		y = value / TILESIZE;
		
		return getSubImg(x, y);
	}
}