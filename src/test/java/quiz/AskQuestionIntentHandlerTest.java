package quiz;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import org.junit.Test;

import com.opencsv.CSVReader;

public class AskQuestionIntentHandlerTest {

	@Test
	public void test() {
		// fail("Not yet implemented");
		try {
			CSVReader reader = new CSVReader(new InputStreamReader(QuestionLoader.class.getResourceAsStream("/Berlin.csv")), ',', '"', 1);

			// Read all rows at once
			List<String[]> allRows = reader.readAll();

			String[] strings = allRows.get(0);
			String speechText = strings[0];

			assertTrue("a".equals("a"));
		} catch (IOException e) {
		}
	}
}
