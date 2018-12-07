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

public class StartQuizIntentHandler implements RequestHandler {	
	@Override
    public boolean canHandle(HandlerInput input) {
        return input.matches(intentName("StartQuizIntent"));
    }
	
	@Override
    public Optional<Response> handle(HandlerInput input) {
        IntentRequest intentRequest = (IntentRequest) input.getRequestEnvelope().getRequest();
        Intent intent = intentRequest.getIntent();
        Map<String, Slot> slots = intent.getSlots();
        
        Map<String, Object> sessionAttributes = input.getAttributesManager().getSessionAttributes();
        QuizRound round = QuizRound.fromSessionAttributes(sessionAttributes);
        StringBuilder speechText = new StringBuilder();

        // Get the player count slot from the list of slots.
        Slot playerCountSlot = slots.get("Anzahl");
        if (playerCountSlot != null
        	&& playerCountSlot.getValue() != null // WTF
        	&& !playerCountSlot.getValue().equals("?") // WTFFFFF????
        ) {
        	try {
        		int playerCount = Integer.parseInt(playerCountSlot.getValue());
            	round.createPlayers(playerCount, speechText);
        	} catch(NumberFormatException e) {
        		speechText.append("Das ist leider keine gültige Spielerzahl. ");
        	}
        } else if (round.players == null) { // playerCountSlot == null
        	speechText.append("Mit wie vielen Spielern möchtest du spielen? ");
        }

        Slot regionSlot = slots.get("Region");
        if (regionSlot != null
        	&& regionSlot.getResolutions() != null
        	&& regionSlot.getResolutions().getResolutionsPerAuthority().size() > 0
        	// answerSlot.getResolutions().getResolutionsPerAuthority().get(0).getAuthority().equals("amzn1.er-authority.echo-sdk.<skill_id>.KnownRegion")
        	&& regionSlot.getResolutions().getResolutionsPerAuthority().get(0).getStatus().getCode() == StatusCode.ER_SUCCESS_MATCH
        	&& regionSlot.getResolutions().getResolutionsPerAuthority().get(0).getValues().size() > 0) {
        	String region = regionSlot.getResolutions().getResolutionsPerAuthority().get(0).getValues().get(0).getValue().getId();
        	
        	round.selectRegion(region, speechText);
        } else if (round.region == null) {
        	speechText.append("Über welche Region möchtest du spielen? ");
        }
        
        if (round.isComplete()) round.askQuestion(speechText); // TODO: only if completed with the current utterance
    	round.intoSessionAttributes(sessionAttributes);
        
        return input.getResponseBuilder()
                .withSpeech(speechText.toString())
                .withReprompt(speechText.toString())
                .build();
    }
}
