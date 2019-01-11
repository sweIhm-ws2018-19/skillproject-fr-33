package quiz;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import static org.junit.Assert.assertNotEquals;

import quiz.model.QuizRound.Norepeat;

@RunWith(Parameterized.class)
public class NoRepeatTest {
	
	public Norepeat noRepeat;
	
	public NoRepeatTest(String[] arr) {
		noRepeat = new Norepeat(arr);
	}

	@Parameters
	public static Collection<String[][]> data(){
		return Arrays.asList(new String[][][] {
			{{"a", "b", "c"}},
			{{"a", "b"}}
		});
	}
	
	@Test
	public void nextUnlikePrevious() {
		String prev = noRepeat.next();
		for (int i=0; i<1000; i++) {
			String cur = noRepeat.next();
			assertNotEquals(cur, prev);
			prev = cur;
		}
	}
	@Test
	public void valueFromInput() {
		assert(Arrays.asList(noRepeat.utterances).contains(noRepeat.next()));
	}
	
}
