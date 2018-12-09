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

public class QuizEndIntentHandler implements RequestHandler {	
	@Override
    public boolean canHandle(HandlerInput input) {
        return input.matches(intentName("QuizEndIntent"));
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
        Slot endslot = slots.get("EndContinue");
        String value = endslot.getValue();
        
        switch (value) {
		case "ende":
		case "beenden": switch (round.players.length) {
							case 1:	speechText.append(" Ok. Dann sag ich dir noch dein Level. "); break;
							default:speechText.append(" Ok. Dann sag ich euch noch euer Level. "); break;
						}
						for(int i=0; i<round.players.length; i++) {
								if(round.players[i].score == 0) {
									speechText.append("Schade "+ round.players[i].name +", du hast leider keine Frage richtig beantwortet. "
											+ "Versuch es doch gleich noch einmal. "
											+ "Beim nächsten mal klappt's bestimmt besser. "
											+ "Hier dein Level: "
											+ "Du bist ein Tourist");
								}
								else if(round.players[i].score == 1) {
									speechText.append("Schade "+ round.players[i].name +". "
											+ "Versuch es doch gleich noch einmal. "
											+ "Beim nächsten mal klappt's bestimmt besser. "
											+ "Hier dein Level: "
											+ "Du bist ein Tourist");
								}
								else if(round.players[i].score == 2) {
									speechText.append(round.players[i].name +", Du musst noch ein bisschen üben. Hier dein Level: "
											+ "Du bist ein Zugezogener");
								}
								else if(round.players[i].score == 3) {
									speechText.append(round.players[i].name +", Du musst noch ein bisschen üben. Hier dein Level: "
											+ "Du bist ein Zugezogener");
								}
								else if(round.players[i].score == 4) {
									speechText.append("Super "+round.players[i].name +". Das ist schon richtig gut. Hier dein Level: "
											+ "Du bist ein Stadtführer");
								}
								else if(round.players[i].score == 5) {
									speechText.append("Sehr gut"+ round.players[i].name +", du hast alle Fragen richtig beantwortet! Du weißt ja wirklich alles."
											+ " Hier ist dein Level. Du bist ein Einheimischer");
								}
						}
					   break;
		case "weiter": break;
		default:
			break;
		}
 /*       if (playerCountSlot != null
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
        */
        return input.getResponseBuilder()
                .withSpeech(speechText.toString())
                .withReprompt(speechText.toString())
                .build();
    } 
}
