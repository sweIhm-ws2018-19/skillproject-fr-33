package quiz.model;

public class Player {
	public int score;
	
	public Player(int s) {
		this.score = s;
	}
	
	public boolean answer(Answer a) {
		if (a.isCorrect)
			score += 1;
		return a.isCorrect;
	}
}
