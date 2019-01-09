package quiz.model;

import java.io.Serializable;
import java.util.Random;

public class Region implements Serializable {
	public String id;
	public Question[] questions;
	
	public Region() {}
	public Region(String id, Question[] qs) {
		this.id = id;
		this.questions = qs;
	}
	public Question nextQuestion(boolean isDemo) {
		if (questions.length == 0)
			return null;
		Question[] old = questions;
		int i = isDemo ? 0 : new Random().nextInt(questions.length);
		questions = new Question[questions.length - 1];
		System.arraycopy(old, 0, questions, 0, i);
		System.arraycopy(old, i+1, questions, i, questions.length-i);
		return old[i];
	}
}
