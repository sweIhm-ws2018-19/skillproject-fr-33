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
	
	public static QuizRound fromSessionAttributes(Map<String, Object> sessionAttributes) throws MalformedURLException {
//      return(QuizRound) sessionAttributes.get("round");
		Region region = new Region(new URL("/Berlin.csv"), new QuestionLoader().load());
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
			players[i] = new Player(0);
	}
	public Question askQuestion() {
		Question q = this.region.questions[0]; // TODO: filter this.region.questions for not yet asked ones, and choose randomly
		this.askedQuestions = Arrays.copyOf(this.askedQuestions, this.askedQuestions.length + 1);
		this.askedQuestions[this.askedQuestions.length - 1] = q;
		return q;
	}
}
