package quiz.model;

public class Answer {
	public String text;
	public boolean isCorrect;
	
	public Answer(String t, boolean c) {
		this.text = t;
		this.isCorrect = c;
	}
}
