package quiz.model;

public class Region {
	String id;
	Question[] questions;
	
	public Region(String id, Question[] qs) {
		this.id = id;
		this.questions = qs;
	}
}
