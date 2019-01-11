package quiz.model;

import java.io.Serializable;

import org.simmetrics.builders.StringMetricBuilder;
import org.simmetrics.StringMetric;
import org.simmetrics.metrics.JaroWinkler;
import org.simmetrics.simplifiers.Simplifiers;
import org.simmetrics.tokenizers.Tokenizers;

public class Answer implements Serializable {
	public String text;
	public boolean isCorrect;
	
	public Answer() {}
	public Answer(String t, boolean c) {
		this.text = t;
		this.isCorrect = c;
	}
	
	private final static StringMetric metric =
		StringMetricBuilder.with(new JaroWinkler())
		.simplify(Simplifiers.removeDiacritics())
		.simplify(Simplifiers.removeNonWord())
		.simplify(Simplifiers.toLowerCase())
		// .tokenize(Tokenizers.whitespace())
		// .tokenize(Tokenizers.qGram(3))
		.build();
	
	public float similarity(String attempt) {
		String lastWord = this.text.replaceFirst(".+\\s", "");
		float whole = metric.compare(this.text, attempt);
		if (lastWord.equals(this.text)) {
			return whole;
		} else {
			return (metric.compare(lastWord, attempt) + whole) / 2;
		}
	}
}
