package quiz;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import quiz.model.Answer;
import quiz.model.Question;
import quiz.model.Region;

public class RegionTest {
	@Test
	public void run() {
		Question q = new Question("url", "frage", new Answer[] {
			new Answer("a", true),new Answer("b", false),new Answer("c", false)
		}, "info");
		Region r = new Region("test", new Question[] { q, q });
		assertEquals(r.questions.length, 2);
		assertEquals(r.nextQuestion(false), q);
		assertEquals(r.questions.length, 1);
		assertEquals(r.nextQuestion(false), q);
		assertEquals(r.questions.length, 0);
		assertEquals(r.nextQuestion(false), null);
	}
}
