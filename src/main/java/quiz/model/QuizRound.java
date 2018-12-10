package quiz.model;

import java.util.Arrays;
import java.util.Map;
import java.util.Random;

import quiz.QuestionLoader;

public class QuizRound {
	class Norepeat {
		private String[] utterances;
		public int lastIdx = 0;
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
	private Norepeat praises = new Norepeat(new String[] {"Sehr gut", "Großartig", "Ausgezeichnet", "Richtig", "Wahnsinn", "Super", "Spitze! Das war richtig", "Toll"});
	private Norepeat declines = new Norepeat(new String[] {"Das war leider die falsche Antwort. ", "Leider falsch. ", "Das war leider nicht korrekt. "});
	
	public static QuizRound fromSessionAttributes(Map<String, Object> sessionAttributes) {
//      return(QuizRound) sessionAttributes.get("round");
		QuizRound round = new QuizRound(null, null);
		Integer players = (Integer) sessionAttributes.get("players");
		if (players != null)
			round.createPlayers(players, new StringBuilder());
		String region = (String) sessionAttributes.get("region");
		if (region != null) {
			round.region = new Region(region, null);
			new QuestionLoader(round.region).load();
			
			Integer askedQuestionsSize = (Integer) sessionAttributes.get("askedQuestionsSize");
			if (askedQuestionsSize != null) {
				int len = askedQuestionsSize;
				round.askedQuestions = Arrays.copyOfRange(round.region.questions, 0, len);
				round.region.questions = Arrays.copyOfRange(round.region.questions, len, round.region.questions.length);
			}
		}
		Integer lastPraiseIdx = (Integer) sessionAttributes.get("praiseIdx");
		if (lastPraiseIdx != null)
			round.praises.lastIdx = lastPraiseIdx;
		Integer lastFalseIdx = (Integer) sessionAttributes.get("falseIdx");
		if (lastFalseIdx != null)
			round.declines.lastIdx = lastFalseIdx;
		
//    	sessionAttributes.put("round", round);
		return round;
	}
	public void intoSessionAttributes(Map<String, Object> sessionAttributes) {
		if (players != null) {
			sessionAttributes.put("players", players.length);
		}
		if (region != null) {
			sessionAttributes.put("region", region.id);
			sessionAttributes.put("askedQuestionsSize", askedQuestions.length);
		}
		sessionAttributes.put("praiseIdx", praises.lastIdx);
		sessionAttributes.put("falseIdx", declines.lastIdx);
	}
	
	public QuizRound(Region r, Player[] ps) {
		this.region = r;
		this.players = ps;
	}
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
		if (region.questions.length == 0) {
			speechText.append("Tut mir leid, ich habe keine neuen Fragen mehr. ");
			return;
		}
		Question q = this.region.questions[0]; // TODO: filter this.region.questions for not yet asked ones, and choose randomly
		int asked = this.askedQuestions.length;
		this.askedQuestions = Arrays.copyOf(this.askedQuestions, asked + 1);
		this.askedQuestions[asked] = q;
		// q.shuffleAnswers(); // TODO: doesn't get persisted yet
		speechText.append(players[asked % players.length].name + ": ");
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
