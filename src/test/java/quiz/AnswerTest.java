package quiz;

import static org.junit.Assert.*;

import org.junit.Test;

import quiz.model.Answer;

public class AnswerTest {

	@Test
	public void test() {
		Answer x = new Answer("Schwalbe", false);
		assertEquals(1, x.similarity("Schwalbe"), 0);
		assertTrue(1 > x.similarity("albe"));
		
		Answer y = new Answer("Theodor Fontane", true);
		assertTrue(0.5 > y.similarity("dsfh sdaklfjsdaj fasd"));
		assertTrue(0.5 < y.similarity("Fontane"));
	}
}
