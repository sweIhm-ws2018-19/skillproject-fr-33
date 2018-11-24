package quiz.model;

import java.net.URL;
import java.util.Arrays;
import java.util.Collections;

public class Question {
	public URL id;
	public String text;
	public Answer[] answers;
	
	public Question(URL id, String t, Answer[] as) {
		this.id = id;
		this.text = t;
		this.answers = as;
	}
	
	public void shuffleAnswers() {
		Collections.shuffle(Arrays.asList(this.answers));
	}
	
	public int countAnswers() {
		return this.answers.length;
	}
	
	public int correctAnswerIndex() {
		for (int i=0; i<this.answers.length; i++)
			if (this.answers[i].isCorrect)
				return i;
		throw new IllegalStateException("question has no correct answer");
	}
}
