package main.java.colorpicker.handlers;

import static com.amazon.ask.request.Predicates.intentName;
import static main.java.colorpicker.handlers.WhatsMyColorIntentHandler.COLOR_KEY;
import static main.java.colorpicker.handlers.WhatsMyColorIntentHandler.COLOR_SLOT;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.amazon.ask.dispatcher.request.handler.RequestHandler;
import com.amazon.ask.model.Intent;
import com.amazon.ask.model.IntentRequest;
import com.amazon.ask.model.Request;
import com.amazon.ask.model.Response;
import com.amazon.ask.model.Slot;
import com.amazon.ask.response.ResponseBuilder;

public class StartQuizIntentHandler implements RequestHandler {
	public static final String ANSWER_SLOT = "Answer";
	
	@Override
    public boolean canHandle(HandlerInput input) {
        return input.matches(intentName("StartQuizIntent"));
    }
	
	@Override
    public Optional<Response> handle(HandlerInput input) {
        String speechText;
        String answerGPS = (String) input.getAttributesManager().getSessionAttributes().get(ANSWER_SLOT);

        speechText = "Hallo! Schön, dass du da bist! Willst du, dass ich dich über GPS orte?";
        
        if(answerGPS.equals("Ja"))
        {
        	speechText = "Okay. Lass uns loslegen! Du befindest Dich gerade in der Nähe des Starnberger Sees. Möchtest Du zu dieser Region ein Quiz spielen?";
        }
        else if(answerGPS.equals("Nein"))
        {
        	speechText = "Ok. Auf deiner Route liegen die Regionen Starnberger See, Garmisch und Tiroler Alpen. Über welche Region möchtest du spielen?";
        }
        else
        {
        	speechText = "Ich habe deine Antwort nicht verstanden. Könntest du sie bitte noch einmal wiederholen?";
        }
        
        return input.getResponseBuilder()
                .withSpeech(speechText)
                .withSimpleCard("ColorSession", speechText)
                .build();
    }
}
