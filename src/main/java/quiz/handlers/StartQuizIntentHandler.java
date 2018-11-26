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

        // Get the color slot from the list of slots.
        Slot playerCountSlot = slots.get("Anzahl");
        if (playerCountSlot != null && playerCountSlot.getValue() != null) {
        	int playerCount = Integer.parseInt(playerCountSlot.getValue());
        	round.createPlayers(playerCount);
        	speechText.append("Wir spielen mit "+playerCount+" Spielern. "); 
        } else { // playerCountSlot == null
        	speechText.append("Mit wie vielen Spielern möchtest du spielen? ");
        }
        
        if (round.isComplete()) round.askQuestion(speechText);
        
        return input.getResponseBuilder()
                .withSpeech(speechText.toString())
                .withReprompt(speechText.toString())
                .build();
    }
}
