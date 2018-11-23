package quiz;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import com.opencsv.CSVReader;

import quiz.model.Answer;
import quiz.model.Question;

public class QuestionLoader {

	public Question[] load() {
		try {
			return new Question[] {
					new Question(new URL("question.example"), "Was ist der zweite Buchstabe des Alphabets?", new Answer[] {
							new Answer("A", false),
							new Answer("B", true),
							new Answer("C", false)
					})
			};
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return null;
		}
		/*
		try {

//			CSVReader reader = new CSVReader(new InputStreamReader(ClassLoader.getSystemResourceAsStream("/Berlin.csv")), ',', '"', 1);
			CSVReader reader = new CSVReader(new InputStreamReader(QuestionLoader.class.getResourceAsStream("/Berlin.csv")), ',', '"', 1);

			// Read all rows at once
			List<String[]> allRows = reader.readAll();

			String[] tt = allRows.get(1)[0].replaceAll("\"|^\\d", "").split(";");
			String[] ss = Arrays.copyOfRange(tt, 1, tt.length);

			// ss[0] + "A ." + ss[1] + " ? B ." + ss[2] + " ?Oder C ." + ss[3];
			return ;
		} catch (IOException e) {
			return ;
		}
		*/
	}
}
