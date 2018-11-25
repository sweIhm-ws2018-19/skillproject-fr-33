package quiz.model;

import java.io.InputStreamReader;
import java.util.List;
import quiz.model.Question;

import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;

/** The QuestionLoader loads question that are needed in a quiz round.
 *  @author fabian sinning
 * */
public class QuestionLoader {
	private String region;
	
	/** Sets a new resource identifier for that QuestionLoader.
	 *  @author fabian sinning
	 *  @param region A resource identifier for the CSV reader
	 *  @return Reference of that QuestionLoader
	 * */
	public QuestionLoader chooseRegion(String region) {
		this.region = region;
		return this;
	}
	
	/** Loads an Array of Question.
	 *  @author fabian sinning
	 *  @return Question Array with all questions
	 * */
	public Question[] load() {
		CsvToBean<QuestionCSV> csvToBean = new CsvToBeanBuilder<QuestionCSV>(new InputStreamReader(QuestionLoader.class.getResourceAsStream(region)))
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
