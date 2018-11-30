package quiz;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

import quiz.model.Question;
import quiz.model.Region;

@RunWith(Parameterized.class)
public class QuestionLoaderTest {
	
	public Question[] arr;
	
	@Before
	public void init() {
		Region region = new Region("Tests", null);
		new QuestionLoader(region).load();
		arr = region.questions;
	}

	@Parameters
	public static Collection<String[]> data(){
		return Arrays.asList(new String[][] {
			{"0","Wer war Miss Marple ?","Eine Detektivin","Eine Krankenschwester","Eine Lehrerin"},
			{"1","Welches Tier ist ein Fleischfesser?","Katze","Pferd","Meerschweinchen"},
			{"2","Was ist 3 * 3 ?","9","8","10"},
			{"3","Wie viele Klassen gibt es in der Grundschule ?","4","5","9"}
		});
	}
	
	@Parameter(0)
	public String index;
	
	@Parameter(1)
	public String question;
	
	@Parameter(2)
	public String answer;
	
	@Parameter(3)
	public String alt1;
	
	@Parameter(4)
	public String alt2;
	
	@Test
	public void test() {
		assertEquals(question,arr[Integer.parseInt(index)].text);
		assertEquals(answer,arr[Integer.parseInt(index)].answers.get(0).text);
		assertEquals(alt1,arr[Integer.parseInt(index)].answers.get(1).text);
		assertEquals(alt2,arr[Integer.parseInt(index)].answers.get(2).text);
	}

}
