package quiz.handlers;

import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.amazon.ask.dispatcher.request.handler.RequestHandler;
import com.amazon.ask.model.DialogState;
import com.amazon.ask.model.Intent;
import com.amazon.ask.model.IntentRequest;
import com.amazon.ask.model.Response;
import com.amazon.ask.model.Slot;
import com.amazon.ask.model.slu.entityresolution.StatusCode;

import quiz.model.QuizGame;

import java.util.Map;
import java.util.Optional;
import java.util.Random;

import static com.amazon.ask.request.Predicates.*;

public class GameHandler implements RequestHandler {
	@Override
	public boolean canHandle(HandlerInput input) {
		// we should have handled all other requests already, but one never knows
		return input.matches(requestType(IntentRequest.class)); // input.getRequest() instanceof IntentRequest
		// return true; I can do anything!
	}

	@Override
	public Optional<Response> handle(HandlerInput input) {
		IntentRequest intentRequest = (IntentRequest) input.getRequestEnvelope().getRequest();
		Intent intent = intentRequest.getIntent();
		Map<String, Slot> slots = intent.getSlots();

		StringBuilder speechText = new StringBuilder();
		Map<String, Object> sessionAttributes = input.getAttributesManager().getSessionAttributes();
		QuizGame game = QuizGame.fromSessionAttributes(sessionAttributes);
		
		if (input.matches(intentName("StartRoundIntent"))) {
			// Get the player count slot from the list of slots.
			Slot playerCountSlot = slots.get("Anzahl");
			if (playerCountSlot != null
				&& playerCountSlot.getValue() != null // WTF
				&& !playerCountSlot.getValue().equals("?") // WTFFFFF????
			) {
				try {
					int playerCount = Integer.parseInt(playerCountSlot.getValue());
					game.selectPlayerCount(playerCount, speechText);
				} catch (NumberFormatException e) {
					speechText.append("Das ist leider keine gültige Spielerzahl. ");
				}
			} else if (game.playerCount == 0) { // playerCountSlot == null
				speechText.append("Wieviele Spieler wollen mitspielen? ");
				return input.getResponseBuilder().withSpeech(speechText.toString()).addElicitSlotDirective("Anzahl", null).build();
			}

			Slot regionSlot = slots.get("Region");
			if (regionSlot != null
				&& regionSlot.getResolutions() != null
				&&!regionSlot.getResolutions().getResolutionsPerAuthority().isEmpty()
				// answerSlot.getResolutions().getResolutionsPerAuthority().get(0).getAuthority().equals("amzn1.er-authority.echo-sdk.<skill_id>.KnownRegion")
				&& regionSlot.getResolutions().getResolutionsPerAuthority().get(0).getStatus().getCode() == StatusCode.ER_SUCCESS_MATCH
				&&!regionSlot.getResolutions().getResolutionsPerAuthority().get(0).getValues().isEmpty()
			) {
				String region = regionSlot.getResolutions().getResolutionsPerAuthority().get(0).getValues().get(0).getValue().getId();
				game.selectRegion(region, speechText);
			} else if (game.playerCount != 0 && game.regionId == null) {
				speechText.append("Über welche Region möchtest du spielen? ");
				return input.getResponseBuilder().withSpeech(speechText.toString()).addElicitSlotDirective("Region", null).build();
			}

			speechText.append(intentRequest.getDialogState());
			// see https://stackoverflow.com/questions/53176017/alexa-dialog-model-step-and-dialogstate-is-never-in-completed
			// doesn't work with custom validation though
			// if (intentRequest.getDialogState() != DialogState.COMPLETED)
			//	return input.getResponseBuilder().addDelegateDirective(null).build();
			
			game.checkComplete(speechText);
		} else if (input.matches(intentName("SelectAnswerIntent"))) { // TODO: check whether we are in a state where a question can be answered
			Slot answerSlot = slots.get("Answer");
			// Oh well:
			if (answerSlot != null
				&& answerSlot.getResolutions() != null
				&& answerSlot.getResolutions().getResolutionsPerAuthority().size() > 0
				// answerSlot.getResolutions().getResolutionsPerAuthority().get(0).getAuthority().equals("amzn1.er-authority.echo-sdk.<skill_id>.Selection")
				&& answerSlot.getResolutions().getResolutionsPerAuthority().get(0).getStatus().getCode() == StatusCode.ER_SUCCESS_MATCH
				&& answerSlot.getResolutions().getResolutionsPerAuthority().get(0).getValues().size() > 0
			) {
				// answerSlot.getResolutions().getResolutionsPerAuthority().stream().filter(res -> res.getValues().contains(answerSlot.getValue())) ???
				int answerIndex = Integer.parseInt(answerSlot.getResolutions().getResolutionsPerAuthority().get(0).getValues().get(0).getValue().getId());
				game.selectAnswer(answerIndex, speechText);
			} else {
				speechText.append("Sorry. Kannst du das noch mal anders formulieren? ");
			}
		} else if (input.matches(intentName("AMAZON.RepeatIntent"))) {
			if (game.round != null && game.round.askedQuestions.length == 0) {
				speechText.append("Ich habe nichts das ich im Moment wiederholen kann.");
			} else {
				speechText.append("Klar, ich stell dir die Frage noch einmal. ");
				game.round.askedQuestions[game.round.askedQuestions.length - 1].ask(speechText);
			}
		} else if (input.matches(intentName("AMAZON.YesIntent").or(intentName("AMAZON.NoIntent"))) && game.round != null && game.round.askedQuestions.length == 0) {
			if (input.matches(intentName("AMAZON.NoIntent"))) {
				game.end(speechText);
				return input.getResponseBuilder().withSpeech(speechText.toString()).withShouldEndSession(true).build();
			} else {
				String[] yeah = new String[] {"Großartig! ", "Spitze! ", "Toll! "};
				int num = new Random().nextInt(yeah.length);
				speechText.append(yeah[num]);
				// TODO: Möchtest du weitere Fragen zu [REGION] spielen oder dir eine neue Region aussuchen?
				game.round.askNewQuestion(speechText);
			}
		} else if (input.matches(intentName("AMAZON.StopIntent").or(intentName("AMAZON.CancelIntent")))) {
			return input.getResponseBuilder().withSpeech("Schade, dass du nicht mehr mit mir spielen willst. Bis zum nächsten mal.").build();
		} else if (input.matches(intentName("AMAZON.FallbackIntent"))) { // true ???
			speechText.append("Fallback: "+intentRequest);
		} else {
			speechText.append("Tut mir leid, das weiss ich nicht. Sage einfach Hilfe.");
		}
		game.intoSessionAttributes(sessionAttributes);
		return input.getResponseBuilder().withSpeech(speechText.toString()).withReprompt(speechText.toString()).build();
	}
}