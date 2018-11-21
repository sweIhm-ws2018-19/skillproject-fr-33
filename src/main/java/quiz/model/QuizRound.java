package main.java.quiz.model;

import java.util.Arrays;

public class QuizRound {
	public int length = 5;
	public Question[] askedQuestions = new Question[0];
	public Region region;
	public Player[] players;
	
	public QuizRound(Region r, Player[] ps) {
		this.region = r;
		this.players = ps;
	}
	
	public Question askQuestion() {
		Question q = this.region.questions[0]; // TODO: filter this.region.questions for not yet asked ones, and choose randomly
		this.askedQuestions = Arrays.copyOf(this.askedQuestions, this.askedQuestions.length + 1);
		this.askedQuestions[this.askedQuestions.length - 1] = q;
		return q;
	}
	
}
