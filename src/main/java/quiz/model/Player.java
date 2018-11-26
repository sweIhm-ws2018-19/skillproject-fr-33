package quiz.model;

public class Player {
	public String name;
	public int score;
	
	public Player(String n, int s) {
		this.name = n;
		this.score = s;
	}
	
	public boolean answer(Answer a) {
		if (a.isCorrect)
			score += 1;
		return a.isCorrect;
	}
}
