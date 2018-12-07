package quiz.handlers;

import static com.amazon.ask.request.Predicates.intentName;

import java.util.Map;
import java.util.Optional;
import java.util.Random;

import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.amazon.ask.dispatcher.request.handler.RequestHandler;
import com.amazon.ask.model.Intent;
import com.amazon.ask.model.IntentRequest;
import com.amazon.ask.model.Response;

import quiz.model.QuizRound;

public class EndRoundIntentHandler implements RequestHandler {
	@Override
	public boolean canHandle(HandlerInput input) {
		// TODO: check whether we are in a state where a question can be answered
		return input.matches(intentName("AMAZON.YesIntent").or(intentName("AMAZON.NoIntent")));
	}

	@Override
	public Optional<Response> handle(HandlerInput input) {
		IntentRequest intentRequest = (IntentRequest) input.getRequestEnvelope().getRequest();
		Intent intent = intentRequest.getIntent();

		StringBuilder speechText = new StringBuilder();
		Map<String, Object> sessionAttributes = input.getAttributesManager().getSessionAttributes();
		QuizRound round = QuizRound.fromSessionAttributes(sessionAttributes);

		/*Die Level beziehen sich jeweils auf eine Runde mit 5 erreichbaren Punkten:
		0 Punkte und 1 Punkt -> Tourist
		2&3 Punkte -> Zugezogener
		4 Punkte -> Stadtführer
		5 Punkte -> Einheimischer
		
		0: Schade, du hast leider keine Frage richtig beantwortet. Versuch es doch gleich noch einmal. Beim nächsten mal klappt's bestimmt besser. Hier dein Level: du bist ein Tourist.
		1: Schade, du hast leider nur einen Punkt erreicht. Versuch es doch gleich noch einmal. Beim nächsten mal klappt's bestimmt besser. Hier dein Level: du bist ein Tourist.
		2&3: Du hast [2;3] Punkte erreicht und musst noch ein bisschen üben. Hier dein Level: du bist ein Zugezogener.
		4: Super, du hast 4 Punkte erreicht. Das ist schon richtig gut. Hier dein Level: du bist ein Stadtführer.
		5: Sehr gut, du hast alle Fragen richtig beantwortet! Du weißt ja wirklich alles. Hier dein Level: du bist ein Einheimischer.
		*/
		
		if (round.isComplete() && round.askedQuestions.length == 0) {
			if(input.matches(intentName("AMAZON.NoIntent"))) {
				return input.getResponseBuilder()
		                .withSpeech("Schade, dass du nicht mehr mit mir spielen willst. Bis zum nächsten mal.")
		              //Du hast insgesamt x Punkte erreicht und bist ein Profi. (add)
		                .withShouldEndSession(true)
		                .build();
			} else {
				String[] yeah = new String[] {"Großartig! ", "Spitze! ", "Toll! "};
				int num = new Random().nextInt(yeah.length);
				speechText.append(yeah[num]);
				round.askNewQuestion(speechText); // TODO: only if completed with the current utterance
				round.intoSessionAttributes(sessionAttributes);
				return input.getResponseBuilder().withSpeech(speechText.toString()).withReprompt(speechText.toString()).build();
			}
		} else {
			speechText.append("Das habe ich leider nicht Verstanden. ");
			return input.getResponseBuilder().withSpeech(speechText.toString()).withReprompt(speechText.toString()).build();
		}
	}
}
