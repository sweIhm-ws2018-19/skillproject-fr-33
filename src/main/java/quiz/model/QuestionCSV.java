package quiz.model;

import com.opencsv.bean.CsvBindByName;

public class QuestionCSV {
	
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
