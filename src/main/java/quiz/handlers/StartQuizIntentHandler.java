package quiz.handlers;

import static com.amazon.ask.request.Predicates.intentName;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.Optional;

import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.amazon.ask.dispatcher.request.handler.RequestHandler;
import com.amazon.ask.model.Intent;
import com.amazon.ask.model.IntentRequest;
import com.amazon.ask.model.Request;
import com.amazon.ask.model.Response;
import com.amazon.ask.model.Slot;

import quiz.QuestionLoader;
import quiz.model.Player;
import quiz.model.QuizRound;
import quiz.model.Region;

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
        	int playerCount = Integer.parseInt(playerCountSlot.getValue());
        	round.createPlayers(playerCount);
        	speechText.append("Wir spielen mit "+playerCount+" Spielern. "); 
        } else if (round.players == null) { // playerCountSlot == null
        	speechText.append("Mit wie vielen Spielern möchtest du spielen? ");
        }
        
        if (round.isComplete()) round.askQuestion(speechText);
    	round.intoSessionAttributes(sessionAttributes);
        
        return input.getResponseBuilder()
                .withSpeech(speechText.toString())
                .withReprompt(speechText.toString())
                .build();
    }
}
