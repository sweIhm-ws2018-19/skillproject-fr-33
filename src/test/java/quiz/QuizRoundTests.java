package quiz;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;

import junitparams.Parameters;
import quiz.model.Answer;
import quiz.model.Player;
import quiz.model.Question;
import quiz.model.QuizRound;
import quiz.model.Region;
import junitparams.JUnitParamsRunner;

@RunWith(JUnitParamsRunner.class)
public class QuizRoundTests {
	public Player player = new Player("",0,0);
	public Question question = new Question();
	public QuizRound quizround = new QuizRound(null, new Player[] { player });
	Question q = new Question("url", "frage", new Answer[] {
		new Answer("a", true),new Answer("b", false),new Answer("c", false)
	}, "info");
	Region r = new Region("test", new Question[] { q });
	
	@Test
	public void AskNewQuestionEmptyTest() {
		quizround.region = r;
		quizround.region.nextQuestion(false);
		
		final StringBuilder speechText = new StringBuilder();
		quizround.toNextQuestion(speechText);
		assertEquals("Tut mir leid, ich habe keine neuen Fragen mehr. ",speechText.toString());
	}
	@Parameters({
		"0",
		"1"
	})
	@Test
	public void SelectAnswer(int runs) {
		final StringBuilder speechText = new StringBuilder();
		final int answerIdx = 0;

		quizround.askedQuestions = new Question[runs];
		for(int i=0; i<runs;i++) {
			quizround.askedQuestions[i] = q;
		}
		quizround.selectAnswer(answerIdx, speechText);
		if(runs==0) {assertEquals("Ich habe noch gar nichts gefragt. ",speechText.toString());}
		if(runs==1) {  };
	}
	
	Question q2 = new Question("url", "frage", new Answer[] {
			new Answer("a", true),new Answer("b", false),new Answer("c", false)
		}, "info");
	Region r2 = new Region("test", new Question[] { q2, q2, q2, q2, q2 });
	
	@Parameters({
		"1, Los geht's mit deinen ersten 5 Fragen. ",
		"2, Ich stelle euch jeweils abwechselnd 5 Fragen. Ladies first! Auf geht's\\, <Spielername>! ",
		"3, Auf geht's mit der ersten Runde. <Spielername> beginnt. ",
		"4, Los geht's mit den ersten 5 Fragen für <Spielername>",
		"5, Los geht's mit der ersten Runde. Frage 1 ist für <Spielername>: "
	})
	@Test
	public void AskNewQuestionRespondTest(int playerLength,String expected){
		Player[] players2 = new Player[playerLength];
		for(int i=0; i<playerLength;i++) {
			players2[i] = new Player("<Spielername>", 0, 0) ;
		}
		quizround.players = players2;
		quizround.region = r2;
		quizround.region.nextQuestion(false);
		
		final StringBuilder speechText = new StringBuilder();
		quizround.toNextQuestion(speechText);
		assertTrue(speechText.toString().contains(expected));
	}
	
}
