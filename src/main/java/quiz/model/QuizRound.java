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
	public static class Norepeat implements Serializable {
		public String[] utterances;
		public int lastIdx = 0;
		Norepeat() {}
		public Norepeat(String[] u) {
			utterances = u;
		}
		public String next() {
			lastIdx += 1 + new Random().nextInt(utterances.length-1);
			if (lastIdx >= utterances.length) {
				lastIdx -= utterances.length;
			}
			return utterances[lastIdx];
		}
	}
	public static final String[] questionOrdinals = { "erste", "zweite", "dritte", "vierte", "fünfte" };
	public static final int LENGTH = questionOrdinals.length;
	public Question[] askedQuestions = new Question[0];
	public Region region;
	public static Player[] players;
	public Norepeat praises = new Norepeat(new String[] { "Super", "Wahnsinn", "Spitze", "Toll", "Fantastisch", "Großartig", "Stark", "Sehr gut", "Das ist richtig", "Genauso ist es", "Volltreffer", "Stimmt genau", "Korrekt", "Haargenau", "Vollkommen richtig" });
	public Norepeat declines = new Norepeat(new String[] { "Falsch", "Leider falsch", "Leider nicht richtig", "Leider kein Treffer", "Leider keinen Punkt für dich", "Das war nichts", "Schade" });
	public Norepeat corrections = new Norepeat(new String[] { "Die richtige Lösung ist ", "Die richtige Lösung wäre gewesen ", "Die Lösung lautet ", "Richtig wäre gewesen" });
	
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
		if(count <= 0) {
			speechText.append("Netter Versuch. ");
			return;
		}
		if (count >= 6) {
			speechText.append("Es können höchstens fünf Spieler teilnehmen. ");
			return;
		}
		// valid number, we now create the players array:
		players = new Player[count];
		if (count == 1) {
			players[0] = new Player("", 0);
			speechText.append("Alles klar. Dann spielen nur wir beide! ");
			return;
		}
		if (count == 2) { 
			players[0] = new Player("Heidi", 0);
			players[1] = new Player("Peter", 0);
			speechText.append("Spitze. Zu zweit macht's immer mehr Spaß."
					+ " Ich nenn euch jetzt einfach mal "+players[0].name+" und "+players[1].name+". ");
			return;
		} else if (count == 3) {
			players[0] = new Player("Justus Jonas", 0);
			players[1] = new Player("Peter Shaw", 0);
			players[2] = new Player("Bob Andrews", 0);
			speechText.append("Yeih. Ihr seid die drei Fragezeichen. ");
		} else if (count == 4) {
			players[0] = new Player("Mickey", 0);
			players[1] = new Player("Minney", 0);
			players[2] = new Player("Donald", 0);
			players[3] = new Player("Daisy", 0);
			speechText.append("Cool! Vier gewinnt! ");
		} else {
			players[0] = new Player("Harry Potter", 0);
			players[1] = new Player("Hermine", 0);
			players[2] = new Player("Ron", 0);
			players[3] = new Player("Hedwig", 0);
			players[4] = new Player("Sprechender Hut", 0);
			speechText.append("Alles klar. ");
		}
		for (int i=0; i<players.length; i++)
			speechText.append("Spieler "+(i+1)+", du bist "+players[i].name+". ");
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
		String playerName = players[asked % players.length].name;
		if (asked == 0) { // TODO: only in first round of *game*
			if (players.length == 1) {
				speechText.append("Los geht's mit deinen ersten "+LENGTH+" Fragen. ");
			} else if (players.length == 2) {
				speechText.append("Ich stelle euch jeweils abwechselnd "+LENGTH+" Fragen. "
					+ "Ladies first! Auf geht's, "+playerName+"! ");
			} else if (players.length == 3) {
				speechText.append("Auf geht's mit der ersten Runde. "+playerName+" beginnt. ");
			} else if (players.length == 4) {
				speechText.append("Los geht's mit den ersten "+LENGTH+" Fragen für "+playerName+". "); // ???
			} else if (players.length == 5) {
				speechText.append("Los geht's mit der ersten Runde. Frage 1 ist für "+playerName+": ");
			}
		} else {
			speechText.append("Deine "+questionOrdinals[asked / players.length] + " Frage");
			if (!playerName.isEmpty()) {
				speechText.append(", " + playerName);
			}
			speechText.append(": ");
		}
		q.shuffleAnswers();
		q.ask(speechText);
	}
	public void selectAnswer(int answerIndex, StringBuilder speechText) {
		int lastAsked = askedQuestions.length - 1;
		if (lastAsked < 0) {
			speechText.append("Ich habe noch gar nichts gefragt. ");
			return;
		}
		Question lastQuestion = askedQuestions[lastAsked];
		Player currentPlayer = players[lastAsked % players.length];
		if (answerIndex < 0) {
			if (answerIndex == -1) {
				speechText.append(declines.next() + ". " + corrections.next() + " " + lastQuestion.correctAnswer() + ". ");
			} else {
				// You unlocked the hidden feature!
				speechText.append("Ich kenne zwar die ultimative Frage nicht, aber das ist sicher die richtige Antwort. ");
				currentPlayer.score += 1;
			}
		} else {
			Answer answer = lastQuestion.answers.get(answerIndex);
			currentPlayer.answer(answer);
			speechText.append("<audio src='soundbank://soundlibrary/ui/gameshow/"+(answer.isCorrect? "amzn_ui_sfx_gameshow_positive_response_01'/>" : "amzn_ui_sfx_gameshow_negative_response_02'/>"));
			speechText.append(answer.isCorrect
				? praises.next() +"! Übrigens, " + lastQuestion.getInfo() + ". "
				: declines.next() + ". " + corrections.next() + " " + lastQuestion.correctAnswer() + ". ");
		}
		if (askedQuestions.length < players.length * LENGTH) {
			askNewQuestion(speechText);
		} else {
			askedQuestions = new Question[0];
			speechText.append("Die Runde ist zu Ende. Das war die letzte Frage in dieser Runde. ");
			for (int i=0; i<players.length; i++)
				speechText.append(players[i].name + ", du hast " + players[i].getScore() + " Punkte erreicht. ");
			speechText.append("Willst du weiterspielen?"); // TODO: oder das Quiz beenden
		}
	}
}
