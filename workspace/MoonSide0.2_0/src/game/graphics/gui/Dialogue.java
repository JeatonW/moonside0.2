package game.graphics.gui;

public class Dialogue {
	
	//text being written
	private String text1;
	private String text2;
	
	//links to next dialogues
	private Dialogue next1;
	private Dialogue next2;
	
	//answer options
	private String op1;
	private String op2;
	
	//whether or not this dialogue is a question (contains answer options)
	private boolean isQuestion = false;
	
	//what answer option has been chosen
	private int chosenOption = 0;
	
	/**single dialogue without a link to a next dialogue**/
	public Dialogue(String text) {
		this.text1 = text;
		this.next1 = null;
		
		this.text2 = null;
		this.next2 = null;
		this.op1 = null;
		this.op2 = null;
		
		this.isQuestion = false;
	}

	/**single dialogue with a link to a next dialogue**/
	public Dialogue(String text, Dialogue next) {
		this.text1 = text;
		this.next1 = next;
		
		this.text2 = null;
		this.next2 = null;
		this.op1 = null;
		this.op2 = null;
		
		this.isQuestion = false;
	}
	
	/**a dialogue that contains a question with two answer options and response text for each. no links to next dialogues**/
	public Dialogue(String op1, String text1, String op2, String text2) {
		this.op1 = op1;
		this.op2 = op2;
		this.next1 = null;
		this.next2 = null;
		this.text1 = text1;
		this.text2 = text2;
		this.isQuestion = true;
	}

	/**a dialogue that contains a question with two answer options and response text for each. there are links to next dialogues**/
	public Dialogue(String op1, String text1, Dialogue next1, String op2, String text2, Dialogue next2) {
		this.op1 = op1;
		this.op2 = op2;
		this.next1 = next1;
		this.next2 = next2;
		this.text1 = text1;
		this.text2 = text2;
		this.isQuestion = true;
	}
	
	//setters
	public void setChosenOption(int option) { this.chosenOption = option; }
	
	//getters
	public Dialogue getNext() { 
		if(!isQuestion) { return getNext(0); }
		else { return getNext(chosenOption); }
	}
	public Dialogue getNext(int option) {
		if(next1 == null) { return null; }
		if(option == 0) { return next1; }
		else if(option == 1) { return next2; }
		else { return null; }
	}
	public String getText() { return getText(0); }
	public String getText(int option) {
		if(option == 0) { return text1; }
		else if(option == 1) { return text2; }
		else { return null; }
	}
	public String getOptionOne() { return op1; }
	public String getOptionTwo() { return op2; }
	public boolean isQuestion() { return isQuestion; }
}
