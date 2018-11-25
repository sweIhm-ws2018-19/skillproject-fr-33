package quiz.handlers;

import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.amazon.ask.dispatcher.request.handler.RequestHandler;
import com.amazon.ask.model.LaunchRequest;
import com.amazon.ask.model.Response;

import quiz.model.QuestionLoader;
import quiz.model.QuizRound;
import quiz.model.Region;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
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
        String speechText = "Hallo. Willkommen zum Reisequiz. Wieviele Spieler sollen mitspielen?";
        String repromptText = "Wieviele Spieler sollen mitspielen?";
        
        Map<String, Object> sessionmap = new HashMap<>();
        try {
			sessionmap.put("round", new QuizRound(new Region(new URL("/Berlin.csv"), new QuestionLoader().load()), null));
		} catch (MalformedURLException e) { e.printStackTrace();}
        
        input.getAttributesManager().setSessionAttributes(sessionmap);
        
        return input.getResponseBuilder()
                .withSpeech(speechText)
                .withReprompt(repromptText)
                .build();
    }
}
