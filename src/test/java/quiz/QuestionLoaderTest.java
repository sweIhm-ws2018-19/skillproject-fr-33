package quiz;

import static org.junit.Assert.*;

import java.net.MalformedURLException;
import java.net.URL;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import quiz.model.Answer;
import quiz.model.Question;

public class QuestionLoaderTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void test() {
		Question[] questions = new QuestionLoader().chooseRegion("/Berlin.csv").load();
		assertTrue(questions.length == 1);
		assertEquals("Wie viele Einwohner hat Berlin etwa?", questions[0].getQuestion());
		for(Answer answer : questions[0].getAnswers()){
			if(answer.text.equals("3,7 Mio.")) {
				assertTrue(answer.isCorrect);
			}
			else if(answer.text.equals("4,3 Mio.")) {
				assertFalse(answer.isCorrect);
			}
			else {
				assertEquals("2,4 Mio.",answer.text);
				assertFalse(answer.isCorrect);
			}
		}
	}

}
