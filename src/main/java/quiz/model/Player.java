package quiz.model;

import java.io.Serializable;

public class Player implements Serializable {
	public String name;
	public int roundScore;
	public int score;
	
	public Player() {}
	public Player(String n, int r, int s) {
		this.name = n;
		this.roundScore = r;
		this.score = s;
	}
	public Player(String n) {
		this(n, 0, 0);
	}
	
	public boolean answer(Answer a) {
		if (a.isCorrect)
			roundScore += 1;
		return a.isCorrect;
	}
	public int endRound() {
		score += roundScore;
		int r = roundScore;
		roundScore = 0;
		return r;
	}
	public int getScore() {
		return score;
	}
}
