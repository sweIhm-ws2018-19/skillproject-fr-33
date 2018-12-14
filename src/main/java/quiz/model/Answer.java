package quiz.model;

import java.io.Serializable;

public class Answer implements Serializable {
	public String text;
	public boolean isCorrect;
	
	public Answer() {}
	public Answer(String t, boolean c) {
		this.text = t;
		this.isCorrect = c;
	}
}
