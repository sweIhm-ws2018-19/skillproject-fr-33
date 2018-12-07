package quiz.model;

import java.io.Serializable;

public class Region implements Serializable {
	public String id;
	public Question[] questions;
	
	public Region() {}
	public Region(String id, Question[] qs) {
		this.id = id;
		this.questions = qs;
	}
}
