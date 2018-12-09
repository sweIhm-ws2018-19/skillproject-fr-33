package quiz;

import java.io.InputStreamReader;
import java.util.List;

import quiz.model.Answer;
import quiz.model.Question;
import quiz.model.Region;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;



public class QuestionLoader {


	public static class QuestionCSV {
			@CsvBindByName(column = "")
			public int index;
			
		 	@CsvBindByName(column = "Frage")
		    private String frage;

		    @CsvBindByName(column = "Richtige Antwort")
		    private String antwort;

		    @CsvBindByName(column = "Alternative 1")
		    private String alta;

		    @CsvBindByName(column = "Alternative 2")
		    private String altb;
		    
		    @CsvBindByName(column = "Info richtige Antwort")
		    private String info;
		    
		    public String getFrage() { return frage; }
		    public String getAntwort() { return antwort; }
		    public String getAlta() { return alta; }
		    public String getAltb() { return altb; }
		    public String getInfo() { return info; }
	}
	
	private Region region;
	

	public QuestionLoader(Region r) {
		this.region = r;
	}
	

	public void load() {
		InputStreamReader input = new InputStreamReader(QuestionLoader.class.getResourceAsStream("/"+region.id+".csv"));
		CsvToBean<QuestionCSV> csvToBean = new CsvToBeanBuilder<QuestionCSV>(input)
				.withSeparator(';')
				.withType(QuestionCSV.class)
				.withIgnoreLeadingWhiteSpace(true)
				.build();
		List<QuestionCSV> questions = csvToBean.parse();
		
		
		region.questions = questions.stream().map(q -> new Question(region.id+"/"+q.index, q.getFrage(),
				new Answer[] {
						new Answer(q.getAntwort(),true),
						new Answer(q.getAlta(),false),
						new Answer(q.getAltb(),false)
				}, q.getInfo()
		)).toArray(Question[]::new);
	}
}
