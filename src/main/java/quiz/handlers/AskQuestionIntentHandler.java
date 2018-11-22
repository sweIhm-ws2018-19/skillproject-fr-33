/*
     Copyright 2018 Amazon.com, Inc. or its affiliates. All Rights Reserved.

     Licensed under the Apache License, Version 2.0 (the "License"). You may not use this file
     except in compliance with the License. A copy of the License is located at

         http://aws.amazon.com/apache2.0/

     or in the "license" file accompanying this file. This file is distributed on an "AS IS" BASIS,
     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for
     the specific language governing permissions and limitations under the License.
*/

package quiz.handlers;

import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.amazon.ask.dispatcher.request.handler.RequestHandler;
import com.amazon.ask.model.Intent;
import com.amazon.ask.model.IntentRequest;
import com.amazon.ask.model.Request;
import com.amazon.ask.model.Response;
import com.amazon.ask.model.Slot;
import com.amazon.ask.response.ResponseBuilder;
import com.opencsv.CSVReader;

import quiz.ColorPickerStreamHandler;

import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.amazon.ask.request.Predicates.intentName;

public class AskQuestionIntentHandler implements RequestHandler {
    @Override
    public boolean canHandle(HandlerInput input) {
        return input.matches(intentName("AskQuestionIntent"));
    }

    @Override
    public Optional<Response> handle(HandlerInput input) {
    	/*
        Request request = input.getRequestEnvelope().getRequest();
        IntentRequest intentRequest = (IntentRequest) request;
        Intent intent = intentRequest.getIntent();
        Map<String, Slot> slots = intent.getSlots();

        // Get the color slot from the list of slots.
        Slot favoriteColorSlot = slots.get("Anzahl");

        String speechText, repromptText;
        boolean isAskResponse = false;

        // Check for favorite color and create output to user.
        if (favoriteColorSlot != null) {
            // Store the user's favorite color in the Session and create response.
            String favoriteColor = favoriteColorSlot.getValue();
            input.getAttributesManager().setSessionAttributes(Collections.singletonMap("ANZAHL", favoriteColor)); //COLOR_KEY

            speechText = String.format("Es nehmen %s Spieler am Quiz teil.", favoriteColor);
            repromptText = "Frage nach meiner Lieblingsfarbe.";
            
            if(favoriteColor.matches("eins")) {
            	ColorPickerStreamHandler.num_players = 1;
            }
            else if(favoriteColor.matches("eins")) {
            	ColorPickerStreamHandler.num_players = 2;
            }
            else if(favoriteColor.matches("eins")) {
            	ColorPickerStreamHandler.num_players = 3;
            }
            else {
            	ColorPickerStreamHandler.num_players = 4;
            }
            ColorPickerStreamHandler.current_player = 1;

        } else {
            // Render an error since we don't know what the users favorite color is.
            speechText = "Ungige Eingabe. Es knnen zwischen 1 und 4 Menschen mitspielen. Bitte whle erneut aus";
            repromptText =
                    "Ich weiss nicht welches Deine Lieblingsfarbe ist. Sag mir Deine Lieblingsfarbe. Sage zum Beispiel: ich mag blau.";
            isAskResponse = true;
        }
	*/
    	String speechText = "test";
    	try {
    		
    	 CSVReader reader = new CSVReader(new InputStreamReader(AskQuestionIntentHandler.class.getResourceAsStream("/Berlin.csv")), ',', '"', 1);
	       
	      //Read all rows at once
	      List<String[]> allRows = reader.readAll();
	      
	      String[] tt = allRows.get(1)[0].replaceAll("\"|^\\d","").split(";");
	      String[] ss = Arrays.copyOfRange(tt,1,tt.length);

	      speechText = ss[0] + "A ." + ss[1] + " ? B ." + ss[2] + " ?Oder C ." + ss[3];
    	}
    	catch(IOException e) {} 
    	
        ResponseBuilder responseBuilder = input.getResponseBuilder();

        responseBuilder.withSimpleCard("ColorSession", speechText)
                .withSpeech(speechText)
                .withShouldEndSession(false);

     /*   if (isAskResponse) {
            responseBuilder.withShouldEndSession(false)
                    .withReprompt(repromptText);
        } */

        return responseBuilder.build();
    }

}
