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
import com.amazon.ask.model.Response;

import quiz.model.QuizRound;

import java.util.Optional;

import static com.amazon.ask.request.Predicates.intentName;

public class HelpIntentHandler implements RequestHandler {
    @Override
    public boolean canHandle(HandlerInput input) {
        return input.matches(intentName("AMAZON.HelpIntent"));
    }

    @Override
    public Optional<Response> handle(HandlerInput input) {
        String speechText = "In diesem Quiz werden dir Fragen zu bestimmten Regionen und Städten gestellt. \n"
        		+ " Du kannst deine Route berechnen lassen oder eine bestimmte Region wählen.\n"
        		+ " Jede Quizrunde werden dir "+QuizRound.length+" Fragen gestellt. "
        		+ "Für jede korrekte Antwort erhältst du einen Punkt. "
        		+ "Wähle die Anzahl der Spieler wenn du das Quiz startest.\n "
        		+ "Du kannst alleine oder in einer Gruppe von bis zu fünf Spielern spielen.";
        return input.getResponseBuilder()
                .withSimpleCard("ColorSession", speechText)
                .withSpeech(speechText)
                .withReprompt(speechText)
                .withShouldEndSession(false)
                .build();
    }
}
