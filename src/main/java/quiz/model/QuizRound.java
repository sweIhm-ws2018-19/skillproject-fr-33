package quiz.model;

import java.util.Arrays;
import java.util.Map;
import java.util.Random;

import java.io.IOException;
import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import quiz.QuestionLoader;

public class QuizRound implements Serializable {
	static public int length = 2;
	public Question[] askedQuestions = new Question[0];
	public Region region;
	public Player[] players;
	public int lastPraiseIdx = 0;
	
	static ObjectMapper mapper = new ObjectMapper();
	
	public static QuizRound fromSessionAttributes(Map<String, Object> sessionAttributes) {
		String round = (String) sessionAttributes.get("round");
		if (round != null)
			try {
				return mapper.readValue(round, QuizRound.class);
			} catch (IOException e) {
				e.printStackTrace();
			}
		return new QuizRound(null, null);
	}
	public void intoSessionAttributes(Map<String, Object> sessionAttributes) {
		try {
			sessionAttributes.put("round", mapper.writeValueAsString(this));
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
	}
	
	public QuizRound() {}
	public QuizRound(Region r, Player[] ps) {
		this.region = r;
		this.players = ps;
	}
	@JsonIgnore
	public boolean isComplete() {
		return region != null && players != null && players.length > 0;
	}
	
	public void createPlayers(int count) {
		// TODO: Range validation
		players = new Player[count];
		for (int i=0; i<count; i++)
			players[i] = new Player("Spieler "+(i+1), 0);
	}
	public void selectRegion(String region, StringBuilder speechText) {
		if (region.equals("Berlin") || region.equals("Ostsee") || region.equals("Dresden")) {
			this.region = new Region(region, null);
			new QuestionLoader(this.region).load();
		} else {
			speechText.append("In "+region+" kenne ich mich leider nicht aus. ");
		}
	}
	public void askQuestion(StringBuilder speechText) {
		if (region.questions.length == 0) {
			speechText.append("Tut mir leid, ich habe keine neuen Fragen mehr. ");
			return;
		}
		Question q = this.region.questions[0]; // TODO: filter this.region.questions for not yet asked ones, and choose randomly
		int asked = this.askedQuestions.length;
		this.askedQuestions = Arrays.copyOf(this.askedQuestions, asked + 1);
		this.askedQuestions[asked] = q;
		speechText.append(players[asked % players.length].name + ": ");
		q.shuffleAnswers();
		q.ask(speechText);
	}
	public void selectAnswer(int answerIndex, StringBuilder speechText) {
		String[] praises = new String[] {"Sehr gut", "Großartig", "Ausgezeichnet", "Richtig", "Wahnsinn", "Super", "Spitze", "Toll"};
		lastPraiseIdx += 1 + new Random().nextInt(praises.length-1);
		if (lastPraiseIdx >= praises.length) {
			lastPraiseIdx -= praises.length;
		}
		String praise = praises[lastPraiseIdx];
		
		int lastAsked = askedQuestions.length - 1;
		if (lastAsked < 0) {
			speechText.append("Ich habe noch gar nichts gefragt. ");
			return;
		}
		Player currentPlayer = players[lastAsked % players.length];
		Answer answer = askedQuestions[lastAsked].answers.get(answerIndex);
		currentPlayer.answer(answer);
		speechText.append(answer.isCorrect ? praise+"! " : "Falsch! ");
		if (askedQuestions.length < players.length * length) {
			askQuestion(speechText);
		} else {
			askedQuestions = new Question[0];
			speechText.append("Die Runde ist zu Ende. ");
			// TODO: gleich weiter?
		}
	}
}
