package quiz;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;

import junitparams.Parameters;
import junitparams.JUnitParamsRunner;

import quiz.model.Answer;
import quiz.model.Player;
import quiz.model.QuizGame;
import quiz.model.QuizRound;

@RunWith(JUnitParamsRunner.class)
public class ScoreLevelTest {
	/**
	 * test code:
	 * int averagePoints = (int) Math.round(player.getScore() / (double) roundCount);
	 */
	
	public Player player = new Player("",0,0);
	
	public Answer answer = new Answer("text",true);
	public QuizGame game = new QuizGame();
	
	public final int playerCount = 1;
	public final int roundCount = 2;
	

	@Test
	public void ScoreTest() {
		for(int i=5;i>0;i--)
		{
			player.answer(answer);
		}
		assertEquals(5, player.endRound());
	}
	
	@Test
	public void ScoreRoundTest() {
		for(int i=0;i<10;)
		{
			player.answer(answer);
			i++;
			if(i%5 == 0) { 
				assertEquals(5,player.roundScore);
				player.endRound();
			}
		}
		assertEquals(10,player.getTotalScore());
	}
	
	@Parameters({
		"0, Tourist",
		"2, Tourist",
		"4, Zugezogener",
		"6, Zugezogener",
		"7, Stadtführer",
		"8, Stadtführer",
		"9, Einheimischer",
		"10, Einheimischer"
	})
	@Test
	public void LevelTest(int score, String expected) {
		StringBuilder speechText = new StringBuilder();
		game.roundCount = 2;
		game.round = new QuizRound(null, new Player[] { new Player("<Spielername>",5,score) });
		
		game.end(speechText);
		System.out.println(speechText);
		String out = speechText.toString();
		assertTrue(out.contains("<Spielername>"));
		assertTrue(out.contains(expected));
	}
}