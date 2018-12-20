package quiz.model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnore;

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
	@JsonIgnore
	public String getScore() {
		return (score == 1)? "einen Punkt " : score +" Punkte" ;
	}
}
