package quiz.handlers;

import static com.amazon.ask.request.Predicates.intentName;

import java.util.Map;
import java.util.Optional;

import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.amazon.ask.dispatcher.request.handler.RequestHandler;
import com.amazon.ask.model.Intent;
import com.amazon.ask.model.IntentRequest;
import com.amazon.ask.model.Response;
import com.amazon.ask.model.Slot;
import com.amazon.ask.model.slu.entityresolution.StatusCode;

import quiz.model.QuizRound;

public class SelectAnswerIntentHandler implements RequestHandler {
	@Override
	public boolean canHandle(HandlerInput input) {
		// TODO: check whether we are in a state where a question can be answered
		return input.matches(intentName("SelectAnswerIntent"));
	}

	@Override
	public Optional<Response> handle(HandlerInput input) {
		IntentRequest intentRequest = (IntentRequest) input.getRequestEnvelope().getRequest();
		Intent intent = intentRequest.getIntent();
		Map<String, Slot> slots = intent.getSlots();

		StringBuilder speechText = new StringBuilder();
		Map<String, Object> sessionAttributes = input.getAttributesManager().getSessionAttributes();
		QuizRound round = QuizRound.fromSessionAttributes(sessionAttributes);

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
			round.selectAnswer(answerIndex, speechText);
		} else {
			speechText.append("Diese Antwort habe ich leider nicht verstanden. ");
		}

		round.intoSessionAttributes(sessionAttributes);

		return input.getResponseBuilder().withSpeech(speechText.toString()).withReprompt(speechText.toString()).build();
	}
}
