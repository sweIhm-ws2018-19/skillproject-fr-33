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

import quiz.model.Player;
import quiz.model.QuestionLoader;
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
        QuizRound round = (QuizRound) sessionAttributes.get("round");
        if (round == null) {
        	try {
				Region region = new Region(new URL("/Berlin.csv"), new QuestionLoader().chooseRegion("/Berlin.csv").load());
	        	round = new QuizRound(region, null);
	        	sessionAttributes.put("round", round);
			} catch (MalformedURLException e) {
				e.printStackTrace(); // where would this go ??? >> Ins Nirgendwo :-)
			}
        }
        String speechText = "";

        // Get the color slot from the list of slots.
        Slot playerCountSlot = slots.get("Anzahl");
        if (playerCountSlot != null) {
        	int playerCount = Integer.parseInt(playerCountSlot.getValue());
        	// TODO: Range validation
        	round.players = new Player[playerCount];
        	for (int i=0; i<playerCount; i++)
        		round.players[i] = new Player(0);
        	speechText += "Wir spielen mit "+playerCount+" Spielern. "; 
        } else { // playerCountSlot == null
        	speechText += "Mit wie vielen Spielern möchtest du spielen? ";
        }
        // String speechText = "Ich habe deine Antwort nicht verstanden. Könntest du sie bitte noch einmal wiederholen?";
        
        // if (round.isComplete()) speechText += round.askQuestion().text; 
        
        return input.getResponseBuilder()
                .withSpeech(speechText)
                .build();
    }
}
