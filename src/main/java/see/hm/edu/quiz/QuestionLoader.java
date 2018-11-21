package see.hm.edu.quiz;
import java.io.FileNotFoundException;
import java.io.FileReader;

import com.opencsv.CSVReader;

public class QuestionLoader {
	
	public QuestionLoader() {
		try {
			CSVReader reader = new CSVReader(new FileReader("emps.csv"), ';');
		}
		catch(FileNotFoundException e) {}
	}
}
