package quiz.model;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import quiz.model.Answer;

public class Question {
	public String id;
	public String text;
	public List<Answer> answers;
	public String info;
	
	public Question(String id, String t, Answer[] as, String info) {
		this.id = id;
		this.text = t;
		this.answers = Arrays.asList(as);
		this.info = info;
	}
	
	public void shuffleAnswers() {
		Collections.shuffle(answers);
	}
	
	public int countAnswers() {
		return this.answers.size();
	}
	
	public int correctAnswerIndex() {
		for (int i=0; i<this.answers.size(); i++)
			if (this.answers.get(i).isCorrect)
				return i;
		throw new IllegalStateException("question has no correct answer");
	}

	public void ask(StringBuilder speechText) {
		// TODO: SSML
		speechText.append(text + " ");
		char i = 'A';
		for (Answer a: answers) {
			speechText.append(i++ + " - " + a.text + " ");
		}
	}
	
	public String getInfo() { return info; } // info
}
