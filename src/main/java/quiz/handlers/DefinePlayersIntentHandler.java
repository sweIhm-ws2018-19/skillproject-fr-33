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

import quiz.QuizStreamHandler;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

import static com.amazon.ask.request.Predicates.intentName;

public class DefinePlayersIntentHandler implements RequestHandler {
    @Override
    public boolean canHandle(HandlerInput input) {
        return input.matches(intentName("DefinePlayersIntent"));
    }

    @Override
    public Optional<Response> handle(HandlerInput input) {
        Request request = input.getRequestEnvelope().getRequest();
        IntentRequest intentRequest = (IntentRequest) request;
        Intent intent = intentRequest.getIntent();
        Map<String, Slot> slots = intent.getSlots();

        // Get the color slot from the list of slots.
        Slot playerCountSlot = slots.get("Anzahl");

        String speechText, repromptText = "";
        boolean isAskResponse = false;

        // Check for favorite color and create output to user.
        if (playerCountSlot != null) {
            // Store the user's favorite color in the Session and create response.
        	String favoriteColor = playerCountSlot.getValue();
            input.getAttributesManager().setSessionAttributes(Collections.singletonMap("ANZAHL", favoriteColor)); //COLOR_KEY

            speechText = String.format("Es nehmen %s Spieler am Quiz teil.", favoriteColor);

        } else {
            // Render an error since we don't know what the users favorite color is.
            speechText = "Ungültige Eingabe. Es können zwischen 1 und 4 Menschen mitspielen. Bitte wähle erneut aus";
            repromptText =
                    "Ich weiss nicht welches Deine Lieblingsfarbe ist. Sag mir Deine Lieblingsfarbe. Sage zum Beispiel: ich mag blau.";
            isAskResponse = true;
        }

        ResponseBuilder responseBuilder = input.getResponseBuilder();

        responseBuilder.withSimpleCard("ColorSession", speechText)
                .withSpeech(speechText)
                .withShouldEndSession(false);

        if (isAskResponse) {
            responseBuilder.withShouldEndSession(false)
                    .withReprompt(repromptText);
        }

        return responseBuilder.build();
    }

}
