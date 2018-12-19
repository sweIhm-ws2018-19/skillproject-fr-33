package quiz.handlers;

import static com.amazon.ask.request.Predicates.intentName;

import java.util.Map;
import java.util.Optional;

import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.amazon.ask.dispatcher.request.handler.RequestHandler;
import com.amazon.ask.model.Response;

import quiz.model.QuizGame;

public class RepeatIntentHandler implements RequestHandler {
	@Override
	public boolean canHandle(HandlerInput input) {
		// TODO: check whether we are in a state where a question can be answered
		return input.matches(intentName("AMAZON.RepeatIntent"));
	}

	@Override
	public Optional<Response> handle(HandlerInput input) {
		StringBuilder speechText = new StringBuilder();
		Map<String, Object> sessionAttributes = input.getAttributesManager().getSessionAttributes();
		QuizGame game = QuizGame.fromSessionAttributes(sessionAttributes);

		if (game.round != null && game.round.askedQuestions.length == 0) {
			speechText.append("Ich habe nichts das ich im Moment wiederholen kann.");
		} else {
			speechText.append("Klar, ich stell dir die Frage noch einmal. ");
			game.round.askedQuestions[game.round.askedQuestions.length - 1].ask(speechText);
		}

		return input.getResponseBuilder().withSpeech(speechText.toString()).withReprompt(speechText.toString()).build();
	}
}
