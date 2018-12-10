package quiz.model;

import java.io.Serializable;

public class Player implements Serializable {
	public String name;
	public int score;
	
	public Player() {}
	public Player(String n, int s) {
		this.name = n;
		this.score = s;
	}
	
	public boolean answer(Answer a) {
		if (a.isCorrect)
			score += 1;
		return a.isCorrect;
	}
	public int getScore() {
		return score;
	}
}
