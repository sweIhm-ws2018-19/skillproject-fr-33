package quiz.model;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import quiz.model.Question;

import com.opencsv.CSVReader;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;

public class QuestionLoader {
	private String region;
	
	public QuestionLoader chooseRegion(String region) {
		this.region = region;
		return this;
	}

	public Question[] load() {
		CsvToBean<QuestionCSV> csvToBean = new CsvToBeanBuilder(new InputStreamReader(QuestionLoader.class.getResourceAsStream(region)))
				.withSeparator(';')
				.withType(QuestionCSV.class)
				.withIgnoreLeadingWhiteSpace(true)
				.build();
		List<QuestionCSV> questions = csvToBean.parse();
		
		
		return new Question[] {
				new Question(region, questions.get(1).getFrage(),
						new Answer[] {
								new Answer(questions.get(1).getAntwort(),true),
								new Answer(questions.get(1).getAlta(),false),
								new Answer(questions.get(1).getAltb(),false)
								}
				)
		};
	}
}
