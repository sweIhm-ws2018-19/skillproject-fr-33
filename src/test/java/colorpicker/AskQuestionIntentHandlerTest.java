package colorpicker;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import org.junit.Test;

import com.opencsv.CSVReader;

import junit.framework.Assert;
import see.hm.edu.handlers.AskQuestionIntentHandler;

public class AskQuestionIntentHandlerTest {

	@Test
	public void test() {
		//fail("Not yet implemented");
		try {
		 CSVReader reader = new CSVReader(new InputStreamReader(AskQuestionIntentHandler.class.getResourceAsStream("/Bsp.csv")), ',', '"', 1);
	       
	      //Read all rows at once
	      List<String[]> allRows = reader.readAll();
	      
	      String[] strings = allRows.get(0);
	      String speechText = strings[1];
	      assertTrue("Beispiel".equals(speechText));}
	      catch(IOException e) {}
		}
	}

