package quiz;

import java.io.IOException;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import javax.json.Json;

import org.junit.Test;

public class SkillTest {
	@Test
	public void run() throws IOException {
		InputStream in = getClass().getResourceAsStream("/e2e/input.json");
		PipedOutputStream out = new PipedOutputStream();
		InputStream result = new PipedInputStream(out);
		new QuizStreamHandler().handleRequest(in, out, null);
		System.out.print(Json.createReader(result).readObject());
	}
}
