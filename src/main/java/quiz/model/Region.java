package quiz.model;

import java.net.URL;

public class Region {
	public URL id;
	public Question[] questions;
	
	public Region(URL id, Question[] qs) {
		this.id = id;
		this.questions = qs;
	}
}
