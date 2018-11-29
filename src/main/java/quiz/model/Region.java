package quiz.model;

public class Region {
	public String id;
	public Question[] questions;
	
	public Region(String id, Question[] qs) {
		this.id = id;
		this.questions = qs;
	}
}
