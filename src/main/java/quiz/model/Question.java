package quiz.model;

import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import quiz.model.Answer;

public class Question {
	public String id;
	public String text;
	public List<Answer> answers;
	
	public Question(String id, String t, Answer[] as) {
		this.id = id;
		this.text = t;
		this.answers = Arrays.asList(as);
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
	
	public Answer[] getAnswers() {
		shuffleAnswers();
		return (Answer[])answers.toArray();
	}
	
	public String getQuestion() {
		return text;
	}
}
