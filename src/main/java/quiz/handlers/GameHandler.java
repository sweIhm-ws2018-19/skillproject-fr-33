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
import quiz.model.QuizGame.GameState;

import java.util.Map;
import java.util.Optional;

import static com.amazon.ask.request.Predicates.*;

public class GameHandler implements RequestHandler {
	private QuizGame game;
	private StringBuilder speechText;

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

		speechText = new StringBuilder();
		Map<String, Object> sessionAttributes = input.getAttributesManager().getSessionAttributes();
		game = QuizGame.fromSessionAttributes(sessionAttributes);
		
		if (input.matches(intentName("AMAZON.StopIntent")) || (input.matches(intentName("AMAZON.CancelIntent")) && game.state != GameState.PROMPT_CONTINUE_GAME)) {
			return input.getResponseBuilder().withSpeech("Schade, dass du nicht mehr mit mir spielen willst. Bis zum nächsten mal.").build();
		} else if (input.matches(intentName("StartRoundIntent"))) {
			readPlayerCountSlot(slots.get("Anzahl"));
			readRegionSlot(slots.get("Region"));

			// speechText.append(intentRequest.getDialogState());
			// see https://stackoverflow.com/questions/53176017/alexa-dialog-model-step-and-dialogstate-is-never-in-completed
			// doesn't work with custom validation though
			// if (intentRequest.getDialogState() != DialogState.COMPLETED)
			//	return input.getResponseBuilder().addDelegateDirective(null).build();
			
			game.checkComplete(speechText);
		} else if (game.state == GameState.INQUIRE_PLAYER_COUNT) {
			if (input.matches(intentName("SelectPlayerCountIntent"))) {
				if (slots.get("Single") != null && slots.get("Single").getValue() != null) {
					game.selectPlayerCount(1, speechText);
				} else {
					readPlayerCountSlot(slots.get("Anzahl"));
				}
				game.checkComplete(speechText);
			} else {
				intentStateMismatch(intent);
			}
		} else if (game.state == GameState.INQUIRE_REGION) {
			if (input.matches(intentName("SelectRegionIntent"))) {
				readRegionSlot(slots.get("Region"));
				game.checkComplete(speechText);
			} else {
				intentStateMismatch(intent);
			}
		} else if (game.state == GameState.QUIZ_QUESTION) {
			if (input.matches(intentName("SelectAnswerIntent"))) {
				readAnswerSlot(slots.get("AnswerSelection"));
			} else if (input.matches(intentName("AMAZON.RepeatIntent"))) {
				speechText.append("Klar, ich stell dir die Frage noch einmal. ");
			} else {
				speechText.append("Tut mir leid, das habe ich nicht verstanden. Sage einfach A, B oder C, um die Frage zu beantworten. ");
			}
		} else if (game.state == GameState.PROMPT_CONTINUE_GAME) {
			if (input.matches(intentName("AMAZON.NoIntent").or(intentName("AMAZON.CancelIntent")))) {
				game.end(speechText);
				game.intoSessionAttributes(sessionAttributes);
				return input.getResponseBuilder().withSpeech(speechText.toString()).withShouldEndSession(true).build();
			} else if (input.matches(intentName("AMAZON.YesIntent").or(intentName("AMAZON.ResumeIntent")))) {
				game.cont(speechText);
			} else {
				intentStateMismatch(intent);
			}
		} else {
			throw new Error("unknow game state");
		}
		game.intoSessionAttributes(sessionAttributes);
		return game.respond(input.getResponseBuilder(), speechText);
	}

	private void intentStateMismatch(Intent intent) {
		if ("AMAZON.RepeatIntent".equals(intent.getName())) {
			speechText.append("Ich habe nichts das ich im Moment wiederholen kann.");
		} else if ("AMAZON.FallbackIntent".equals(intent.getName())) {
			speechText.append("Tut mir leid, das kenne ich nicht. Sage einfach Hilfe. ");
		} else {
			speechText.append("Tut mir leid, "+intent.getName()+" kann ich nicht. ");
		}
	}

	private void readRegionSlot(Slot regionSlot) {
		String regionId = extractSlotId(regionSlot, "KnownRegion");
		if (regionId != null) {
			game.selectRegion(regionId, speechText);
		}
	}

	private void readPlayerCountSlot(Slot playerCountSlot) {
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
		}
	}

	private void readAnswerSlot(Slot answerSlot) {
		String answerId = extractSlotId(answerSlot, "Selection");
		if (answerId != null) {
			int answerIndex = Integer.parseInt(answerId);
			game.selectAnswer(answerIndex, speechText);
		} else {
			speechText.append("Sorry. Kannst du das noch mal anders formulieren? ");
		}
	}

	private static String extractSlotId(Slot slot, String type) {
		// TODO: return Optional<String> and use ifPresentOrElse as soon as Java 9 is available...
		return Optional.ofNullable(slot)
		.flatMap(s -> Optional.ofNullable(s.getResolutions()))
		.map(ress -> ress.getResolutionsPerAuthority())
		.filter(aress -> !aress.isEmpty())
		.map(aress -> aress.get(0))
		// .filter(res -> res.getAuthority().equals("amzn1.er-authority.echo-sdk.<skill_id>."+type))
		.filter(res -> res.getStatus().getCode() == StatusCode.ER_SUCCESS_MATCH)
		.map(res -> res.getValues())
		.filter(vals -> !vals.isEmpty())
		.map(vals -> vals.get(0).getValue().getId())
		.orElse(null);
	}
}