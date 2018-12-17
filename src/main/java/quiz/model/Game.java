package quiz.model;

import java.io.Serializable;

import quiz.model.QuizRound;

public class Game implements Serializable{
	private static Player[] players;
	
	private int[] quizrounds;
	private String[] levels;
	private int playerCount;
	private int[] points;
	
	public void CountRound(){
		
		
		players = QuizRound.players;
		playerCount = players.length;
		boolean moreThanOneRound = false;
		
		//Get played QuizRounds
		if(players[0].getScore()>5) { moreThanOneRound = true; }
		
		//Save Levels
		for(int i=0; i<playerCount; i++) {
			points[i] = QuizRound.players[i].getScore();
			//Calculate Levels
			if(moreThanOneRound) { quizrounds[i] = points[i]/5; }
			else { quizrounds[i] = 1; }
			points[i] = points[i]/quizrounds[i];
			if(points[i] <= 1) { levels[i] = "Tourist"; }
			if(points[i] > 1 && points[i] <=3) { levels[i] = "Zugezogener"; }
			if(points[i] == 4) { levels[i] = "Stadtführer"; }
			if(points[i] == 5) { levels[i] = "Einheimischer"; }
		}
	}
	public int[] GetPoints() {
		return points;
	}
	public String[] GetLevel() {
		return levels;
	}
}
