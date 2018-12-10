package quiz;

import org.junit.Ignore;
import org.junit.Test;

import quiz.model.QuizRound;

public class GameTest {
	@Test
	@Ignore("Doesn't work locally")
	public void checkVersionAvailable() {
		assert(QuizRound.version != null);
	}
}
