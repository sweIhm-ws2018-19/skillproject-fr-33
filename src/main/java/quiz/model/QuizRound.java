package quiz.model;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Map;

import quiz.QuestionLoader;

public class QuizRound {
	public int length = 5;
	public Question[] askedQuestions = new Question[0];
	public Region region;
	public Player[] players;
	
	public static QuizRound fromSessionAttributes(Map<String, Object> sessionAttributes) {
//      return(QuizRound) sessionAttributes.get("round");
		Region region = new Region("/Berlin.csv", new QuestionLoader().chooseRegion("/Berlin.csv").load());
		QuizRound round = new QuizRound(region, null);
		String players = (String) sessionAttributes.get("players");
		if (players != null)
			round.createPlayers(3);
//    	sessionAttributes.put("round", round);
		return round;
	}
	public QuizRound(Region r, Player[] ps) {
		this.region = r;
		this.players = ps;
	}
	public boolean isComplete() {
		return region != null && players != null && players.length > 0;
	}
	
	public void createPlayers(int count) {
		// TODO: Range validation
		players = new Player[count];
		for (int i=0; i<count; i++)
			players[i] = new Player("Spieler "+i, 0);
	}
	public Question askQuestion(StringBuilder speechText) {
		Question q = this.region.questions[0]; // TODO: filter this.region.questions for not yet asked ones, and choose randomly
		int asked = this.askedQuestions.length;
		this.askedQuestions = Arrays.copyOf(this.askedQuestions, asked + 1);
		this.askedQuestions[asked] = q;
		speechText.append(players[asked % players.length].name + ": ");
		speechText.append(q.text);
		return q;
	}
}
