package quiz.model;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.Random;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import quiz.QuestionLoader;

public class QuizRound implements Serializable {
	static public class Norepeat {
		public String[] utterances;
		public int lastIdx = 0;
		Norepeat() {}
		Norepeat(String[] u) {
			utterances = u;
		}
		String next() {
			lastIdx += 1 + new Random().nextInt(utterances.length-1);
			if (lastIdx >= utterances.length) {
				lastIdx -= utterances.length;
			}
			return utterances[lastIdx];
		}
	}
	static final public int length = 2;
	public Question[] askedQuestions = new Question[0];
	public Region region;
	public Player[] players;
	public Norepeat praises = new Norepeat(new String[] {"Sehr gut", "Großartig", "Ausgezeichnet", "Richtig", "Wahnsinn", "Super", "Spitze! Das war richtig", "Toll"});
	public Norepeat declines = new Norepeat(new String[] {"Das war leider die falsche Antwort. ", "Leider falsch. ", "Das war leider nicht korrekt. "});
	
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
	
	public void createPlayers(int count, StringBuilder speechText) {
		if(count >= 6 || count <= 0) {
			speechText.append("Es können höchstens fünf Spieler teilnehmen. Bitte gib jetzt die Spieleranzahl ein. ");
		} else {
			players = new Player[count];
			for (int i=0; i<count; i++)
				players[i] = new Player("Spieler "+(i+1), 0);
			speechText.append("Wir spielen mit "+count+" Spielern. ");
		}
	}
	public void selectRegion(String region, StringBuilder speechText) {
		if (region.equals("Berlin") || region.equals("Ostsee") || region.equals("Dresden")) {
			this.region = new Region(region, null);
			new QuestionLoader(this.region).load();
		} else {
			speechText.append("In "+region+" kenne ich mich leider nicht aus. ");
		}
	}
	public void askNewQuestion(StringBuilder speechText) {
		Question q = this.region.nextQuestion();
		if (q == null) {
			speechText.append("Tut mir leid, ich habe keine neuen Fragen mehr. ");
			return;
		}
		int asked = this.askedQuestions.length;
		this.askedQuestions = Arrays.copyOf(this.askedQuestions, asked + 1);
		this.askedQuestions[asked] = q;
		speechText.append(players[asked % players.length].name + ": ");
		q.shuffleAnswers();
		q.ask(speechText);
	}
	public void selectAnswer(int answerIndex, StringBuilder speechText) {
		int lastAsked = askedQuestions.length - 1;
		if (lastAsked < 0) {
			speechText.append("Ich habe noch gar nichts gefragt. ");
			return;
		}
		Player currentPlayer = players[lastAsked % players.length];
		Answer answer = askedQuestions[lastAsked].answers.get(answerIndex);
		currentPlayer.answer(answer);
		speechText.append(answer.isCorrect ? praises.next() +"! ": declines.next());
		if (askedQuestions.length < players.length * length) {
			askNewQuestion(speechText);
		} else {
			askedQuestions = new Question[0];
			speechText.append("Die Runde ist zu Ende. Das war die letzte Frage in dieser Runde. ");
			for (int i=0; i<players.length; i++)
				speechText.append(players[i].name + ", du hast " + players[i].getScore() + " Punkte erreicht. ");
			speechText.append("Hast du Lust, noch weiter zu spielen?");
		}
	}
}
