package quiz.handlers;

import static com.amazon.ask.request.Predicates.intentName;

import java.util.Map;
import java.util.Optional;
import java.util.Random;

import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.amazon.ask.dispatcher.request.handler.RequestHandler;
import com.amazon.ask.model.Response;

import quiz.model.QuizRound;

public class EndRoundIntentHandler implements RequestHandler {
	@Override
	public boolean canHandle(HandlerInput input) {
		if (input.matches(intentName("AMAZON.YesIntent").or(intentName("AMAZON.NoIntent")))) {
			Map<String, Object> sessionAttributes = input.getAttributesManager().getSessionAttributes();
			QuizRound round = QuizRound.fromSessionAttributes(sessionAttributes);
			
			return round.isComplete() && round.askedQuestions.length == 0;
		}
		return false; 
	}

	@Override
	public Optional<Response> handle(HandlerInput input) {
		StringBuilder speechText = new StringBuilder();
		Map<String, Object> sessionAttributes = input.getAttributesManager().getSessionAttributes();
		QuizRound round = QuizRound.fromSessionAttributes(sessionAttributes);

		if (input.matches(intentName("AMAZON.NoIntent"))) {
			speechText.append(" Ok. Dann sag ich " + (round.players.length == 1 ? "dir noch dein" : "euch noch euer") + " Level. ");
			for (int i=0; i<round.players.length; i++) {
				if (round.players[i].score == 0) {
					speechText.append("Schade "+ round.players[i].name +", du hast leider keine Frage richtig beantwortet. "
							+ "Versuch es doch gleich noch einmal. "
							+ "Beim nächsten mal klappt's bestimmt besser. "
							+ "Hier dein Level: "
							+ "Du bist ein Tourist");
				} else if (round.players[i].score == 1) {
					speechText.append("Schade "+ round.players[i].name +". "
							+ "Versuch es doch gleich noch einmal. "
							+ "Beim nächsten mal klappt's bestimmt besser. "
							+ "Hier dein Level: "
							+ "Du bist ein Tourist");
				} else if (round.players[i].score == 2 || round.players[i].score == 3) {
					speechText.append(round.players[i].name +", Du musst noch ein bisschen üben. Hier dein Level: "
							+ "Du bist ein Zugezogener");
				} else if (round.players[i].score == 4) {
					speechText.append("Super "+round.players[i].name +". Das ist schon richtig gut. Hier dein Level: "
							+ "Du bist ein Stadtführer");
				} else if(round.players[i].score == 5) {
					speechText.append("Sehr gut"+ round.players[i].name +", du hast alle Fragen richtig beantwortet! Du weißt ja wirklich alles."
							+ " Hier ist dein Level: Du bist ein Einheimischer");
				}
			}
			return input.getResponseBuilder().withSpeech(speechText.toString()).withShouldEndSession(true).build();
		} else {
			String[] yeah = new String[] {"Großartig! ", "Spitze! ", "Toll! "};
			int num = new Random().nextInt(yeah.length);
			speechText.append(yeah[num]);
			// TODO: Möchtest du weitere Fragen zu [REGION] spielen oder dir eine neue Region aussuchen?
			round.askNewQuestion(speechText);
			round.intoSessionAttributes(sessionAttributes);
			return input.getResponseBuilder().withSpeech(speechText.toString()).withReprompt(speechText.toString()).build();
		}
	}
}
