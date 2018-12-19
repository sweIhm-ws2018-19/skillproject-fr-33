package quiz.handlers;

import static com.amazon.ask.request.Predicates.intentName;

import java.util.Map;
import java.util.Optional;
import java.util.Random;

import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.amazon.ask.dispatcher.request.handler.RequestHandler;
import com.amazon.ask.model.Response;

import quiz.model.QuizGame;

public class EndRoundIntentHandler implements RequestHandler {
	@Override
	public boolean canHandle(HandlerInput input) {
		if (input.matches(intentName("AMAZON.YesIntent").or(intentName("AMAZON.NoIntent")))) {
			Map<String, Object> sessionAttributes = input.getAttributesManager().getSessionAttributes();
			QuizGame game = QuizGame.fromSessionAttributes(sessionAttributes);
			
			return game.round != null && game.round.askedQuestions.length == 0;
		}
		return false;
	}

	@Override
	public Optional<Response> handle(HandlerInput input) {
		StringBuilder speechText = new StringBuilder();
		Map<String, Object> sessionAttributes = input.getAttributesManager().getSessionAttributes();
		QuizGame game = QuizGame.fromSessionAttributes(sessionAttributes);

		if (input.matches(intentName("AMAZON.NoIntent"))) {
			game.end(speechText);
			return input.getResponseBuilder().withSpeech(speechText.toString()).withShouldEndSession(true).build();
		} else {
			String[] yeah = new String[] {"Großartig! ", "Spitze! ", "Toll! "};
			int num = new Random().nextInt(yeah.length);
			speechText.append(yeah[num]);
			// TODO: Möchtest du weitere Fragen zu [REGION] spielen oder dir eine neue Region aussuchen?
			game.round.askNewQuestion(speechText);
			game.intoSessionAttributes(sessionAttributes);
			return input.getResponseBuilder().withSpeech(speechText.toString()).withReprompt(speechText.toString()).build();
		}
	}
}
