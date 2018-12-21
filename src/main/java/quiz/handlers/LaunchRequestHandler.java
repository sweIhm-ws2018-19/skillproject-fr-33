package quiz.handlers;

import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.amazon.ask.dispatcher.request.handler.RequestHandler;
import com.amazon.ask.model.LaunchRequest;
import com.amazon.ask.model.Response;

import quiz.model.QuizGame;
import quiz.model.QuizGame.GameState;

import java.util.Optional;

import static com.amazon.ask.request.Predicates.requestType;

/*
 * Your service receives a LaunchRequest when the user invokes the skill with the invocation name,
 * but does not provide any command mapping to an intent
 */
public class LaunchRequestHandler implements RequestHandler {
	@Override
	public boolean canHandle(HandlerInput input) {
		return input.matches(requestType(LaunchRequest.class));
	}

	@Override
	public Optional<Response> handle(HandlerInput input) {
		StringBuilder speechText = new StringBuilder().append("Hallo. Willkommen zu OnToury 0.1.4. ");
		QuizGame game = new QuizGame();
		game.intoSessionAttributes(input.getAttributesManager().getSessionAttributes());
		return game.respond(input.getResponseBuilder(), speechText);
	}
}
