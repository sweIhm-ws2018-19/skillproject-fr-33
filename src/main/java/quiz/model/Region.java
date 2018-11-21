package main.java.quiz.model;

import java.net.URL;

public class Region {
	URL id;
	Question[] questions;
	
	public Region(URL id, Question[] qs) {
		this.id = id;
		this.questions = qs;
	}
}
