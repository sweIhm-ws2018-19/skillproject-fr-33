package quiz;

import static org.junit.Assert.*;

import java.util.Arrays;

import org.junit.Test;

import quiz.model.Answer;
import quiz.model.Question;

public class QuestionTest {

	@Test
	public void test() {
		Question question = new Question("url", "frage", new Answer[] {
				new Answer("a", true),new Answer("b", false),new Answer("c", false),new Answer("d", false),new Answer("e", false)
		});
		
		// With 3 variables(Options) there are only 6 permutations
		// while with 5 variables(120 permutations) the Probability of
		// 2 equal permutations is much lower
		Answer[] answers1 = question.getAnswers();
		Answer[] answers2 = question.getAnswers();			// should be not equal to answer1
		assertFalse(Arrays.equals(answers1, answers2));
	}

}
