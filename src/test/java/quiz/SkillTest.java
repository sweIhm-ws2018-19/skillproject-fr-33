package quiz;

import java.io.*;
import java.net.URISyntaxException;
// import java.nio.file.*;

import javax.json.Json;

import org.junit.Test;

public class SkillTest {
	@Test
	public void run() throws IOException, URISyntaxException {
		// PathMatcher matcher = FileSystems.getDefault().getPathMatcher("glob:**/e2e/*.in.json");
		// matcher.matches(Paths.get(f.toURI())
		final int pipeBufferSize = 4096; // increase if the test is blocking on writing to `out`
		for (File f: new File(getClass().getResource("/e2e").toURI()).listFiles(f -> f.getName().endsWith(".in.json"))) {
			System.out.println(f.getName());

			// InputStream in = new FileInputStream(f);
			InputStream in = getClass().getResourceAsStream("/e2e/"+f.getName());
			PipedOutputStream out = new PipedOutputStream();
			InputStream result = new PipedInputStream(out, pipeBufferSize);
			new QuizStreamHandler().handleRequest(in, out, null);
			System.out.println(Json.createReader(result).readObject());
		}
	}
}
