package quiz.model;

import java.util.Arrays;
import java.util.Map;
import java.util.Random;

import quiz.QuestionLoader;
import quiz.*;

public class QuizRound {
	static final public int length = 2;
	public Question[] askedQuestions = new Question[0];
	public Region region;
	public Player[] players;
	private int lastPraiseIdx = 0;
	
	
	public static QuizRound fromSessionAttributes(Map<String, Object> sessionAttributes) {
//      return(QuizRound) sessionAttributes.get("round");
		QuizRound round = new QuizRound(null, null);
		Integer players = (Integer) sessionAttributes.get("players");
		if (players != null)
			round.createPlayers(players,new StringBuilder());
		
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
			round.lastPraiseIdx = lastPraiseIdx;
		
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
		sessionAttributes.put("praiseIdx", lastPraiseIdx);
	}
	
	public QuizRound(Region r, Player[] ps) {
		this.region = r;
		this.players = ps;
	}
	public boolean isComplete() {
		return region != null && players != null && players.length > 0;
	}
	
	public void createPlayers(int count, StringBuilder speechText) {
		// TODO: Range validation
		players = new Player[count];
			if(count == 1) {
				players[0] = new Player("",0);
				speechText.append("Alles klar. Dann spielen nur wir beide! Los geht´s mit deinen ersten fünf Fragen.");
			}
			else if(count == 2) { 
				players[0] = new Player("Heidi",0);
				players[1] = new Player("Peter",0);
				speechText.append("Spitze. Zu zweit macht's immer mehr Spaß."
						+ " Ich nenn euch jetzt einfach mal Heidi und Peter."
						+ " Ich stelle euch jeweils abwechselnd 5 Fragen."
						+ " Ladies first! Auf geht's, Heidi!");
			}
			else if(count == 3) {
				players[0] = new Player("Justus Jonas",0);
				players[1] = new Player("Peter Shaw",0);
				players[2] = new Player("Bob Andrews",0);
				speechText.append("Yeih. Ihr seid die drei Fragezeichen. Spieler 1,"
						+ " du bist Justus Jonas, Spieler 2, du Peter Shaw, und Spieler 3, Bob Andrews."
						+ " Auf geht´s mit der ersten Runde. Justus Jonas beginnt.");
			}
			else if(count == 4) {
				players[0] = new Player("Mickey",0);
				players[1] = new Player("Minney",0);
				players[2] = new Player("Donald",0);
				players[3] = new Player("Daisy",0);
				speechText.append("Cool! Vier gewinnt! "
						+ "Spieler 1, du heißt Mickey, Spieler 2, du bist Minney,"
						+ " Spieler 3, du Donald, und Spieler 4 Daisy."
						+ " Los geht´s mit den ersten fünf Fragen für Mickey.");
			}
			else {
				players[0] = new Player("Harry Potter",0);
				players[1] = new Player("Hermine",0);
				players[2] = new Player("Ron",0);
				players[3] = new Player("Hedwig",0);
				players[4] = new Player("Sprechender Hut",0);
				speechText.append("Alles klar. Spieler 1, du bist Harry Potter."
						+ " Spieler 2, du bist Hermine, Spieler 3, du bist Ron,"
						+ " Spieler 4, Du bist Hedwig und Spieler 5,"
						+ " du bist der sprechende Hut. Los geht´s mit der ersten Runde.  Frage 1 ist für Harry:");
			}
		//	players[i] = new Player("Spieler "+(i+1), 0);
		//}
    	//speechText.append("Wir spielen mit "+count+" Spielern. ");
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
		if(players.length == 1) {
			speechText.append(players[asked % players.length].name);
		}
		else {
			speechText.append(players[asked % players.length].name + ": ");
		}
		//	speechText.append(players[asked % players.length].name + ": ");
		// q.shuffleAnswers(); // TODO: doesn't get persisted yet
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
