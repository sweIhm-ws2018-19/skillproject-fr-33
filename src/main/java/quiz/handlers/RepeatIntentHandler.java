package quiz.handlers;

import static com.amazon.ask.request.Predicates.intentName;

import java.util.Map;
import java.util.Optional;

import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.amazon.ask.dispatcher.request.handler.RequestHandler;
import com.amazon.ask.model.Intent;
import com.amazon.ask.model.IntentRequest;
import com.amazon.ask.model.Response;

import quiz.model.QuizRound;

public class RepeatIntentHandler implements RequestHandler {
	@Override
	public boolean canHandle(HandlerInput input) {
		// TODO: check whether we are in a state where a question can be answered
		return input.matches(intentName("AMAZON.RepeatIntent"));
	}

	@Override
	public Optional<Response> handle(HandlerInput input) {
		IntentRequest intentRequest = (IntentRequest) input.getRequestEnvelope().getRequest();
		Intent intent = intentRequest.getIntent();

		StringBuilder speechText = new StringBuilder();
		Map<String, Object> sessionAttributes = input.getAttributesManager().getSessionAttributes();
		QuizRound round = QuizRound.fromSessionAttributes(sessionAttributes);

		if (round.askedQuestions.length == 0) {
			speechText.append("Ich habe nichts das ich im Moment wiederholen kann.");
		} else {
			round.askedQuestions[round.askedQuestions.length - 1].ask(speechText);
		}
		// round.intoSessionAttributes(sessionAttributes);

		return input.getResponseBuilder().withSpeech(speechText.toString()).withReprompt(speechText.toString()).build();
	}
}
