package quiz.model;

import java.util.Arrays;
import java.util.Random;

import java.io.Serializable;

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
	public Player[] players;
	public boolean isDemo = false;
	public Norepeat praises = new Norepeat(new String[] { "Super", "Wahnsinn", "Spitze", "Toll", "Fantastisch", "Großartig", "Stark", "Sehr gut", "Das ist richtig", "Genauso ist es", "Volltreffer", "Stimmt genau", "Korrekt", "Haargenau", "Vollkommen richtig" });
	public Norepeat declines = new Norepeat(new String[] { "Falsch", "Leider falsch", "Leider nicht richtig", "Leider kein Treffer", "Leider keinen Punkt für dich", "Das war nichts", "Schade" });
	public Norepeat corrections = new Norepeat(new String[] { "Die richtige Lösung ist ", "Die richtige Lösung wäre gewesen ", "Die Lösung lautet ", "Richtig wäre gewesen" });
	
	public QuizRound() {}
	public QuizRound(Region r, Player[] ps) {
		this.region = r;
		this.players = ps;
	}

	public void toNextQuestion(StringBuilder speechText) {
		Question q = this.region.nextQuestion(isDemo);
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
	}
}
